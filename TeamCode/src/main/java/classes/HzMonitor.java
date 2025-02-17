package classes;

public class HzMonitor {
    private long lastTime;
    private double hz;

    // Constructor initializes the lastTime.
    public HzMonitor() {
        lastTime = System.nanoTime();
        hz = 0;
    }

    /**
     * Call this method in your opmode's loop to update the frequency.
     * @return The calculated loop frequency in Hz.
     */
    public double update() {
        long currentTime = System.nanoTime();
        double elapsedSeconds = (currentTime - lastTime) / 1e9;
        lastTime = currentTime;
        // Avoid division by zero.
        hz = (elapsedSeconds > 0) ? 1.0 / elapsedSeconds : 0;
        return hz;
    }

    /**
     * Optionally, you can get the last computed frequency.
     * @return The last calculated Hz value.
     */
    public double getHz() {
        return hz;
    }
}