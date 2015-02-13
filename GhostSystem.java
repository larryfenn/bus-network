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
		embarkp[0] = .75;
		embarkp[1] = .25;
		embarkp[2] = .25;
		embarkp[3] = .25;
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
		Simulation s = new Simulation(30, 8, 3, embarkp, disembarkp, travelTimes, .5);
		System.out.println(s.log.toString());
		s.run();
		System.out.println(s.log.toString());

	}
}
