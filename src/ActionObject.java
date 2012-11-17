public class ActionObject{
	private String type, strategy;
	private int time;
	
	public ActionObject(String actionType, String strategyType, int timeOfAction){
		this.type = actionType;
		this.strategy = strategyType;
		this.time = timeOfAction;
	}
	public String getType(){
		return type;
	}
	public String getStrategy(){
		return strategy;
	}
	public int getTime(){
		return time;
	}
}