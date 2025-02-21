/**
 * The Plant class represents a juice processing plant that processes oranges
 * through various stages such as fetching, peeling, juicing, and bottling.
 * Workers are assigned specific roles, and the plant operates by managing queues
 * for each stage of processing. The plant can handle multiple workers and provides
 * a summary of processing results.
 *
 * @author Nathan Williams & Molly O'Connor
 * @since 2025-02-20
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Plant implements Runnable {
    /**
     * Processing duration in milliseconds
     */
    public static final long PROCESSING_TIME = 5 * 1000;
    /**
     * Number of plants to be initialized
     */
    private static final int NUM_PLANTS = 2;
    /**
     * Oranges required per bottle of juice
     */
    public final int ORANGES_PER_BOTTLE = 3;
    /**
     * Given plant Number
     */
    private final int plantNumber;
    /**
     * Main thread that runs the plant operations.
     */
    private final Thread thread;
    /**
     * List of worker instances responsible for processing oranges at different stages.
     */
    private final List<Worker> workers;
    /**
     * List of threads assigned to workers.
     */
    private final List<Thread> workerThreads;
    /**
     * Shared queues for oranges ready to be fetched, peeled, juiced, bottled, or processed
     */
    private final LinkedBlockingQueue<Orange> readyToFetchQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Orange> readyToPeelQueue = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<Orange> readyToJuiceQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Orange> readyToBottleQueue = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<Orange> readyToProcessQueue = new LinkedBlockingQueue<>();
    /**
     * Workers responsible for fetching, peeling, juicing, or bottling
     */
    Worker fetcher = new Worker(1, "Fetcher", readyToFetchQueue, readyToPeelQueue, Orange.State.Fetched);
    Worker peeler = new Worker(1, "Peeler", readyToPeelQueue, readyToJuiceQueue, Orange.State.Peeled);
    Worker juicer = new Worker(2, "Juicer", readyToJuiceQueue, readyToBottleQueue, Orange.State.Squeezed);
    Worker bottler = new Worker(2, "Bottler", readyToBottleQueue, readyToProcessQueue, Orange.State.Bottled);
    /**
     * Total number of oranges provided to the plant for processing.
     */
    private int orangesProvided;
    /**
     * Total number of oranges successfully processed into juice.
     */
    private int orangesProcessed;
    /**
     * Flag to control whether the plant should continue working.
     * It is marked as volatile to ensure visibility across threads.
     */
    private volatile boolean timeToWork;

    /**
     * Constructs a new Plant instance with a given plant number.
     *
     * @param plantNumber The unique identifier for this plant.
     */
    Plant(int plantNumber) {
        this.plantNumber = plantNumber;
        this.workerThreads = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.orangesProvided = 0;
        this.orangesProcessed = 0;
        this.thread = new Thread(this, "Plant[" + plantNumber + "]");
    }

    /**
     * Main method to start the juice processing plants.
     */
    public static void main(String[] args) throws InterruptedException {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(1 + i);
            plants[i].startPlant();
        }

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) {
            p.stopPlant();
        }
        for (Plant p : plants) {
            p.waitToStop();
        }

        // Summarize the results
        int totalProvided = 0;
        int totalFetched = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;

        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalFetched += p.fetcher.getFetchedCount();
            totalProcessed += p.getTotalProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }

        System.out.println("\n======= Juice Plant Processing Summary =======");
        System.out.println("Total Oranges Provided: " + totalProvided);
        System.out.println("Total Oranges Fetched: " + totalFetched);
        System.out.println("Total Oranges Processed: " + totalProcessed);
        System.out.println("Total Bottles Created: " + totalBottles);
        System.out.println("Total Oranges Wasted: " + totalWasted);
        System.out.println("==============================================");
    }

    /**
     * Delays the program execution for a given time.
     *
     * @param time   The delay time in milliseconds.
     * @param errMsg Error message to display in case of interruption.
     */
    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

    /**
     * Starts the plant by initializing and starting the necessary workers.
     */
    public void startPlant() {
        timeToWork = true;
        thread.start();
        if (plantNumber % 2 != 0) {
            workerThreads.add(new Thread(fetcher));
            workerThreads.add(new Thread(peeler));
            workers.add(fetcher);
            workers.add(peeler);
        } else {
            workerThreads.add(new Thread(juicer));
            workerThreads.add(new Thread(bottler));
            workers.add(juicer);
            workers.add(bottler);
        }
        workerThreads.forEach(Thread::start);
    }

    /**
     * Stops the plant and all associated worker threads.
     */
    public void stopPlant() {
        timeToWork = false;
        workers.forEach(Worker::stopWorker);
    }

    /**
     * Waits for all threads to stop gracefully.
     *
     * @throws InterruptedException If thread interruption occurs.
     */
    public void waitToStop() throws InterruptedException {
        for (Thread t : workerThreads) {
            t.join();
        }
        thread.join();
    }

    /**
     * Runs the plant's orange processing loop.
     */
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Processing oranges");
        int count = 0;
        while (timeToWork) {
            // Use this to give user feedback that the plant is processing
            if ((count + 1) % 40 == 0) {
                System.out.println(".");
            }
            try {
                readyToFetchQueue.put(new Orange());
                orangesProvided++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            count++;
        }
        System.out.println(Thread.currentThread().getName() + " Done Processing");
    }

    /**
     * @return The total number of oranges provided by the plant.
     */
    public int getProvidedOranges() {
        return orangesProvided;
    }

    /**
     * @return The total number of processed oranges.
     */
    public int getTotalProcessedOranges() {
        orangesProcessed = bottler.getProcessedOranges();
        return orangesProcessed;
    }

    /**
     * @return The total number of bottles created.
     */
    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    /**
     * @return The number of wasted oranges that didn't fit into full bottles.
     */
    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }
}