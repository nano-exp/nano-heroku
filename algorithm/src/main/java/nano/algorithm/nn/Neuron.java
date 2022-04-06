package nano.algorithm.nn;

import java.util.function.Function;

public class Neuron {

    @FunctionalInterface
    public interface ActivationFunction {

        double apply(double input);
    }

    private double[] weight;
    private double threshold;
    private final ActivationFunction f;

    public Neuron(double[] weight, double threshold, ActivationFunction f) {
        this.weight = weight;
        this.threshold = threshold;
        this.f = f;
    }

    public void updateWeight(Function<double[], double[]> updater) {
        this.weight = updater.apply(this.weight);
    }

    public void updateThreshold(Function<Double, Double> updater) {
        this.threshold = updater.apply(this.threshold);
    }

    public double apply(double[] input) {
        assert input.length == this.weight.length;
        var s = 0;
        for (var i = 0; i < input.length; i++) {
            var w_i = this.weight[i];
            var x_i = input[i];
            s += w_i * x_i;
        }
        return this.f.apply(s - this.threshold);
    }

    public static boolean floatEquals(double a, double b) {
        var t = 0.00001;
        var d = a - b;
        return -t < d && d < t;
    }
}
