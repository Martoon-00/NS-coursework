package ru.ifmo.lang;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Allows to organize an experiment some times.
 * Example of usage:
 * for (Typke typke : new Experiment(x -> x * x, -3, 5, 2).mapX(x -> -x).mapY(y -> 2 * y).get()) {
 * System.out.println(String.format("(%.1f; %.1f)", typke.x, typke.y));
 * }
 * This will have the following output:
 * (3.0; 18.0)
 * (1.0; 2.0)
 * (-1.0; 2.0)
 * (-3.0; 18.0)
 * (-5.0; 50.0)
 */
public class Experiment {
    private final List<Typke> measures;

    /**
     * Creates an experiment. f is launched on every value in [a; b] with specified step.
     * For example,
     * new Experiment(x -> x * x, -3, 5, 2)
     * will create a set of measures: (-3, 9), (-1, 1), (1, 1), (3, 9), (5, 25)
     */
    public Experiment(Function<Double, Double> f, double a, double b, double step) {
        measures = new ArrayList<>();
        for (double x = a; x < b * (1 + 1e-9); x += step) {
            double y;
            try {
                y = f.apply(x);
            } catch (Exception e) {
                y = Double.NaN;
            }
            measures.add(new Typke(x, y));
        }
    }

    private Experiment(List<Typke> measures) {
        this.measures = measures;
    }

    /**
     * @return results of experiment
     */
    public List<Typke> get() {
        return measures;
    }

    /**
     * Maps input values of experiments.
     * For example,
     * new Experiment(x -> x * x, -3, 5, 2).mapX(x -> -x)
     * will create an experiment with following set of measures: (3, 9), (1, 1), (-1, 1), (-3, 9), (-5, 25)
     *
     * @param f mapping function
     * @return new Experiment instance with changes applied
     */
    public Experiment mapX(Function<Double, Double> f) {
        return new Experiment(measures.stream().map(typke -> new Typke(f.apply(typke.x), typke.y)).collect(Collectors.toList()));
    }

    /**
     * Maps result values of experiments.
     * For example,
     * new Experiment(x -> x * x, -3, 5, 2).mapY(y -> 2 * y)
     * will create an experiment with following set of measures: (-3, 18), (-1, 2), (1, 2), (3, 18), (5, 50)
     *
     * @param f mapping function
     * @return new Experiment instance with changes applied
     */
    public Experiment mapY(Function<Double, Double> f) {
        return new Experiment(measures.stream().map(typke -> new Typke(typke.x, f.apply(typke.y))).collect(Collectors.toList()));
    }


    /**
     * Help to create a series of experiments on same x diapason but with different functions.
     */
    public static class ExperimentSeries {
        private final double a, b, step;
        private final ArrayList<Function<Double, Double>> xMaps = new ArrayList<>();
        private final ArrayList<Function<Double, Double>> yMaps = new ArrayList<>();

        /**
         * Creates experiment series with specified interval and step
         *
         * @param a    left end of interval
         * @param b    right end of interval
         * @param step step
         */
        public ExperimentSeries(double a, double b, double step) {
            this.a = a;
            this.b = b;
            this.step = step;
        }

        /**
         * Creates experiment with specified function
         */
        public Experiment create(Function<Double, Double> f) {
            Experiment experiment = new Experiment(f, a, b, step);
            for (Function<Double, Double> xMap : xMaps) {
                experiment = experiment.mapX(xMap);
            }
            for (Function<Double, Double> yMap : yMaps) {
                experiment = experiment.mapY(yMap);
            }
            return experiment;
        }

        /**
         * Adds future x mapping
         *
         * @param f mapping function
         * @return same object in new state
         */
        public ExperimentSeries mapX(Function<Double, Double> f) {
            xMaps.add(f);
            return this;
        }

        /**
         * Adds future y mapping
         *
         * @param f mapping function
         * @return same object in new state
         */
        public ExperimentSeries mapY(Function<Double, Double> f) {
            yMaps.add(f);
            return this;
        }
    }

}
