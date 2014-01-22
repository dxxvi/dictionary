package home;

import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
@Startup
public class SingletonEJB {
    private final Logger logger = LoggerFactory.getLogger(SingletonEJB.class);

    @Resource(name = "H2Database", type = javax.sql.DataSource.class)
    private DataSource h2DataSource;

    @PostConstruct
    void init() {
        Connection connection = null;
        try {
            connection = h2DataSource.getConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("/tomee.h2.sql")));
            RunScript.execute(connection, bufferedReader);
        }
        catch (SQLException ex) {
            logger.error("Error in working with H2 connection", ex);
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException ex) {
                logger.error("Error in closing H2 connection", ex);
            }
        }
    }
}
