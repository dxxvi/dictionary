package home;

import org.h2.tools.RunScript;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyTestWatcher extends TestWatcher {
    private final Logger logger = LoggerFactory.getLogger(MyTestWatcher.class);

    // TestWatcher does nothing in this method
    @Override
    protected void succeeded(Description description) {
//        logger.debug(interpreteDescription(description) + " succeeded.");
    }

    // TestWatcher does nothing in this method
    @Override
    protected void failed(Throwable e, Description description) {
//        logger.debug(interpreteDescription(description) + " failed.");
    }

    // TestWatcher does nothing in this method
    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
//        logger.debug(interpreteDescription(description) + " skipped.");
    }

    // TestWatcher does nothing in this method
    @Override
    protected void starting(Description description) {
        if (description.getAnnotation(TestDataPrepare.class) != null) {
            Connection connection = getH2Connection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream("/FAC_TABLES.ddl.sql")));
                RunScript.execute(connection, bufferedReader);
                connection.commit();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            finally {
                try {
                    connection.close();
                }
                catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    // TestWatcher does nothing in this method
    @Override
    protected void finished(Description description) {
//        logger.debug(interpreteDescription(description) + " finished.");
    }

    private Connection getH2Connection() {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection("jdbc:h2:/dev/shm/myh2", "sa", "");
        }
        catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String interpreteDescription(Description description) {
        TestDataPrepare testDataPrepare = description.getAnnotation(TestDataPrepare.class);
        return "{ testClass: " + description.getTestClass().getName() + ", methodName: " +
                description.getMethodName() +
                (testDataPrepare != null ? ", value: " + testDataPrepare.value() : "") +
                " }";
    }
}
