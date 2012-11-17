/**
 *
 * @author Team Gredona
 *
 */
public abstract class Strategy {

    public static final int BUY = 1;
    public static final int HOLD = 0;
    public static final int SELL = -1;
    public static final int FAST_PERIOD = 5;
    public static final int SLOW_PERIOD = 20;
    /**
     * The type of strategy
     */
    String type;
    /**
     * Instantiates a "first in first out" buffer of size SLOW_PERIOD = 20 or
     * FAST_PERIOD = 5 that contains doubles. When the buffer is full (ie size()
     * = 20 or 5, respectively), the head is removed to create space
     */
    protected CircularFIFOBuffer<Double> slowDataBuffer = new CircularFIFOBuffer(SLOW_PERIOD);
    protected CircularFIFOBuffer<Double> fastDataBuffer = new CircularFIFOBuffer(FAST_PERIOD);
    protected double currentFastMovingAverage, currentSlowMovingAverage;
    protected double previousFastMovingAverage, previousSlowMovingAverage;
    protected double oldestFastDatapoint, oldestSlowDatapoint;

    /**
     * Constructor
     */
    public Strategy() {
    }

    /**
     * Updates the strategy with a new data point.
     *
     * @return the action recommended by the strategy ( BUY, HOLD, or SELL )
     */
    public int update(double newDataPoint) {
        updateSlowDataBuffer(newDataPoint);
        updateFastDataBuffer(newDataPoint);
        previousSlowMovingAverage = currentSlowMovingAverage;
        currentSlowMovingAverage = computeSlowMovingAverage();
        previousFastMovingAverage = currentFastMovingAverage;
        currentFastMovingAverage = computeFastMovingAverage();
        return decideTradingAction();
    }

    /**
     * @param newDataPoint
     */
    protected void updateFastDataBuffer(double newDataPoint) {
        oldestFastDatapoint = fastDataBuffer.peek();
        fastDataBuffer.add(newDataPoint);
    }

    /**
     * @param newDataPoint
     */
    protected void updateSlowDataBuffer(double newDataPoint) {
        oldestSlowDatapoint = slowDataBuffer.peek();
        fastDataBuffer.add(newDataPoint);
    }

    /**
     *
     * @return the average over the last 20
     */
    protected abstract double computeSlowMovingAverage();

    /**
     * @return the average over the last 5
     */
    protected abstract double computeFastMovingAverage();

    /**
     * Detects crossover.  If there is a crossover, this method decides whether or not to buy or sell.  Otherwise it holds.
     * @return the strategy's recommended course of action
     */
    protected int decideTradingAction() {
        if (currentFastMovingAverage == currentSlowMovingAverage) {
            if (currentFastMovingAverage > previousFastMovingAverage) {
                return BUY;
            }
            if (currentFastMovingAverage < previousFastMovingAverage) {
                return SELL;
            }
        }

        if (previousFastMovingAverage > previousSlowMovingAverage && currentFastMovingAverage < currentSlowMovingAverage) {
            return BUY;
        }
        
        if (previousFastMovingAverage < previousSlowMovingAverage && currentFastMovingAverage > currentSlowMovingAverage) {
            return SELL;
          
        }
        return HOLD;
    }

    /**
     * @return the name or type of strategy
     */
    @Override
    public String toString() {
        return type;
    }
}
