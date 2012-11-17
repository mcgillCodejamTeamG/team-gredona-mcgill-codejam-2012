
import net.sf.json.JSONObject;  // need to include in dependencies
import net.sf.json.JSONSerializer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.Integer;
/**
* Thread which takes action methods sends information to the server and creates JSON
* @author TeamG
**/

public class ActionThread extends Thread{
	Queue<ActionObject> q;
	JSONObject json;
	Socket skt;
	DataOutputStream dOut;
	BufferedReader dIn;
	boolean loop = true;

/**
*	Constructor for the thread, sets up socket connection and Data Streams, also begins JSON
* @author TeamG
**/
	public ActionThread(){
		json = new JSONObject();
		json.put("team","Team G")
		json.put("destination", "mcgillcodejam2012@gmail.com")
		q = new LinkedList<ActionObject>();
		try{
			skt = new Socket("localhost", 1234);
			dOut = new DataOutputStream(skt.getOutputStream());
			dIn = new BufferedReader(new InputStreamReader(skt.getInputStream()));
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

/**
* Sends a buy order to the server, adds to JSON
* @author TeamG
**/
	private buy(ActionObject buy){
		int price = 0;
		dOut.writeChar(66);

		int time = buy.getTime();
		String strategy = buy.getStrategy();

		String input = dIn.readLine();
		try{
			price = java.lang.Integer.parseInt(input);
		}
		catch (NumberFormatException e){
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
	}

/**
* Sends a sell order to the server, adds to JSON
* @author TeamG
**/
	private sell(ActionObject sell){
		int price = 0;
		dOut.writeChar(83);

		int time = sell.getTime();
		String strategy = sell.getStrategy();

		String input = dIn.readLine();
		try{
			price = java.lang.Integer.parseInt(input);
		}
		catch (NumberFormatException e){
			e.printStackTrace(); //The price sent isn't a price, so an error occurred
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
	}

/**
* Ends the socket connection and datastreams, saves the json to a .txt file
* @author TeamG
**/
	private end(){
		loop = false;
		dOut.flush();
		dOut.close();
		dIn.flush();
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
* @author TeamG
**/
	public addAction(String action, String strategy, int time){
		ActionObject action = new ActionObject(action, strategy, time);
		q.add(ActionObject action);
	}
/**
* Sets up the runnable loop which checks whether the Queue is empty, dequeues the next action if there is one
* @author TeamG
**/
	public void run(){
		while(loop){
			if (q.peek()!=null){
				ActionObject currentAction = q.remove();
				if(currentAction.getType() == "BUY"){
					this.buy(currentAction);
				}
				else if(currentAction.getType() == "SELL"){
					this.sell(currentAction);
				}
				else if(currentAction.getType() == "END"){
					this.end()
				}
			}
		}
	}
}