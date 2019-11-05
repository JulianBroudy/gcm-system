package com.broudy.gcm.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to DataBaseSource class!!!!!
 * <p>
 * Created on the 2nd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class DataBaseSource {

  private static Logger logger = LogManager.getLogger(DataBaseSource.class);

  private static DataBaseSource onlyInstance = new DataBaseSource();
  private String user, password, schema;

  private DataBaseSource() {
    loadDriver();
  }

  public static DataBaseSource getInstance() {
    return onlyInstance;
  }

  /**
   * Loads driver - 1 time thing.
   */
  private void loadDriver() {
    try {
      logger.trace("Loading driver class...");
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (Exception e) {
      logger.error(e);
    }
    logger.trace("Driver class successfully loaded");
  }

  /**
   * Establishes a new connection with currently set credentials.
   * <p>
   * Connection must be closed after querying it.
   */
  public Connection getConnection() throws SQLException {
    try {
      Objects.requireNonNull(schema, "Schema is not set yet!");
      Objects.requireNonNull(user, "User is not set yet!");
      Objects.requireNonNull(password, "Password is not set yet!");
    } catch (NullPointerException npE) {
      logger.error(npE);
    }
    Connection connection;
    logger.trace("Establishing connection...");
    connection = DriverManager
        .getConnection("jdbc:mysql://localhost:3306/" + schema + "?serverTimezone=Asia/Jerusalem",
            user, password);
    return logger.traceExit(connection);
  }

  public void setUser(String user) {
    logger.traceEntry(user);
    this.user = user;
    logger.traceExit(user);
  }

  public void setPassword(String password) {
    logger.traceEntry(password);
    this.password = password;
    logger.traceExit(password);
  }

  public void setSchema(String schema) {
    logger.traceEntry(password);
    this.schema = schema;
    logger.traceExit(password);
  }
}
