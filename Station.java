/**
   Station stores a local clock, the last seen bus's departure, and a queue
   of PassengerGroup objects.
 **/

import java.util.*;

public class Station {
	public double clock;
	public double lastBusTime;
	private LinkedList<PassengerGroup> queue;

	public Station() {
		clock = 0;
		lastBusTime = 0;
		queue = new LinkedList<PassengerGroup>();
	}

	// ignore groups of size 0
	public void enqueue(PassengerGroup pg) {
		if (pg.count != 0)
			queue.offer(pg);
	}

	// returns null if queue empty
	public PassengerGroup dequeue() {
		return queue.pollFirst();
	}

	public void requeue(PassengerGroup pg) {
		if (pg.count != 0)
			queue.addFirst(pg);
	}

	public int getTotalPop() {
		int total = 0;
		for (PassengerGroup pg : queue) {
			total += pg.count;
		}
		return total;
	}

	public int getGroups() {
		return queue.size();
	}

	public String toString() {
		return queue.toString();
	}

	public int tallyFailures(double time, double threshold) {
		int total = 0;
		for (PassengerGroup pg : queue) {
			// Ramp function ___/``` failures vs. time
			if (time > pg.endTime + threshold) {
				total += pg.count;
			} else if (time > pg.startTime + threshold) {
				total += Math.floor((time - pg.startTime - threshold) * pg.count
				                    / (pg.endTime - pg.startTime));
			}
		}
		return total;
	}
}
