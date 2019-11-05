package com.broudy.gcm.control;

import com.broudy.gcm.control.services.UsersDAON;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.dtos.UserDTO;
import ocsf.server.ConnectionToClient;
import ocsf.server.ObservableOriginatorServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to GCMServer class!!!!!
 * <p>
 * Created on the 2nd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class GCMServer extends ObservableOriginatorServer {

  private static final Logger logger = LogManager.getLogger(GCMServer.class);

  private static final int DEFAULT_PORT = 4332;

  private static GCMServer onlyInstance = new GCMServer(DEFAULT_PORT);

  /**
   * Constructs a new server.
   *
   * @param port the port on which to listen.
   */
  private GCMServer(int port) {
    super(port);
    logger.trace(port);
  }

  public static GCMServer getInstance() {
    return onlyInstance;
  }

  @Override
  protected synchronized void serverStarted() {
    logger.trace("Server has been successfully started");
  }

  @Override
  protected synchronized void clientConnected(ConnectionToClient client) {
    logger.trace("Client " + client + " has been successfully connected");
  }

  @Override
  protected synchronized void clientDisconnected(ConnectionToClient client) {
    logger.trace("Client " + client + " has been successfully disconnected");
    ClientsInquiry<UserDTO> userInfo = ClientsInquiry.of(UserDTO.class);
    userInfo.setTheDTO((UserDTO) client.getInfo("userInfo"));
    userInfo.setInquiry(Inquiry.USER_ALTER_ONLINE_STATUS);
    new UsersDAON().processInquiry(userInfo);
  }

  @Override
  protected synchronized void listeningException(Throwable exception) {
    logger.error("Listening exception! ", exception);
  }

  @Override
  protected synchronized void serverStopped() {
    logger.error("Server has been stopped");
  }
}
