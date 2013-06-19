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

public class FindOptimalSFRParameters extends HostBasedTaskRunner<SimulationResults> {

	private static final int MAX_FULL_POWER_RBS = 100;
	private static final double MAX_EDGE_UES = 1.0;

	protected final String algorithm;

	public static void main(final String[] args) throws ConfigurationException, InterruptedException, ExecutionException {
		new FindOptimalSFRParameters(FieldNames.SFR_ALGO, ExperimentSettings.SFR_PARAMETERS_EXPERIMENT_PATH, ExperimentSettings.THREADS).run();
	}

	public FindOptimalSFRParameters(String algorithm, String path, int threadsPerBox) {
		super(path, threadsPerBox);
		this.algorithm = algorithm;
	}

	@SuppressWarnings("boxing")
	@Override
	protected List<Configuration> setupSimsToRun() throws ConfigurationException {
		List<Configuration> configsToRun = new ArrayList<Configuration>();
		int simulationId = 1;
		for (int fullPowerRBs = 1; fullPowerRBs <= MAX_FULL_POWER_RBS; fullPowerRBs += 2) {
			for (double maxEdgeUsers = 0.05; maxEdgeUsers <= MAX_EDGE_UES; maxEdgeUsers += 0.05) {
				Configuration configuration = new PropertiesConfiguration("experiments/experiment.properties").interpolatedConfiguration();
				configuration.setProperty(FieldNames.EXPERIMENT_ID, ExperimentSettings.SFR_PARAMETERS_EXPERIMENT_ID);
				configuration.setProperty(FieldNames.CHROMOSOME_ID, simulationId++);
				configuration.setProperty(FieldNames.SFR_NUM_HIGH_POWER_RBS, fullPowerRBs);
				configuration.setProperty(FieldNames.SFR_EDGE_USERS, maxEdgeUsers);
				configuration.setProperty(FieldNames.SCENARIO_PATH, ExperimentSettings.SFR_PARAMETERS_EXPERIMENT_PATH);
				configuration.setProperty(FieldNames.ALGORITHM, algorithm);

				configsToRun.add(configuration);
			}
		}
		return configsToRun;
	}

	@Override
	protected AbstractConfiguredRunnable<SimulationResults> makeTask(Configuration config) {
		return new Simulator(config);
	}
}
