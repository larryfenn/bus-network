/**
   SpawnEvent class for logging spawn events
   (implement compares? need a way to sort... consult data analysis software)
   All spawning events need to be logged!
 **/

public class SpawnEvent {
	public static double time;
	public static int station;
	public static int count;

	public SpawnEvent(double time, int station, int count) {
		this.time = time;
		this.station = station;
		this.count = count;
	}

	public String toString() {
	  return "" + time + "," + station + "," + count;
	}
}
