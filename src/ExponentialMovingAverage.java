/**
 *
 * @author Team Gredona
 */
public class ExponentialMovingAverage extends Strategy {

    public ExponentialMovingAverage() {
        type = "ExponentialMovingAverage";
    }

    @Override
    protected double computeSlowMovingAverage() {
        //int t = slowDataBuffer.size();
        double alpha = 2 / (SLOW_PERIOD + 1);
        if (slowDataBuffer.size() == 1) {
            return slowDataBuffer.peekLast();
        }
        return currentSlowMovingAverage + alpha * (slowDataBuffer.peekLast() - currentSlowMovingAverage);
    }

    @Override
    protected double computeFastMovingAverage() {
        //int t = slowDataBuffer.size();
        double alpha = 2 / (SLOW_PERIOD + 1);
        if (fastDataBuffer.size() == 1) {
            return fastDataBuffer.peekLast();
        }
        return currentFastMovingAverage + alpha * (fastDataBuffer.peekLast() - currentFastMovingAverage);
    }
}
