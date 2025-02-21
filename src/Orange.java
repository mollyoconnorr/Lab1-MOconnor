/**
 * Represents an Orange that goes through various processing states.
 * The orange starts in the Fetched state and can progress through
 * multiple states, including Peeled, Squeezed, Bottled, and Processed.
 *
 * @author Nathan Williams & Molly O'Connor
 * @since 2025-02-20
 */
public class Orange {
    private State state;
    private int OrangeNumber = 1;

    /**
     * Constructs a new Orange. Initializes its state to Fetched
     * and increments the OrangeNumber.
     */
    public Orange() {
        OrangeNumber++;
        state = State.Fetched;
        doWork();
    }

    /**
     * Returns the orange number, which is a unique identifier for the orange.
     *
     * @return The orange number.
     */
    public int getOrangeNumber() {
        return OrangeNumber;
    }

    /**
     * Sets the orange number.
     *
     * @param orangeNumber The orange number to set.
     */
    public void setOrangeNumber(int orangeNumber) {
        OrangeNumber = orangeNumber;
    }

    /**
     * Returns the current state of the orange.
     *
     * @return The current state of the orange.
     */
    public State getState() {
        return state;
    }

    /**
     * Runs the process for this orange. The orange can move to the next
     * state, performing work necessary for each state transition.
     * Throws an IllegalStateException if the orange has already been processed.
     */
    public synchronized void runProcess() {
        // Don't attempt to process an already completed orange
        if (state == State.Processed) {
            throw new IllegalStateException("This orange has already been processed");
        }
        doWork();
        state = state.getNext();
    }

    /**
     * Simulates the work necessary to complete the current state of the orange.
     * The method causes the thread to sleep for the duration of the state’s processing time.
     */
    private void doWork() {
        // Sleep for the amount of time necessary to do the work
        try {
            Thread.sleep(state.timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }

    /**
     * Enum representing the various states an orange can go through.
     * Each state has a defined time to complete its processing.
     */
    public enum State {
        Fetched(15),
        Peeled(38),
        Squeezed(29),
        Bottled(17),
        Processed(1);

        private static final int finalIndex = State.values().length - 1;

        final int timeToComplete;

        /**
         * Constructor for each state, with a specified time to complete.
         *
         * @param timeToComplete The time in milliseconds required to complete the state.
         */
        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        /**
         * Returns the next state that an orange can transition to.
         *
         * @return The next state.
         * @throws IllegalStateException if the current state is already the final state.
         */
        State getNext() {
            int currIndex = this.ordinal();
            if (currIndex >= finalIndex) {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }
}
