package uk.co.downthewire.jLTE.experiments.preliminaryExperiments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings;
import uk.co.downthewire.jLTE.experiments.utils.HostBasedTaskRunner;
import uk.co.downthewire.jLTE.simulator.AbstractConfiguredRunnable;
import uk.co.downthewire.jLTE.simulator.Simulator;
import uk.co.downthewire.jLTE.simulator.results.SimulationResults;
import uk.co.downthewire.jLTE.simulator.utils.FieldNames;

public class ThreadsExperiment extends HostBasedTaskRunner<SimulationResults> {

	public static void main(final String[] args) throws ConfigurationException, InterruptedException, ExecutionException {
		int[] threadValues = { 2, 5, 10, 20 };
		for (int threads: threadValues) {
			long startTime = System.currentTimeMillis();
			new ThreadsExperiment(ExperimentSettings.TESTING_EXPERIMENT_PATH, threads).run();
			long endTime = System.currentTimeMillis();

			LOG.error(threads + " threads, time taken: " + (endTime - startTime) / 1000 + " seconds");
		}
	}

	public ThreadsExperiment(String experimentPath, int threads) {
		super(experimentPath, threads);
	}

	@SuppressWarnings("boxing")
	@Override
	protected List<Configuration> setupSimsToRun() throws ConfigurationException {
		List<Configuration> configs = new ArrayList<Configuration>();
		int simulationId = 1;
		for (double seed: ExperimentSettings.SEEDS) {
			Configuration configuration = new PropertiesConfiguration("system.properties").interpolatedConfiguration();
			configuration.setProperty(FieldNames.EXPERIMENT_ID, 9);
			configuration.setProperty(FieldNames.CHROMOSOME_ID, simulationId++);
			configuration.setProperty(FieldNames.SEED, seed);
			configuration.setProperty(FieldNames.SPEED, 120);
			configuration.setProperty(FieldNames.NUM_UES, 1150);
			configuration.setProperty(FieldNames.ALGORITHM, FieldNames.ADAPTIVE_SFR);
			configuration.setProperty(FieldNames.TRAFFIC_TYPE, FieldNames.FULL);
			configuration.setProperty(FieldNames.SCENARIO_PATH, ExperimentSettings.TESTING_EXPERIMENT_PATH);
			configs.add(configuration);
		}
		return configs;
	}

	@Override
	protected AbstractConfiguredRunnable<SimulationResults> makeTask(Configuration config) {
		return new Simulator(config);
	}
}
