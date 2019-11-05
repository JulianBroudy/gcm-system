package com.broudy.gcm.boundary;

import com.broudy.gcm.control.DataBaseSource;
import com.broudy.gcm.control.GCMServer;
import com.broudy.gcm.control.ServerCommunicationHandler;
import com.broudy.gcm.control.ServerLauncher;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Server's initial setup view controller overseeing server & data base connection initialization.
 * TODO provide a summary to ServerInitializingController class!!!!!
 * <p>
 * Created on the 3rd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
@SuppressWarnings("Duplicates")
public class ServerInitializingController {

  private static final Logger logger = LogManager.getLogger(ServerInitializingController.class);

  @FXML
  private Label stateLBL;
  @FXML
  private HBox portHB;
  @FXML
  private JFXTextField portTF;
  @FXML
  private FontAwesomeIconView portICON;
  @FXML
  private HBox userHB;
  @FXML
  private JFXTextField userTF;
  @FXML
  private FontAwesomeIconView userICON;
  @FXML
  private HBox passwordHB;
  @FXML
  private JFXPasswordField passwordPF;
  @FXML
  private FontAwesomeIconView passwordICON;
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

  private ConnectionInitializingService connectionInitializingService;
  private ConnectionDisconnectingService connectionDisconnectingService;


  private StringProperty portIconStyleClass;
  private StringProperty userIconStyleClass;
  private StringProperty passwordIconStyleClass;


