package nano.algorithm.nn;

import org.apache.commons.math3.analysis.function.Sigmoid;

public abstract class ActivationFunctions {

    public static Neuron.ActivationFunction step() {
        return (input) -> input >= 0 ? 1 : 0;
    }

    public static Neuron.ActivationFunction sgn() {
        return (input) -> input >= 0 ? 1 : -1;
    }

    public static Neuron.ActivationFunction linear() {
        return (input) -> input;
    }

    public static Neuron.ActivationFunction sigmoid() {
        return new Sigmoid(0, 1)::value;
    }

}
