/**
   One simulation: given the parameters, runs and logs data for one simulation.

   Travel time structure: for K lots
   INDEX | MEANING
       0 : Travel time checkpoint to arrivals
       1 : Travel time arrivals to Lot 1
       k : Travel time Lot k - 1 to Lot k
   K + 1 : Travel time Lot K to departures
   K + 2 : Travel time departures to checkpoint
   Note that this is an array of size K + 3; or in other words, 1 + #stops

   Embarkp structure: for K lots
   (these are the queues)
   INDEX | MEANING
       0 : arrivals terminal
       1 : Lot 1
       k : Lot k
       K : Lot K
   K + 1 : Nobody queues at departures or the checkpoint; not used!
   ' stops.size() ' is exactly the number K + 1

   Disembarkp structure: for K lots
   INDEX | MEANING
       0 : Lot 1
       1 : Lot 2
       k : Lot k - 1
   K + 1 : Lot K
   K + 2 : Actually, everyone gets off at departures; not used!

   Stop names: for K lots
   INDEX | MEANING
       0 | Arrivals
       1 | Lot 1
       k | Lot k
       K | Lot K
   K + 1 | Departures
   K + 2 | Checkpoint (not used)
 **/
import java.util.List;
import java.util.ArrayList;

public class Simulation {
	private List<Station> stops;
	private List<Bus> buses;
	private int busCapacity;
	private int fleet;
	private double headway;
	public double cutoff;
	private double[] embarkp;
	private double[] disembarkp;
	private double[] travelTimes;
	private double loadTime;
	public Logger log;
	public double quality;

	private double checkpointTime;

	public double ipaWait;
	public int groupCount;

	private boolean firstBus;

	public Simulation(int busCapacity, double cutoff, double[] embarkp,
	                  double[] disembarkp, double[] travelTimes, double loadTime) {
		this.busCapacity = busCapacity;
		this.cutoff = cutoff;
		this.embarkp = embarkp;
		this.disembarkp = disembarkp;
		this.travelTimes = travelTimes;
		this.loadTime = loadTime;
		quality = 1;
	}

	public void initialize(int fleet, double headway) {
		this.fleet = fleet;
		this.headway = headway;
		checkpointTime = 0;
		buses = new ArrayList<Bus>();
		stops = new ArrayList<Station>();
		for (int i = 0; i < fleet; i++) {
			buses.add(new Bus(busCapacity));
		}
		for (int i = 0; i < embarkp.length; i++) {
			stops.add(new Station());
		}
		log = new Logger();
		ipaWait = 0;
		groupCount = 0;
		firstBus = true;
	}

	public void run() {
		for (int b = 0; b < buses.size(); b++) {
			Bus curBus = buses.get(b);
			// the buses are all "right at" the checkpoint
			double startCycle = curBus.clock;
			checkpoint(b);
			curBus.nextStop(travelTimes[0]);
			// bus now at Arrivals
			boardPassengers(b, 0);
			// bus going through lots
			for (int s = 1; s < stops.size(); s++) {
				Station curStop = stops.get(s);
				curBus.nextStop(travelTimes[s]);
				unloadPassengers(b, s);
				boardPassengers(b, s);
			}
			curBus.nextStop(travelTimes[stops.size()]);
			// bus now at Departures
			unloadPassengers(b, stops.size());

			// bus stops "right at" the checkpoint
			curBus.nextStop(travelTimes[stops.size() + 1]);
			curBus.location = -1;
		}
	}

	// NO loading should happen here! Only holding to satisfy the headway.
	private void checkpoint(int b) {
		if(!firstBus) {
			Bus curBus = buses.get(b);
			curBus.ipaFlag = curBus.clock < checkpointTime + headway;
			curBus.clock = Math.max(curBus.clock, checkpointTime + headway);
			checkpointTime = curBus.clock;
		}
		firstBus = false;
	}