  @FXML
  private void initialize() {
    stateLBL.setText("disconnected");
    progressBarAP.setOpacity(0);

    connectionInitializingService = new ConnectionInitializingService();
    connectionDisconnectingService = new ConnectionDisconnectingService();

    portIconStyleClass = new SimpleStringProperty();
    userIconStyleClass = new SimpleStringProperty();
    passwordIconStyleClass = new SimpleStringProperty();

    initializeEventHandlers();
    initializeBindings();
    initializeListeners();

    //  TODO remove shortcut
    stateLBL.setOnMouseClicked(click -> {
      portTF.setText("4332");
      userTF.setText("root");
      passwordPF.setText("password");
      connectBTN.fireEvent(
          new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false,
              false, false, false, false, false, false, false, false, null));
    });
  }

  /**
   * Initializes all needed event handlers.
   */
  private void initializeEventHandlers() {
    minimizeBTN.setOnMouseClicked(
        click -> ((Stage) ((Node) click.getSource()).getScene().getWindow()).setIconified(true));

    exitBTN.setOnMouseClicked(click -> {
      Platform.exit();
      System.exit(0);
    });

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
   * Initializes all needed event bindings.
   */
  private void initializeBindings() {

    createIconsStyleClassBindings(portIconStyleClass, portTF);
    createIconsStyleClassBindings(userIconStyleClass, userTF);
    createIconsStyleClassBindings(passwordIconStyleClass, passwordPF);

    connectBTN.textProperty().bind(
        Bindings.when(stateLBL.textProperty().isEqualTo("connected")).then("Reconnect")
            .otherwise("Connect"));

    connectBTN.disableProperty().bind(portTF.textProperty().isEmpty()
        .or(userTF.textProperty().isEmpty().or(passwordPF.textProperty().isEmpty())));

    disconnectBTN.disableProperty().bind(stateLBL.textProperty().isNotEqualTo("connected"));
  }

  /**
   * Creates a StringBinding that matches the correct icon's style class based on some conditions.
   *
   * @param stringProperty the StringBinding to bind.
   * @param textInputControl the control which the stringProperty is dependent on.
   */
  private void createIconsStyleClassBindings(StringProperty stringProperty,
      TextField textInputControl) {
    stringProperty.bind(
        Bindings.when(textInputControl.textProperty().isNotEmpty()).then("glyph-icon-yellow")
            .otherwise(Bindings.when(textInputControl.focusedProperty()).then("glyph-icon-blue")
                .otherwise("glyph-icon-blue-light")));
  }


  /**
   * Initializes all needed listeners.
   */
  private void initializeListeners() {
    initializeIconColorsSwitcheroo(portIconStyleClass, portICON);
    initializeIconColorsSwitcheroo(userIconStyleClass, userICON);
    initializeIconColorsSwitcheroo(passwordIconStyleClass, passwordICON);
    initializeHBoxColorSwitcheroo(portHB, portTF);
    initializeHBoxColorSwitcheroo(userHB, userTF);
    initializeHBoxColorSwitcheroo(passwordHB, passwordPF);
  }

  /**
   * Adds a listener to the icon that sets the style class to the StringProperty's value.
   *
   * @param iconStyleClass the StringProperty to set.
   * @param fieldIcon the icon to add the listener to.
   */
  private void initializeIconColorsSwitcheroo(StringProperty iconStyleClass,
      GlyphIcon<?> fieldIcon) {
    iconStyleClass.addListener((observable, oldValue, newValue) -> {
      fieldIcon.getStyleClass().clear();
      fieldIcon.getStyleClass().add(newValue);
    });
  }

  /**
   * Adds a listener that sets the right colors to the HBoxes.
   *
   * @param containerHB HBox to alter its color.
   * @param textInputControl the TextField the HBox's color is dependant on.
   */
  private void initializeHBoxColorSwitcheroo(HBox containerHB, TextField textInputControl) {
    textInputControl.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        containerHB.getStyleClass().clear();
        containerHB.getStyleClass().add("container-line-blue");
      } else {
        containerHB.getStyleClass().clear();
        containerHB.getStyleClass().add("container-line-blue-light");
      }
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
        logger.trace(event);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        stateLBL.setTextFill(Color.valueOf("#FFFF00"));
        logger.debug("playing OnScheduled transition");
      });

      setOnSucceeded(event -> {
        logger.trace(event);
        fadeTransition.setToValue(0);

        if ((boolean) event.getSource().getValue()) {
          PauseTransition pauseBeforeMinimizing = new PauseTransition(Duration.seconds(1.5));
          pauseBeforeMinimizing.setOnFinished(finished -> {
            logger.trace("finished event...");
            ServerLauncher.getPrimaryStage().setIconified(true);
          });
          pauseBeforeMinimizing.play();
          stateLBL.setTextFill(Color.valueOf("#8cff00"));
        } else {
          stateLBL.setTextFill(Color.valueOf("#E32636"));
        }
        fadeTransition.play();
        logger.debug("playing OnSucceeded transition");
      });
    }

    @Override
    protected Task<Boolean> createTask() {
      return new Task<Boolean>() {
        @Override
        protected Boolean call() {
          try {
            updateMessage("setting port...");
            Thread.sleep(1000);
            GCMServer.getInstance().setPort(Integer.parseInt(portTF.getText()));

            updateMessage("listen attempt...");
            Thread.sleep(600);
            try {
              GCMServer.getInstance().listen();
            } catch (IOException e) {
              updateMessage("failed to listen, please retry...");
              logger.traceExit(e);
              return false;
            }

            DataBaseSource.getInstance().setSchema("gcm-db");

            updateMessage("setting user...");
            Thread.sleep(300);
            DataBaseSource.getInstance().setUser(userTF.getText());

            updateMessage("setting password...");
            Thread.sleep(400);
            DataBaseSource.getInstance().setPassword(passwordPF.getText());

            updateMessage("testing connection...");
            Thread.sleep(500);
            try (Connection connection = DataBaseSource.getInstance().getConnection()) {
              updateMessage("connected");
            } catch (SQLException sqlException) {
              updateMessage("DB connection failed!");
              logger.error(sqlException);
              return logger.traceExit(false);
            }
          } catch (InterruptedException interruptedException) {
            updateMessage("something went wrong, please retry...");
            logger.error(interruptedException);
            return logger.traceExit(false);
          }
          ServerCommunicationHandler messageHandler = new ServerCommunicationHandler();
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
            Thread.sleep(300);

            updateMessage("disconnecting from database...");
            Thread.sleep(500);

            updateMessage("stopping server...");
            Thread.sleep(600);

          } catch (InterruptedException interruptedException) {
            updateMessage("something went wrong, please retry...");
            logger.traceExit(interruptedException);
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
