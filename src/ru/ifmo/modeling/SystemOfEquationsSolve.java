package ru.ifmo.modeling;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SystemOfEquationsSolve {
    /**
     * 'functions' are constructing a system of equations as {f_1, ..., f_n}
     * And we solve the system:
     * f_1(...) = 0
     * ...
     * f_n(...) = 0
     */
    private List<Function<List<Double>, Double>> functions;
    /**
     * 'derivatives' contains matrix of partial derivatives of 'functions', as
     * d(f_1)/d(1) d(f_1)/d(2) ... d(f_1)/d(n) -- derivatives.get(0)
     * d(f_2)/d(1) d(f_2)/d(2) ... d(f_2)/d(n) -- derivatives.get(1)
     * ...
     * d(f_n)/d(1) d(f_n)/d(2) ... d(f_n)/d(n) -- derivatives.get(size - 1)
     */
    List<List<Function<List<Double>, Double>>> derivatives;

    public SystemOfEquationsSolve(List<Function<List<Double>, Double>> functions,
                                  List<List<Function<List<Double>, Double>>> derivatives) {
        this.functions = functions;
        this.derivatives = derivatives;
    }

    private double[][] substituteInDerivatives(List<Double> values) {
        double[][] res = new double[derivatives.size()][derivatives.size()];
        for (int i = 0; i < derivatives.size(); ++i) {
            List<Function<List<Double>, Double>> funcs = derivatives.get(i);
            for (int j = 0; j < funcs.size(); ++j) {
                res[i][j] = funcs.get(j).apply(values);
            }
        }

        return res;
    }

    private double[] substituteInFunctions(List<Double> values) {
        double[] res = new double[functions.size()];
        for (int i = 0; i < functions.size(); ++i) {
            res[i] = functions.get(i).apply(values);
        }

        return res;
    }

    private double getDistanceBetweenSolutions(List<Double> first, List<Double> second) {
        double sum = 0.0;
        for (int i = 0; i < first.size(); ++i) {
            sum += Math.pow(first.get(i) - second.get(i), 2);
        }

        return Math.sqrt(sum);
    }

    private double[][] inverse(double A[][]) {
        int n = A.length;
        int row[] = new int[n];
        int col[] = new int[n];
        double temp[] = new double[n];
        int hold, I_pivot, J_pivot;
        double pivot, abs_pivot;

        if (A[0].length != n) {
            // inconsistent array sizes
            return null;
        }
        for (int k = 0; k < n; k++) {
            row[k] = k;
            col[k] = k;
        }
        for (int k = 0; k < n; k++) {
            pivot = A[row[k]][col[k]];
            I_pivot = k;
            J_pivot = k;
            for (int i = k; i < n; i++) {
                for (int j = k; j < n; j++) {
                    abs_pivot = Math.abs(pivot);
                    if (Math.abs(A[row[i]][col[j]]) > abs_pivot) {
                        I_pivot = i;
                        J_pivot = j;
                        pivot = A[row[i]][col[j]];
                    }
                }
            }
            if (Math.abs(pivot) < 1.0E-10) {
                // matrix is singular
                return null;
            }
            hold = row[k];
            row[k] = row[I_pivot];
            row[I_pivot] = hold;
            hold = col[k];
            col[k] = col[J_pivot];
            col[J_pivot] = hold;

            A[row[k]][col[k]] = 1.0 / pivot;
            for (int j = 0; j < n; j++) {
                if (j != k) {
                    A[row[k]][col[j]] = A[row[k]][col[j]] * A[row[k]][col[k]];
                }
            }

            for (int i = 0; i < n; i++) {
                if (k != i) {
                    for (int j = 0; j < n; j++) {
                        if (k != j) {
                            A[row[i]][col[j]] = A[row[i]][col[j]] - A[row[i]][col[k]] *
                                    A[row[k]][col[j]];
                        }
                    }
                    A[row[i]][col[k]] = -A[row[i]][col[k]] * A[row[k]][col[k]];
                }
            }
        }

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                temp[col[i]] = A[row[i]][j];
            }
            for (int i = 0; i < n; i++) {
                A[i][j] = temp[i];
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                temp[row[j]] = A[i][col[j]];
            }
            for (int j = 0; j < n; j++) {
                A[i][j] = temp[j];
            }
        }

        return A;
    }

    /**
     * Newton method for solving a system of non-linear equations set with 'functions'
     *
     * @param initial
     * @param e
     * @param maxIteration
     * @return
     */
    public List<Double> getSolution(List<Double> initial, double e, int maxIteration) {
        List<Double> solutionPrev = new ArrayList<>(initial.size());
        List<Double> solution = new ArrayList<>(initial.size());
        for (int i = 0; i < initial.size(); ++i) {
            solutionPrev.add(initial.get(i));
            solution.add(initial.get(i));
        }

        for (int i = 0; i < maxIteration; ++i) {
            Matrix x = (new Matrix(inverse(substituteInDerivatives(solution))))
                       .times(new Matrix(substituteInFunctions(solution), solution.size()));
            for (int j = 0; j < solution.size(); ++j) {
                solution.set(j, solution.get(j) - x.get(j, 0));
            }

            if (getDistanceBetweenSolutions(solutionPrev, solution) < e) {
                break;
            }

            Collections.copy(solutionPrev, solution);
        }


        return solution;
    }
}











