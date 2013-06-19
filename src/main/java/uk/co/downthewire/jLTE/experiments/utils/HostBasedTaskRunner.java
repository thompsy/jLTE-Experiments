package uk.co.downthewire.jLTE.experiments.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import uk.co.downthewire.jLTE.ea.EAFields;
import uk.co.downthewire.jLTE.simulator.AbstractConfiguredRunnable;
import uk.co.downthewire.jLTE.simulator.utils.FieldNames;

public abstract class HostBasedTaskRunner<X> {

	protected static Logger LOG = LoggerFactory.getLogger(HostBasedTaskRunner.class);

	private final int threads;

	public HostBasedTaskRunner(String path, int numThreadsPerBox) {
		this.threads = numThreadsPerBox;
		configureLogging(path + getHostname() + "-experiment.log");
	}

	@SuppressWarnings("boxing")
	public void run() throws ConfigurationException, InterruptedException, ExecutionException {
		List<Configuration> allConfigs = setupSimsToRun();
		String hostname = getHostname();
		List<Configuration> configsToRun = getConfigsToRunOnThisMachine(allConfigs, hostname);

		List<Integer> experimentIds = new ArrayList<Integer>();
		for (Configuration config: configsToRun) {
			if (config.containsKey(EAFields.EXPERIMENT_ID))
				experimentIds.add(config.getInt(EAFields.EXPERIMENT_ID));
			else if (config.containsKey(FieldNames.EXPERIMENT_ID))
				experimentIds.add(config.getInt(FieldNames.EXPERIMENT_ID));
			else
				throw new RuntimeException("No experiment id found.");
		}

		LOG.error("{} jobs to run in total", allConfigs.size());
		LOG.error("Running {} jobs on this box[{}], experimentIds: {}", configsToRun.size(), hostname, experimentIds);

		runTasks(configsToRun);
	}

	private void runTasks(List<Configuration> configs) throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(threads);

		List<Future<X>> futures = new ArrayList<Future<X>>();
		for (Configuration config: configs) {
			AbstractConfiguredRunnable<X> task = makeTask(config);
			LOG.error("adding task: {}", task.getId());
			futures.add(executorService.submit(task));
		}
		for (Future<X> future: futures) {
			LOG.error("Running next task...");
			future.get();
			LOG.error("Finished task...");
		}
		executorService.shutdown();
		LOG.error("Shutting down...");
	}

	private static int getHostIndex(List<String> hosts, String hostname) {
		int hostIndex = hosts.indexOf(hostname);

		if (hostIndex == -1) {
			throw new RuntimeException("Host " + hostname + " is not in HOST_LIST therefore we can't run anything");
		}
		return hostIndex;
	}

	private static List<Configuration> getConfigsToRunOnThisMachine(List<Configuration> allConfigs, String hostname) {
		List<Configuration> configsToRun = new ArrayList<Configuration>();
		int hostIndex = getHostIndex(ExperimentSettings.HOST_LIST, hostname);
		while (hostIndex < allConfigs.size()) {
			Configuration config = allConfigs.get(hostIndex);
			configsToRun.add(config);
			hostIndex += ExperimentSettings.HOST_LIST.size();
		}
		return configsToRun;
	}

	private static String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public void configureLogging(String logFilename) {
		MDC.put("userid", logFilename);
		LOG.error("Starting HostBasedTaskRunner...");
	}

	protected abstract List<Configuration> setupSimsToRun() throws ConfigurationException;

	protected abstract AbstractConfiguredRunnable<X> makeTask(Configuration config);
}
