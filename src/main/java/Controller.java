import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Controller {

    private String modelName;
    private Object currentModel;
    private String scriptResults;

    private String[] years;


    public Controller(String modelName) {
        this.modelName = modelName;

        try {
            Class<?> modelClass = Class.forName(modelName);
            currentModel = modelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creating model: " + modelName, e);
        }
    }

    public Controller readDataFrom(String fname) {
        try {
            List<String> lines = Files.readAllLines(Path.of(fname));
            int ll = 0;

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                if (parts[0].equals("LATA")) {
                    ll = parts.length - 1;

                    years = Arrays.copyOfRange(parts, 1, parts.length);

                    Field llField = currentModel.getClass().getDeclaredField("LL");
                    llField.setAccessible(true);
                    llField.setInt(currentModel, ll);
                    break;
                }
            }

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                String varName = parts[0];

                if (varName.equals("LATA")) continue;

                try {
                    Field field = currentModel.getClass().getDeclaredField(varName);
                    if (field.isAnnotationPresent(Bind.class)) {
                        field.setAccessible(true);

                        double[] values = new double[ll];

                        for (int i = 0; i < ll; i++) {
                            if (i < parts.length - 1) {
                                values[i] = Double.parseDouble(parts[i + 1]);
                            } else {
                                values[i] = Double.parseDouble(parts[parts.length - 1]);
                            }
                        }

                        field.set(currentModel, values);
                    }
                } catch (NoSuchFieldException e) {
                    continue;
                } catch (Exception e) {
                    System.out.println("Error processing field: " + varName);
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + fname, e);
        }
        return this;
    }

    public Controller runModel() {
        try {
            Method runMethod = currentModel.getClass().getMethod("run");
            runMethod.invoke(currentModel);
        } catch (Exception e) {
            System.out.println("Error details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error running model", e);
        }
        return this;
    }


    public Controller runScript(String script) {
        StringBuilder output = new StringBuilder();
        try {
            File tempScript = File.createTempFile("script", ".py");
            FileWriter writer = new FileWriter(tempScript);

            for (Field field : currentModel.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Bind.class)) {
                    field.setAccessible(true);
                    Object value = field.get(currentModel);
                    if (value instanceof double[]) {
                        double[] array = (double[]) value;
                        writer.write(field.getName() + " = [");
                        for (int i = 0; i < array.length; i++) {
                            writer.write(String.valueOf(array[i]));
                            if (i < array.length - 1) writer.write(", ");
                        }
                        writer.write("]\n");
                    } else {
                        writer.write(field.getName() + " = " + value + "\n");
                    }
                }
            }

            output.append(getResultsAsTsv());
            writer.write(script);
            writer.close();

            Process p = Runtime.getRuntime().exec("python " + tempScript.getAbsolutePath());

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                output.append(line).append("\n");
            }

            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = err.readLine()) != null) {
                output.append("Error: ").append(line).append("\n");
            }

            p.waitFor();
            tempScript.delete();

            this.scriptResults = output.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error executing Python script: " + e.getMessage(), e);
        }
        return this;
    }

    public Controller runScriptFromFile(String fname) {
        try {
            String script = Files.readString(Path.of(fname));
            return runScript(script);
        } catch (Exception e) {
            this.scriptResults = "Error running script from file: " + fname + "\n" + e.getMessage();
            throw new RuntimeException("Error running script from file: " + fname, e);
        }
    }


    public String getResultsAsTsv() {

        if (scriptResults != null && !scriptResults.isEmpty()) {
            return scriptResults;
        }

        StringBuilder result = new StringBuilder();

        if (years != null) {
            result.append("LATA");
            for (String year : years) {
                result.append("\t").append(year);
            }
            result.append("\n");
        }

        try {
            Field[] fields = currentModel.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Bind.class)) {
                    field.setAccessible(true);
                    Object value = field.get(currentModel);

                    result.append(field.getName());

                    if (value instanceof double[]) {
                        double[] array = (double[]) value;
                        for (double v : array) {
                            result.append("\t").append(v);
                        }
                    } else {
                        result.append("\t").append(value);
                    }
                    result.append("\n");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting results as TSV", e);
        }
        return result.toString();
    }
}
