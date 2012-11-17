

/**
 *
 * @author Team Gredona
 */
public class SimpleMovingAverage extends Strategy {

    /**
     * for use by Triangular Moving Averages
     */
    protected CircularFIFOBuffer<Double> fastSMABuffer = new CircularFIFOBuffer(FAST_PERIOD);
    protected CircularFIFOBuffer<Double> slowSMABuffer = new CircularFIFOBuffer(FAST_PERIOD);

    public SimpleMovingAverage() {
        type = "Simple Moving Average";
    }

    /**
     * @return
     */
    @Override
    protected double computeSlowMovingAverage() {
        if (SLOW_PERIOD > slowDataBuffer.size()) {
            int t = slowDataBuffer.size();
            double sum = 0;
            for (double datapoint : slowDataBuffer) {
                sum += datapoint;
            }
            return (sum / t);
        }
        double slowMovingAverage = currentSlowMovingAverage - oldestSlowDatapoint / SLOW_PERIOD + slowDataBuffer.peekLast() / SLOW_PERIOD;
        slowSMABuffer.add(slowMovingAverage);
        return slowMovingAverage;
    }

    @Override
    protected double computeFastMovingAverage() {
        if (FAST_PERIOD > fastDataBuffer.size()) {
            int t = fastDataBuffer.size();
            double sum = 0;
            for (double datapoint : fastDataBuffer) {
                sum += datapoint;
            }
            return (sum / t);
        }
        double fastMovingAverage = currentFastMovingAverage - oldestFastDatapoint / FAST_PERIOD + fastDataBuffer.peekLast() / FAST_PERIOD;
        slowSMABuffer.add(fastMovingAverage);
        return fastMovingAverage;
    }

    public double getSlowBufferValue(int index) {
        return slowSMABuffer.get(index);
    }

    public double getFastBufferValue(int index) {
        return fastSMABuffer.get(index);
    }
}
