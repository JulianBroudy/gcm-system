package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.MapDTO;
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
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class MapsDAON extends AbsDAO<MapDTO> {

  private static Logger logger = LogManager.getLogger(MapsDAON.class);


  @Override
  protected ServersResponse<MapDTO> processQuery(Connection connection,
      ClientsInquiry<MapDTO> clientsInquiry) throws SQLException {

    MapDTO mapRequest = clientsInquiry.getTheDTO();
    PreparedStatement statement = prepareStatement(connection, clientsInquiry);
    ServersResponse<MapDTO> serversResponse = ServersResponse.of(MapDTO.class);

    switch (clientsInquiry.getInquiry()) {

      case MAP_FETCH_ONE: {
        statement.setInt(1, mapRequest.getID());
        MapDTO fetchedMap = getOne(connection, statement);
        if (fetchedMap != null) {
          serversResponse.setTheDTO(fetchedMap);
          serversResponse.setResponse(Response.CITY_FETCHED_ONE);
        }
        break;
      }

      case MAP_OF_CITY_FETCH_ALL:
      case EMPLOYEE_MAP_OF_CITY_FETCH_ALL: {
        statement.setInt(1, mapRequest.getCityID());
        serversResponse.setListOfDTOs(getAll(connection, statement));
        serversResponse.setResponse(clientsInquiry.getInquiry() == Inquiry.MAP_OF_CITY_FETCH_ALL
            ? Response.MAP_OF_CITY_FETCHED_ALL : Response.EMPLOYEE_MAP_OF_CITY_FETCHED_ALL);
        break;
      }

      case MAP_DETACHED_FETCH_ALL: {
        serversResponse.setListOfDTOs(getAll(connection, statement));
        serversResponse.setResponse(Response.MAP_DETACHED_FETCHED_ALL);
        break;
      }

      case MAP_FETCH_SITES_LIST: {
        statement.setInt(1, mapRequest.getID());
        serversResponse.setExtraParameters(getListOfIntegers(statement));
        serversResponse.setResponse(Response.MAP_FETCHED_SITES_LIST);
        break;
      }

      case MAP_ATTACH_MAPS_TO_CITY: {
        CallableStatement callableStatement = (CallableStatement) statement;
        int cityID = mapRequest.getCityID();
        callableStatement.setInt(1, cityID);
        for (MapDTO map : clientsInquiry.getListOfDTOs()) {
          callableStatement.setInt(2, map.getID());
          execute(callableStatement);
        }

        /*MapDTO mapWithCityID = new MapDTO();
        mapWithCityID.setCityID(cityID);
        clientsInquiry.setTheDTO(mapWithCityID);
        clientsInquiry.setInquiry(Inquiry.EMPLOYEE_MAP_OF_CITY_FETCH_ALL);
        serversResponse.setListOfDTOs((List<MapDTO>) processQuery(connection, clientsInquiry));*/
        serversResponse.setResponse(Response.MAP_ATTACHED_MAPS_TO_CITY);
        break;
      }

      case MAP_DETACH_MAPS_FROM_CITY: {
        CallableStatement callableStatement = connection
            .prepareCall(clientsInquiry.getInquiry().getQuery());
        for (MapDTO map : clientsInquiry.getListOfDTOs()) {
          callableStatement.setInt(1, map.getID());
          execute(callableStatement);
          map.setCityID(0);
        }/*
        clientsInquiry.setInquiry(Inquiry.MAP_DETACHED_FETCH_ALL);
        serversResponse.setListOfDTOs((List<MapDTO>) processQuery(connection, clientsInquiry));*/
        serversResponse.setResponse(Response.MAP_DETACHED_MAPS_FROM_CITY);
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
  protected MapDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    MapDTO aMap = new MapDTO();
    aMap.setID(resultSet.getInt("MapID"));
    aMap.setName(resultSet.getString("MapName"));
    aMap.setCityID(resultSet.getInt("CityID"));
    aMap.setVersion(resultSet.getBigDecimal("MapVersion").doubleValue());
    aMap.setState(State.valueOf(resultSet.getString("State")));
    aMap.setPreviousID(resultSet.getInt("PreviousMapID"));
    PreparedStatement preparedStatement = prepareStatement(connection,
        Inquiry.MAP_FETCH_SITES_LIST);
    // .prepareStatement(Inquiry.MAP_FETCH_SITES_LIST.getQuery());
    preparedStatement.setInt(1, aMap.getID());
    aMap.setSites(getListOfIntegers(preparedStatement));
    return aMap;
  }
}
