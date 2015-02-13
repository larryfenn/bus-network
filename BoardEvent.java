/**
   BoardEvent class for logging boarding/deboarding events
   (implement compares? need a way to sort... consult data analysis software)
   + is loading
   - is unloading
   All loading and unloading events need to be logged!
 **/

public class BoardEvent {
	public static double time;
	public static int station;
	public static int bus;
	public static int popChange;

	public BoardEvent(double time, int station, int bus, int popChange) {
		this.time = time;
		this.station = station;
		this.bus = bus;
		this.popChange = popChange;
	}

	public String toString() {
	  return "" + time + "," + station + "," + bus + "," + popChange;
	}
}
