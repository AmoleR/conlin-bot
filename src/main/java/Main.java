import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import io.github.cdimascio.dotenv.*;
import java.time.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime nextRun = now.withHour(8).withMinute(0).withSecond(0);
        if (now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initalDelay = duration.getSeconds();

        Dotenv dotenv = Dotenv.load();

        String token = dotenv.get("TOKEN");

        JDA api = JDABuilder.createDefault(token).build();

        Listener listener = new Listener();

        listener.update();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                listener.update();
            } catch (Exception e) {
            }
        }, initalDelay, TimeUnit.MINUTES.toSeconds(15), TimeUnit.SECONDS);

        api.addEventListener(listener);

        api.getPresence().setActivity(Activity.playing("The limit of the function never depends on the value of the function at that point."));
    }
}