import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Queue;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Thread which takes action methods sends information to the server and creates
 * JSON
 *
 * @author Team Gredona
 *
 *
 */
public class ActionThread extends Thread {
    
    private enum ConnectionState {
        
        LISTENING, CONNECTING, CONNECTED, CLOSED
    }
    private Queue<ActionObject> q;
    private JSONObject json;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ConnectionState state;
    private int port;
    private InetAddress host;

    /**
     * Constructor for the thread, sets up socket connection and Data Streams,
     * also begins JSON
     *
     * @author TeamG
     *
     */
    public ActionThread(InetAddress host, int port) {
        
        this.host = host;
        this.port = port;
        
        json = new JSONObject();
        q = new LinkedList<ActionObject>();
        
        state = ConnectionState.CONNECTING;
        this.start();
    }
    
    synchronized private void connectionOpened() throws IOException {
        //set up input and state
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        
        System.out.println("Trading Connected");
        
        state = ConnectionState.CONNECTED;
        
    }

    /**
     * This is called by the run() method when the connection is closed from the
     * other side. (This is detected when an end-of-stream is encountered on the
     * input stream.) It posts a message to the transcript and sets the
     * connection state to CLOSED.
     */
    synchronized private void connectionClosedFromOtherSide() {
        if (state == ConnectionState.CONNECTED) {
            state = ConnectionState.CLOSED;
        }
    }
    
    synchronized void close() {
        state = ConnectionState.CLOSED;
        try {
            if (socket != null) {
                socket.close();
            }
            
        } catch (IOException e) {
        }
    }

    /**
     * Called from the finally clause of the run() method to clean up after the
     * network connection closes for any reason.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void cleanUp() throws JSONException, IOException {
        state = ConnectionState.CLOSED;
        if (socket != null && !socket.isClosed()) {
            // Make sure that the socket, if any, is closed.
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        
        socket = null;
        in = null;
        out = null;
        
        json.put("team", "Team G");
        json.put("destination", "mcgillcodejam2012@gmail.com");
        
        ESignWrapper.setJSON(json);
        String jsonString = this.json.toString(2);
        Writer output = null;
        File file = new File("codejam.json");
        output = new BufferedWriter(new FileWriter(file));
        output.write(jsonString);
        output.close();
    }

    /**
     * Sends a buy order to the server, adds to JSON
     *
     * @author TeamG
     *
     */
    synchronized private void buy(ActionObject buy) throws IOException, JSONException {
        
        float price = 0;
        out.println('B');
        out.flush();
        if (out.checkError()) {
            System.out.println("\nTrading Error: OCCURRED WHILE TRYING TO SEND DATA.");
            close();
        }
        
        int time = buy.getTime();
        Strategy strategy = buy.getStrategy();


        // Get input************
        char c;
        int size = 0;
        char cbuf[] = new char[7];
        int decimal = 0;
        boolean decimalFlag = false;
        while (decimal < 3) {
            c = (char) in.read();
            
            cbuf[size] = c;
            size++;
            
            if (decimalFlag) {
                decimal++;
            }
            
            if (c == '.') {
                decimalFlag = true;
            }
            
            if (c == 'E') {
                break;
            }
        }
        
        String message = "";
        for (int i = 0; i < size; i++) {
            message += cbuf[i];
        }
        
        
        if (message.charAt(0) == 'E') {
            System.out.println("Received Trading E");
        } else {
            
            
            int manager = ManagerSchedule.getManager(time, strategy.getTypeInt());
            
            JSONObject transaction = new JSONObject();
            transaction.put("time", time);
            transaction.put("type", "buy");
            transaction.put("price", message);
            transaction.put("manager", "manager " + manager);
            transaction.put("strategy", strategy.getAcronym());
            json.accumulate("transactions", transaction);
        }
        
    }

    /**
     * Sends a sell order to the server, adds to JSON
     *
     * @author TeamG
     *
     */
    synchronized private void sell(ActionObject sell) throws IOException, JSONException {
        float price = 0;
        out.println('S');
        out.flush();
        if (out.checkError()) {
            System.out.println("\nTrading Error: OCCURRED WHILE TRYING TO SEND DATA.");
            close();
        }
        
        int time = sell.getTime();
        Strategy strategy = sell.getStrategy();


        // Get input************
        char c;
        int size = 0;
        char cbuf[] = new char[7];
        int decimal = 0;
        boolean decimalFlag = false;
        while (decimal < 3) {
            c = (char) in.read();
            
            cbuf[size] = c;
            size++;
            
            if (decimalFlag) {
                decimal++;
            }
            
            if (c == '.') {
                decimalFlag = true;
            }
            
            if (c == 'E') {
                break;
            }
        }
        String message = "";
        for (int i = 0; i < size; i++) {
            message += cbuf[i];
        }
        
        if (message.charAt(0) == 'E') {
            System.out.println("Received Trading E");
        } else {
            
            
            int manager = ManagerSchedule.getManager(time, strategy.getTypeInt());
            
            JSONObject transaction = new JSONObject();
            transaction.put("time", time);
            transaction.put("type", "sell");
            transaction.put("price", message);
            transaction.put("manager", "manager " + manager);
            transaction.put("strategy", strategy.getAcronym());
            json.accumulate("transactions", transaction);
            
        }
    }

    /**
     * Adds an action to the running Queue of actions
     *
     * @author TeamG
     *
     */
    public ActionObject addAction(String action, Strategy strategy, int time) {
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
        
        try {
            if (state == ConnectionState.CONNECTING) {
                socket = new Socket(host, port);
            }
            connectionOpened();
            
            while (state == ConnectionState.CONNECTED) {
                
                ActionObject currentAction = null;
                while (q.peek() != null) {
                    currentAction = q.remove();
                    if (currentAction.getType().equals("BUY")) {
                        buy(currentAction);
                    } else if (currentAction.getType().equals("SELL")) {
                        sell(currentAction);
                    } else if (currentAction.getType().equals("END")) {
                        cleanUp();
                    }
                }
                
                
            }   //end while connected

        } catch (Exception e) {

            // An error occurred.  Report it to the user, but not
            // if the connection has been closed (since the error
            // might be the expected error that is generated when
            // a socket is closed).
            if (state != ConnectionState.CLOSED) {
                System.out.println("\n\n Trading ERROR:  " + e);
            }
        } finally {
            
            try {
                cleanUp();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
