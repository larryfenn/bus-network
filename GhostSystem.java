/**
   Stop codes:

   Checkpoint - precedes 0 (-1)
   Begin array.
   Arrivals - stop 0
   Lots - 1, 2, ..., N
   Departures - N + 1
   End of array.

   GhostSystem is the main class.
 **/

public class GhostSystem {

	public static void main(String[] args) {
		double[] embarkp = new double[4];
		embarkp[0] = 4;
		embarkp[1] = 1;
		embarkp[2] = 1;
		embarkp[3] = 1;
		double[] disembarkp = new double[3];
		disembarkp[0] = .5;
		disembarkp[1] = .25;
		disembarkp[2] = .25;
		double[] travelTimes = new double[6];
		travelTimes[0] = 2;
		travelTimes[1] = 4;
		travelTimes[2] = 4;
		travelTimes[3] = 4;
		travelTimes[4] = 4;
		travelTimes[5] = 2;
		int busCapacity = 30;
		double cutoff = 10;
		double loadTime = .2;
		int iterations = 10;

		Simulation s = new Simulation(busCapacity, cutoff, embarkp, disembarkp,
		                              travelTimes, loadTime);

		Optimizer o = new Optimizer(s);
		o.ipaOptimize(10, 10);
	}
}
