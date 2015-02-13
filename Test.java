import java.util.*;

public class Test {

	public static void main(String[] args) {
		PassengerGroup pg = new PassengerGroup(10, 1, 4.5);
		System.out.println(pg.waitPoll(13, 10));
	}
}
