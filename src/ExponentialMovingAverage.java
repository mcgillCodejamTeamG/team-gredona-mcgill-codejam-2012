package codejamstrategies;
/**
 * The Exponential Moving Average is similar to the Linear Weighted Moving Average except it applies exponentially decreasing weighting factors to the data points
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
        
        //Because N and t are not dependent on each other, we can let EMA_1 = price_1
        if (slowDataBuffer.size() == 1) {
            return slowDataBuffer.peekLast();
        }
        
        //Then we use the recursive formula for all t > 1
        return currentSlowMovingAverage + alpha * (slowDataBuffer.peekLast() - currentSlowMovingAverage);
    }

    @Override
    protected double computeFastMovingAverage() {
        //int t = slowDataBuffer.size();
        double alpha = 2 / (SLOW_PERIOD + 1);
        
        //Because N and t are not dependent on each other, we can let EMA_1 = price_1
        if (fastDataBuffer.size() == 1) {
            return fastDataBuffer.peekLast();
        }
        
        //Then we use the recursive formula for all t > 1
        return currentFastMovingAverage + alpha * (fastDataBuffer.peekLast() - currentFastMovingAverage);
    }
}
