package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.dtos.PurchaseDTO;
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
public class PurchasesDAON extends AbsDAO<PurchaseDTO> {

  private static Logger logger = LogManager.getLogger(PurchasesDAON.class);


  @Override
  protected ServersResponse<PurchaseDTO> processQuery(Connection connection,
      ClientsInquiry<PurchaseDTO> clientsInquiry) throws SQLException {

    PurchaseDTO purchaseRequest = clientsInquiry.getTheDTO();
    PreparedStatement statement = prepareStatement(connection, clientsInquiry);
    ServersResponse<PurchaseDTO> serversResponse = ServersResponse.of(PurchaseDTO.class);

    switch (clientsInquiry.getInquiry()) {
      case PURCHASE_FETCH_ALL_ACTIVE: {
        statement.setString(1, purchaseRequest.getCustomerEmail());
        serversResponse.setListOfDTOs(getAll(connection, statement));
        serversResponse.setResponse(Response.PURCHASE_FETCHED_ALL_ACTIVE);
        break;
      }
      case PURCHASE_EXTEND_SUBSCRIPTION: {
        statement.setInt(1, purchaseRequest.getID());
        update(statement);
        statement = prepareStatement(connection, Inquiry.PURCHASE_FETCH_ONE);
        statement.setInt(1, purchaseRequest.getID());
        serversResponse.setTheDTO(getOne(connection, statement));
        serversResponse.setResponse(Response.PURCHASE_SUBSCRIPTION_EXTENDED);
        break;
      }
      case PURCHASE_NEW_SUBSCRIPTION: {
        statement.setString(1, purchaseRequest.getCustomerEmail());
        statement.setInt(2, purchaseRequest.getCityID());
        ResultSet resultSet = insert(statement);
        boolean next = resultSet.next();
        if (next) {
          statement = prepareStatement(connection, Inquiry.PURCHASE_FETCH_ONE);
          statement.setInt(1, resultSet.getInt(1));
          serversResponse.setTheDTO(getOne(connection, statement));
          serversResponse.setResponse(Response.PURCHASE_SUCCESSFUL_NEW_SUBSCRIPTION);
          /*
          purchaseRequest.setID(resultSet.getInt(1));
          clientsInquiry.setInquiry(Inquiry.PURCHASE_FETCH_ONE);
          serversResponse = processQuery(connection, clientsInquiry);
          serversResponse.setResponse(Response.PURCHASE_SUCCESSFUL_NEW_SUBSCRIPTION);*/
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
  protected PurchaseDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    PurchaseDTO purchase = new PurchaseDTO();
    purchase.setID(resultSet.getInt("PurchaseID"));
    purchase.setCustomerEmail(resultSet.getString("CustomerEmail"));
    purchase.setCityID(resultSet.getInt("CityID"));
    purchase.setPurchaseDate(resultSet.getDate("PurchaseDate"));
    purchase.setExpirationDate(resultSet.getDate("ExpirationDate"));
    purchase.setExtended(resultSet.getBoolean("WasExtended"));
    return purchase;
  }
}
