package nano.algorithm.nn;

import org.junit.jupiter.api.Test;

import java.util.List;

import static nano.algorithm.nn.Neuron.floatEquals;
import static nano.algorithm.nn.SimplePerceptron.createData;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimplePerceptronTests {

    @Test
    public void test() {
        var dataList = List.of(
                createData(1, 1, 1),
                createData(-1, 1, 1),
                createData(1, -1, 1),
                createData(1, 1, 1)
        );
        var perceptron = new SimplePerceptron(dataList);
        //
        dataList.forEach(it -> assertTrue(floatEquals(perceptron.apply(it.input()), it.output())));
    }
}
