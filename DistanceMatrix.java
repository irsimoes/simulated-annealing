import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/* A distance matrix with an ArrayList of all cities and an ArrayList of ArrayLists of Integers
 * representing the lines/rows of distances as shown in the file.
 */

public class DistanceMatrix {

	private ArrayList<String> cities;
	private ArrayList<ArrayList<Integer>> distances;
	private int n_iter;
	private int delta;

	/*
	 * A constructor that creates a new distance matrix given a file in the
	 * indicated format.
	 */
	public DistanceMatrix(String fileName) {
		cities = new ArrayList<>();
		distances = new ArrayList<>();
		n_iter = 10;
		Scanner sc = null;
		try {
			sc = new Scanner(new BufferedReader(new FileReader(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String[] line = sc.nextLine().trim().split("\\s+");
		cities.add(line[1]);
		while (sc.hasNextLine()) {
			ArrayList<Integer> list = new ArrayList<>();
			line = sc.nextLine().trim().split("\\s+");
			for (int i = 1; i < line.length; i++) {
				if (i == line.length - 1) {
					cities.add(line[i]);
				} else {
					list.add(Integer.parseInt(line[i]));
				}
			}
			distances.add(list);
		}
	}

	/*
	 * A constructor that creates a distance matrix given another and a list of
	 * cities occurring in it. Assumes that the list of cities follows the order in
	 * the original matrix.
	 */
	public DistanceMatrix(DistanceMatrix m, ArrayList<String> cityList) {
		cities = cityList;
		distances = new ArrayList<>();
		n_iter = 10;
		for (int i = 1; i < cities.size(); i++) {
			ArrayList<Integer> list = new ArrayList<>();
			for (int j = 0; j < i; j++) {
				list.add(m.distance(cityList.get(i), cityList.get(j)));
			}
			distances.add(list);
		}
	}

	/*
	 * A constructor that creates a distance matrix given another and a a String
	 * initials. Assumes that the the String are the initials of a list of cities in
	 * the order of the original matrix.
	 */
	public DistanceMatrix(DistanceMatrix m, String initials) {
		cities = m.getCities(initials);
		distances = new ArrayList<>();
		n_iter = 10;
		for (int i = 1; i < cities.size(); i++) {
			ArrayList<Integer> list = new ArrayList<>();
			for (int j = 0; j < i; j++) {
				list.add(m.distance(cities.get(i), cities.get(j)));
			}
			distances.add(list);
		}
	}

	/* Returns the distance between two given cities. */
	public Integer distance(String city1, String city2) {
		int indexC1 = cities.indexOf(city1);
		int indexC2 = cities.indexOf(city2);
		if (indexC1 < indexC2)
			return distances.get(indexC2 - 1).get(indexC1);
		else
			return distances.get(indexC1 - 1).get(indexC2);
	}

	/* Returns the list of cities */
	public ArrayList<String> getCities() {
		return cities;
	}

	/* From a String filter returns a subset of cites with initials in filter. */
	public ArrayList<String> getCities(String filter) {
		ArrayList<String> cityList = new ArrayList<String>();
		for (int i = 0; i < filter.length(); i++) {
			cityList.add(getCity(filter.charAt(i)));
		}
		return cityList;
	}

	/* given a char c returns the city with the first letter c. */
	public String getCity(char c) {
		for (int i = 0; i < cities.size(); i++) {
			if (cities.get(i).charAt(0) == c)
				return cities.get(i);
		}
		return null;
	}

	/* from a list of cities cityList return a String with their initials */
	public String getInitials(ArrayList<String> cityList) {
		String initials = "";
		for (int i = 0; i < cityList.size(); i++) {
			initials += cityList.get(i).charAt(0);
		}
		return initials;
	}

	/* Returns the distances */
	public ArrayList<ArrayList<Integer>> getDistances() {
		return distances;
	}

	/*
	 * Private: shows all cities in the matrix (but the last) for the sake of
	 * showing the distance matrix.
	 */
	private void showCities() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < cities.size() - 1; i++) {
			sb.append(cities.get(i));
			if (i < cities.size() - 2)
				sb.append(", ");
		}
		sb.append("]");
		System.out.println(sb.toString());
	}

	/*
	 * Private: shows all distances in a row in the matrix with adequate sizes to
	 * fit the columns
	 */
	private String showRow(Integer index) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < distances.get(index).size(); i++) {
			int l = cities.get(i).length();
			int d = distances.get(index).get(i);
			sb.append(String.format("%1$" + l + "s", d));
			if (i < distances.get(index).size() - 1)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	/* Shows the distance matrix. */
	public void showDistances() {
		System.out.print("           ");
		this.showCities();
		for (int i = 0; i < distances.size(); i++) {
			System.out.println(String.format("%1$10s" + " " + this.showRow(i), cities.get(i + 1)));
		}
	}

	private double initialTemperature() {
		int maxD = Integer.MIN_VALUE;
		int minD = Integer.MAX_VALUE;

		for (String city : this.getCities()) {
			for (String city2 : this.getCities()) {
				if (!city.equals(city2) && this.distance(city, city2) >= maxD) {
					maxD = this.distance(city, city2);
				}

				if (!city.equals(city2) && this.distance(city, city2) <= minD) {
					minD = this.distance(city, city2);
				}
			}
		}
		// feito com distancia maxima possivel e probabilidade alta inicial
		return (-(2 * maxD - 2 * minD)) / Math.log(0.9);
	}

	private ArrayList<String> initialSolution() {
		ArrayList<String> citiesAux = new ArrayList<>();
		ArrayList<String> sol = new ArrayList<>();

		for (String city : this.getCities()) {
			citiesAux.add(city);
		}

		int randIndex = this.random(0, cities.size());
		;

		sol.add(cities.get(randIndex));
		citiesAux.remove(randIndex);

		String curr = cities.get(randIndex);
		for (int i = 0; i < this.getCities().size() - 1; i++) {
			Iterator<String> itr = citiesAux.iterator();
			String next = null;

			int minDifference = Integer.MAX_VALUE;
			while (itr.hasNext()) {
				String possibleNext = itr.next();
				int difference = this.distance(curr, possibleNext);
				if (difference <= minDifference) {
					minDifference = difference;
					next = possibleNext;
				}
			}

			curr = next;
			sol.add(next);
			citiesAux.remove(next);
		}
		return sol;
	}

	private double cooling(double temperature) {
		double alpha = 0.9;
		temperature = alpha * temperature;
		n_iter = (int) Math.round(n_iter * (1 / alpha));
		return temperature;
	}

	public ArrayList<String> simulatedAnnealing() {
		ArrayList<String> current = this.initialSolution();
		ArrayList<String> next;
		ArrayList<String> best = current;
		double t = this.initialTemperature();
		double r = 0;
		double attempts;
		double acceptedAttempts;

		while (r <= 0.90 && t >= 0.1) {
			attempts = 0;
			acceptedAttempts = 0;
			for (int i = 0; i < n_iter; i++) {
				next = neighbour(current);
				int d = delta;
				if (d < 0) {
					current = next;
					acceptedAttempts++;
					if (this.totalDistance(current) < this.totalDistance(best)) {
						best = current;
					}
				} else {
					double probability = Math.exp(-d / t);
					if (Math.random() <= probability) {
						current = next; // com probabilidade exp(-d/T)
						acceptedAttempts++;
					}
				}
				attempts++;
			}
			t = this.cooling(t);
			r = this.rigidity(attempts, acceptedAttempts);
		}
		return best;
	}

	private ArrayList<String> neighbour(ArrayList<String> sol) {
		ArrayList<String> neighbour = new ArrayList<>();
		ArrayList<String> aux = new ArrayList<>();

		int randomIndex; // i
		int randomIndex2; // j

		do {
			randomIndex = this.random(0, sol.size());
			randomIndex2 = this.random(0, sol.size());
		} while (randomIndex >= randomIndex2);

		for (int j = randomIndex2; j > randomIndex; j--) {
			aux.add(sol.get(j));
		}
		neighbour.addAll(sol.subList(0, randomIndex + 1));
		neighbour.addAll(aux);
		neighbour.addAll(sol.subList(randomIndex2 + 1, sol.size())); // sublist is last exclusive

		delta = this.distance(sol.get(randomIndex), sol.get(randomIndex2))
				+ this.distance(sol.get(randomIndex + 1), sol.get((randomIndex2 + 1) % sol.size()))
				- this.distance(sol.get(randomIndex), sol.get(randomIndex + 1))
				- this.distance(sol.get(randomIndex2), sol.get((randomIndex2 + 1) % sol.size()));

		return neighbour;
	}

	public int totalDistance(ArrayList<String> sol) {
		int dist = 0;
		Iterator<String> itr = sol.iterator();
		String startingCity = itr.next();
		String city = startingCity;
		String city2 = itr.next();

		while (itr.hasNext() && !city.equals(city2)) {
			dist += this.distance(city, city2);
			city = city2;
			city2 = itr.next();
		}
		dist += this.distance(city, city2);
		dist += this.distance(startingCity, city2);
		return dist;
	}

	private int random(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	private double rigidity(double nAttempts, double nAcceptedAttempts) {
		return 1.0 - (nAcceptedAttempts / nAttempts);
	}

}