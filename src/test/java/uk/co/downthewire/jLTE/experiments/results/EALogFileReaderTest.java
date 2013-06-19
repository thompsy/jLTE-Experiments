package uk.co.downthewire.jLTE.experiments.results;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class EALogFileReaderTest {

	@Test
	public void parseEALogFile() throws IOException {
		String filename = "src/test/resources/ea_log_file.log";
		EALogFileReader eaLogfileReader = new EALogFileReader(filename);
		List<String> results = eaLogfileReader.getResults();

		assertEquals("1.1637914666526057,0.05802028405308153,0.10030674862123563,0.013285574343819448,4,0.5813194990362789,0.7503021779880691,true,0.136312832842726,0.8404454700268283", results.get(0));
	}
}
