package com.broudy.gcm.control;

import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.broudy.utils.TextColors;
import java.io.IOException;
import java.net.SocketException;
import javafx.application.Platform;
import ocsf.client.ObservableClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for creating a connection and sending messages to server-side.
 * TODO provide a more in-depth summary to GCMClient class!!!!!
 * <p>
 * Created on the 2nd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class GCMClient extends ObservableClient {

  private static final Logger logger = LogManager.getLogger(GCMClient.class);

  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 4332;

  private static GCMClient onlyInstance = new GCMClient(DEFAULT_HOST, DEFAULT_PORT);

  private GCMClient(String host, int port) {
    super(host, port);
  }

  public static GCMClient getInstance() {
    return onlyInstance;
  }

  @Override
  protected void connectionClosed() {
    logger.trace("Connection has been closed.");
  }

  @Override
  protected void connectionException(Exception exception) {
    logger.error("Connection exception! ", exception);
    if (exception instanceof SocketException) {
      try {
        this.openConnection();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    exception.printStackTrace();
  }

  @Override
  protected void connectionEstablished() {
    logger.trace("Connection has been successfully established.");
  }

    /*public  void send(ClientsInquiry request) {
        // TODO: Check if the runLater helps.
        Platform.runLater(() -> {
            try {
                logger.trace(
                        TextColors.PURPLE.colorThis(request.getRequest()) + " was just sent to server...\nWith: " + request);
                onlyInstance.sendToServer(request);
            } catch (IOException e) {
                logger.error(e);
            }
        });
    }*/

  public <DTO extends AbstractDTO> void send(ClientsInquiry<DTO> clientsInquiry) {
    // TODO: Check if the runLater helps.
    Platform.runLater(() -> {
      try {
        logger.trace(TextColors.PURPLE.colorThis(clientsInquiry.getInquiry())
            + " was just sent to server...\nWith: " + clientsInquiry.getTheDTO());
        onlyInstance.sendToServer(clientsInquiry);
      } catch (IOException e) {
        logger.error(e);
      }
    });
  }
}
