package com.broudy.gcm.boundary.fxmlControllers;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// import com.broudy.gcm.boundary.services.RenderingsStyler;

/**
 * Client's initial setup view controller overseeing client connection initialization.
 * <p>
 * Created on the 4th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
@SuppressWarnings("Duplicates")
public class ClientInitializingController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(ClientInitializingController.class);

  /* Constants */
  private final String NOT_EMPTY_COLOR = "yellow";
  private final String EMPTY_AND_FOCUSED_COLOR = "blue";
  private final String EMPTY_AND_UNFOCUSED_COLOR = "blue-light";

  /* Guice injected fields */
  @Inject
  private StageManager stageManager;

  /* Other variables */
  private ConnectionInitializingService connectionInitializingService;
  private ConnectionDisconnectingService connectionDisconnectingService;

  /* FXML injected elements */
  @FXML
  private Label stateLBL;
  @FXML
  private HBox portHB;
  @FXML
  private JFXTextField portTF;
  @FXML
  private FontAwesomeIconView portICON;
  @FXML
  private HBox hostHB;
  @FXML
  private JFXTextField hostTF;
  @FXML
  private FontAwesomeIconView hostICON;
  @FXML
  private JFXButton disconnectBTN;
  @FXML
  private JFXButton connectBTN;
  @FXML
  private JFXButton minimizeBTN;
  @FXML
  private JFXButton exitBTN;
  @FXML
  private AnchorPane progressBarAP;
  @FXML
  private JFXProgressBar progressBar;


  /**
   * All the one-time initializations should be implemented in this method.
   */
  @Override
  public void initializeEnhancedController() {

  }

  /**
   * This method should implement all the steps that turn an initialized & "unlinked" controller
   * into a "linked" one.
   */
  @Override
  protected void activateController() {
    stateLBL.setText("disconnected");
    progressBarAP.setOpacity(0);

    connectionInitializingService = new ConnectionInitializingService();
    connectionDisconnectingService = new ConnectionDisconnectingService();

    RenderingsStyler.prepareEnhancedTextInputControl(portHB, portTF, portICON, NOT_EMPTY_COLOR,
        EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

    RenderingsStyler.prepareEnhancedTextInputControl(hostHB, hostTF, hostICON, NOT_EMPTY_COLOR,
        EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

    activateBindings();
    activateEventHandlers();

    //  TODO remove shortcut
    stateLBL.setOnMouseClicked(click -> {
      hostTF.setText("localhost");
      portTF.setText("4332");
      connectBTN.fireEvent(
          new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false,
              false, false, false, false, false, false, false, false, null));
    });
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {

  }


  /**
   * Initializes all needed bindings.
   */
  public void activateBindings() {
    connectBTN.textProperty().bind(
        Bindings.when(stateLBL.textProperty().isEqualTo("connected")).then("Reconnect")
            .otherwise("Connect"));

    connectBTN.disableProperty()
        .bind(portTF.textProperty().isEmpty().or(hostTF.textProperty().isEmpty()));

    disconnectBTN.disableProperty().bind(stateLBL.textProperty().isNotEqualTo("connected"));
  }

  /**
   * Initializes all needed event handlers.
   */
  public void activateEventHandlers() {
    minimizeBTN.setOnMouseClicked(click -> stageManager.setIconified(true));

    exitBTN.setOnMouseClicked(click -> stageManager.cleanUpAndExit());

    connectBTN.setOnMouseClicked(click -> {
      stateLBL.textProperty().unbind();
      stateLBL.textProperty().bind(connectionInitializingService.messageProperty());
      progressBar.getStyleClass().clear();
      progressBar.getStyleClass().add("jfx-progress-bar");
      connectionInitializingService.reset();
      connectionInitializingService.start();
    });

    disconnectBTN.setOnMouseClicked(click -> {
      stateLBL.textProperty().unbind();
      stateLBL.textProperty().bind(connectionDisconnectingService.messageProperty());
      progressBar.getStyleClass().clear();
      progressBar.getStyleClass().add("jfx-progress-bar-red");
      connectionDisconnectingService.reset();
      connectionDisconnectingService.start();
    });
  }

  /**
   * A Service class which handles all the actual connection handling as well as simulates waiting
   * periods between steps.
   */
  private class ConnectionInitializingService extends Service<Boolean> {

    FadeTransition fadeTransition;

    /**
     * Constructs the class as well as a fade transition. Sets event handlers for OnSucceeded and
     * OnScheduled.
     */
    private ConnectionInitializingService() {
      fadeTransition = new FadeTransition();
      fadeTransition.setCycleCount(1);
      fadeTransition.setDuration(Duration.seconds(1));
      fadeTransition.setNode(progressBarAP);

      setOnScheduled(event -> {
        logger.trace(event.getEventType());
        fadeTransition.setToValue(1);
        fadeTransition.play();
        stateLBL.setTextFill(Color.valueOf("#FFFF00"));
        logger.debug("playing OnScheduled transition");
      });

      setOnSucceeded(event -> {
        logger.trace(event);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        if ((boolean) event.getSource().getValue()) {
          logger.debug("onSucceeded: " + stageManager);
          stageManager.switchScene(FXMLView.WELCOME_SCREEN);
        } else {
          stateLBL.setTextFill(Color.valueOf("#E32636"));
        }
        logger.debug("playing OnSucceeded transition");
      });
    }

    @Override
    protected Task<Boolean> createTask() {
      return new Task<Boolean>() {
        @Override
        protected Boolean call() {
          try {
            updateMessage("setting host...");
            Thread.sleep(400);
            GCMClient.getInstance().setHost(hostTF.getText());

            updateMessage("setting port...");
            Thread.sleep(300);
            GCMClient.getInstance().setPort(Integer.parseInt(portTF.getText()));

            updateMessage("opening connection...");
            Thread.sleep(600);
            GCMClient.getInstance().openConnection();

          } catch (InterruptedException interruptedException) {
            updateMessage("something went wrong, please retry...");
            logger.error(interruptedException);
            return logger.traceExit(false);
          } catch (IOException e) {
            updateMessage("failed, please retry...");
            logger.error(e);
            return logger.traceExit(false);
          }
          if (GCMClient.getInstance().isConnected()) {
            updateMessage("connected");
          }
          return logger.traceExit(true);
        }
      };
    }
  }

  /**
   * A Service class which handles all the disconnect requests as well as simulates waiting periods
   * between various steps.
   */
  private class ConnectionDisconnectingService extends ConnectionInitializingService {

    /**
     * Constructs the class as well as a fade transition. Sets event handlers for OnSucceeded.
     */
    private ConnectionDisconnectingService() {
      super();
    }

    @Override
    protected Task<Boolean> createTask() {
      return new Task<Boolean>() {
        @Override
        protected Boolean call() {
          try {
            updateMessage("attempting to disconnect...");
            Thread.sleep(500);
            GCMClient.getInstance().closeConnection();
          } catch (InterruptedException interruptedException) {
            updateMessage("something went wrong, please retry...");
            logger.traceExit(interruptedException);
            return false;
          } catch (IOException ioException) {
            updateMessage("something went wrong, please retry...");
            logger.traceExit(ioException);
            return false;
          }
          updateMessage("disconnected");
          logger.traceExit(true);
          return true;
        }
      };
    }
  }

}

