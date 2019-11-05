package com.broudy.gcm.control.services;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.UserClassification;
import com.broudy.gcm.entity.dtos.EmployeeDTO;
import com.broudy.gcm.entity.dtos.UserDTO;
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
public class UsersDAON extends AbsDAO<UserDTO> {

  private static Logger logger = LogManager.getLogger(UsersDAON.class);


  @Override
  protected ServersResponse<UserDTO> processQuery(Connection connection,
      ClientsInquiry<UserDTO> clientsInquiry) throws SQLException {

    UserDTO userRequest = clientsInquiry.getTheDTO();
    PreparedStatement statement = prepareStatement(connection, clientsInquiry);
    ServersResponse<UserDTO> serversResponse = ServersResponse.of(UserDTO.class);

    switch (clientsInquiry.getInquiry()) {

      case USER_ATTEMPT_SIGN_IN: {
        statement.setString(1, userRequest.getEmail());
        statement.setString(2, userRequest.getPassword());
        UserDTO fetchedUser = getOne(connection, statement);
        if (fetchedUser != null) {
          if (fetchedUser.isOnline()) {
            serversResponse.setTheDTO(fetchedUser);
            serversResponse.setResponse(Response.USER_IS_ALREADY_ONLINE);
          } else {
            clientsInquiry.setTheDTO(fetchedUser);
            clientsInquiry.setInquiry(Inquiry.USER_ALTER_ONLINE_STATUS);
            serversResponse = processQuery(connection, clientsInquiry);
            fetchedUser = serversResponse.getTheDTO();
            if (serversResponse.getResponse() == Response.USER_SIGN_IN_SUCCESSFUL) {
              if (fetchedUser.getClassification() != UserClassification.CUSTOMER) {
                /* Get employee ID */
                final ClientsInquiry<EmployeeDTO> employeeInquiry = ClientsInquiry
                    .of(EmployeeDTO.class);
                final EmployeeDTO employee = new EmployeeDTO();
                fetchedUser.deepCopyTo(employee);
                employeeInquiry.setTheDTO(employee);
                employeeInquiry.setInquiry(Inquiry.USER_EMPLOYEE_ID);
                final EmployeeDTO fetchedEmployee = new EmployeesDAON()
                    .processInquiry(employeeInquiry).getTheDTO();
                serversResponse.setTheDTO(fetchedEmployee);
              }
              serversResponse.setResponse(Response.USER_SIGN_IN);
            }
            /* NOT!! USER_SIGN_IN_UNSUCCESSFUL */
          }
        } else {
          /* Authentication failed */
          serversResponse.setTheDTO(userRequest);
          serversResponse.setResponse(Response.USER_AUTHENTICATION_FAILED);
        }
        break;
      }
      case USER_EXIT_SYSTEM:
      case USER_ALTER_ONLINE_STATUS: {
        boolean setTo = !userRequest.isOnline();
        statement.setBoolean(1, setTo);
        statement.setString(2, userRequest.getEmail());
        if (update(statement) == 1) {
          userRequest.setOnline(setTo);
          serversResponse.setTheDTO(userRequest);
          serversResponse.setResponse(
              clientsInquiry.getInquiry() == Inquiry.USER_EXIT_SYSTEM ? Response.EXIT_SYSTEM
                  : setTo ? Response.USER_SIGN_IN_SUCCESSFUL : Response.USER_SIGN_OUT_SUCCESSFUL);
        }
        break;
      }

      case USER_SIGN_UP: {
        statement.setString(1, userRequest.getEmail());
        statement.setString(2, userRequest.getPassword());
        statement.setString(3, userRequest.getFirstName());
        statement.setString(4, userRequest.getLastName());
        statement.setString(5, userRequest.getUsername());
        statement.setString(6, userRequest.getPhoneNumber());
        insert(statement);
        serversResponse.setTheDTO(clientsInquiry.getTheDTO());
        serversResponse.setResponse(Response.USER_SIGN_UP_SUCCESSFUL);
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
  protected UserDTO extractFromResultSet(Connection connection, ResultSet resultSet)
      throws SQLException {
    UserDTO aUser = new UserDTO();
    aUser.setEmail(resultSet.getString("Email"));
    aUser.setPassword(resultSet.getString("Password"));
    aUser.setFirstName(resultSet.getString("FirstName"));
    aUser.setLastName(resultSet.getString("LastName"));
    aUser.setPhoneNumber(resultSet.getString("PhoneNumber"));
    aUser.setUsername(resultSet.getString("Username"));
    aUser.setOnline(resultSet.getBoolean("Online"));
    aUser.setClassification(UserClassification.valueOf(resultSet.getString("UserClassification")));
    return aUser;
  }
}
