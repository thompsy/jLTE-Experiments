package uk.co.downthewire.jLTE.experiments.results;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings;


public class AllExperimentAggregation implements Runnable {

	private final String outputFile;
	private final String[] metrics = { "average", "percentile" };

	private final int ALGO_INDEX = 0;
	private final int SPEED_INDEX = 1;
	private final int UES_INDEX = 2;
	private final int TRAFFIC_INDEX = 3;
	private final int AVERAGE_TPUT_INDEX = 4;
	private final int PERCENTILE_TPUT_INDEX = 5;

	public static void main(String[] args) {
		new AllExperimentAggregation("experiments/results/test.csv").run();
	}

	public AllExperimentAggregation(String outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public void run() {
		Map<String, Map<String, List<String>>> allResults = new HashMap<>();
		addFileToResultsMap(allResults, "experiments/results/baseline-nonAggregated.csv", PERCENTILE_TPUT_INDEX);
		addFileToResultsMap(allResults, "experiments/results/adaptiveSFR.csv", PERCENTILE_TPUT_INDEX + 1);
		addFileToResultsMap(allResults, "experiments/results/adaptiveSFR.csv", PERCENTILE_TPUT_INDEX + 1);
		addFileToResultsMap(allResults, "experiments/results/x2.csv", PERCENTILE_TPUT_INDEX + 1);
		addFileToResultsMap(allResults, "experiments/results/distributedSFR-nonAggregated.csv", PERCENTILE_TPUT_INDEX);
		printHeader();
		printResults(allResults);
	}

	private void printResults(Map<String, Map<String, List<String>>> allResults) {
		// print the results
		for (Entry<String, Map<String, List<String>>> algoResults: allResults.entrySet()) {
			String algo = algoResults.getKey();
			Map<String, List<String>> headerToResultsMap = algoResults.getValue();
			printAlgoResults(algo, headerToResultsMap);
		}
	}

	private void printAlgoResults(String algo, Map<String, List<String>> results) {
		for (int i = 0; i < getMaxResults(results); i++) {
			System.out.print(algo + ",");
			for (String key: getKeys()) {
				if (key.equals("algorithm")) {
					continue;
				}
				List<String> resultsForHeader = results.get(key);
				if (i >= resultsForHeader.size()) {
					System.out.print("-,");
					continue;
				}
				System.out.print(resultsForHeader.get(i) + ",");
			}
			System.out.println();
		}
	}

	private int getMaxResults(Map<String, List<String>> results) {
		int maxResults = 0;
		for (String header: getKeys()) {
			int resultsForHeader = results.get(header).size();
			maxResults = resultsForHeader > maxResults ? resultsForHeader : maxResults;
		}
		return maxResults;
	}

	private void addFileToResultsMap(Map<String, Map<String, List<String>>> allResults, String filename, int percentileTputIndex) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(filename)));

			String line;
			int lineCount = 0;
			while ((line = reader.readLine()) != null) {
				if (lineCount == 0) {
					lineCount++;
					continue;
				}

				String[] parts = line.split(",");
				String algo = parts[ALGO_INDEX];

				// build the per algo results map if we need to
				if (!allResults.containsKey(algo)) {
					HashMap<String, List<String>> algoResults = new HashMap<String, List<String>>();
					for (String key: getKeys()) {
						algoResults.put(key, new ArrayList<String>());
					}
					allResults.put(algo, algoResults);
				}

				String traffic = parts[TRAFFIC_INDEX];
				String speed = parts[SPEED_INDEX];
				String ues = parts[UES_INDEX];
				String avgTput = parts[AVERAGE_TPUT_INDEX];
				String percentileTput = parts[percentileTputIndex];

				String avgTputColumn = buildColumnName(metrics[0], traffic, ues, speed);
				String percentileTputColumn = buildColumnName(metrics[1], traffic, ues, speed);

				allResults.get(algo).get(avgTputColumn).add(avgTput);
				allResults.get(algo).get(percentileTputColumn).add(percentileTput);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printHeader() {
		for (String header: getKeys()) {
			System.out.print(header + ",");
		}
		System.out.println();
	}

	private List<String> getKeys() {
		List<String> headers = new ArrayList<>();
		headers.add("algorithm");
		for (String metric: metrics) {
			for (String traffic: ExperimentSettings.TRAFFIC_LEVELS) {
				for (int ues: ExperimentSettings.UES) {
					for (int speed: ExperimentSettings.SPEEDS) {
						headers.add(buildColumnName(metric, traffic, Integer.toString(ues), Integer.toString(speed)));
					}
				}
			}
		}
		return headers;
	}

	private static String buildColumnName(String metric, String traffic, String ues, String speed) {
		return metric + "_" + traffic + "_" + ues + "_" + speed;
	}
}