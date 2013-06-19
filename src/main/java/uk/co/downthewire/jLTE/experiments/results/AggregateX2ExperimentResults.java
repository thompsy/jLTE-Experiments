package uk.co.downthewire.jLTE.experiments.results;

import static uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings.X2_EXPERIMENT_PATH;
import static uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings.X2_EXPERIMENT_RESULTS_FILE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateX2ExperimentResults extends AggregateAdaptiveExperimentResults {

	public static void main(String[] args) {
		new AggregateX2ExperimentResults(X2_EXPERIMENT_PATH, X2_EXPERIMENT_RESULTS_FILE).run();
	}

	public AggregateX2ExperimentResults(String resultsPath, String outputFile) {
		super(resultsPath, outputFile);
	}

	@Override
	public void run() {
		Map<String, List<String>> keyToResultsListMap = mapKeysToResultList(path);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			writer.write(getHeader() + EAX2LogFileReader.getHeader() + "\n");
			for (String key: keyToResultsListMap.keySet()) {
				for (String result: keyToResultsListMap.get(key)) {
					writer.write(key + "," + result + "\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, List<String>> mapKeysToResultList(String path) {
		Map<String, List<String>> keyToResultsListMap = new HashMap<String, List<String>>();

		try {
			for (String filename: getAllLogFiles(path)) {
				List<String> results = new EAX2LogFileReader(path + filename).getResults();

				String key = getKeyFromLogname(filename);

				if (!keyToResultsListMap.containsKey(key)) {
					keyToResultsListMap.put(key, new ArrayList<String>());
				}
				keyToResultsListMap.get(key).addAll(results);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keyToResultsListMap;
	}
}
