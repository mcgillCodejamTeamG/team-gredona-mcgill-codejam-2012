import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Thread which takes action methods sends information to the server and creates
 * JSON
 *
 * @author TeamG
*
 */
public class ActionThread extends Thread {

    Queue<ActionObject> q;
    JSONObject json;
    Socket skt;
    DataOutputStream dOut;
    BufferedReader dIn;
    boolean loop = true;

    /**
     * Constructor for the thread, sets up socket connection and Data Streams,
     * also begins JSON
     *
     * @author TeamG
*
     */
    public ActionThread() {
        json = new JSONObject();
        try {
            json.put("team", "Team G");
            json.put("destination", "mcgillcodejam2012@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        q = new LinkedList<ActionObject>();
        try {
            skt = new Socket("localhost", 1234);
            dOut = new DataOutputStream(skt.getOutputStream());
            dIn = new BufferedReader(new InputStreamReader(skt.getInputStream()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a buy order to the server, adds to JSON
     *
     * @author TeamG
*
     */
    private ActionObject buy(ActionObject buy) throws IOException, JSONException {
        float price = 0;
        dOut.writeChar(66);

        int time = buy.getTime();
        String strategy = buy.getStrategy();

        String input = dIn.readLine();
        input = input.replace("|", "");
        try {
            price = java.lang.Float.parseFloat(input);
        } catch (NumberFormatException e) {
            e.printStackTrace(); //The price sent isn't a price, so an error occurred
        }

        String manager;
        manager = managerLookup(); // need to get method

        JSONObject transaction = new JSONObject();
        transaction.put("time", time);
        transaction.put("type", "buy");
        transaction.put("price", price);
        transaction.put("manager", manager);
        transaction.put("strategy", strategy);
        json.accumulate("transactions", transaction);
        return buy;
    }

    /**
     * Sends a sell order to the server, adds to JSON
     *
     * @author TeamG
*
     */
    private ActionObject sell(ActionObject sell) throws IOException, JSONException {
        float price = 0;
        dOut.writeChar(83);

        int time = sell.getTime();
        String strategy = sell.getStrategy();

        String input = dIn.readLine();
        input = input.replace("|", "");
        try {
            price = java.lang.Float.parseFloat(input);
        } catch (NumberFormatException e) {
        }

        String manager;
        manager = managerLookup(); // need to get method

        JSONObject transaction = new JSONObject();
        transaction.put("time", time);
        transaction.put("type", "sell");
        transaction.put("price", price);
        transaction.put("manager", manager);
        transaction.put("strategy", strategy);
        json.accumulate("transactions", transaction);
        return sell;
    }

    /**
     * Ends the socket connection and datastreams, saves the json to a .txt file
     *
     * @author TeamG
*
     */
    private void end() throws IOException, JSONException {
        loop = false;
        dOut.flush();
        dOut.close();
        dIn.close();
        skt.close();

        String jsonString = this.json.toString(2);
        Writer output = null;
        File file = new File("json.txt");
        output = new BufferedWriter(new FileWriter(file));
        output.write(jsonString);
        output.close();
    }

    /**
     * Adds an action to the running Queue of actions
     *
     * @author TeamG
*
     */
    public ActionObject addAction(String action, String strategy, int time) {
        ActionObject newaction = new ActionObject(action, strategy, time);
        q.add(newaction);
        return newaction;
    }

    /**
     * Sets up the runnable loop which checks whether the Queue is empty,
     * dequeues the next action if there is one
     *
     * @author TeamG
*
     */
    public void run() {
        while (loop) {
            if (q.peek() != null) {
                ActionObject currentAction = q.remove();
                if ("BUY".equals(currentAction.getType())) {
                    try {
                        this.buy(currentAction);
                    } catch (IOException ex) {
                        Logger.getLogger(ActionThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                        Logger.getLogger(ActionThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if ("SELL".equals(currentAction.getType())) {
                    try {
                        this.sell(currentAction);
                    } catch (IOException ex) {
                        Logger.getLogger(ActionThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                        Logger.getLogger(ActionThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if ("END".equals(currentAction.getType())) {
                    try {
                        this.end();
                    } catch (IOException ex) {
                        Logger.getLogger(ActionThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                        Logger.getLogger(ActionThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}