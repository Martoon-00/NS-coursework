package ru.ifmo.modeling;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Counts K and D coefficients (use getKCounter() ans getDCounter() for this purpose).
 */
public class Coefficients {
    // ----------------------------------------------- coefficients initialization -----------------------------------------------

    /**
     * Element name -> coefficients
     */
    private static Map<String, ElementCoefs> coefs = new HashMap<>();

    static {
        registerElementCoefs("AlCl", new double[]{-51032., 318.9948, 36.94626, -0.001226431, 1.1881743, 5.638541, -5.066135, 5.219347, 62.4345, 3.58, 932.});
        registerElementCoefs("AlCl2", new double[]{-259000., 427.2137, 56.56409, -0.002961273, 1.893842, 12.40072, -22.65441, 21.29898, 97.8875, 5.3, 825.});
        registerElementCoefs("AlCl3", new double[]{-584100., 511.8114, 81.15042, -0.004834879, 2.752097, 13.40078, -21.28001, 16.92868, 133.3405, 5.13, 472.});
        registerElementCoefs("GaCl", new double[]{-70553., 332.2718, 37.11052, -0.000746187, 1.1606512, 4.891346, -4.467591, 5.506236, 105.173, 3.696, 348.2});
        registerElementCoefs("GaCl2", new double[]{-241238., 443.2976, 57.745845, -0.002265112, 1.8755545, 3.66186, -9.356338, 15.88245, 140.626, 4.293, 465.});
        registerElementCoefs("GaCl3", new double[]{-431573., 526.8113, 82.03355, -0.003486473, 2.6855923, 8.278878, -14.5678, 12.8899, 176.080, 5.034, 548.24});
        registerElementCoefs("NH3", new double[]{-45940., 231.1183, 20.52222, 0.000716251, 0.7677236, 244.6296, -251.69, 146.6947, 17.031, 3.0, 300.});
        registerElementCoefs("H2", new double[]{0., 205.5368, 29.50487, 0.000168424, 0.86065612, -14.95312, 78.18955, -82.78981, 2.016, 2.93, 34.1});
        registerElementCoefs("HCl", new double[]{-92310., 243.9878, 23.15984, 0.001819985, 0.6147384, 51.16604, -36.89502, 9.174252, 36.461, 2.737, 167.1});
        registerElementCoefs("N2", new double[]{0., 242.8156, 21.47467, 0.001748786, 0.5910039, 81.08497, -103.6265, 71.30775, 28.0135, 3.798, 71.4});
        registerElementCoefs("Al", new double[]{0., 172.8289, 50.51806, -0.00411847, 1.476107, -458.1279, 2105.75, -4168.337, 26.9815});
        registerElementCoefs("Ga", new double[]{0., 125.9597, 26.03107, 0.001178297, 0.13976, -0.5698425, 0.04723008, 7.212525, 69.723});
        registerElementCoefs("AlN", new double[]{-319000., 123.1132, 44.98092, -0.00734504, 1.86107, 31.39626, -49.92139, 81.22038, 40.988});
        registerElementCoefs("GaN", new double[]{-114000., 160.2647, 52.86351, -0.00799055, 2.113389, 1.313428, -2.441129, 1.945731, 83.730});
    }

    /**
     * Element name -> density
     */
    private static Map<String, Double> densities = new HashMap<>();

    static {
        registerElementDensity("Al", 2690);
        registerElementDensity("Ga", 5900);
        registerElementDensity("AlN", 3200);
        registerElementDensity("GaN", 6150);
    }

    /**
     * i -> T -> K_i(T)
     */
    private static Map<Integer, Function<Double, Double>> KCounters = new HashMap<>();

    static {
        KCounters.put(1, new KBuilder(-1).plus(2, "Al").plus(2, "HCl").minus(2, "AlCl").minus(1, "H2").build());
        KCounters.put(2, new KBuilder(0).plus(1, "Al").plus(2, "HCl").minus(1, "AlCl2").minus(1, "H2").build());
        KCounters.put(3, new KBuilder(1).plus(2, "Al").plus(6, "HCl").minus(2, "AlCl3").minus(3, "H2").build());
        KCounters.put(4, new KBuilder(-1).plus(2, "Ga").plus(2, "HCl").minus(2, "GaCl").minus(1, "H2").build());
        KCounters.put(5, new KBuilder(0).plus(1, "Ga").plus(2, "HCl").minus(1, "GaCl2").minus(1, "H2").build());
        KCounters.put(6, new KBuilder(1).plus(2, "Ga").plus(6, "HCl").minus(2, "GaCl3").minus(3, "H2").build());
        KCounters.put(9, new KBuilder(-1).plus(1, "AlCl3").plus(1, "NH3").minus(1, "AlN").minus(3, "HCl").build());
        KCounters.put(10, new KBuilder(0).plus(1, "GaCl").plus(1, "NH3").minus(1, "GaN").minus(1, "HCl").minus(1, "H2").build());
    }

