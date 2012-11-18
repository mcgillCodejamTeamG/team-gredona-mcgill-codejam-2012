package gui;


import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 *  StockGraph
 * 
 *  This class generates the ChartPanel to be displayed
 * 
 *  @author mattvertescher
 */
public class StockGraph {
    
    private XYSeries priceSeries, fastSeries, slowSeries;
    private String name; 
    private int xLength = 100; 
    private int yLength = 50;
    private ChartPanel chartPanel;
    private DataTest dt;

    /**
     * StockGraph Constructor
     *
     * @author mattvertescher
     */
    public StockGraph(String title) {
        name = title;
        
        //Sample Inputs
        priceSeries = new XYSeries("Stock Price");
        priceSeries.add(0.0, 0.0);
        priceSeries.add(100.0, 10.0);
        
        fastSeries = new XYSeries("Fast Series");
        fastSeries.add(0.0, 0.0);
        fastSeries.add(100.0, 10.0);
        
        slowSeries = new XYSeries("Slow Series");
        slowSeries.add(0.0, 0.0);
        slowSeries.add(100.0, 10.0);
        
        updateChart();
    
        dt = new DataTest();
    }
    
    /**
     * ChartPanel getter for the TradingServerFrame
     *
     * @return Chart Panel
     * @author mattvertescher
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
     
    /**
     * Updates all the data points for all the series 
     *
     * @author mattvertescher
     */
    public void updateAllSeries() {
        updateFast();
        updateSlow();
        updateStockPrice();       
        dt.increment++;
    }
    
    /**
     * Updates the ChartPanel
     *
     * @author mattvertescher
     */
    private void updateChart(){
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(priceSeries);
        collection.addSeries(fastSeries);
        collection.addSeries(slowSeries);
        XYDataset xyDataset = collection;

        JFreeChart chart = ChartFactory.createXYLineChart(name, "Time (Seconds)", "Price (USD)",
                xyDataset, PlotOrientation.VERTICAL, true, true, false);
       
        chartPanel = new ChartPanel(chart);
        chartPanel.setVisible(true);
        chartPanel.setSize(xLength,yLength);
    }
    
    /**
     * Updates the fast series
     *
     * @author mattvertescher
     */
    private void updateFast() {
       // float[] xPoints = new float[] {1,2,3,4,5,6,7,8,9};
       // float[] yPoints = new float[] {12,24,36,48,58,68,79,89,99};
        
        float[] xPoints = dt.getTimes();
        float[] yPoints = dt.getFast();
        
        
        updateSeries(fastSeries, xPoints, yPoints);
    }
    
    /**
     * Updates the slow series
     *
     * @author mattvertescher
     */
    private void updateSlow() {
        //float[] xPoints = new float[] {1,2,3,4,5,6,7,8,9};
        //float[] yPoints = new float[] {10,20,30,41,51,61,72,82,92};
        
        float[] xPoints = dt.getTimes();
        float[] yPoints = dt.getSlow();
        
        updateSeries(slowSeries, xPoints, yPoints);
    }
    
    
    /**
     * Updates the stock price series
     *
     * @author mattvertescher
     */
    private void updateStockPrice() {
        //float[] xPoints = new float[] {1,2,3,4,5,6,7,8,9};
        //float[] yPoints = new float[] {10,22,34,46,54,66,76,88,99};
        
        float[] xPoints = dt.getTimes();
        float[] yPoints = dt.getStockPrice();
        
        updateSeries(priceSeries, xPoints, yPoints);
    }
    
    
    /**
     * Loads new data points for a series 
     * 
     * @param series The series to update
     * @param X The new x points 
     * @param Y The new y points 
     *
     * @author mattvertescher
     */
    private void updateSeries(XYSeries series, float[] X, float[] Y) {
        int length = X.length; 
        series.clear();
        for (int i = 0; i < length; i++) 
            series.add(X[i],Y[i]); 
    }

    
}
