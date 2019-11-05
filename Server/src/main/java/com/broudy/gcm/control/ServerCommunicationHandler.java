package com.broudy.gcm.control;

import com.broudy.gcm.control.services.CitiesDAON;
import com.broudy.gcm.control.services.MapsDAON;
import com.broudy.gcm.control.services.PurchasesDAON;
import com.broudy.gcm.control.services.SitesDAON;
import com.broudy.gcm.control.services.ToursDAON;
import com.broudy.gcm.control.services.UsersDAON;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.ServersResponse.Response;
import com.broudy.gcm.entity.dtos.CityDTO;
import com.broudy.gcm.entity.dtos.EmployeeDTO;
import com.broudy.gcm.entity.dtos.UserDTO;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.broudy.utils.TextColors;
import com.broudy.utils.TextWrapper;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ocsf.server.ConnectionToClient;
import ocsf.server.OriginatorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// TODO:should be singleton.TODO provide a summary to ServerCommunicationHandler class!!!!!

/**
 * Handles all messages received by the server.
 * <p>
 * Directs each message to its corresponding Data Access Object.
 * <br>
 * Created on the 4th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see GCMServer
 */
public class ServerCommunicationHandler implements Observer {

  private static final Logger logger = LogManager.getLogger(ServerCommunicationHandler.class);
  private final static ConcurrentHashMap<ConnectionToClient, ConcurrentHashMap<DTOType, ConcurrentLinkedQueue<ClientsInquiry<? extends AbstractDTO>>>> connectionsToTypeToInquiries;

  static {
    connectionsToTypeToInquiries = new ConcurrentHashMap<>();
  }

  private GCMServer gcmServer;

  /**
   * Constructor.
   */
  public ServerCommunicationHandler() {
    logger.trace("instantiating ServerMessageHandler...");
    gcmServer = GCMServer.getInstance();
    gcmServer.addObserver(this);
  }

  /**
   * This is the executed method when GCMServer notifies observers of changes.
   *
   * @param o is the observable (GCMServer).
   * @param originatorMessage is the received message.
   */
  @Override
  public void update(Observable o, Object originatorMessage) {

    ConnectionToClient connectionToClient = ((OriginatorMessage) originatorMessage).getOriginator();
    Object message = ((OriginatorMessage) originatorMessage).getMessage();
    // If this is an internal message, no handling needed, print.
    if (connectionToClient == null && !(message instanceof ClientsInquiry)) {
      logger.trace(((String) message).substring(4));
    } else {

      // Not an internal message, process request, print.

      ClientsInquiry clientsInquiry = (ClientsInquiry) message;
      Object answer;

      logger.trace(TextColors.YELLOW.colorThis(clientsInquiry.getInquiry())
          + " inquiry received, processing..");

      // ClientRequestHandlingService clientRequestHandlingService = new ClientRequestHandlingService(
      //     connectionToClient, clientsInquiry);
      // clientRequestHandlingService.start();

      ClientInquiriesDelegator clientInquiriesDelegator = new ClientInquiriesDelegator(
          clientsInquiry, connectionToClient);
      new Thread(clientInquiriesDelegator).start();

    }
  }

