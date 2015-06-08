package ru.ifmo;

import ru.ifmo.graphics.Graphics;
import ru.ifmo.lang.CachingWrapper;
import ru.ifmo.modeling.Coefficients;
import ru.ifmo.modeling.EquationSystems;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static ru.ifmo.lang.Experiment.ExperimentSeries;

public class Main {
    public static void main(String[] args) {
        analyzeEqSystem3(0.1);
    }

    public static void analyzeEqSystem1() {
        double R = 8314.4621;
        double sigma = 0.01;

        ExperimentSeries experiments = new ExperimentSeries(350 + 273, 650 + 273, 10).mapX(x -> 1 / x).mapY(y -> Math.log(-y));
        Function<Double, List<Double>> solver = new CachingWrapper<>(EquationSystems.createEquationSystem1(Arrays.asList(50., 50., 50., 50., 500.), 1e-5, 10000));

        BiFunction<Integer, Double, Double> countG = (index, t) -> -solver.apply(t).get(index) / R / t / sigma * Coefficients.getDCounter("AlCl" + (index == 0 ? "" : (index + 1) + "")).apply(t);

        new Graphics("T^-1", "ln V_Al")
                .addGraphic(experiments.create(t -> Arrays.asList(0, 1, 2).stream().map(index -> countG.apply(index, t)).reduce(0., (a, b) -> a + b) * Coefficients.getMolarVolume("Al") * 1e9).get(), "V_Al")
                .show();
        new Graphics("T^-1", "ln G")
                .addGraphic(experiments.create(t -> countG.apply(0, t)).get(), "AlCl")
                .addGraphic(experiments.create(t -> countG.apply(1, t)).get(), "AlCl2")
                .addGraphic(experiments.create(t -> countG.apply(2, t)).get(), "AlCl3")
                .show();
    }


    public static void analyzeEqSystem2() {
        double R = 8314.4621;
        double sigma = 0.01;

        ExperimentSeries experiments = new ExperimentSeries(650 + 273, 950 + 273, 10).mapX(x -> 1 / x).mapY(y -> Math.log(-y));
        Function<Double, List<Double>> solver = new CachingWrapper<>(EquationSystems.createEquationSystem2(Arrays.asList(1.0e4, 15., 100., 15000., 10.), 1e-5, 10000));

        BiFunction<Integer, Double, Double> countG = (index, t) -> -solver.apply(t).get(index) / R / t / sigma * Coefficients.getDCounter("GaCl" + (index == 0 ? "" : (index + 1) + "")).apply(t);

        new Graphics("T^-1", "ln V_Ga")
                .addGraphic(experiments.create(t -> Arrays.asList(0, 1, 2).stream().map(index -> countG.apply(index, t)).reduce(0., (a, b) -> a + b) * Coefficients.getMolarVolume("Ga") * 1e9).get(), "V_Ga")
                .show();
        new Graphics("T^-1", "ln G")
                .addGraphic(experiments.create(t -> countG.apply(0, t)).get(), "GaCl")
                .addGraphic(experiments.create(t -> countG.apply(1, t)).get(), "GaCl2")
                .addGraphic(experiments.create(t -> countG.apply(2, t)).get(), "GaCl3")
                .show();
    }

    public static void analyzeEqSystem3(double h2Portion) {
        double R = 8314.4621;
        double T = 1100 + 273;
        double sigma = 0.01;

        Function<Double, List<Double>> solver = new CachingWrapper<>(EquationSystems.createEquationSystem3(h2Portion, Arrays.asList(50., 50., 50., 50., 100., 0.5), 1e-5, 10000));
        ExperimentSeries experiments = new ExperimentSeries(0, 1, 0.025);

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
