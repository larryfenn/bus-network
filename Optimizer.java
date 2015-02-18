public class Optimizer {
	private static final int NEWTON_TIMEOUT = 100;
	private Simulation s;
	private DataFilter df;

	public Optimizer(Simulation s) {
		this.s = s;
		df = new DataFilter();
	}

	// This runs the simulation until the change in G(u,b) is below the error
	public void sample(int fleet, double headway, double error) {
		s.initialize(fleet, headway);
		for (int i = 0; i < 10; i++) {
			s.run();
		}
		double prevQuality = 1;
		int totalRiders = df.getTotalBoarded(s.log.boardLog);
		int failures = df.sumInt(s.log.qualityRecord);
		s.quality = failures/(double)totalRiders;
		while (Math.abs(s.quality - prevQuality) > error) {
			prevQuality = s.quality;
			s.run();
			failures = df.sumInt(s.log.qualityRecord);
			totalRiders = df.getTotalBoarded(s.log.boardLog);
			s.quality = failures/(double)totalRiders;
			//System.out.println("Looped." + s.quality + " <- " + prevQuality);
		}
	}

	public void ipaOptimize(int fleetGuess, double headwayGuess) {
		int solutionFleet = 0;
		double solutionHeadway = 0;
		for (int i = fleetGuess; i > 0; i--) {
				double u = headwayGuess;
				for (int n = 0; n < NEWTON_TIMEOUT; n++) {
				System.out.println(u);
				sample(i, u, .005);
				if (s.quality < .1) {
					solutionFleet = i;
					solutionHeadway = u;
					n = NEWTON_TIMEOUT;
				} else {
					u = newtonsMethod(u, s.ipaWait/s.groupCount, s.quality);
				}
			}
		}
	}

	// applies Newton's method to get new values for u
	public double newtonsMethod(double u, double derivative, double quality) {
		return u - (quality - .1) / derivative;
	}
}
