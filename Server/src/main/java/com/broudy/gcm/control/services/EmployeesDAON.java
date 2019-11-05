package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.dtos.EmployeeDTO;
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
public class EmployeesDAON extends AbsDAO<EmployeeDTO> {

  private static Logger logger = LogManager.getLogger(EmployeesDAON.class);


  @Override
  protected ServersResponse<EmployeeDTO> processQuery(Connection connection,
      ClientsInquiry<EmployeeDTO> clientsInquiry) throws SQLException {

    EmployeeDTO employeeRequest = clientsInquiry.getTheDTO();
    ServersResponse<EmployeeDTO> serversResponse = ServersResponse.of(EmployeeDTO.class);

    switch (clientsInquiry.getInquiry()) {

      case USER_EMPLOYEE_ID: {
        PreparedStatement statement = prepareStatement(connection, clientsInquiry);
        statement.setString(1, employeeRequest.getEmail());
        clientsInquiry.getTheDTO().setEmployeeID(getInteger(statement));
        serversResponse.setTheDTO(clientsInquiry.getTheDTO());
        serversResponse.setResponse(Response.USER_EMPLOYEE_ID_FETCHED);
      }
      break;

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
  protected EmployeeDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    EmployeeDTO anEmployee = new EmployeeDTO();
    anEmployee.setEmployeeID(resultSet.getInt("EmployeeID"));
    return anEmployee;
  }
}
