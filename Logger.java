import java.util.List;
import java.util.ArrayList;

public class Logger {
	public List<BoardEvent> boardLog;
	public List<SpawnEvent> spawnLog;
	public List<Integer> groupTracker;
	public List<Double> waitRecord;
	public List<Integer> qualityRecord;

	public List<Double> cycleRecord;

	public double time;

	public Logger() {
	  boardLog = new ArrayList<BoardEvent>();
		spawnLog = new ArrayList<SpawnEvent>();

		groupTracker = new ArrayList<Integer>();
		waitRecord = new ArrayList<Double>();
		qualityRecord = new ArrayList<Integer>();
		cycleRecord = new ArrayList<Double>();
		time = 0;
	}

	public void registerBoard(double time, int station, int bus, int popChange) {
		boardLog.add(new BoardEvent(time, station, bus, popChange));
	}

	public void registerSpawn(double time, int station, int count) {
		spawnLog.add(new SpawnEvent(time, station, count));
	}

	public String toString() {
		return boardLog.toString();
	}
}
