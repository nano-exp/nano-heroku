package nano.algorithm.ms;

import java.util.List;
import java.util.function.Predicate;

public class TrafficJamNaiveBayesClassifier {

    private final List<Data> dataList;

    public TrafficJamNaiveBayesClassifier(List<Data> dataList) {
        this.dataList = dataList;
    }

    public double classify(Data data) {
        var dataList = this.dataList;
        //
        double dataSize = dataList.size();
        double trafficJamCount = countData(it -> it.trafficJam() == data.trafficJam());
        double rainingCount = countData(it -> it.raining() == data.raining());
        double holidayCount = countData(it -> it.holiday() == data.holiday());
        double epidemicCount = countData(it -> it.epidemic() == data.epidemic());
        double rainingTrafficJamCount = countData(it -> it.raining() == data.raining() && it.trafficJam() == data.trafficJam());
        double holidayTrafficJamCount = countData(it -> it.holiday() == data.holiday() && it.trafficJam() == data.trafficJam());
        double epidemicTrafficJamCount = countData(it -> it.epidemic() == data.epidemic() && it.trafficJam() == data.trafficJam());
        //
        return ((rainingTrafficJamCount / trafficJamCount) * (holidayTrafficJamCount / trafficJamCount) * (epidemicTrafficJamCount / trafficJamCount))
                * (trafficJamCount / dataSize)
                / ((rainingCount / dataSize) * (holidayCount / dataSize) * (epidemicCount / dataSize));
    }

    private double countData(Predicate<Data> predicate) {
        return this.dataList.stream().filter(predicate).count();
    }

    public record Data(boolean raining, boolean holiday, boolean epidemic, boolean trafficJam) {

        public Data negate() {
            return new Data(this.raining, this.holiday, this.epidemic, !this.trafficJam);
        }
    }
}
