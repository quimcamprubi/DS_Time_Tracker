import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Utils {
    public static LocalDateTime formatTime(LocalDateTime time) {
        if (time != null) return time.truncatedTo(ChronoUnit.SECONDS);
        return null;
    }

    public static int roundDuration(Duration duration) {
        int millis = (int) duration.toMillis();
        return (millis + 500)/1000;
    }
}