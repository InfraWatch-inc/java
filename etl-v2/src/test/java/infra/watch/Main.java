package infra.watch;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class Main {
    public static void main(String[] args) {
        // Dados: 4 observações, 2 variáveis independentes
        double[][] X = {
                {1, 2},
                {2, 1},
                {4, 3},
                {3, 5}
        };

        double[] Y = {5, 6, 10, 12};

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(Y, X); // y, x

        double[] beta = regression.estimateRegressionParameters(); // coeficientes β
        double r2 = regression.calculateRSquared();                // R²

        System.out.printf("Coeficientes: %s\n", java.util.Arrays.toString(beta));
        System.out.printf("R² = %.4f\n", r2);
    }
}
