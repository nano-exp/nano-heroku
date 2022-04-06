package nano.algorithm.ms;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static nano.algorithm.ms.TrafficJamNaiveBayesClassifier.Data;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NaiveBayesClassifierTests {

    @Test
    public void test() {
        var dataList = List.of(
                new Data(true, true, true, true),
                new Data(false, true, true, true),
                new Data(true, false, true, true),
                new Data(true, true, false, true),
                new Data(false, true, false, false),
                new Data(true, false, false, false),
                new Data(false, false, false, false),
                new Data(false, false, true, false)
        );
        var classifier = new TrafficJamNaiveBayesClassifier(dataList);
        //
        var testClassify = (Consumer<Data>) (data) -> assertTrue(classifier.classify(data) > classifier.classify(data.negate()));
        testClassify.accept(new Data(true, true, true, true));
        testClassify.accept(new Data(false, false, false, false));
    }
}
