import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private JList<String> modelList;
    private JList<String> dataList;
    private JButton runButton;
    private JButton scriptButton;
    private JButton adhocScriptButton;
    private JPanel resultPanel;
    private JTextArea resultArea;
    private Controller controller;

    public GUI() {
        setTitle("AME");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select model and data"));

        DefaultListModel<String> modelListModel = new DefaultListModel<>();
        modelListModel.addElement("Model_1");
        modelListModel.addElement("Model_2");
        modelListModel.addElement("Model_3");
        modelList = new JList<>(modelListModel);
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListModel<String> dataListModel = new DefaultListModel<>();
        dataListModel.addElement("data_1.txt");
        dataListModel.addElement("data_2.txt");
        dataList = new JList<>(dataListModel);
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(modelList), new JScrollPane(dataList));
        selectionPanel.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        runButton = new JButton("Run model");
        scriptButton = new JButton("Run script from file");
        adhocScriptButton = new JButton("Create and run ad-hoc script");

        buttonPanel.add(runButton);
        buttonPanel.add(scriptButton);
        buttonPanel.add(adhocScriptButton);
        selectionPanel.add(buttonPanel, BorderLayout.SOUTH);

        resultPanel = new JPanel(new BorderLayout());
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(selectionPanel, BorderLayout.WEST);
        add(resultPanel, BorderLayout.CENTER);

        runButton.addActionListener(e -> runModel());
        scriptButton.addActionListener(e -> runScriptFromFile());
        adhocScriptButton.addActionListener(e -> createAndRunScript());
    }

    private void runModel() {
        String selectedModel = modelList.getSelectedValue();
        String selectedData = dataList.getSelectedValue();

        if (selectedModel == null || selectedData == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both model and data file",
                    "Selection required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controller = new Controller(selectedModel);
            controller.readDataFrom(selectedData).runModel();

            String results = controller.getResultsAsTsv();
            resultArea.setText(results);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error running model: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runScriptFromFile() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Please run model first",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.runScriptFromFile(fileChooser.getSelectedFile().getPath());
                String results = controller.getResultsAsTsv();
                resultArea.setText(results);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error running script: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createAndRunScript() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Please run model first",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextArea scriptArea = new JTextArea(20, 40);
        JScrollPane scrollPane = new JScrollPane(scriptArea);

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Enter Python Script", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                controller.runScript(scriptArea.getText());
                String results = controller.getResultsAsTsv();
                resultArea.setText(results);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error running script: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI frame = new GUI();
            frame.setVisible(true);
        });
    }
}