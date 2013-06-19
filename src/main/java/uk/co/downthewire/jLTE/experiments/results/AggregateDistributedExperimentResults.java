package uk.co.downthewire.jLTE.experiments.results;

import static uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings.DISTRIBUTED_SFR_EXPERIMENT_PATH;
import static uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings.DISTRIBUTED_SFR_EXPERIMENT_RESULTS_FILE;

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

import uk.co.downthewire.jLTE.simulator.results.AggregatedSimulationResults;
import uk.co.downthewire.jLTE.simulator.results.SimulationResults;
import uk.co.downthewire.jLTE.simulator.results.SimulationResultsAggregator;

public class AggregateDistributedExperimentResults implements Runnable {

	static Pattern logNamePattern = Pattern.compile("(\\d+).\\d+_(.*)_sp(\\d+)_ues(\\d+)_i(\\d+)_(.*)_s(.*)_\\d+_\\d+.log");
	private final String path;
	private final String outputFile;

	public static void main(String[] args) {
		new AggregateDistributedExperimentResults(DISTRIBUTED_SFR_EXPERIMENT_PATH, DISTRIBUTED_SFR_EXPERIMENT_RESULTS_FILE).run();
	}

	public AggregateDistributedExperimentResults(String resultsPath, String outputFile) {
		this.path = resultsPath;
		this.outputFile = outputFile;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();

		Map<String, List<SimulationResults>> keyToResultsListMap = mapKeysToResultList(path);
		Map<String, AggregatedSimulationResults> keyToAggregatedResultsMap = mapKeysToAggregatedResults(keyToResultsListMap);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			writer.write(getHeader() + AggregatedSimulationResults.header() + "\n");
			for (String key: keyToResultsListMap.keySet()) {
				writer.write(key + "," + keyToAggregatedResultsMap.get(key) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Time taken: " + (System.currentTimeMillis() - startTime) / 1000);
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

		List<String> allLogFiles;
		try {
			allLogFiles = getAllLogFiles(path);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		for (String filename: allLogFiles) {

			SimulationResults results;
			try {
				results = new SimulatorLogFileReader(path + "/results/" + filename).getResults();
			} catch (IOException e) {
				System.out.println("Trouble getting results from file: " + filename);
				continue;
			}

			String key = getKeyFromLogname(filename);

			if (!keyToResultsListMap.containsKey(key)) {
				keyToResultsListMap.put(key, new ArrayList<SimulationResults>());
			}
			keyToResultsListMap.get(key).add(results);
		}
		return keyToResultsListMap;
	}

	public static String getHeader() {
		return "Algorithm,speed,UEs,traffic,expId,";
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

	public static String getExperimentIdFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(1);
		}
		return null;
	}

	public static String getKeyFromLogname(String logname) {
		return getAlgorithmFromLogname(logname) + "," + //
		getSpeedFromLogname(logname) + "," + //
		getUEsFromLogname(logname) + "," + //
		getTrafficFromLogname(logname) + "," + //
		getExperimentIdFromLogname(logname);
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