/**
 * Represents a Worker in the processing plant responsible for processing oranges.
 * The Worker fetches, peels, juices (squeezes) or bottles oranges from a process queue, processes them, and places them
 * into a new queue after processing.
 *
 * @author Molly O'Connor
 * @since 2025-02-20
 */

import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable {
    private final String workerName;
    private final LinkedBlockingQueue<Orange> processQueue;
    private final LinkedBlockingQueue<Orange> newProcessQueue;
    private final Orange.State assignedState;
    private int orangesProcessed = 0;
    private volatile boolean running = true;
    private int fetchedCount = 0;

    /**
     * Constructs a Worker for processing oranges in a specific plant.
     *
     * @param plantNumber     The number of the plant this worker belongs to.
     * @param workerName      The name of the worker.
     * @param processQueue    The queue from which the worker fetches oranges for processing.
     * @param newProcessQueue The queue where the worker places processed oranges.
     * @param assignedState   The state assigned to this worker (defines the type of work).
     */
    public Worker(int plantNumber, String workerName, LinkedBlockingQueue<Orange> processQueue,
                  LinkedBlockingQueue<Orange> newProcessQueue, Orange.State assignedState) {
        this.assignedState = assignedState;
        this.workerName = "Plant[" + plantNumber + "] - " + workerName;
        this.processQueue = processQueue;
        this.newProcessQueue = newProcessQueue;
    }

    /**
     * Stops the worker from processing further oranges.
     */
    public void stopWorker() {
        running = false;
    }

    /**
     * Executes the worker's processing loop. Continuously fetches oranges from the processQueue,
     * processes them, and places them into the newProcessQueue.
     */
    @Override
    public void run() {
        while (running) {
            try {
                // Fetch an orange to process
                Orange orange = processQueue.take();
                Orange.State expectedState = getAssignedState();

                // Process the orange if it's in the assigned state
                if (orange.getState() == expectedState) {
                    if (orange.getState() == Orange.State.Fetched) {
                        fetchedCount++;
                    }
                    orange.runProcess();
                }

                // Once the orange is processed, place it in the new queue
                if (orange.getState() == Orange.State.Processed) {
                    // Comments left for logging purposes for future use of program
                    // System.out.println(workerName + "Worker " + expectedState + " (" + orange + "), fully processed");
                    newProcessQueue.put(orange);
                    orangesProcessed++;
                } else {
                    // If the orange is not yet processed, requeue it for further processing
                    // System.out.println(workerName + "Worker " + expectedState + " (" + orange + "), ready to be "+ orange.getState());
                    newProcessQueue.put(orange);
                }
            } catch (InterruptedException e) {
                // Handle interruption
                Thread.currentThread().interrupt();  // Re-interrupt
                // System.out.println(workerName + " interrupted. Stopping...");
                break;
            }
        }
    }

    /**
     * Returns the assigned state of this worker.
     *
     * @return The state assigned to the worker.
     */
    public Orange.State getAssignedState() {
        return assignedState;
    }

    /**
     * Returns the number of oranges processed by this worker.
     *
     * @return The number of processed oranges.
     */
    public int getProcessedOranges() {
        return orangesProcessed;
    }

    /**
     * Returns the number of fetched oranges (only applies to fetcher worker).
     *
     * @return The number of fetched oranges.
     */
    public int getFetchedCount() {
        return fetchedCount;
    }

}
