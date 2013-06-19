package uk.co.downthewire.jLTE.experiments.results;

import static uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings.ADAPTIVE_SFR_EXPERIMENT_PATH;
import static uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings.ADAPTIVE_SFR_EXPERIMENT_RESULTS_FILE;

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

public class AggregateAdaptiveExperimentResults implements Runnable {

	// ea_2_AdaptiveSFR_sp30_ues285_i100_LIGHT_s11111.11111_092050_11012013.log
	static Pattern logNamePattern = Pattern.compile("ea_\\d+_(.*)_sp(\\d+)_ues(\\d+)_i(\\d+)_(.*)_s(.*)_\\d+_\\d+.log");
	protected final String path;
	protected final String outputFile;

	public static void main(String[] args) {
		new AggregateAdaptiveExperimentResults(ADAPTIVE_SFR_EXPERIMENT_PATH, ADAPTIVE_SFR_EXPERIMENT_RESULTS_FILE).run();
	}

	public AggregateAdaptiveExperimentResults(String resultsPath, String outputFile) {
		this.path = resultsPath;
		this.outputFile = outputFile;
	}

	@Override
	public void run() {
		Map<String, List<String>> keyToResultsListMap = mapKeysToResultList(path);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			writer.write(getHeader() + EALogFileReader.getHeader() + "\n");
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
				List<String> results = new EALogFileReader(path + filename).getResults();

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

	public static String getHeader() {
		return "Algorithm,speed,UEs,traffic,";
	}

	public static List<String> getAllLogFiles(String path) throws IOException {

		final List<String> files = new ArrayList<String>();

		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "ea*.log");
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
			return logNameMatcher.group(1);
		}
		return null;
	}

	public static String getSpeedFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(2);
		}
		return null;
	}

	public static String getIterationsFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(4);
		}
		return null;
	}

	public static String getUEsFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(3);
		}
		return null;
	}

	public static String getTrafficFromLogname(String logname) {
		Matcher logNameMatcher = logNamePattern.matcher(logname);
		if (logNameMatcher.matches()) {
			return logNameMatcher.group(5);
		}
		return null;
	}

}