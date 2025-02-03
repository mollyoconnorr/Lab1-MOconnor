public class Mutex {
    private boolean threadActive = false;

    public synchronized boolean acquire() {
        if (!threadActive) {
            threadActive = true;
            return true;
        }
        return false;
    }

    public synchronized void release() {
        if (threadActive) {
            threadActive = false;
        }
    }
}