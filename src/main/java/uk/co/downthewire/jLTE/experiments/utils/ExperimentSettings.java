package uk.co.downthewire.jLTE.experiments.utils;

import static uk.co.downthewire.jLTE.simulator.utils.FieldNames.ADAPTIVE_SFR;
import static uk.co.downthewire.jLTE.simulator.utils.FieldNames.DISTRIBUTED_SFR;
import static uk.co.downthewire.jLTE.simulator.utils.FieldNames.MAXCI_ALGO;
import static uk.co.downthewire.jLTE.simulator.utils.FieldNames.PROPORTIONATE_FAIR_ALGO;
import static uk.co.downthewire.jLTE.simulator.utils.FieldNames.RANDOM_ALGO;
import static uk.co.downthewire.jLTE.simulator.utils.FieldNames.SERFR_ALGO;
import static uk.co.downthewire.jLTE.simulator.utils.FieldNames.SFR_ALGO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExperimentSettings {

	public static final double[] SEEDS = { 0.0, 11111.11111, 22222.22222, 33333.33333, 44444.44444, 55555.55555, 66666.66666, 77777.77777, 88888.88888, 99999.99999 };
	public static final int[] UES = { 285, 680, 1150 };
	public static final String[] TRAFFIC_LEVELS = { "LIGHT", "MIXED", "HEAVY", "FULL" };
	public static final int[] SPEEDS = { 3, 30, 120 };

	public static final int THREADS = 5;

	// Algorithms
	public static final String[] ALL_ALGORITHMS = { RANDOM_ALGO, MAXCI_ALGO, PROPORTIONATE_FAIR_ALGO, SFR_ALGO, SERFR_ALGO, ADAPTIVE_SFR, DISTRIBUTED_SFR };
	public static final String[] BASELINE_ALGORITHMS = { RANDOM_ALGO, MAXCI_ALGO, PROPORTIONATE_FAIR_ALGO, SFR_ALGO, SERFR_ALGO };
	public static final String[] EXTRA_ALGORITHMS = { ADAPTIVE_SFR };

	public static final String TESTING_EXPERIMENT_PATH = "resources/test/integ/03-19eNodeBs/";

	// Preliminary experiments
	public static final String NUM_ITERATIONS_EXPERIMENT_PATH = "experiments/01-prelim-numIterations/";
	public static final int NUM_ITERATIONS_EXPERIMENT_ID = 1;

	public static final String SFR_PARAMETERS_EXPERIMENT_PATH = "experiments/02-prelim-optimalSFRParameters/";
	public static final int SFR_PARAMETERS_EXPERIMENT_ID = 2;

	public static final String SERFR_PARAMETERS_EXPERIMENT_PATH = "experiments/03-prelim-optimalSerFRParameters/";
	public static final int SERFR_PARAMETERS_EXPERIMENT_ID = 3;

	// Main experiments
	public static final String BASELINE_EXPERIMENT_PATH = "experiments/04-baseline/";
	public static final String BASELINE_EXPERIMENT_RESULTS_FILE = "experiments/results/baseline.csv";
	public static final int BASELINE_EXPERIMENT_ID = 4;

	public static final String ADAPTIVE_SFR_EXPERIMENT_PATH = "experiments/05-adaptiveSFR/";
	public static final String ADAPTIVE_SFR_EXPERIMENT_RESULTS_FILE = "experiments/results/adaptiveSFR.csv";
	public static final int ADAPTIVE_SFR_EXPERIMENT_ID = 5;

	public static final String X2_EXPERIMENT_PATH = "experiments/06-x2/";
	public static final String X2_EXPERIMENT_RESULTS_FILE = "experiments/results/x2.csv";
	public static final int X2_EXPERIMENT_ID = 6;
	public static final String[] X2_EXPERIMENT_ALGORITHMS = { ADAPTIVE_SFR };

	public static final String DISTRIBUTED_SFR_EXPERIMENT_PATH = "experiments/07-distributedSFR/";
	public static final String DISTRIBUTED_SFR_EXPERIMENT_RESULTS_FILE = "experiments/results/distributedSFR.csv";
	public static final int DISTRIBUTED_SFR_EXPERIMENT_ID = 7;

	// Hosts across which to split the work
	private static final String[] HOSTS = { "localhost" };

	public static final List<String> HOST_LIST = new ArrayList<String>();
	static {
		Collections.addAll(HOST_LIST, HOSTS);
	}

}
