package uk.co.downthewire.jLTE.experiments;

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

public class BaselineExperiment extends HostBasedTaskRunner<SimulationResults> {

	public static void main(final String[] args) throws ConfigurationException, InterruptedException, ExecutionException {
		new BaselineExperiment(ExperimentSettings.BASELINE_EXPERIMENT_PATH).run();
	}

	public BaselineExperiment(String experimentPath) {
		super(experimentPath, ExperimentSettings.THREADS);
	}

	@SuppressWarnings("boxing")
	@Override
	protected List<Configuration> setupSimsToRun() throws ConfigurationException {
		List<Configuration> configs = new ArrayList<Configuration>();
		int simulationId = 1;
		for (String algorithm: ExperimentSettings.BASELINE_ALGORITHMS) {
			for (int speed: ExperimentSettings.SPEEDS) {
				for (int numUEs: ExperimentSettings.UES) {
					for (String traffic: ExperimentSettings.TRAFFIC_LEVELS) {
						for (double seed: ExperimentSettings.SEEDS) {
							Configuration configuration = new PropertiesConfiguration("system.properties").interpolatedConfiguration();
							configuration.setProperty(FieldNames.EXPERIMENT_ID, ExperimentSettings.BASELINE_EXPERIMENT_ID);
							configuration.setProperty(FieldNames.CHROMOSOME_ID, simulationId++);
							configuration.setProperty(FieldNames.SEED, seed);
							configuration.setProperty(FieldNames.SPEED, speed);
							configuration.setProperty(FieldNames.NUM_UES, numUEs);
							configuration.setProperty(FieldNames.ALGORITHM, algorithm);
							configuration.setProperty(FieldNames.TRAFFIC_TYPE, traffic);
							configuration.setProperty(FieldNames.SCENARIO_PATH, ExperimentSettings.BASELINE_EXPERIMENT_PATH);
							configs.add(configuration);
						}
					}
				}
			}
		}
		return configs;
	}

	@Override
	protected AbstractConfiguredRunnable<SimulationResults> makeTask(Configuration config) {
		return new Simulator(config);
	}
}
