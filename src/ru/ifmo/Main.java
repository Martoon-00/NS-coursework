package ru.ifmo;

import ru.ifmo.graphics.Graphics;
import ru.ifmo.lang.CachingWrapper;
import ru.ifmo.lang.Experiment;
import ru.ifmo.modeling.Coefficients;
import ru.ifmo.modeling.EquationSystems;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        analyzeEqSystem2();
    }

    public static void analyzeEqSystem1() {
        double R = 8314.4621;
        double sigma = 0.01;

        Experiment.ExperimentSeries experiments = new Experiment.ExperimentSeries(350 + 273, 650 + 273, 3).mapX(x -> 1 / x).mapY(Math::log);

        Function<Double, List<Double>> solver = new CachingWrapper<>(EquationSystems.createEquationSystem1(Arrays.asList(50., 50., 50., 50., 500.), 1e-2, 10000));

        new Graphics("T^-1", "ln V_Al")
                .addGraphic(experiments.create(t -> solver.apply(t).subList(0, 3).stream().reduce(0., (a, b) -> a + b) * Coefficients.getMolarVolume("Al") * 1e9).get(), "V_Al")
                .show();
        new Graphics("T^-1", "ln G")
                .addGraphic(experiments.create(t -> solver.apply(t).get(0) / R / t / sigma * Coefficients.getDCounter("AlCl").apply(t)).get(), "AlCl")
                .addGraphic(experiments.create(t -> solver.apply(t).get(1) / R / t / sigma * Coefficients.getDCounter("AlCl2").apply(t)).get(), "AlCl2")
                .addGraphic(experiments.create(t -> solver.apply(t).get(2) / R / t / sigma * Coefficients.getDCounter("AlCl3").apply(t)).get(), "AlCl3")
                .show();
    }


    public static void analyzeEqSystem2() {
        double R = 8314.4621;
        double sigma = 0.01;

        Experiment.ExperimentSeries experiments = new Experiment.ExperimentSeries(650 + 273, 950 + 273, 3).mapX(x -> 1 / x).mapY(Math::log);

        Function<Double, List<Double>> equationSystem =
//                EquationSystems.createEquationSystem2(Arrays.asList(010., 010., 010., 010., 000.), 1e-2, 10000);   any of these two give the save result
                EquationSystems.createEquationSystem2(Arrays.asList(100., 100., 100., 100., 11000.), 1e-2, 10000);

        Function<Double, List<Double>> solver = new CachingWrapper<>(equationSystem);

        new Graphics("T^-1", "ln V_Ga")
                .addGraphic(experiments.create(t -> solver.apply(t).subList(0, 3).stream().reduce(0., (a, b) -> a + b) * Coefficients.getMolarVolume("Ga") * 1e9).get(), "V_Ga")
                .show();
        new Graphics("T^-1", "ln G")
                .addGraphic(experiments.create(t -> solver.apply(t).get(0) / R / t / sigma * Coefficients.getDCounter("GaCl").apply(t)).get(), "GaCl")
                .addGraphic(experiments.create(t -> solver.apply(t).get(1) / R / t / sigma * Coefficients.getDCounter("GaCl2").apply(t)).get(), "GaCl2")
                .addGraphic(experiments.create(t -> solver.apply(t).get(2) / R / t / sigma * Coefficients.getDCounter("GaCl3").apply(t)).get(), "GaCl3")
                .show();
    }

    public static void analyzeEqSystem3(double h2Portion) {
        double R = 8314.4621;
        double T = 1100 + 273;
        double sigma = 0.01;

        Function<Double, List<Double>> solver = new CachingWrapper<>(EquationSystems.createEquationSystem3(h2Portion, Arrays.asList(50., 50., 50., 50., 100., 0.5), 1e-2, 10000));
        Experiment.ExperimentSeries experiments = new Experiment.ExperimentSeries(0, 1, 0.01);

        Function<Double, Double> countG_AlCl3 = xg -> (30 * xg - solver.apply(xg).get(0)) / R / T / sigma * Coefficients.getDCounter("AlCl3").apply(T);
        Function<Double, Double> countG_GaCl = xg -> (30 * (1 - xg) - solver.apply(xg).get(1)) / R / T / sigma * Coefficients.getDCounter("GaCl").apply(T);

        new Graphics("x^g", "x")
                .addGraphic(experiments.create(xg -> solver.apply(xg).get(5)).get(), "x")
                .show();
        new Graphics("x^g", "V_AlGaN")
                .addGraphic(experiments.create(xg -> (countG_AlCl3.apply(xg) * Coefficients.getMolarVolume("AlN") + countG_GaCl.apply(xg) * Coefficients.getMolarVolume("GaN")) * 1e9).get(), "V_AlGaN")
                .show();
        new Graphics("x^g", "G")
                .addGraphic(experiments.create(countG_AlCl3).get(), "G_AlCl3")
                .addGraphic(experiments.create(countG_GaCl).get(), "G_GaCl")
                .show();
    }

}
