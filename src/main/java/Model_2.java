public class Model_2 {
    @Bind private int LL;   // number of years

    // Basic economic indicators from Model_1
    @Bind private double[] KI;    // private consumption
    @Bind private double[] KS;    // public consumption
    @Bind private double[] INW;   // investments
    @Bind private double[] EKS;   // export
    @Bind private double[] IMP;   // import
    @Bind private double[] PKB;   // GDP

    // Labor market indicators from Model_2
    @Bind private double[] WAGE;  // average wage
    @Bind private double[] EMP;   // employed population
    @Bind private double[] UNEMP; // unemployed population

    // Financial indicators from Model_3
    @Bind private double[] INF;   // inflation rate
    @Bind private double[] INT;   // interest rate

    // Growth rates
    @Bind private double[] twKI;    // private consumption growth
    @Bind private double[] twKS;    // public consumption growth
    @Bind private double[] twINW;   // investment growth
    @Bind private double[] twEKS;   // export growth
    @Bind private double[] twIMP;   // import growth
    @Bind private double[] twWAGE;  // wage growth rate
    @Bind private double[] twINF;   // inflation growth rate
    private double temp;

    public Model_2() {}

    public void run() {
        EMP[0] = EMP[0];
        UNEMP[0] = PKB[0] * 0.6 - EMP[0];
        WAGE[0] = WAGE[0];
        KI[0] = KI[0];

        int t = 1;
        EMP[t] = EMP[0];
        double laborForce = PKB[0] * 0.6;
        UNEMP[t] = laborForce - EMP[t];
        WAGE[t] = WAGE[0] * twWAGE[t];
        KI[t] = KI[0] * (1 + (WAGE[t] - WAGE[0])/WAGE[0]);

        for (t = 2; t < LL; t++) {
            double gdpGrowth = (PKB[t-1] - PKB[t-2])/PKB[t-2];
            EMP[t] = EMP[t-1] * (1 + gdpGrowth);
            laborForce = PKB[t-1] * 0.6;
            UNEMP[t] = laborForce - EMP[t];
            WAGE[t] = WAGE[t-1] * twWAGE[t] * (1 + gdpGrowth);
            KI[t] = KI[t-1] * (1 + (WAGE[t] - WAGE[t-1])/WAGE[t-1]);
        }
    }
}