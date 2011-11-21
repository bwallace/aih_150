package ml.utility;

import java.util.Random;

public class Globals {

	// some constants regarding instance feature and prediction vectors
	public static final byte UNSORTED = 0;
	public static final byte ID_SORTED = 1;
	public static final byte STRENGTH_SORTED = 2;

    // RNG stuff
    public static long rngSeed = System.currentTimeMillis();
    public static Random rng = new Random(rngSeed);

    public static void seedRNG(long s) {
    	rngSeed = s;
    	rng = new Random(rngSeed);
    }
    
    public static double randDouble() {
    	return rng.nextDouble();
    }
    
    public static int randInt() {
    	return rng.nextInt();
    }
    
    public static int randInt(int n) {
    	return rng.nextInt(n);
    }
    
    public static double randGaussian() {
    	return rng.nextGaussian();
    }
    
    public static double randGaussian(double mean, double stddev) {
    	return randGaussian() * stddev + mean;
    }
    
    public static int randGaussianInt(int mean, int stddev) {
    	return (int) Math.round(randGaussian(mean, stddev));
    }
}
