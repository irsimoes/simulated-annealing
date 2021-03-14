import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		DistanceMatrix d= new DistanceMatrix("distancias.txt");
		DistanceMatrix doutput= new DistanceMatrix(d, input);
		ArrayList<String> solution = doutput.simulatedAnnealing();
		System.out.println(doutput.getInitials(solution));
		System.out.println(doutput.totalDistance(solution));
	}

}
