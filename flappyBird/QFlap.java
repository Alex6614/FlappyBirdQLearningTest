package flappyBird;

import java.util.TreeMap;

public class QFlap {
	
	String prevState;
	// False means no press and True means press
	boolean prevAction;
	String nowState;
	double learningRate = 7.5;
	double discount = 0.5; // used to  be 1
	TreeMap<String, state> map;
	// THINK ABOUT FIRST CASE
	// THINK ABOUT SETTING FIRST CASE TO NULL AFTER GAME OVER
	
	// Initialize QFlap
	public QFlap() {
		map = new TreeMap<String, state>();
		prevState = null;
		prevAction = false;
	}
	
	// Class for each state
	public class state{
		
		int heightToBottomPipe;
		int distanceToNextPipe;
		double pressActionReward;
		double noPressActionReward;
		
		//Initialize the state with height to next bottom pipe
		//and distance to next set of pipe as parameters
		public state(int h,int d){
			heightToBottomPipe = h;
			distanceToNextPipe = d;
			pressActionReward = 0;
			noPressActionReward = 0;
		}
		
		//Change the reward of this state for pressing a button
		public void changeActionReward(double newReward){
			pressActionReward = newReward;
		}
		
		//Change the reward of this state for not pressing a button
		public void changeNoActionReward(double newReward){
			noPressActionReward = newReward;
		}
		
		public double getActionReward(){
			return pressActionReward;
		}
		
		public double getNoActionReward(){
			return noPressActionReward;
		}
		
	}

	
	// Create a unique ID for each state
	String IdMaker(int height, int distance){
		String a;
		String b;
		int heightDigits = String.valueOf(height).length();
		int distanceDigits = String.valueOf(distance).length();
		
		if(heightDigits == 3){
			a = String.valueOf(height);
		} else if (heightDigits == 2){
			a = "0" + String.valueOf(height);
		} else {
			a = "00" + String.valueOf(height);
		}
		
		if(distanceDigits == 3){
			b = String.valueOf(distance);
		} else if (distanceDigits == 2){
			b = "0" + String.valueOf(distance);
		} else {
			b = "00" + String.valueOf(distance);
		}
		
		String c = a + b;
//System.out.println(c);
		return c;
	}

	// Updates the reward for previous state and may initialize new states
	
	public void update(int height, int distance, boolean isDead){
		nowState = IdMaker(height, distance);
		double firstReward;
		double secondReward;
		if(isDead == true){
			firstReward = -1000;
		} else {
			firstReward = 5; // used to be 50
		}
		if(!isDead){
		if(!map.containsKey(nowState)){
			state s = new state(height, distance);
			map.put(nowState, s);
			secondReward = 0;
		} else {
			secondReward = Math.max(map.get(nowState).getActionReward(), 
					map.get(nowState).getNoActionReward());
		}} else {secondReward = 0;}
		if(prevState != null){
		if(prevAction == true){
			double newReward = map.get(prevState).getActionReward() + 
					learningRate * (firstReward + discount * secondReward
							- map.get(prevState).getActionReward());
System.out.println("JUMP" + newReward);
			map.get(prevState).changeActionReward(newReward);
		} else {
			double newReward = map.get(prevState).getNoActionReward() + 
					learningRate * (firstReward + discount * secondReward
							- map.get(prevState).getNoActionReward());
System.out.println("NEIN" + newReward);
			map.get(prevState).changeNoActionReward(newReward);
		}
		}
		
		prevState = nowState;
		if(isDead == true){
			prevState = null;
		}
	}
	
	// Decides whether or not to press jump
	public boolean decide(int height, int distance, boolean isDead){
		if(isDead){
			prevAction = false;
			return false;
		}
		String nowID = IdMaker(height, distance);
		state nowState = map.get(nowID);
		if(nowState.pressActionReward > nowState.noPressActionReward){
			prevAction = true;
			return true;
		} else if(nowState.noPressActionReward > nowState.pressActionReward){
			prevAction = false;
			return false;
		} else {
			prevAction = false;
			return false;
		}
	}
}