  private synchronized void send(ServersResponse<? extends AbstractDTO> answer,
      ConnectionToClient client) {
    try {
      client.sendToClient(answer);
      logger.trace("Response has has been successfully sent to: " + client);
      logger.trace(TextWrapper.leftIndent(answer.getResponse(), 72, 135, false, TextColors.GREEN),
          answer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  class ClientInquiriesDelegator implements Runnable {


    private final ClientsInquiry<? extends AbstractDTO> clientsInquiry;
    private final ConnectionToClient connectionToClient;

    public ClientInquiriesDelegator(ClientsInquiry<? extends AbstractDTO> clientsInquiry,
        ConnectionToClient connectionToClient) {
      this.clientsInquiry = clientsInquiry;
      this.connectionToClient = connectionToClient;
    }

    @Override
    public void run() {

//            connectionsToTypeToInquiries.putIfAbsent(connectionToClient, new ConcurrentHashMap<>());
//            connectionsToTypeToInquiries.get(connectionToClient).putIfAbsent(clientsInquiry.getInquirysDTOType(), new ConcurrentLinkedQueue<>());
//            while (!connectionsToTypeToInquiries.get(connectionToClient).get(clientsInquiry.getInquirysDTOType()).isEmpty()) {
//
//            }
//            connectionsToTypeToInquiries.get(connectionToClient).get(clientsInquiry.getInquirysDTOType()).add(clientsInquiry);

      ServersResponse<? extends AbstractDTO> serversResponse = null;

      switch (clientsInquiry.getInquirysDTOType()) {

        case USER: {
          // UsersDAON usersDAO = new UsersDAON();
          // serversResponse = usersDAO.processInquiry(clientsInquiry);
          // Get UsersDAO from factory

          if (clientsInquiry.getInquiry() == Inquiry.USER_DISCONNECT_OTHER_CONNECTIONS) {
            ServersResponse<UserDTO> closeSessionResponse = ServersResponse.of(UserDTO.class);
            UserDTO userToConnect = (UserDTO) clientsInquiry.getTheDTO();
            closeSessionResponse.setTheDTO(userToConnect);
            closeSessionResponse.setResponse(Response.USER_FORCE_SIGN_OUT);
            boolean copiedUserInfo = false;
            Thread[] clientThreadList = GCMServer.getInstance().getClientConnections();
            for (int i = 0; i < clientThreadList.length; i++) {
              if (clientThreadList[i] instanceof ConnectionToClient) {
                final ConnectionToClient connection = (ConnectionToClient) clientThreadList[i];
                final UserDTO userInfo = (UserDTO) connection.getInfo("UserINFO");
                if (userInfo != null) {
                  if (userInfo.getEmail().equals(userToConnect.getEmail()) && userInfo.getPassword()
                      .equals(userToConnect.getPassword()) && userInfo.isOnline()) {
                    send(closeSessionResponse, connection);
                    if (!copiedUserInfo) {
                      connectionToClient.setInfo("UserINFO", userInfo);
                      copiedUserInfo = true;
                    }
                  }
                }
              }
            }
            ServersResponse<UserDTO> confirmationResponse = ServersResponse.of(UserDTO.class);
            confirmationResponse.setTheDTO((UserDTO) connectionToClient.getInfo("UserINFO"));
            confirmationResponse.setResponse(Response.USER_SIGN_IN);
            serversResponse = confirmationResponse;
          } else {
            serversResponse = new UsersDAON().processInquiry(clientsInquiry);
            if (serversResponse.getResponse() == Response.USER_SIGN_IN || (
                serversResponse.getResponse() == Response.USER_IS_ALREADY_ONLINE
                    && connectionToClient.getInfo("UserINFO") == null)) {
              connectionToClient.setInfo("UserINFO", serversResponse.getTheDTO());
            }
          }
          break;
        }

        case CITY: {
          // CitiesDAON usersDAO = new CitiesDAON();
          serversResponse = new CitiesDAON().processInquiry(clientsInquiry);
          if (serversResponse.getResponse() == Response.CITY_SAVED_AND_LOCKED) {
            EmployeeDTO userInfo = (EmployeeDTO) connectionToClient.getInfo("UserINFO");
            ClientsInquiry<CityDTO> saveToWorkspaceInquiry = ClientsInquiry.of(CityDTO.class)
                .setExtraParameters(userInfo.getEmployeeID());
            saveToWorkspaceInquiry.setTheDTO((CityDTO) serversResponse.getTheDTO());
            saveToWorkspaceInquiry.setInquiry(Inquiry.CITY_LOCK_TO_EMPLOYEE);
            new CitiesDAON().processInquiry(saveToWorkspaceInquiry);
          }
          break;
        }

        case MAP: {
          serversResponse = new MapsDAON().processInquiry(clientsInquiry);
          break;
        }

        case SITE: {
          serversResponse = new SitesDAON().processInquiry(clientsInquiry);
          break;
        }

        case TOUR: {
          serversResponse = new ToursDAON().processInquiry(clientsInquiry);
          break;
        }

        case PURCHASE: {
          serversResponse = new PurchasesDAON().processInquiry(clientsInquiry);
          break;
        }

        default: {
          logger.error(TextColors.RED.colorThis(
              "Processing " + TextColors.RED.colorThis("failed!") + " unknown DTO type: "
                  + clientsInquiry.getInquirysDTOType()));
        }

      }

//            connectionsToTypeToInquiries.get(connectionToClient).get(clientsInquiry.getInquirysDTOType()).remove(clientsInquiry);
      send(serversResponse, connectionToClient);

    }

  }
}


