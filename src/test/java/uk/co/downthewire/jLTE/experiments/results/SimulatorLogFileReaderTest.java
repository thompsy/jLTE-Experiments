package uk.co.downthewire.jLTE.experiments.results;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import uk.co.downthewire.jLTE.simulator.results.SimulationResults;

public class SimulatorLogFileReaderTest {

	@Test
	public void parseSimulatorLogFile() throws IOException {

		String filename = "src/test/resources/simulator_log_file.log";

		SimulatorLogFileReader simulatorLogFileReader = new SimulatorLogFileReader(filename);
		SimulationResults results = simulatorLogFileReader.getResults();

		assertEquals(1.38, results.avergeTput, 0.01);
		assertEquals(21.60, results.maxUETput, 0.01);
		assertEquals(0.033, results.percentileTput, 0.01);
	}

}
