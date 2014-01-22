package home;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMapperTest {
    private final Logger logger = LoggerFactory.getLogger(ObjectMapperTest.class);

    @Rule
    public MyTestWatcher myTestWatcher = new MyTestWatcher();

    @Test
    @TestDataPrepare(reviewers = {"reviewer 1", "reviewer 2"})
    public void main() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("E MMM d xxxx K:m:ss a");
        logger.debug(dtf.print(new DateTime()));
    }
}
