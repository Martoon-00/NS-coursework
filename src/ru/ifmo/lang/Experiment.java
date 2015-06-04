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
            measures.add(new Typke(x, f.apply(x)));
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

}
