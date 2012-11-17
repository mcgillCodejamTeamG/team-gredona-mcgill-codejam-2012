import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
 * */ 



public class ActionThread extends Thread {

    private enum ConnectionState { LISTENING, CONNECTING, CONNECTED, CLOSED }

	
    Queue<ActionObject> q;
    JSONObject json;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    ConnectionState state; 
    
    
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
    }

    
    synchronized private void connectionOpened() throws IOException{
    	//set up input and state
    	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	out = new PrintWriter(socket.getOutputStream()); 
    	state = ConnectionState.CONNECTED;

    }
    
    /**
     * This is called by the run() method when the connection is closed
     * from the other side.  (This is detected when an end-of-stream is
     * encountered on the input stream.)  It posts a message to the
     * transcript and sets the connection state to CLOSED.
     */
    synchronized private void connectionClosedFromOtherSide() {
       if (state == ConnectionState.CONNECTED) {
          state = ConnectionState.CLOSED;
       }
    }
    
    
    synchronized void close() {
	         state = ConnectionState.CLOSED;
	         try {
	            if (socket != null)
	               socket.close();
	           
	         }
	         catch (IOException e) {
	         }
	      }
    
    /**
     * Called from the finally clause of the run() method to clean up
     * after the network connection closes for any reason.
     */
    private void cleanUp() {
       state = ConnectionState.CLOSED;
       if (socket != null && !socket.isClosed()) {
             // Make sure that the socket, if any, is closed.
          try {
             socket.close();
          }
          catch (IOException e) {
          }
       }
    
       socket = null;
       in = null;
       out = null; 
       
       
       String jsonString = this.json.toString(2);
		Writer output = null;
		File file = new File("json.txt");
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

        int time = buy.getTime();
        Strategy strategy = buy.getStrategy();

        
        // Get input************
        char c; 
		int size = 0; 
		char cbuf[] = new char[7]; 
		while (true){
			c = (char)in.read(); 
			if (c == '|' || size >= cbuf.length)
				break; 
			cbuf[size] = c; 
			size++; 
		}
		String message = ""; 
		for (int i = 0; i < size; i++)
			message += cbuf[i]; 
	
		if (message.charAt(0) == 'E'){
			connectionClosedFromOtherSide(); 
		}else{
		
			try {
				price = Float.parseFloat(message); 
			}catch(NumberFormatException e){
				System.out.println("Error string not float"); 
				System.out.println(e); 
			}
			
			int manager = ManagerSchedule.getManager(time, strategy.getTypeInt());

	        JSONObject transaction = new JSONObject();
	        transaction.put("time", time);
	        transaction.put("type", "buy");
	        transaction.put("price", price);
	        transaction.put("manager", "Manager"+manager);
	        transaction.put("strategy", strategy.toString());
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

        int time = sell.getTime();
        Strategy strategy = sell.getStrategy();

   
        // Get input************
        char c; 
		int size = 0; 
		char cbuf[] = new char[7]; 
		while (true){
			c = (char)in.read(); 
			if (c == '|' || size >= cbuf.length)
				break; 
			cbuf[size] = c; 
			size++; 
		}
		String message = ""; 
		for (int i = 0; i < size; i++)
			message += cbuf[i]; 
	
		if (message.charAt(0) == 'E'){
			connectionClosedFromOtherSide(); 
		}else{
			
			try {
				price = Float.parseFloat(message); 
			}catch(NumberFormatException e){
				System.out.println("Error string not float"); 
				System.out.println(e); 
			}
			
			
			int manager = ManagerSchedule.getManager(time, strategy.getTypeInt());

	        JSONObject transaction = new JSONObject();
	        transaction.put("time", time);
	        transaction.put("type", "sell");
	        transaction.put("price", price);
	        transaction.put("manager", "manager"+manager);
	        transaction.put("strategy", strategy.toString());
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
        this.notify(); 
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
    		if (state == ConnectionState.CONNECTING){
    			socket = new Socket(remoteHost, port); 
    		}
    		connectionOpened(); 
    		
    		char cbuf[] = new char[7]; 
    		while (state == ConnectionState.CONNECTED){
    			
    			 while (q.peek() != null) {
    	                ActionObject currentAction = q.remove();
    	                
    	                switch (currentAction.getType()){
    	            
    	                	case "BUY":
    	                		buy(currentAction); 
    	                		break; 
    	                	case "SELL":
    	                		sell(currentAction); 
    	                	case "END":
    	                		cleanUp(); 
    	                	
    	                
    	                }
    	            }	
    			 
    			 this.wait(); 
    		}
    		
    	}catch (Exception e) {    		
    		
	               // An error occurred.  Report it to the user, but not
	               // if the connection has been closed (since the error
	               // might be the expected error that is generated when
	               // a socket is closed).
	            if (state != ConnectionState.CLOSED){
	               System.out.println("\n\n Trading ERROR:  " + e);
	            }
    	}finally{
    		
    		cleanUp(); 
    	}
    }
    

}
