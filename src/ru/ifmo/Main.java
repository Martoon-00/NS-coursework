package ru.ifmo;

import ru.ifmo.graphics.Graphics;
import ru.ifmo.lang.CachingWrapper;
import ru.ifmo.lang.Experiment;
import ru.ifmo.modeling.EquationSystems;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        analyzeEqSystem3();
    }

    public static void analyzeEqSystem3() {
        double R = 8314.4621;
        double T = 1100 + 273;
        double sigma = 0.01;

        Function<Double, List<Double>> solver = new CachingWrapper<>(EquationSystems.createEquationSystem3(0., Arrays.asList(1e5, 1e5, 1e5, 1e5, 1e5, 0.2), 1e-3, 1000));

        Graphics graphics = new Graphics("x^g", "");
        graphics.addGraphic(new Experiment(xg -> (30 * xg - solver.apply(xg).get(0)) / R / T / sigma, 0, 0.8, 0.2).get(), "G_AlCl3");
//        graphics.addGraphic(new Experiment(xg -> (30 * (1 - xg) - solver.apply(xg).get(1)) / R / T / sigma, 0, 1, 0.2).get(), "G_GaCl");
        graphics.show();
    }

}
