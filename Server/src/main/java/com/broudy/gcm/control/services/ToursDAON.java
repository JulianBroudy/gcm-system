package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.TourDTO;
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
public class ToursDAON extends AbsDAO<TourDTO> {

  private static Logger logger = LogManager.getLogger(ToursDAON.class);


  @Override
  protected ServersResponse<TourDTO> processQuery(Connection connection,
      ClientsInquiry<TourDTO> clientsInquiry) throws SQLException {

    TourDTO tourRequest = clientsInquiry.getTheDTO();
    PreparedStatement statement = prepareStatement(connection, clientsInquiry);
    ServersResponse<TourDTO> serversResponse = ServersResponse.of(TourDTO.class);

    switch (clientsInquiry.getInquiry()) {

      case EMPLOYEE_TOUR_OF_MAP_FETCH_ALL:
      case TOUR_OF_MAP_FETCH_ALL: {
        statement.setInt(1, clientsInquiry.getExtraParameterAt(1));
        serversResponse.setListOfDTOs(getAll(connection, statement));
        serversResponse.setResponse(Response.TOUR_OF_MAP_FETCHED_ALL);
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
  protected TourDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    TourDTO aTour = new TourDTO();
    aTour.setID(resultSet.getInt("TourID"));
    aTour.setMapID(resultSet.getInt("MapID"));
    aTour.setState(State.valueOf(resultSet.getString("State")));
    aTour.setPreviousID(resultSet.getInt("PreviousTourID"));
    PreparedStatement preparedStatement = prepareStatement(connection,
        Inquiry.TOUR_FETCH_SITES_LIST);
    // PreparedStatement preparedStatement = connection
    //     .prepareStatement(Inquiry.TOUR_FETCH_SITES_LIST.getQuery());
    preparedStatement.setInt(1, aTour.getID());
    aTour.setSites(getListOfIntegers(preparedStatement));
    return aTour;
  }
}
