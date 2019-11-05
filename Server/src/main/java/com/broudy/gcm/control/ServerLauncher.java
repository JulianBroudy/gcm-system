package com.broudy.gcm.control;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to ServerLauncher class!!!!!
 * <p>
 * Created on the 2nd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ServerLauncher extends Application {

  private static final Logger logger = LogManager.getLogger(ServerLauncher.class);
  private static Stage primaryStage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    logger.trace("start method");
    ServerLauncher.primaryStage = primaryStage;
    Parent root = null;
    try {
      root = FXMLLoader.load(getClass().getResource("/views/ServerInitializingVIEW.fxml"));
    } catch (IOException e) {
      logger.error(e);
    }
    primaryStage.setScene(new Scene(root));
    primaryStage.initStyle(StageStyle.TRANSPARENT);
    primaryStage.show();
  }

  /**
   * Gets the Stage's value.
   *
   * @return the value of the Stage.
   */
  public static Stage getPrimaryStage() {
    return primaryStage;
  }
}
