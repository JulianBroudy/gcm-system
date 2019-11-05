package com.broudy.gcm.boundary.fxmlControllers;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.boundary.fxmlControllers.customer.CustomerSidePaneController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.PurchasesRepository;
import com.broudy.gcm.control.services.EventHandlingService;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class controls the sidepane of an unregistered customer.
 * <p>
 * Created on the 24th of October, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class JustBrowseSidePaneController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(CustomerSidePaneController.class);

  private final PurchasesRepository purchasesRepository;
  private final SceneSwitcheroo sceneSwitcheroo;

  /* Guice injected fields */
  @Inject
  private StageManager stageManager;
  @Inject
  private EventHandlingService eventHandlingService;

  @FXML
  private JFXButton signInBTN;
  @FXML
  private JFXButton registerBTN;
  @FXML
  private JFXButton helpBTN;
  @FXML
  private JFXButton donateBTN;
  @FXML
  private JFXButton contactUsBTN;
  @FXML
  private JFXButton exitBTN;

  @Inject
  public JustBrowseSidePaneController(PurchasesRepository purchasesRepository,
      SceneSwitcheroo sceneSwitcheroo) {
    this.purchasesRepository = purchasesRepository;
    this.sceneSwitcheroo = sceneSwitcheroo;
  }

  @Override
  public void initializeEnhancedController() {

  }

  @Override
  protected void activateController() {
    purchasesRepository.getBeansList().clear();
    sceneSwitcheroo.setInitialView(FXMLView.CUSTOMER_CATALOG_BROWSING);
    signInBTN.setOnAction(e -> stageManager.switchScene(FXMLView.WELCOME_SCREEN));
    registerBTN.setOnAction(eventHandlingService::registerButtonHandler);
    helpBTN.setOnAction(eventHandlingService::helpButtonHandler);
    donateBTN.setOnAction(eventHandlingService::donateButtonHandler);
    contactUsBTN.setOnAction(eventHandlingService::contactUsButtonHandler);
    exitBTN.setOnAction(eventHandlingService::exitButtonHandler);
  }

  @Override
  protected void deactivateController() {

  }
}
