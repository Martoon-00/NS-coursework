package ru.ifmo.modeling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EquationSystems {


    /**
     * Order of variables: AlCl3(0), GaCl(1), NH3(2), HCl(3), H2(4), x(5)
     *
     * @param H2Portion portion of H2 in H2 + N2.
     *                  2 cases of this value should be considered: 0 and 0.1
     * @return function x^g -> list of P_i^g (5), x, G_i (5)
     */
    public static Function<Double, List<Double>> createEquationSystem3(double H2Portion, List<Double> initial, double epsilon, int maxIterationNumber) {
        double t = 1100 + 273;
        double K9 = Coefficients.getKCounter(9).apply(t);
        double K10 = Coefficients.getKCounter(10).apply(t);
        ArrayList<String> elementNames = new ArrayList<>(Arrays.asList("AlCl3", "GaCl", "NH3", "HCl", "H2"));
        return xg -> {
            ArrayList<Double> pg = new ArrayList<>(Arrays.asList(xg * 30, (1 - xg) * 30, 1500., 0., H2Portion * 98470));
            BiFunction<List<Double>, Integer, Double> mf = (vars, k) -> Coefficients.getDCounter(elementNames.get(k)).apply(t) * (pg.get(k) - vars.get(k));

            List<Function<List<Double>, Double>> functions = Arrays.asList(
                    vars -> vars.get(0) * vars.get(2) - K9 * vars.get(5) * Math.pow(vars.get(3), 3),
                    vars -> vars.get(1) * vars.get(2) - K10 * (1 - vars.get(5)) * vars.get(3) * vars.get(4),
                    vars -> mf.apply(vars, 3) + 2 * mf.apply(vars, 4) + 3 * mf.apply(vars, 2),
                    vars -> 3 * mf.apply(vars, 0) + mf.apply(vars, 1) + mf.apply(vars, 3),
                    vars -> mf.apply(vars, 0) + mf.apply(vars, 1) - mf.apply(vars, 2),
                    vars -> mf.apply(vars, 0) * (1 - vars.get(5)) - mf.apply(vars, 1) * vars.get(5)
            );
            List<List<Function<List<Double>, Double>>> derivatives = Arrays.asList(
                    Arrays.asList(  // derivatives of function(0)
                            vars -> vars.get(2),
                            vars -> 0.,
                            vars -> vars.get(0),
                            vars -> -3 * K9 * vars.get(5) * Math.pow(vars.get(3), 2),
                            vars -> 0.,
                            vars -> -K9 * Math.pow(vars.get(3), 3)
                    ),
                    Arrays.asList(  // derivatives of function(1)
                            vars -> 0.,
                            vars -> vars.get(2),
                            vars -> vars.get(1),
                            vars -> -K10 * (1 - vars.get(5)) * vars.get(4),
                            vars -> -K10 * (1 - vars.get(5)) * vars.get(3),
                            vars -> K10 * vars.get(3) * vars.get(4)
                    ),
                    Arrays.asList(  // derivatives of function(2)
                            vars -> 0.,
                            vars -> 0.,
                            vars -> -3 * Coefficients.getDCounter("NH3").apply(t),
                            vars -> -Coefficients.getDCounter("HCl").apply(t),
                            vars -> -2 * Coefficients.getDCounter("H2").apply(t),
                            vars -> 0.
                    ),
                    Arrays.asList( // derivatives of function(3)
                            vars -> -3 * Coefficients.getDCounter("AlCl3").apply(t),
                            vars -> -Coefficients.getDCounter("GaCl").apply(t),
                            vars -> 0.,
                            vars -> -2 * Coefficients.getDCounter("HCl").apply(t),
                            vars -> 0.,
                            vars -> 0.
                    ),
                    Arrays.asList( // derivatives of function(4)
                            vars -> -Coefficients.getDCounter("AlCl3").apply(t),
                            vars -> -Coefficients.getDCounter("GaCl").apply(t),
                            vars -> Coefficients.getDCounter("NH3").apply(t),
                            vars -> 0.,
                            vars -> 0.,
                            vars -> 0.
                    ),
                    Arrays.asList( // derivatives of function(5)
                            vars -> -Coefficients.getDCounter("AlCl3").apply(t) * (1 - vars.get(5)),
                            vars -> Coefficients.getDCounter("GaCl").apply(t) * vars.get(5),
                            vars -> 0.,
                            vars -> 0.,
                            vars -> 0.,
                            vars -> -mf.apply(vars, 0) - mf.apply(vars, 1)
                    )
            );

            System.out.println(xg);
            for (List<Function<List<Double>, Double>> derivative : derivatives) {
                for (Function<List<Double>, Double> function : derivative) {
                    System.out.print(String.format("%.2f ", function.apply(initial)));
                }
                System.out.println();
            }
            System.out.println();


            return new SystemOfEquationsSolve(functions, derivatives, t).getSolution(initial, epsilon, maxIterationNumber);
        };
    }
}
