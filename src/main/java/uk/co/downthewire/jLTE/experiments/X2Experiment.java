package uk.co.downthewire.jLTE.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import uk.co.downthewire.jLTE.ea.EAFields;
import uk.co.downthewire.jLTE.ea.EAMain;
import uk.co.downthewire.jLTE.experiments.utils.ExperimentSettings;
import uk.co.downthewire.jLTE.experiments.utils.HostBasedTaskRunner;
import uk.co.downthewire.jLTE.simulator.AbstractConfiguredRunnable;

public class X2Experiment extends HostBasedTaskRunner<Integer> {

	public static void main(final String[] args) throws ConfigurationException, InterruptedException, ExecutionException {
		new X2Experiment(ExperimentSettings.X2_EXPERIMENT_PATH, 1).run();
	}

	public X2Experiment(String path, int numThreadsPerBox) {
		super(path, numThreadsPerBox);
	}

	@Override
	@SuppressWarnings("boxing")
	public List<Configuration> setupSimsToRun() throws ConfigurationException {

		List<Configuration> configs = new ArrayList<Configuration>();
		int experimentId = 1;
		for (String algorithm: ExperimentSettings.X2_EXPERIMENT_ALGORITHMS) {
			for (int ue: ExperimentSettings.UES) {
				for (String traffic: ExperimentSettings.TRAFFIC_LEVELS) {
					for (int speed: ExperimentSettings.SPEEDS) {
						Configuration config = new PropertiesConfiguration("ea.properties").interpolatedConfiguration();
						config.setProperty(EAFields.X2_ENABLED, true);
						config.setProperty(EAFields.ALGORITHM, algorithm);
						config.setProperty(EAFields.NUM_UES, ue);
						config.setProperty(EAFields.TRAFFIC, traffic);
						config.setProperty(EAFields.SPEED, speed);
						config.setProperty(EAFields.EXPERIMENT_ID, experimentId++);
						config.setProperty(EAFields.EXPERIMENT_PATH, ExperimentSettings.X2_EXPERIMENT_PATH);
						configs.add(config);
					}
				}
			}
		}
		return configs;
	}

	@Override
	protected AbstractConfiguredRunnable<Integer> makeTask(Configuration config) {
		return new EAMain(config);
	}

}
