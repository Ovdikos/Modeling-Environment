public class Model_1 {

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
    private double temp ;
    public Model_1 ( ) {}
    public void run ( ) {
        PKB = new double[LL];
        PKB[0] = KI[0] + KS[0] + INW[0] + EKS[0] - IMP[0];
        for (int t = 1; t < LL; t++) {
            KI[t] = twKI[t] * KI[t - 1];
            KS[t] = twKS[t] * KS[t - 1];
            INW[t] = twINW[t] * INW[t - 1];
            EKS[t] = twEKS[t] * EKS[t - 1];
            IMP[t] = twIMP[t] * IMP[t - 1];
            PKB[t] = KI[t] + KS[t] + INW[t] + EKS[t] - IMP[t];
        }
    }
}
