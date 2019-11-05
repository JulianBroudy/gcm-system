package com.broudy.gcm.control.services;

import com.broudy.gcm.control.DataBaseSource;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.broudy.utils.TextColors;
import com.broudy.utils.TextWrapper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to AbsDAO class!!!!!
 * <p>
 * Created on the 19th of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public abstract class AbsDAO<DTO extends AbstractDTO> {

  private static final Logger logger = LogManager.getLogger(AbsDAO.class);

  /**
   * This is the only method that should be called for data retrieval/insertion/deletion/...
   * This method prepares a statement and processes the query.
   *
   * @param clientsInquiry the client's inquiry to be processed.
   *
   * @return a response to send to the client.
   */
  public final ServersResponse<DTO> processInquiry(
      ClientsInquiry<? extends AbstractDTO> clientsInquiry) {

    ServersResponse<DTO> serversResponse = null;
    /* Get a connection ready */
    try (Connection connection = DataBaseSource.getInstance().getConnection()) {
      serversResponse = processQuery(connection, (ClientsInquiry<DTO>) clientsInquiry);
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }
    return serversResponse;
  }

  protected abstract ServersResponse<DTO> processQuery(Connection connection,
      ClientsInquiry<DTO> clientsInquiry) throws SQLException;

  /**
   * This method extracts all columns from database to the DTO.
   *
   * @param resultSet is the result set that came from the SQL query.
   *
   * @return the DTO.
   */
  protected final DTO extractFromResultSet(ResultSet resultSet) throws SQLException {
    return extractFromResultSet(null, resultSet);
  }

  protected abstract DTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException;


  protected final <ST extends PreparedStatement> ST prepareStatement(Connection connection,
      ClientsInquiry clientsInquiry) {
    if (clientsInquiry.getInquiry().isCallable()) {
      return (ST) prepareCall(connection, clientsInquiry.getInquiry());
    } else
      /* Prepare Statement */ {
      return (ST) prepareStatement(connection, clientsInquiry.getInquiry());
    }
  }

  /*protected final<ST extends PreparedStatement> ST prepareStatement(Connection connection, Inquiry inquiry) {
    if(inquiry.isCallable()){
      CallableStatement callableStatement = null;
      try {
        callableStatement = connection.prepareCall(inquiry.getQuery());
      } catch (SQLException sqlException) {
        logger.error(sqlException);
      }
      return (ST) callableStatement;
    }else {
      PreparedStatement preparedStatement = null;
      *//* Prepare Statement *//*
      try {
        preparedStatement = connection.prepareStatement(inquiry.getQuery(), Statement.RETURN_GENERATED_KEYS);
      } catch (SQLException sqlException) {
        logger.error(sqlException);
      }
      return (ST) preparedStatement;
    }
  }*/

  protected final PreparedStatement prepareStatement(Connection connection, Inquiry inquiry) {
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection
          .prepareStatement(inquiry.getQuery(), Statement.RETURN_GENERATED_KEYS);
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }
    return preparedStatement;

  }

  protected final CallableStatement prepareCall(Connection connection, Inquiry inquiry) {
    CallableStatement callableStatement = null;
    try {
      callableStatement = connection.prepareCall(inquiry.getQuery());
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }
    return callableStatement;
  }

  /**
   * Each connection must be closed after each execution of a SQL query.
   * Also, each ResultSet must be closed. The way it happens here is by declaring them inside "()"
   * of the try clause.
   */
  public DTO getOne(PreparedStatement statement) {
    return getOne(null, statement);
  }

  /**
   * Each connection must be closed after each execution of a SQL query.
   * Also, each ResultSet must be closed. The way it happens here is by declaring them inside "()"
   * of the try clause.
   */
  public DTO getOne(Connection connection, PreparedStatement statement) {
    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));

    DTO theDTO = null;
    try (ResultSet resultSet = statement.executeQuery()) {
      if (resultSet.next()) {
        theDTO = (connection == null ? extractFromResultSet(resultSet)
            : extractFromResultSet(connection, resultSet));
      }
    } catch (SQLException sqlException) {
      logger.error(TextColors.RED.colorThis(sqlException));
    }
    return logAndExit(statement, theDTO);
  }


  /**
   * @return List of DTOs from database.
   */
  public List<DTO> getAll(PreparedStatement statement) {
    return getAll(null, statement);
  }

  /**
   * @return List of DTOs from database.
   */
  public List<DTO> getAll(Connection connection, PreparedStatement statement) {

    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));

    List<DTO> dtoList = null;
    try (ResultSet resultSet = statement.executeQuery()) {
      dtoList = new ArrayList<>();
      if (connection == null) {
        while (resultSet.next()) {
          DTO aDTO = extractFromResultSet(resultSet);
          dtoList.add(aDTO);
        }
      } else {
        while (resultSet.next()) {
          DTO aDTO = extractFromResultSet(connection, resultSet);
          dtoList.add(aDTO);
        }
      }
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }

    return logAndExit(statement, dtoList);
  }

  public ResultSet insert(PreparedStatement statement) throws SQLException {
    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));
    statement.executeUpdate();
    return logAndExit(statement, statement.getGeneratedKeys());
  }

  public int update(PreparedStatement statement) {
    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));
    int affectedRows = -1;
    try {
      affectedRows = statement.executeUpdate();
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }

    return logAndExit(statement, affectedRows);
  }

  public boolean delete(PreparedStatement statement) throws SQLException {
    return execute(statement);
  }

  public boolean execute(PreparedStatement statement) throws SQLException {
    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));

    statement.execute();

    return logAndExit(statement, true);
  }

  public ResultSet execute(CallableStatement statement) throws SQLException {
    // logger.traceEntry(
    //     TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));
    ResultSet resultSet = null;
    try {
      resultSet = statement.executeQuery();
    } catch (SQLException sqlException) {
      logger.error(TextColors.RED.colorThis(sqlException));
    }
    return logAndExit(statement, resultSet);
  }

  public Integer getInteger(PreparedStatement statement) {
    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));
    /* Indicating it's invalid */
    int count = -1;
    try (ResultSet resultSet = statement.executeQuery()) {
      if (resultSet.next()) {
        count = resultSet.getInt(1);
      }
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }

    return logAndExit(statement, count);
  }

  /**
   * @return List of integers from database.
   */
  public List<Integer> getListOfIntegers(PreparedStatement statement) {

    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));

    List<Integer> listOfIDs = null;
    try (ResultSet resultSet = statement.executeQuery()) {
      listOfIDs = new ArrayList<>();

      while (resultSet.next()) {
        int id = resultSet.getInt(1);
        listOfIDs.add(id);
      }
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }

    return logAndExit(statement, listOfIDs);
  }

  /**
   * @return List of Strings from database.
   */
  public List<String> getListOfStrings(PreparedStatement statement) {

    logger.traceEntry(
        TextWrapper.leftIndent(statement.toString().substring(43), 78, 135, TextColors.YELLOW));

    List<String> listOfStrings = null;
    try (ResultSet resultSet = statement.executeQuery()) {
      listOfStrings = new ArrayList<>();

      while (resultSet.next()) {
        String string = resultSet.getString(1);
        listOfStrings.add(string);
      }
    } catch (SQLException sqlException) {
      logger.error(sqlException);
    }

    return logAndExit(statement, listOfStrings);
  }

  /**
   * Convenience method for a nice looking console printing.
   *
   * @param whichStatement the statement which have been executed.
   * @param result the method's original return value.
   * @param <ORV> the original return value.
   *
   * @return result.
   */
  private <ORV> ORV logAndExit(PreparedStatement whichStatement, ORV result) {
    return logger.traceExit(TextWrapper
        .leftIndent(whichStatement.toString().substring(43), 78, 135, false, TextColors.CYAN)
        + TextWrapper.leftIndent(result, 78, 135, true, TextColors.PURPLE), result);
  }
}
