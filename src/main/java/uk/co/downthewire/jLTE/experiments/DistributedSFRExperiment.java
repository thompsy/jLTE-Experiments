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

public class DistributedSFRExperiment extends HostBasedTaskRunner<SimulationResults> {

	public static void main(final String[] args) throws ConfigurationException, InterruptedException, ExecutionException {
		new DistributedSFRExperiment(ExperimentSettings.DISTRIBUTED_SFR_EXPERIMENT_PATH).run();
	}

	public DistributedSFRExperiment(String experimentPath) {
		super(experimentPath, 2);
	}

	@SuppressWarnings("boxing")
	@Override
	protected List<Configuration> setupSimsToRun() throws ConfigurationException {
		List<Configuration> configs = new ArrayList<Configuration>();
		int simulationId = 91;
		for (int ue: ExperimentSettings.UES) {
			for (int speed: ExperimentSettings.SPEEDS) {
				for (double seed: ExperimentSettings.SEEDS) {
					for (String traffic: ExperimentSettings.TRAFFIC_LEVELS) {
						Configuration configuration = new PropertiesConfiguration("system.properties").interpolatedConfiguration();
						configuration.setProperty(FieldNames.EXPERIMENT_ID, 7);
						configuration.setProperty(FieldNames.CHROMOSOME_ID, simulationId++);
						configuration.setProperty(FieldNames.SEED, seed);
						configuration.setProperty(FieldNames.SPEED, speed);
						configuration.setProperty(FieldNames.NUM_UES, ue);
						configuration.setProperty(FieldNames.ALGORITHM, FieldNames.DISTRIBUTED_SFR);
						configuration.setProperty(FieldNames.TRAFFIC_TYPE, traffic);
						configuration.setProperty(FieldNames.SCENARIO_PATH, ExperimentSettings.DISTRIBUTED_SFR_EXPERIMENT_PATH);
						configuration.setProperty(FieldNames.ITERATIONS, 200000);
						configuration.setProperty(FieldNames.DISTRIBUTED_SFR_RANDOM_START, true);
						configuration.setProperty(FieldNames.DISTRIBUTED_SFR_CONSENSUS, false);
						configuration.setProperty(FieldNames.DISTRIBUTED_SFR_WINDOW, 100);
						configuration.setProperty(FieldNames.DISTRIBUTED_SFR_CONSENSUS_PROPORTION, 0.75);
						configs.add(configuration);
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
