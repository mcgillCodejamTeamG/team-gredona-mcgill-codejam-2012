

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client {

	private static int defaultPort = 3000; 
	private static final int NUM_STRATEGIES = 4; 
	
	  /**
	    * Possible states of the thread that handles the network connection.
	    */
	private enum ConnectionState { LISTENING, CONNECTING, CONNECTED, CLOSED }
	
	private int port;
	private InetAddress host; 
	
	private PriceFeed priceFeed;
	
	private int time; 
	private Strategy[] strategies; 
	
	public Client(){
	
		port = defaultPort; 
		try {
			host = InetAddress.getByName(null);
		} catch (UnknownHostException e) {
			System.exit(1); 
			e.printStackTrace();
		}
		
		strategies = new Strategy[NUM_STRATEGIES]; 
	}
	
	
	
	public Client(InetAddress host, int port){
		
		this.host = host;
		this.port = port; 
		
		strategies = new Strategy[NUM_STRATEGIES];

	}
	
	
	public void connect(){
		if (priceFeed != null)
			priceFeed.close(); 
		priceFeed = new PriceFeed(host, port); 
	}
	 
	public void addStrategy(Strategy s){
		
		for (int i = 0 ;  i < NUM_STRATEGIES; i++)
			if (strategies[i] == null){
				strategies[i] = s;
				break; 
			}		
	}
	
	public void postMessage(String s){
		System.out.println(s); 
	}
	
	public void send(String c){
		priceFeed.send(c); 
	}
	
	private class PriceFeed extends Thread{
		
		private volatile ConnectionState state;
		private InetAddress remoteHost;
	    private int port;
	    private Socket socket;
	    private BufferedReader in;
	    private PrintWriter out;

	    
	      
	      /**
	       * Open a connection to specified computer and port.  The constructor
	       * does not perform any network operations; it just sets some
	       * instance variables and starts the thread.
	       */
	    PriceFeed(InetAddress host, int port) {
	         state = ConnectionState.CONNECTING;
	         this.remoteHost = host;
	         this.port = port;
	         postMessage("\nCONNECTING TO " + host + " ON PORT " + port + "\n");
	         start();
	      }
	      
	    synchronized private void connectionOpened() throws IOException{
	    	//set up input and state
	    	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	out = new PrintWriter(socket.getOutputStream()); 
	    	state = ConnectionState.CONNECTED;
	    	postMessage("Connected"); 
	    }
	    
	    /**
	       * This is called by the run() method when the connection is closed
	       * from the other side.  (This is detected when an end-of-stream is
	       * encountered on the input stream.)  It posts a message to the
	       * transcript and sets the connection state to CLOSED.
	       */
	      synchronized private void connectionClosedFromOtherSide() {
	         if (state == ConnectionState.CONNECTED) {
	            postMessage("\nCONNECTION CLOSED FROM OTHER SIDE\n");
	            state = ConnectionState.CLOSED;
	         }
	      }
	      
	      /**
	       * Handles forwarding price to each strategy to calculate running averages to
	       * determine if it is necessary to buy or sell. 
	       * @param price the price last read in from price feed sent over by exchange
	       */
	      synchronized private void received(double price){
	    	  if (state == ConnectionState.CONNECTED){
	    		  
	    		  for (Strategy s: strategies){
	    			  
	    			  int result = (int)s.update(price); 
	    			  
	    			  // TODO add trade/sell event to trade booking queue
	    			  switch (result){
	    			  		case Strategy.SELL: 
	    			  			postMessage("Sell"); 
	    			  			break;
	    			  		case Strategy.BUY:
	    			  			postMessage("Buy"); 
	    			  			break; 
	    			  		default:
	    			  }
	    		  }
	    	  }
	    		   
	      }
	      
	      synchronized private void send(String c){
	    	  
	    	  if(state == ConnectionState.CONNECTED){

	    		  postMessage("Sent: "+c); 
	    		  out.println(c);
		          out.flush();
		          if (out.checkError()) {
		             postMessage("\nERROR OCCURRED WHILE TRYING TO SEND DATA.");
		             close();
		           }
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
	         postMessage("\n*** CONNECTION CLOSED ***\n");
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
	      }
	      
	    public void run(){

	    	try {
	    		if (state == ConnectionState.CONNECTING){
	    			socket = new Socket(remoteHost, port); 
	    		}
	    		connectionOpened(); 
	    		
	    		char cbuf[] = new char[7]; 
	    		while (state == ConnectionState.CONNECTED){
	    			
	    			// increment time
	    			time++; 
	    			char c; 
	    			int size = 0; 
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
	    			
	    			if (message.charAt(0) == 'C')
	    				connectionClosedFromOtherSide(); 
	    			else{
	    				//postMessage(""+ time +": " + message); 
	    	
	    				try {
	    					received(Double.parseDouble(message)); 
	    				}catch(NumberFormatException e){
	    					System.out.println("Error string not double"); 
	    					System.out.println(e); 
	    				}
	    				
	    			}
	    			    				 
	    		}
	    	}catch (Exception e) {
		               // An error occurred.  Report it to the user, but not
		               // if the connection has been closed (since the error
		               // might be the expected error that is generated when
		               // a socket is closed).
		            if (state != ConnectionState.CLOSED)
		               postMessage("\n\n ERROR:  " + e);
	    	}finally{
	    		cleanUp(); 
	    	}
	    }
	}
	
	
	
}
