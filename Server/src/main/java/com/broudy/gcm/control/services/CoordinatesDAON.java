package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.dtos.CoordinatesDTO;
import com.broudy.utils.TextColors;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to UsersDAON class!!!!!
 * <p>
 * Created on the 19th of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CoordinatesDAON extends AbsDAO<CoordinatesDTO> {

  private static Logger logger = LogManager.getLogger(CoordinatesDAON.class);


  @Override
  protected ServersResponse<CoordinatesDTO> processQuery(Connection connection,
      ClientsInquiry<CoordinatesDTO> clientsInquiry) throws SQLException {

    CoordinatesDTO coordinatesRequest = clientsInquiry.getTheDTO();
    PreparedStatement statement = prepareStatement(connection, clientsInquiry);
    ServersResponse<CoordinatesDTO> serversResponse = ServersResponse.of(CoordinatesDTO.class);

    switch (clientsInquiry.getInquiry()) {
      case COORDINATES_FETCH_ONE: {
        statement.setInt(1, coordinatesRequest.getID());
        serversResponse.setTheDTO(getOne(statement));
        serversResponse.setResponse(Response.COORDINATES_FETCHED_ONE);
        break;
      }
      case COORDINATES_SAVE: {
        //   "UPDATE `gcm-db`.`coordinates` SET `Latitude` = ?, `Longitude` = ? WHERE (`CoordinatesID` = ?);";
        statement.setDouble(1, coordinatesRequest.getXCoordinate());
        statement.setDouble(2, coordinatesRequest.getYCoordinate());
        statement.setInt(3, coordinatesRequest.getID());
        if (update(statement) != 1) {
          throw new SQLException("Could not update coordinates!");
        }
        serversResponse.setTheDTO(coordinatesRequest);
        serversResponse.setResponse(Response.COORDINATES_SAVED);
        break;
      }
      case COORDINATES_SAVE_AND_LOCK: {
        // "INSERT INTO `gcm-db`.`coordinates` (`Latitude`, `Longitude`) VALUES (?, ?);";
        statement.setDouble(1, coordinatesRequest.getXCoordinate());
        statement.setDouble(2, coordinatesRequest.getYCoordinate());
        ResultSet resultSet = insert(statement);
        if (resultSet.next()) {
          coordinatesRequest.setID(resultSet.getInt(1));
        } else {
          throw new SQLException("Could not insert coordinates!");
        }
        serversResponse.setTheDTO(coordinatesRequest);
        serversResponse.setResponse(Response.COORDINATES_SAVED_AND_LOCKED);
        break;
      }

      default: {
        logger.trace(
            "Request " + clientsInquiry.getInquiry() + " was" + TextColors.RED.colorThis(" NOT ")
                + "processed!");
      }
    }
    return serversResponse;
  }

  /**
   * This method extracts all columns from database to the DTO.
   *
   * @param resultSet is the result set that came from the SQL query.
   *
   * @return the DTO.
   */
  @Override
  protected CoordinatesDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    CoordinatesDTO coordinates = new CoordinatesDTO();
    coordinates.setID(resultSet.getInt("CoordinatesID"));
    coordinates.setXCoordinate(resultSet.getDouble("Latitude"));
    coordinates.setYCoordinate(resultSet.getDouble("Longitude"));
    return coordinates;
  }
}
