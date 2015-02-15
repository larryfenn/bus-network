/**
   PassengerGroup is a single group of passengers
 **/
public class PassengerGroup {
	public int count;
	public double startTime;
	public double endTime;

	public PassengerGroup(int count, double startTime, double endTime) {
		this.count = count;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void decrement(int amount) {
		if (amount > count)
			count = 0;
		count -= amount;
	}

	// returns how many people had to wait longer than waitTime
	public int waitPoll(double time, double waitTime) {
		if ((time - startTime) <= waitTime) {
			return 0;
		} else {
			return (int)Math.floor(count * (time - waitTime - startTime)
		                  / (endTime - startTime));
		}
}

	public String toString() {
		return "[" + count + "]"  + ":" + "(" + startTime + ", " + endTime + ")";
	}
}