	private double boardPassengers(int b, int s) {
		Bus curBus = buses.get(b);
		Station curStop = stops.get(s);

		if (curBus.clock < curStop.lastBusTime) {
			curBus.clock = curStop.lastBusTime;
			int aheadBusIndex;
			if (b == 0) {
				aheadBusIndex = buses.size() - 1;
			} else {
				aheadBusIndex = b - 1;
			}
			curBus.ipaFlag = buses.get(aheadBusIndex).ipaFlag || curBus.ipaFlag;
		}
		double totalBoardTime = 0;
		double singleTime = curBus.clock - curStop.clock;
		PassengerGroup newRiders = new PassengerGroup(getPoisson(embarkp[s] *
		                                                         singleTime),
		                                              curStop.clock,
		                                              curBus.clock);
		log.registerSpawn(curBus.clock, s, newRiders.count);
		if (newRiders.count != 0) {
			curStop.enqueue(newRiders);
		}
		curStop.clock = curBus.clock;

		PassengerGroup nowBoarding = curStop.dequeue();
		if (nowBoarding == null || curBus.getRoom() == 0) {
			curStop.lastBusTime = curBus.clock;
			if (nowBoarding != null)
				curStop.requeue(nowBoarding);
			return 0;
		}

		// split group logic; use the formula & the linearity assumptions
		if (curBus.getRoom() < nowBoarding.count) {
			double splitTime = nowBoarding.endTime -
			                   curBus.getRoom() * (nowBoarding.endTime
			                                       - nowBoarding.startTime)
			                   / nowBoarding.count;
			curStop.requeue(new PassengerGroup(nowBoarding.count - curBus.getRoom(),
			                                   nowBoarding.startTime,
			                                   splitTime));
			nowBoarding = new PassengerGroup(curBus.getRoom(),
			                                 splitTime,
			                                 nowBoarding.endTime);
		}

		// now the group is perfectly sized to fit into the bus
		totalBoardTime += nowBoarding.count * loadTime;

		double tardyProp;
		if (curBus.clock > nowBoarding.endTime + cutoff) {
			tardyProp = 1;
		} else if (curBus.clock < nowBoarding.startTime + cutoff) {
			tardyProp = 0;
		} else {
			tardyProp = nowBoarding.endTime - (curBus.clock - cutoff);
		}
		tardyProp /= (nowBoarding.endTime - nowBoarding.startTime);
		double ghost = nowBoarding.count * tardyProp;
		ipaWait += (curBus.ipaFlag ? 1 : 0) * ghost;
		groupCount++;

		log.qualityRecord.add(nowBoarding.waitPoll(curBus.clock, cutoff));
		log.registerBoard(curBus.clock, s, b, nowBoarding.count);
		curBus.load(nowBoarding.count, loadTime, s == 0);

		return totalBoardTime + boardPassengers(b, s);
	}

	private void unloadPassengers(int b, int s) {
		Bus curBus = buses.get(b);
		int offCount = 0;
		boolean atDepartures = false;
		if (s == stops.size()) {
			atDepartures = true;
			offCount = curBus.toDepartures;
		} else {
			offCount = getBinomial(curBus.toLots, getOffProb(s));
		}
		log.registerBoard(curBus.clock, s, b, -offCount);
		curBus.unload(offCount, loadTime, atDepartures);
	}

	private double getOffProb(int s) {
		// s is the stop number - 0 for arrivals, 1 for lot 1, etc.
		// disembarkp is an array starting at index 0 but whose probabilities
		// correspond to the disembark probability for lot 1, 2, etc.
		// as a result, the array indices are off by 1 and thus are corrected
		double p = 0;
		for (int i = 1; i <= s; i++) {
			p += disembarkp[i - 1];
		}
		return p;
	}

	private int getPoisson(double lambda) {
		double l = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;
		while (p > l) {
			p *= Math.random();
			k++;
		}
		return (k == 0) ? 0 : k - 1;
	}

	private int getBinomial(int n, double p) {
		int x = 0;
		for (int i = 0; i < n; i++) {
			if (Math.random() < p)
				x++;
		}
		return x;
	}
}
