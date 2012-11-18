

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TableData {

	ArrayList<String[]> ema, sma, tma, lwma; 
	
 
	public TableData(JSONObject json){
		
		// Load Data
		
		JSONArray trans = null; 
		try {
			trans = (JSONArray) json.get("transactions");
		} catch (JSONException e) {
			System.out.println("Error<TableData>: not JSON object "); 
			e.printStackTrace();
			System.exit(1); 
		} 
		
		
		for (int i = 0; i < trans.length(); i++){
			try {
				
				JSONObject obj = trans.getJSONObject(i); 
				
				String price = obj.getString("price"); 
				String time =  "" + obj.getInt("time");
				String type =  obj.getString("type");
				
				String row[] = new String[3]; 
				row[0] = time;
				row[1] = type;
				row[2] = price; 
				
				// add row to proper table 
				String strat = obj.getString("strategy"); 
				if (strat.equals("EMA"))
					ema.add(row); 
				else if (strat.equals("TMA"))
					tma.add(row);
				else if (strat.equals("SMA"))
					sma.add(row);
				else if (strat.equals("LWMA"))
					lwma.add(row);
				else 
					System.out.println("Error: Strategy could not be found"); 
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public String[][] getEMATable(){
		return (String[][]) ema.toArray();  
	}
	
	public String[][] getSMATable(){
		return (String[][]) sma.toArray();  
	}
	public String[][] getTMATable(){
		return (String[][]) tma.toArray();  
	}
	public String[][] getLWMATable(){
		return (String[][]) lwma.toArray();  
	}
	
	 
}
