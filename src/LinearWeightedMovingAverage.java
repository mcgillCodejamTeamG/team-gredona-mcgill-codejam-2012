

/**
 * The Linear Weighted Moving Average uses weighting factors to assign more importance to recent data points.
 * @author Team Gredona
 */
public class LinearWeightedMovingAverage extends Strategy {

    public LinearWeightedMovingAverage() {
        type = "Linear Weighted Moving Average";
    }

    @Override
    protected double computeSlowMovingAverage() {
        int t = slowDataBuffer.size();
        int sumOfWeightingFactors = (t * (1 + t)) / 2;
        double sumOfWeightedPrices = 0;
        //if (slowDataBuffer.getLimit() > slowDataBuffer.size()) {
        for (int index = 0; index < t; index++) {
            
            //For any period N, the current price is multiplied by N, the previous price is multiplied by N-1, and so on
            sumOfWeightedPrices += slowDataBuffer.get(index) * (index + 1); 
        }

        return (sumOfWeightedPrices / sumOfWeightingFactors);
        //}
    }

    @Override
    protected double computeFastMovingAverage() {
        int t = fastDataBuffer.size();
        int sumOfWeightingFactors = (t * (1 + t)) / 2;
        double sumOfWeightedPrices = 0;
        //if (fastDataBuffer.getLimit() > fastDataBuffer.size()) {
        for (int index = 0; index < t; index++) {
            //For any period, the current price is multiplied by N, the previous price is multiplied by N-1, and so on
            sumOfWeightedPrices += fastDataBuffer.get(index) * (index + 1);
        }

        return (sumOfWeightedPrices / sumOfWeightingFactors);
        //}
    }
}
