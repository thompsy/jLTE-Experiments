package uk.co.downthewire.jLTE.experiments.results;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings;
import uk.co.downthewire.jLTE.simulator.results.AggregatedSimulationResults;
import uk.co.downthewire.jLTE.simulator.results.SimulationResults;
import uk.co.downthewire.jLTE.simulator.results.SimulationResultsAggregator;

public class AggregateSimulationExperimentResults implements Runnable {

	static Pattern logNamePattern = Pattern.compile("(\\d+).\\d+_(.*)_sp(\\d+)_ues(\\d+)_i(\\d+)_(.*)_s(.*)_\\d+_\\d+.log");
	private final String path;
	private final String outputFile;

	public static void main(String[] args) {
		new AggregateSimulationExperimentResults(ExperimentSettings.DISTRIBUTED_SFR_EXPERIMENT_PATH, ExperimentSettings.DISTRIBUTED_SFR_EXPERIMENT_RESULTS_FILE).run();
	}

	public AggregateSimulationExperimentResults(String resultsPath, String outputFile) {
		this.path = resultsPath;
		this.outputFile = outputFile;
	}

	@Override
	public void run() {
		Map<String, List<SimulationResults>> keyToResultsListMap = mapKeysToResultList(path);
		// Map<String, AggregatedSimulationResults> keyToAggregatedResultsMap = mapKeysToAggregatedResults(keyToResultsListMap);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			writer.write(getHeader() + AggregatedSimulationResults.header() + "\n");
			for (String key: keyToResultsListMap.keySet()) {
				for (SimulationResults result: keyToResultsListMap.get(key)) {
					writer.write(key + "," + result + "\n");

				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, AggregatedSimulationResults> mapKeysToAggregatedResults(Map<String, List<SimulationResults>> keyToResultsListMap) {
		Map<String, AggregatedSimulationResults> keyToAggregatedResultsMap = new HashMap<String, AggregatedSimulationResults>();
		for (String key: keyToResultsListMap.keySet()) {
			SimulationResultsAggregator aggregator = new SimulationResultsAggregator();
			aggregator.aggregate(keyToResultsListMap.get(key));
			AggregatedSimulationResults aggregatedResult = aggregator.getResult();
			keyToAggregatedResultsMap.put(key, aggregatedResult);
		}
		return keyToAggregatedResultsMap;
	}

	private static Map<String, List<SimulationResults>> mapKeysToResultList(String path) {
		Map<String, List<SimulationResults>> keyToResultsListMap = new HashMap<String, List<SimulationResults>>();

		try {
			for (String filename: getAllLogFiles(path)) {
				SimulationResults results = new SimulatorLogFileReader(path + "/results/" + filename).getResults();

				String key = getKeyFromLogname(filename);

				if (!keyToResultsListMap.containsKey(key)) {
					keyToResultsListMap.put(key, new ArrayList<SimulationResults>());
				}
				keyToResultsListMap.get(key).add(results);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keyToResultsListMap;
	}

	public static String getHeader() {
		return "Algorithm,speed,UEs,traffic,";
	}

	public static List<String> getAllLogFiles(String path) throws IOException {

		final List<String> files = new ArrayList<String>();

		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "results/*.log");
		Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (matcher.matches(file)) {
					// System.out.println(file);
					files.add(file.getFileName().toString());
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});

		return files;
	}

	public static String getKeyFromLogname(String logname) {
		return getAlgorithmFromLogname(logname) + "," + //
		getSpeedFromLogname(logname) + "," + //
		getUEsFromLogname(logname) + "," + //
		getTrafficFromLogname(logname);
	}

	public static String getAlgorithmFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			String id = logNameMatcher.group(1);

			String algo = logNameMatcher.group(2);
			if (id.equals("7")) {
				return algo + "-Greedy";
			}
			if (id.equals("8")) {
				return algo + "-Consensus";
			}

		}
		return null;
	}

	public static String getSpeedFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(3);
		}
		return null;
	}

	public static String getIterationsFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(5);
		}
		return null;
	}

	public static String getUEsFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(4);
		}
		return null;
	}

	public static String getTrafficFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(6);
		}
		return null;
	}

}