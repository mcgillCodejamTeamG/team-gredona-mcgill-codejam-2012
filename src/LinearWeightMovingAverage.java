

/**
 * @author Team Gredona
 */
public class LinearWeightMovingAverage extends Strategy {

    public LinearWeightMovingAverage() {
        type = "Linear Weighted Moving Average";
    }

    @Override
    protected double computeSlowMovingAverage() {
        int t = slowDataBuffer.size();
        int sumOfWeightingFactors = (t * (1 + t)) / 2;
        double sumOfWeightedPrices = 0;
        //if (slowDataBuffer.getLimit() > slowDataBuffer.size()) {
        for (int index = 0; index < t; index++) {
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
            sumOfWeightedPrices += fastDataBuffer.get(index) * (index + 1);
        }

        return (sumOfWeightedPrices / sumOfWeightingFactors);
        //}
    }
}
