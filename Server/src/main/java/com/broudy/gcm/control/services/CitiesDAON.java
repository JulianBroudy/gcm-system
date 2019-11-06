package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.CityDTO;
import com.broudy.utils.TextColors;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to UsersDAON class!!!!!
 * <p>
 * Created on the 19th of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CitiesDAON extends AbsDAO<CityDTO> {

  private static Logger logger = LogManager.getLogger(CitiesDAON.class);


  @Override
  protected ServersResponse<CityDTO> processQuery(Connection connection,
      ClientsInquiry<CityDTO> clientsInquiry) throws SQLException {

    CityDTO cityRequest = clientsInquiry.getTheDTO();
    PreparedStatement statement = prepareStatement(connection, clientsInquiry);
    ServersResponse<CityDTO> serversResponse = ServersResponse.of(CityDTO.class);

    switch (clientsInquiry.getInquiry()) {

      case CITY_FETCH_ONE: {
        statement.setInt(1, cityRequest.getID());

        CityDTO fetchedCity = getOne(connection, statement);
        if (fetchedCity != null) {
          serversResponse.setTheDTO(fetchedCity);
          serversResponse.setResponse(Response.CITY_FETCHED_ONE);
        }
        break;
      }
      case CITY_FETCH_ALL:
      case EMPLOYEE_CITY_FETCH_ALL: {
        serversResponse.setListOfDTOs(getAll(connection, statement));
        serversResponse.setResponse(
            clientsInquiry.getInquiry() == Inquiry.CITY_FETCH_ALL ? Response.CITY_FETCHED_ALL
                : Response.EMPLOYEE_CITY_FETCHED_ALL);
        break;
      }
      case CITY_SAVE: {
        statement.setString(1, cityRequest.getName());
        statement.setString(2, cityRequest.getDescription());
        statement.setInt(3, cityRequest.getID());
        update(statement);
        serversResponse.setTheDTO(cityRequest);
        serversResponse.setResponse(Response.CITY_SAVED);
        break;
      }
      case CITY_SAVE_AND_LOCK: {
        statement.setString(1, cityRequest.getName());
        statement.setDouble(2, cityRequest.getCollectionVersion());
        statement.setInt(3, cityRequest.getID());
        statement.setInt(4, cityRequest.getNumberOfMaps());
        statement.setString(5, cityRequest.getDescription());
        ResultSet resultSet = insert(statement);
        boolean next = resultSet.next();
        if (next) {

          cityRequest.setPreviousID(cityRequest.getID());
          cityRequest.setID(resultSet.getInt(1));
          cityRequest.setState(State.LOCKED);

          statement = prepareStatement(connection, Inquiry.CITY_UPDATE_SITES_COUNT);
          statement.setInt(1, cityRequest.getID());
          statement.setInt(2, cityRequest.getID());
          execute(statement);

          serversResponse.setTheDTO(cityRequest);
          serversResponse.setResponse(Response.CITY_SAVED_AND_LOCKED);

          // cityRequest.setRequest(ClientsInquiries.CITY_FETCH_ALL);
          // answer = processQuery(cityRequest);
        }
        break;
      }
      case CITY_GET_SITES_COUNT:
      case CITY_GET_MAPS_COUNT: {
        statement.setInt(1, cityRequest.getID());
        serversResponse.setExtraParameters(getInteger(statement));
        serversResponse.setResponse(Response.CITY_MAPS_COUNT_FETCHED);
        break;
      }
      case CITY_REQUEST_APPROVAL:
      case CITY_CANCEL_APPROVAL_REQUEST: {
        statement.setInt(1, cityRequest.getID());
        if (update(statement) != -1) {
          clientsInquiry.setInquiry(Inquiry.EMPLOYEE_WORKSPACE_FETCH_ALL);
          serversResponse = processQuery(connection, clientsInquiry);
        }
        break;
      }
      case CITY_REJECT_APPROVAL_REQUEST: {
        statement.setInt(1, cityRequest.getID());
        if (update(statement) != -1) {
          // cityRequest.setState(State.REJECTED);
          serversResponse.setTheDTO(cityRequest);
          serversResponse.setResponse(Response.CITY_REJECTED_APPROVAL_REQUEST);
        }
        break;
      }
      case CITY_APPROVE_APPROVAL_REQUEST: {
        CallableStatement callableStatement = prepareCall(connection, clientsInquiry.getInquiry());
        callableStatement.setInt(1, cityRequest.getID());
        callableStatement.setInt(2, cityRequest.getPreviousID());
        ResultSet resultSet = execute(callableStatement);
        serversResponse.setTheDTO(cityRequest);
        serversResponse.setResponse(Response.CITY_APPROVED_APPROVAL_REQUEST);
        break;
      }
      case CITY_DELETE_ONE: {
        statement.setInt(1, cityRequest.getID());
        if (delete(statement)) {
          // clientsInquiry.setInquiry(Inquiry.CITY_UNLOCK_FROM_EMPLOYEE);
          // serversResponse = processQuery(connection, clientsInquiry);
          clientsInquiry.setInquiry(Inquiry.EMPLOYEE_WORKSPACE_FETCH_ALL);
          serversResponse = processQuery(connection, clientsInquiry);
        }
        break;
      }
      case CITY_LOCK_TO_EMPLOYEE: {
        statement.setInt(1, clientsInquiry.getExtraParameterAt(1));
        statement.setInt(2, cityRequest.getID());
        insert(statement);
        break;
      }
      case CITY_UNLOCK_FROM_EMPLOYEE: {
        statement.setInt(1, cityRequest.getID());
        delete(statement);
        serversResponse.setTheDTO(cityRequest);
        serversResponse.setResponse(Response.CITY_UNLOCKED_FROM_EMPLOYEE);
        break;
      }

      case EMPLOYEE_WORKSPACE_FETCH_ALL: {
        statement.setInt(1, clientsInquiry.getExtraParameterAt(1));
        List<Integer> listOfCityIDs = getListOfIntegers(statement);
        clientsInquiry.setInquiry(Inquiry.CITY_FETCH_ONE);

        List<CityDTO> citiesOfEmployee = new ArrayList<>();
        for (Integer cityID : listOfCityIDs) {
          clientsInquiry.getTheDTO().setID(cityID);
          citiesOfEmployee.add(processQuery(connection, clientsInquiry).getTheDTO());
        }
        serversResponse.setListOfDTOs(citiesOfEmployee);
        serversResponse.setResponse(Response.EMPLOYEE_WORKSPACE_FETCHED_ALL);
        break;
      }

      case EMPLOYEE_CITY_APPROVAL_REQUEST_FETCH_ALL: {
        serversResponse.setListOfDTOs(getAll(connection, statement));
        serversResponse.setResponse(Response.EMPLOYEE_CITY_APPROVAL_REQUEST_FETCHED_ALL);
        break;
      }
      case CITY_REJECTED_TO_LOCKED: {
        statement.setInt(1, cityRequest.getID());
        execute(statement);
        cityRequest.setState(State.LOCKED);
        serversResponse.setTheDTO(cityRequest);
        serversResponse.setResponse(Response.CITY_READY_FOR_EDITING);
        break;
      }

      case SPECIAL_GET_EMPLOYEE_EMAIL_BY_CITY:{
        statement.setInt(1,cityRequest.getID());
        try (ResultSet resultSet = statement.executeQuery()) {
          if (resultSet.next()) {
            serversResponse.setExtraParameters(resultSet.getInt(1), resultSet.getString(2));
          }
        } catch (SQLException sqlException) {
          logger.error(sqlException);
        }
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
  protected CityDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    CityDTO aCity = new CityDTO();
    aCity.setID(resultSet.getInt("CityID"));
    if (connection != null) {
      PreparedStatement preparedStatement = prepareStatement(connection,
          Inquiry.CITY_GET_SITES_NAMES);
      preparedStatement.setInt(1, aCity.getID());
      aCity.setListOfSites(getListOfStrings(preparedStatement));
    }

    aCity.setName(resultSet.getString("CityName"));
    aCity.setCollectionVersion(resultSet.getBigDecimal("CollectionVersion").doubleValue());
    aCity.setState(State.valueOf(resultSet.getString("State")));
    aCity.setPreviousID(resultSet.getInt("PreviousCityID"));
    aCity.setReleaseDate(resultSet.getDate("ReleaseDate"));
    aCity.setNumberOfSites(resultSet.getInt("NumberOfSites"));
    aCity.setNumberOfMaps(resultSet.getInt("NumberOfMaps"));
    aCity.setNumberOfDownloads(resultSet.getInt("NumberOfDownloads"));
    aCity.setDescription(resultSet.getString("Description"));

    return aCity;
  }
}
