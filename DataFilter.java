import java.util.List;

/**
   DataFilter is the data processing class for the Logger data set
**/
public class DataFilter {

	public DataFilter() {
	}

	public int sumInt(List<Integer> l) {
		int total = 0;
		for (Integer i : l) {
			total += i;
		}
		return total;
	}

	public double getQualityBoarded(List<BoardEvent> l) {
		return 0;
	}

	public int getTotalBoarded(List<BoardEvent> l) {
		int total = 0;
		for (BoardEvent be : l) {
			if (be.popChange >= 0) {
				total += be.popChange;
			}
		}
		return total;
	}

	public int getTotalSpawned(List<SpawnEvent> l) {
		int total = 0;
		for (SpawnEvent se : l) {
			total += se.count;
		}
		return total;
	}
}
