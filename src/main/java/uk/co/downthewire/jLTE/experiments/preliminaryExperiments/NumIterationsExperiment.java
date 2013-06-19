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

public class NumIterationsExperiment extends HostBasedTaskRunner<SimulationResults> {

	private static int ITERATIONS = 2500;
	private static int NUM_UES = 1150;
	private static int SPEED = 120;
	private static String TRAFFIC = FieldNames.FULL;

	public static void main(final String[] args) throws ConfigurationException, InterruptedException, ExecutionException {
		new NumIterationsExperiment(ExperimentSettings.NUM_ITERATIONS_EXPERIMENT_PATH, 1).run();
	}

	public NumIterationsExperiment(String path, int numThreads) {
		super(path, numThreads);
		LOG.error("Setting up simsToRun");
	}

	@Override
	@SuppressWarnings("boxing")
	protected List<Configuration> setupSimsToRun() throws ConfigurationException {
		List<Configuration> configs = new ArrayList<Configuration>();
		int simulationId = 1;

		for (String algorithm: ExperimentSettings.EXTRA_ALGORITHMS) {
			for (double seed: ExperimentSettings.SEEDS) {

				Configuration configuration = new PropertiesConfiguration("system.properties").interpolatedConfiguration();
				configuration.setProperty(FieldNames.EXPERIMENT_ID, ExperimentSettings.NUM_ITERATIONS_EXPERIMENT_ID);
				configuration.setProperty(FieldNames.SCENARIO_PATH, ExperimentSettings.NUM_ITERATIONS_EXPERIMENT_PATH);
				configuration.setProperty(FieldNames.CHROMOSOME_ID, simulationId++);
				configuration.setProperty(FieldNames.SEED, seed);
				configuration.setProperty(FieldNames.ITERATIONS, ITERATIONS);
				configuration.setProperty(FieldNames.ALGORITHM, algorithm);
				configuration.setProperty(FieldNames.SPEED, SPEED);
				configuration.setProperty(FieldNames.NUM_UES, NUM_UES);
				configuration.setProperty(FieldNames.TRAFFIC_TYPE, TRAFFIC);
				configuration.setProperty(FieldNames.X2_ENABLED, true);
				configs.add(configuration);
			}
		}
		return configs;
	}

	@Override
	protected AbstractConfiguredRunnable<SimulationResults> makeTask(Configuration config) {
		return new Simulator(config);
	}
}
