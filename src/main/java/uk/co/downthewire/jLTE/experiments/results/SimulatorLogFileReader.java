package uk.co.downthewire.jLTE.experiments.results;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.downthewire.jLTE.simulator.results.SimulationResults;

public class SimulatorLogFileReader {

	private final String filename;

	Pattern percentilePattern = Pattern.compile(".*5th percentile Tput = (.*) Mbps");
	Pattern averagePattern = Pattern.compile(".*Average.*all.*= (.*) Mbps");
	Pattern maxPattern = Pattern.compile(".*Max.*all.*= (.*) Mbps");

	public SimulatorLogFileReader(String filename) {
		this.filename = filename;
	}

	private SimulationResults parseFile() throws IOException {

		double percentile = -1;
		double average = -1;
		double max = -1;

		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		String line;

		while ((line = reader.readLine()) != null) {
			Matcher percentileMatcher = percentilePattern.matcher(line);
			if (percentileMatcher.matches()) {
				percentile = Double.parseDouble(percentileMatcher.group(1));
			}

			Matcher averageMatcher = averagePattern.matcher(line);
			if (averageMatcher.matches()) {
				average = Double.parseDouble(averageMatcher.group(1));
			}

			Matcher maxMatcher = maxPattern.matcher(line);
			if (maxMatcher.matches()) {
				max = Double.parseDouble(maxMatcher.group(1));
			}
		}
		reader.close();

		if (percentile == -1 || average == -1 || max == -1) {
			throw new IOException("We couldn't parse all of the expected values from the log file");
		}

		return new SimulationResults(percentile, average, max, null);
	}

	public SimulationResults getResults() throws IOException {
		return parseFile();
	}

}
