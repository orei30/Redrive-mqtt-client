import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RecordsStringQueue {

    private BlockingQueue<String> recordsStringQueue;

    public RecordsStringQueue() {
        this.recordsStringQueue = new ArrayBlockingQueue(1024);
    }

    public synchronized void add(String record) {
        recordsStringQueue.add(record);
        notifyAll();
    }

    public synchronized String take() throws InterruptedException {
        while (recordsStringQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return recordsStringQueue.take();
    }
}
