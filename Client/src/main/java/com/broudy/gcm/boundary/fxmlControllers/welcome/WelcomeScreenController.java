package com.broudy.gcm.boundary.fxmlControllers.welcome;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.FillingForm;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.ClientMessagesReceiver;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.services.EventHandlingService;
import com.broudy.gcm.entity.guice.EnhancedFXMLLoaderDependenciesModule;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the welcome screen.<br> The initialization of the <code>IncomingMessageManager</code> is
 * done when this controller is instantiated for the first time.
 * <p>
 * Created on the 5th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see ClientMessagesReceiver
 * @see EnhancedFXMLLoaderDependenciesModule#configure
 */
public class WelcomeScreenController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(WelcomeScreenController.class);

  /* Guice injected fields */
  @Inject
  private StageManager stageManager;
  @Inject
  private ClientMessagesReceiver clientMessagesReceiver;
  @Inject
  private EventHandlingService eventHandlingService;
  @Inject
  private FillingForm fillingForm;


  /* FXML injected elements */
  @FXML
  private JFXDrawer formDRAWER;
  @FXML
  private JFXButton signInBTN;
  @FXML
  private JFXButton registerBTN;
  @FXML
  private JFXButton justBrowseBTN;
  @FXML
  private JFXButton minimizeBTN;
  @FXML
  private JFXButton exitBTN;

  @Inject
  public WelcomeScreenController() {
  }

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
    activateEventHandlers();
    fillingForm.integrateDrawer(formDRAWER);
    fillingForm.setInitialForm(FXMLView.SIGN_IN_FORM);
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {

  }

  /**
   * Initializes all needed event handlers.
   */
  public void activateEventHandlers() {
    // minimizeBTN.setOnAction(eventHandlingService::minimizeButtonHandler);
    minimizeBTN.setOnAction(eventHandlingService::minimizeButtonHandler);
    exitBTN.setOnAction(eventHandlingService::exitButtonHandler);
    // exitBTN.setOnAction(click -> stageManager.cleanUpAndExit());

    signInBTN.setOnMouseClicked(click -> fillingForm.loadForm(FXMLView.SIGN_IN_FORM));

    registerBTN.setOnMouseClicked(click -> fillingForm.loadForm(FXMLView.SIGN_UP_FORM_1));

    justBrowseBTN.setOnMouseClicked(e -> stageManager.switchScene(FXMLView.MAIN_CONTAINER));
  }

  /**
   * Convenience method to reduce number of injections in other classes.
   */
  public void showSignInForm() {
    // formDRAWER.open();
    fillingForm.loadForm(FXMLView.SIGN_IN_FORM);
  }

  /**
   * Convenience method to reduce number of injections in other classes.
   */
  public void showRegistrationForm() {
    // formDRAWER.open();
    fillingForm.loadForm(FXMLView.SIGN_UP_FORM_1);
  }

}
