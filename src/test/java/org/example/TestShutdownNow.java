package org.example;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.OracleContainer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestShutdownNow {

    private static final Logger LOG = LoggerFactory.getLogger(TestShutdownNow.class);

    @Test
    void testShutdownNowStatementV2() throws Exception {
        try (OracleContainer oc = new OracleContainer("gvenzl/oracle-xe:slim-faststart")) {
            oc.start();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try (Connection con = DriverManager.getConnection(oc.getJdbcUrl(),oc.getUsername(),oc.getPassword());
                     CallableStatement stmt = con.prepareCall("{call dbms_session.sleep(5)}")) {
                    con.setAutoCommit(false);
                    stmt.execute();
                } catch (SQLException e) {
                   throw new RuntimeException(e);
                }
            });
            TimeUnit.SECONDS.sleep(3);
            executor.shutdownNow();
        }
    }
}
