/**
   Bus.java stores a local clock, passengers TO, and passengers FROM arrivals.
   Additionally, a location flag is given for logging purposes.
   Additionally an IPA flag is used to determine if it is affected by the
   headway used.
 **/

public class Bus {
	private int capacity;
	public int location;
	public double clock;
	public boolean ipaFlag;
	public int toDepartures;
	public int toLots;

	public Bus(int capacity) {
		this.capacity = capacity;
		location = -1; // starts at the checkpoint
		clock = 0;
		ipaFlag = false;
	  toDepartures = 0;
		toLots = 0;
	}

	public int getRoom() {
		return capacity - (toLots + toDepartures);
	}

	public void delay(double holdTime) {
		clock += holdTime;
	}

	public void nextStop(double travelTime) {
		clock += travelTime;
		location++;
	}

	public void load(int count, double loadTime, boolean atArrivals) {
		clock += count * loadTime;
		if (atArrivals) {
			toLots = count;
		} else {
			toDepartures = count;
		}
	}

	public void unload(int count, double loadTime, boolean atDepartures) {
		clock += count * loadTime;
		if (atDepartures) {
			toDepartures -= count;
		} else {
			toLots -= count;
		}
	}

	public String toString() {
		return "[" + toLots + "+" + toDepartures + "/" + capacity + "]" +
		       "(" + location + ")" + ipaFlag + clock;
	}
}
