package uk.co.downthewire.jLTE.experiments.results;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EAX2LogFileReader {

	private final String filename;

	Pattern generationPattern = Pattern.compile(".*--- Generation ([0-9]+) ---.*");
	Pattern paretoFrontPattern = Pattern.compile(".*ERROR ea.ParetoFront - ([0-9]).*");
	Pattern paretoFrontHeaderPattern = Pattern.compile(".*ERROR ea.ParetoFront - id.*");

	private static int CHROMOSOME_ID = 1;

	private static int EDGE_THRESHOLD = 2;
	private static int PROPORTION_OF_HIGH_POWER_RBS = 3;
	private static int RANDOM_HIGH_POWER_RBS = 4;
	private static int RANDOM_TRIGGER = 5;
	private static int REDUCED_POWER_FACTOR = 6;

	private static int X2_RBS_PER_MSG = 7;
	private static int X2_MSG_ALL_NEIGHBOURS = 8;
	private static int X2_MSG_LIFETIME = 9;
	private static int X2_MSG_WAIT_TIME = 10;

	private static int AVERAGE_TPUT = 11;
	private static int AVERAGE_TPUT_STDDEV = 12;
	private static int PERCENTILE_TPUT = 13;
	private static int PERCENTILE_TPUT_STDDEV = 14;

	public EAX2LogFileReader(String filename) {
		this.filename = filename;
	}

	public static String getHeader() {

		return "averageTput,averageTputStddev,percentileTput,percentileTputStddev,chromosomeId,edgeThreshold,proportionOfHighPowerRBs,randomHighPowerRBs,randomTrigger,reducedPowerFactor,x2RBsPerMsg,x2MsgAllNeighbours,x2MsgLifetime,x2MsgWaitTime";
	}

	private List<String> parseFile() throws IOException {

		List<String> results = new ArrayList<String>();

		int chromosomeId;

		double edgeThreshold;
		double proportionOfHighPowerRBs;
		boolean randomHighPowerRBs;
		double randomTrigger;
		double reducedPowerFactor;

		double x2RBsPerMsg;
		boolean x2MsgAllNeighbours;
		double x2MsgLifetime;
		double x2MsgWaitTime;

		double averageTput;
		double averageTputStddev;
		double percentileTput;
		double percentileTputStddev;

		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		String line;

		while ((line = reader.readLine()) != null) {

			System.out.println(line);
			Matcher generationMatcher = generationPattern.matcher(line);
			if (generationMatcher.matches()) {
				System.out.println("Generation: " + line);
				results = new ArrayList<String>();
			}

			Matcher paretoMatcher = paretoFrontPattern.matcher(line);
			if (paretoMatcher.matches()) {
				String[] parts = line.split(",");

				chromosomeId = Integer.parseInt(parts[CHROMOSOME_ID].split(" - ")[1]);

				edgeThreshold = Double.parseDouble(parts[EDGE_THRESHOLD]);
				proportionOfHighPowerRBs = Double.parseDouble(parts[PROPORTION_OF_HIGH_POWER_RBS]);
				randomHighPowerRBs = Boolean.parseBoolean(parts[RANDOM_HIGH_POWER_RBS]);
				randomTrigger = Double.parseDouble(parts[RANDOM_TRIGGER]);
				reducedPowerFactor = Double.parseDouble(parts[REDUCED_POWER_FACTOR]);

				x2RBsPerMsg = Double.parseDouble(parts[X2_RBS_PER_MSG]);
				x2MsgAllNeighbours = Boolean.parseBoolean(parts[X2_MSG_ALL_NEIGHBOURS]);
				x2MsgLifetime = Double.parseDouble(parts[X2_MSG_LIFETIME]);
				x2MsgWaitTime = Double.parseDouble(parts[X2_MSG_WAIT_TIME]);

				averageTput = Double.parseDouble(parts[AVERAGE_TPUT]);
				averageTputStddev = Double.parseDouble(parts[AVERAGE_TPUT_STDDEV]);
				percentileTput = Double.parseDouble(parts[PERCENTILE_TPUT]);
				percentileTputStddev = Double.parseDouble(parts[PERCENTILE_TPUT_STDDEV]);

				String outputLine = averageTput + "," + //
				averageTputStddev + "," + //
				percentileTput + "," + //
				percentileTputStddev + "," + //
				chromosomeId + "," + //
				edgeThreshold + "," + //
				proportionOfHighPowerRBs + "," + //
				randomHighPowerRBs + "," + //
				randomTrigger + "," + //
				reducedPowerFactor + "," + //
				x2RBsPerMsg + "," + //
				x2MsgAllNeighbours + "," + //
				x2MsgLifetime + "," + //
				x2MsgWaitTime + ",";

				results.add(outputLine);

			}
		}
		reader.close();

		return results;
	}

	public List<String> getResults() throws IOException {
		return parseFile();
	}

}
