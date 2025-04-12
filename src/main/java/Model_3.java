public class Model_3 {
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

    public Model_3() {}

    public void run() {
        INF[0] = INF[0];
        INT[0] = INT[0];
        INW[0] = INW[0];
        EKS[0] = EKS[0];
        IMP[0] = IMP[0];
        PKB[0] = KI[0] + KS[0] + INW[0] + EKS[0] - IMP[0];

        int t = 1;
        INF[t] = INF[0] * twINF[t];
        INT[t] = INT[0] + (INF[t] - 0.02) * 1.5;
        INW[t] = INW[0] * (1 - (INT[t] - INT[0]));
        EKS[t] = EKS[0] * twEKS[t] * (1 - INF[t]);
        IMP[t] = IMP[0] * twIMP[t] * (1 + INF[t]);
        PKB[t] = KI[t] + KS[t] + INW[t] + EKS[t] - IMP[t];

        for (t = 2; t < LL; t++) {
            double gdpGrowth = (PKB[t-1] - PKB[t-2])/PKB[t-2];
            INF[t] = INF[t-1] * twINF[t] * (1 + gdpGrowth);
            INT[t] = INT[t-1] + (INF[t] - 0.02) * 1.5;
            INW[t] = INW[t-1] * (1 - (INT[t] - INT[t-1]));
            EKS[t] = EKS[t-1] * twEKS[t] * (1 - INF[t]);
            IMP[t] = IMP[t-1] * twIMP[t] * (1 + INF[t]);
            PKB[t] = KI[t] + KS[t] + INW[t] + EKS[t] - IMP[t];
        }
    }
}