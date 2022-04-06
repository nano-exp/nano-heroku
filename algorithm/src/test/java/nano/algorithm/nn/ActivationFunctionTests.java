package nano.algorithm.nn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static nano.algorithm.nn.ActivationFunctions.sigmoid;

public class ActivationFunctionTests {

    @Test
    public void testSigmoid() {
        var sigmoid = sigmoid();
        Assertions.assertEquals(1.0, sigmoid.apply(1000));
        Assertions.assertEquals(0.0, sigmoid.apply(-1000));
        Assertions.assertEquals(0.5, sigmoid.apply(0));
    }
}
