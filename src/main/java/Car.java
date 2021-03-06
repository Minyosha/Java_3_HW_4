import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Car implements Runnable {
    private static int CARS_COUNT;
    public static final CyclicBarrier prepareBarrier = new CyclicBarrier(MainClass.CARS_COUNT);
    public static final CountDownLatch waitForRace = new CountDownLatch(MainClass.CARS_COUNT);
    public static final CountDownLatch waitForFinish = new CountDownLatch(MainClass.CARS_COUNT);
    private static boolean isWinnerFinished = false;
    public static String winnerName;
    public static final ReentrantLock winnerLock = new ReentrantLock();
    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            waitForRace.countDown();
            prepareBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        waitForFinish.countDown();
        winnerFinished(this.getName());

    }

    public boolean winnerFinished(String name) {
        if (!isWinnerFinished) {
            try {
                winnerLock.lock();
                isWinnerFinished = true;
                System.out.println(name + " - WIN");
            } finally {
                winnerName = name;
                winnerLock.unlock();
            }
            return true;
        } else {
            return false;
        }
    }
}