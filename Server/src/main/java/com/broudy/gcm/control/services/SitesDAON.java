package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.CoordinatesDTO;
import com.broudy.gcm.entity.dtos.SiteCategory;
import com.broudy.gcm.entity.dtos.SiteDTO;
import com.broudy.utils.TextColors;
import java.sql.CallableStatement;
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
public class SitesDAON extends AbsDAO<SiteDTO> {

  private static Logger logger = LogManager.getLogger(SitesDAON.class);


  @Override
  protected ServersResponse<SiteDTO> processQuery(Connection connection,
      ClientsInquiry<SiteDTO> clientsInquiry) throws SQLException {

    SiteDTO siteRequest = clientsInquiry.getTheDTO();
    PreparedStatement statement = prepareStatement(connection, clientsInquiry);
    ServersResponse<SiteDTO> serversResponse = ServersResponse.of(SiteDTO.class);

    switch (clientsInquiry.getInquiry()) {
      case SITE_OF_CITY_FETCH_ALL: {
        statement.setInt(1, siteRequest.getCityID());
        serversResponse.setListOfDTOs(getAll(statement));
        serversResponse.setResponse(Response.SITE_OF_CITY_FETCHED_ALL);
        break;
      }
      case SITE_SAVE: {
        // "UPDATE `gcm-db`.`sites` SET `SiteName` = ?, `CityID` = ?, `Description` = ?, "
        // + "`RecommendedVisitDuration` = ?, `Category` = ?, `Accessible` = ?, WHERE (`SiteID` = ?);";
        ClientsInquiry<CoordinatesDTO> coordinatesInquiry = ClientsInquiry.of(CoordinatesDTO.class);
        coordinatesInquiry.setTheDTO(siteRequest.getCoordinatesDTO());
        coordinatesInquiry.setInquiry(Inquiry.COORDINATES_SAVE);
        siteRequest.setCoordinatesDTO(
            new CoordinatesDAON().processInquiry(coordinatesInquiry).getTheDTO());
        statement.setString(1, siteRequest.getName());
        statement.setInt(2, siteRequest.getCityID());
        statement.setString(3, siteRequest.getDescription());
        statement.setString(4, siteRequest.getRecommendedVisitDuration());
        statement.setString(5, siteRequest.getCategory().getName());
        statement.setBoolean(6, siteRequest.isAccessible());
        statement.setInt(7, siteRequest.getID());
        update(statement);
        // if (update(statement) != 1) {
        //   throw new SQLException("Could not update site!");
        // }
        siteRequest.setState(State.LOCKED);
        serversResponse.setTheDTO(siteRequest);
        serversResponse.setResponse(Response.SITE_SAVED);
        break;
      }
      case SITE_SAVE_AND_LOCK: {
        ClientsInquiry<CoordinatesDTO> coordinatesInquiry = ClientsInquiry.of(CoordinatesDTO.class);
        coordinatesInquiry.setTheDTO(siteRequest.getCoordinatesDTO());
        coordinatesInquiry.setInquiry(Inquiry.COORDINATES_SAVE_AND_LOCK);
        siteRequest.setCoordinatesDTO(
            new CoordinatesDAON().processInquiry(coordinatesInquiry).getTheDTO());
        CallableStatement callableStatement = prepareCall(connection, clientsInquiry.getInquiry());
        callableStatement.setInt(1, clientsInquiry.getExtraParameterAt(1));
        callableStatement.setString(2, siteRequest.getName());
        callableStatement.setInt(3, siteRequest.getCityID());
        callableStatement.setString(4, siteRequest.getDescription());
        callableStatement.setString(5, siteRequest.getRecommendedVisitDuration());
        callableStatement.setInt(6, siteRequest.getCoordinatesDTO().getID());
        callableStatement.setString(7, siteRequest.getCategory().getName());
        callableStatement.setBoolean(8, siteRequest.isAccessible());
        ResultSet resultSet = execute(callableStatement);
        if (resultSet.next()) {
          siteRequest.setPreviousID(-1);
          siteRequest.setID(resultSet.getInt(1));
          siteRequest.setState(State.LOCKED);

        /*  statement = prepareStatement(connection, Inquiry.CITY_UPDATE_SITES_COUNT);
          statement.setInt(1, siteRequest.getCityID());
          statement.setInt(2, siteRequest.getCityID());
          execute(statement);*/

          serversResponse.setTheDTO(siteRequest);
          serversResponse.setResponse(Response.SITE_SAVED_AND_LOCKED);
        }
        break;
      }
      case SITE_DELETE: {
        statement.setInt(1, siteRequest.getID());
        delete(statement);

        // ClientsInquiry<CityDTO> citySitesCountUpdate = ClientsInquiry.of(CityDTO.class);
        // citySitesCountUpdate.setInquiry(Inquiry.CITY_UPDATE_SITES_COUNT);
        //
        // new CitiesDAON().processInquiry()

        statement = prepareStatement(connection, Inquiry.CITY_UPDATE_SITES_COUNT);
        statement.setInt(1, siteRequest.getCityID());
        statement.setInt(2, siteRequest.getCityID());
        execute(statement);

        serversResponse.setTheDTO(siteRequest);
        serversResponse.setResponse(Response.SITE_DELETED);
        break;
      }
      case SITE_OF_CITY_DELETE_ALL: {
        statement.setInt(1, siteRequest.getCityID());
        delete(statement);
        serversResponse.setTheDTO(siteRequest);
        serversResponse.setResponse(Response.SITE_OF_CITY_DELETED);
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
  protected SiteDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    SiteDTO aSite = new SiteDTO();
    aSite.setID(resultSet.getInt("SiteID"));
    aSite.setName(resultSet.getString("SiteName"));
    aSite.setCityID(resultSet.getInt("CityID"));
    aSite.setDescription(resultSet.getString("Description"));
    aSite.setRecommendedVisitDuration(resultSet.getString("RecommendedVisitDuration"));
    SiteCategory siteCategory = new SiteCategory();
    aSite.setCategory(siteCategory);
    siteCategory.setName((resultSet.getString("Category")));
    aSite.setAccessible(resultSet.getBoolean("Accessible"));
    aSite.setState(State.valueOf(resultSet.getString("State")));
    aSite.setPreviousID(resultSet.getInt("PreviousSiteID"));

    CoordinatesDTO coordinatesRequest = new CoordinatesDTO();
    coordinatesRequest.setID(resultSet.getInt("CoordinatesID"));
    ClientsInquiry<CoordinatesDTO> coordinatesInquiry = ClientsInquiry.of(CoordinatesDTO.class);
    coordinatesInquiry.setTheDTO(coordinatesRequest);
    coordinatesInquiry.setInquiry(Inquiry.COORDINATES_FETCH_ONE);
    aSite.setCoordinatesDTO(new CoordinatesDAON().processInquiry(coordinatesInquiry).getTheDTO());

    return aSite;
  }
}
