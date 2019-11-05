package com.broudy.gcm.control;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLLoader;
import com.broudy.gcm.entity.guice.EnhancedFXMLLoaderDependenciesModule;
import com.broudy.gcm.entity.guice.PrimaryDependenciesModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Launches client-side application. Starts-up with a Host & Port input screen for convenience and
 * educational purposes.
 * <p>
 * It is possible to override it simply by changing the initially loaded <code>FXMLView</code> in
 * {@link #showInitialView} method. Also, <code>GCMClient</code> must be instantiated in this class
 * as well as calling {@link GCMClient#openConnection}. In that case the default Host & Port are
 * "localhost" & "4332".
 * </p>
 * Created on the 2nd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see FXMLView
 */
public class ClientLauncher extends Application {

  private static final Logger logger = LogManager.getLogger(ClientLauncher.class);
  private Stage primaryStage;
  private StageManager stageManager;

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Initializes Guice's injector.
   */
  @Override
  public void start(Stage primaryStage) {
    logger.traceEntry("#start" + "initial stage: " + primaryStage.toString());
    this.primaryStage = primaryStage;

    initializeInjectors();

    showInitialView();
    logger.traceExit("#start");
  }

  /**
   * This method: 1- Initializes Guice's injector with its dependency modules. 2- Sets the primary
   * injector to the <code>EnhancedFXMLLoader</code>. 3- Instantiates the the {@see StageManager}.
   * <p>
   * Note: each time the {@see StageManager} is activated it creates an {@see EnhancedFXMLLoader}
   * instance which loads the FXML file & its {@see EnhancedFXMLcontroller}. It, in turn, loads all
   * related FXML files & their controllers, for each controller resolves it's dependencies.
   *
   * @see EnhancedFXMLLoader
   */
  public void initializeInjectors() {
    logger.traceEntry("#initializeInjectors");

    logger.debug("Creating primaryInjector...");
    Injector primaryInjector = Guice.createInjector(new PrimaryDependenciesModule(primaryStage),
        new EnhancedFXMLLoaderDependenciesModule());

    logger.debug("Setting EnhancedFXMLLoader's injector...");
    EnhancedFXMLLoader.setInjector(primaryInjector);
    stageManager = primaryInjector.getInstance(StageManager.class);

    logger.trace(stageManager);
    logger.traceExit("#initializeInjectors");
  }

  private void showInitialView() {
    logger.debug("#showInitialView");
    stageManager.switchScene(FXMLView.CLIENT_STARTUP);
  }

}
