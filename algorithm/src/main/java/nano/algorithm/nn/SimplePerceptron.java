package nano.algorithm.nn;

import java.util.List;

import static nano.algorithm.nn.ActivationFunctions.sgn;
import static nano.algorithm.nn.Neuron.floatEquals;

public class SimplePerceptron {

    private final Neuron neuron = new Neuron(new double[]{0, 0}, 0, sgn());

    public SimplePerceptron(List<Data> dataList) {
        this.train(dataList);
    }

    private void train(List<Data> dataList) {
        for (var it : dataList) {
            var x = it.input();
            var y = it.output();
            var _y = this.neuron.apply(x);
            while (!floatEquals(y, _y)) {
                var d = y - _y;
                this.neuron.updateWeight((weight) -> new double[]{weight[0] + d * x[0], weight[1] + d * x[1]});
                this.neuron.updateThreshold((threshold) -> threshold + d);
                _y = this.neuron.apply(x);
            }
        }
    }

    public double apply(double[] input) {
        return this.neuron.apply(input);
    }

    public static Data createData(double x1, double x2, double y) {
        return new Data(new double[]{x1, x2}, y);
    }

    public record Data(double[] input, double output) {
    }
}
