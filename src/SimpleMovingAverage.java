

/**
 * Unweighted mean of the last N data points
 *
 * @author Team Gredona
 */
public class SimpleMovingAverage extends Strategy {

    /**
     * for use by Triangular Moving Averages
     */
    protected CircularFIFOBuffer<Float> fastSMABuffer = new CircularFIFOBuffer(FAST_PERIOD);
    protected CircularFIFOBuffer<Float> slowSMABuffer = new CircularFIFOBuffer(FAST_PERIOD);

    public SimpleMovingAverage() {
        type = "Simple Moving Average";
    }

    /**
     * @return
     */
    @Override
    protected float computeSlowMovingAverage() {
        int t = slowDataBuffer.size();
        
        //for the first N = SLOW_PERIOD = 20 data points, we simply take the average
        if (SLOW_PERIOD > t) {
            float sum = 0;
            for (float datapoint : slowDataBuffer) {
                sum += datapoint;
            }
            return (sum / t);
        }

        //When calculating successive values, a new value comes into the sum and and old one drops out, meaning full summation each time isn't necessary
        float slowMovingAverage = currentSlowMovingAverage - oldestSlowDatapoint / SLOW_PERIOD + slowDataBuffer.peekLast() / SLOW_PERIOD;
        slowSMABuffer.add(slowMovingAverage);
        return slowMovingAverage;
    }

    @Override
    protected float computeFastMovingAverage() {
        int t = fastDataBuffer.size();
        
        //for the first N = FAST_PERIOD = 20 data points, we simply take the average
        if (FAST_PERIOD > t) {
            float sum = 0;
            for (float datapoint : fastDataBuffer) {
                sum += datapoint;
            }
            return (sum / t);
        }
        
        //When calculating successive values, a new value comes into the sum and and old one drops out, meaning full summation each time isn't necessary
        float fastMovingAverage = currentFastMovingAverage - oldestFastDatapoint / FAST_PERIOD + fastDataBuffer.peekLast() / FAST_PERIOD;
        slowSMABuffer.add(fastMovingAverage);
        return fastMovingAverage;
    }

    /**
     *
     * @param index of the element
     * @return the float at the index of slowSMABuffer
     */
    public float getSlowBufferValue(int index) {
        return slowSMABuffer.get(index);
    }

    /**
     * @param index of the element
     * @return the float at the index of fastSMABuffer
     */
    public float getFastBufferValue(int index) {
        return fastSMABuffer.get(index);
    }
}