    // ----------------------------------------------- methods -----------------------------------------------

    private static void registerElementCoefs(String element, double[] params) {
        coefs.put(element.toUpperCase(), new ElementCoefs(params));
    }

    private static void registerElementDensity(String element, double density) {
        densities.put(element.toUpperCase(), density);
    }

    /**
     * @param element name of element. Not case-sensitive.
     * @return coefficients for specified element
     * @throws NullPointerException if no such element found
     */
    public static ElementCoefs getElementCoefs(String element) {
        return coefs.get(element.toUpperCase());
    }

    /**
     * @param element name of element. Not case-sensitive.
     * @return density
     * @throws NullPointerException if no such element found
     */
    public static double getElementDensity(String element) {
        return densities.get(element.toUpperCase());
    }

    /**
     * @param element name of element x
     * @return function T -> G_x
     * @throws NullPointerException if no such element found
     */
    private static Function<Double, Double> getGCounter(String element) {
        return coefs.get(element.toUpperCase()).getGCounter();
    }

    /**
     * @param number i
     * @return function T -> K_i, or null if K_i doesn't specified
     */
    public static Function<Double, Double> getKCounter(int number) {
        return KCounters.get(number);
    }

    /**
     * @param element element name x (for example, "AlCl3"). Not case-sensitive.
     * @return function T -> D_x
     * @throws NullPointerException if no such element found
     */
    public static Function<Double, Double> getDCounter(String element) {
        return coefs.get(element.toUpperCase()).getDCounter();
    }

    public static double getMolarVolume(String element) {
        return getElementCoefs(element).mu / getElementDensity(element);
    }

    // ----------------------------------------------- useful classes -----------------------------------------------

    /**
     * Contains information about coefficients of some element (h, f1..7, mu, sigma, eps)
     */
    public static class ElementCoefs {
        public final double h, mu, sigma, eps;
        public final double[] f = new double[7];

        /**
         * @param params array with params in order: h, f1, f2, ... f7, mu, [sigma, eps].
         *               (parameters in braces are optional)
         */
        public ElementCoefs(double[] params) {
            h = params[0];
            System.arraycopy(params, 1, f, 0, 7);
            mu = params[8];
            if (params.length > 9) {
                sigma = params[9];
                eps = params[10];
            } else {
                sigma = eps = Double.NaN;
            }
        }

        /**
         * @return function T -> K for specified coefficients.
         */
        public Function<Double, Double> getGCounter() {
            return t -> {
                double x = t / 1e4;
                return h - t * (f[0] + f[1] * Math.log(x) + (f[2] / x + f[3]) / x + (f[4] + (f[5] + f[6] * x) * x) * x);
            };
        }

        /**
         * @return function T -> D for specified coefficients.
         */
        public Function<Double, Double> getDCounter() {
            return t -> {
                ElementCoefs n2 = getElementCoefs("N2");
                double sigma_ = (sigma + n2.sigma) / 2;
                double eps_ = Math.sqrt(eps * n2.eps);
                double mu_ = 2 * mu * n2.mu / (mu + n2.mu);
                return 2.628e-2 * Math.pow(t, 1.5) / (1e5 * sigma_ * 1.074 * Math.pow(t / eps_, -0.1604) * Math.sqrt(mu_));
            };
        }
    }

    /**
     * Builds formula of K according to general formula type: K = sum(k * G_x) * P_A ^ n
     */
    private static class KBuilder {
        /**
         * Power of P_A in formula of K.
         */
        private final double P_power;

        /**
         * x -> coefficient with which G_x is encountered in formula
         */
        private final Map<String, Integer> G_coefs = new HashMap<>();

        /**
         * Initiates formula with no G_x addend, only with power of P_A;
         *
         * @param p_power power of P_A
         */
        public KBuilder(double p_power) {
            P_power = p_power;
        }

        /**
         * Add an addend "k * G_x" with sign "+" to formula.
         *
         * @param coef    k
         * @param element x
         * @return same KBuilder, in new state
         */
        public KBuilder plus(int coef, String element) {
            element = element.toUpperCase();
            if (G_coefs.containsKey(element.toUpperCase()))
                throw new RuntimeException("Element have already been added in formula");
            G_coefs.put(element, coef);
            return this;
        }

        /**
         * Add an addend "k * G_x" with sign "-" to formula.
         *
         * @param coef    k
         * @param element x
         * @return same KBuilder, in new state
         */
        public KBuilder minus(int coef, String element) {
            return plus(-coef, element);
        }

        /**
         * @return T -> K, according to internal formula.
         */
        public Function<Double, Double> build() {
            return t -> {
                Double deltaG = G_coefs.entrySet().stream().reduce(0.0, (res, coef) -> res + coef.getValue() * getGCounter(coef.getKey()).apply(t), (a, b) -> a + b);
                return Math.exp(-deltaG / 8.3144621 / t) * Math.pow(1e5, P_power);
            };
        }
    }
}
