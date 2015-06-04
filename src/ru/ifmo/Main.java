package ru.ifmo;

import ru.ifmo.graphics.Graphics;
import ru.ifmo.lang.CachingWrapper;
import ru.ifmo.lang.Experiment;
import ru.ifmo.modeling.Coefficients;
import ru.ifmo.modeling.EquationSystems3;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        analyzeEqSystem3(0.1);
    }

    public static void analyzeEqSystem3(double h2Portion) {
        double R = 8314.4621;
        double T = 1100 + 273;
        double sigma = 0.01;
        Function<String, Double> mr = element -> Coefficients.getElementCoefs(element).mu / Coefficients.getElementDensity(element);

        Function<Double, List<Double>> solver = new CachingWrapper<>(EquationSystems3.createEquationSystem3(h2Portion, Arrays.asList(50., 50., 1300., 0., 100., 0.5), 1e-2, 10000));
        Experiment.ExperimentSeries experiments = new Experiment.ExperimentSeries(0, 1, 0.01);

        new Graphics("x^g", "G")
                .addGraphic(experiments.create(xg -> (30 * xg - solver.apply(xg).get(0)) / R / T / sigma).get(), "G_AlCl3")
                .addGraphic(experiments.create(xg -> (30 * (1 - xg) - solver.apply(xg).get(1)) / R / T / sigma).get(), "G_GaCl")
                .show();
        new Graphics("x^g", "V_AlGaN")
                .addGraphic(experiments.create(xg -> (solver.apply(xg).get(0) * mr.apply("AlN") + solver.apply(xg).get(1) * mr.apply("GaN")) * 1e9).get(), "V_AlGaN")
                .show();
        new Graphics("x^g", "x")
                .addGraphic(experiments.create(xg -> solver.apply(xg).get(5)).get(), "x")
                .show();
    }

}
