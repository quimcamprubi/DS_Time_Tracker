import java.time.Duration;

/*
Class used to round the Duration to seconds. The default Duration.toSeconds() simply truncates the Duration, so that a duration
of 15.99 will be parsed to 15 seconds, instead of the 16 seconds we believe it should be parsed to. To solve this, we implemented
a roundDuration utility. This class will probably contain more general utility methods if we create more in future deliveries.
*/
public class Utils {
  public static int roundDuration(Duration duration) {
    int millis = (int) duration.toMillis();
    return (millis + 500) / 1000;
  }
}