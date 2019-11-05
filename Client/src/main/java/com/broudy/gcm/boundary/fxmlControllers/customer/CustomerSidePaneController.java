package com.broudy.gcm.boundary.fxmlControllers.customer;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.services.EventHandlingService;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class controls sidepane's clicks.
 * <p>
 * Created on the 20th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CustomerSidePaneController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(CustomerSidePaneController.class);

  private final SceneSwitcheroo sceneSwitcheroo;

  /* Guice injected fields */
  @Inject
  private StageManager stageManager;
  @Inject
  private EventHandlingService eventHandlingService;

  /* FXML injected elements */
  @FXML
  private JFXButton myAccountBTN;
  @FXML
  private JFXButton catalogBrowsingBTN;
  @FXML
  private JFXButton activeSubscriptionsBTN;
  @FXML
  private JFXButton purchaseHistoryBTN;
  @FXML
  private JFXButton helpBTN;
  @FXML
  private JFXButton donateBTN;
  @FXML
  private JFXButton contactUsBTN;
  @FXML
  private JFXButton logOutBTN;

  @Inject
  public CustomerSidePaneController(SceneSwitcheroo sceneSwitcheroo) {
    this.sceneSwitcheroo = sceneSwitcheroo;
  }

  @Override
  public void initializeEnhancedController() {

  }

  @Override
  protected void activateController() {
    myAccountBTN.setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.CUSTOMER_MY_ACCOUNT));
    catalogBrowsingBTN
        .setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.CUSTOMER_CATALOG_BROWSING));
    activeSubscriptionsBTN
        .setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.CUSTOMER_ACTIVE_SUBSCRIPTIONS));
    purchaseHistoryBTN
        .setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.CUSTOMER_PURCHASE_HISTORY));

    helpBTN.setOnAction(eventHandlingService::helpButtonHandler);
    donateBTN.setOnAction(eventHandlingService::donateButtonHandler);
    contactUsBTN.setOnAction(eventHandlingService::contactUsButtonHandler);
    logOutBTN.setOnAction(eventHandlingService::logOutButtonHandler);
  }

  @Override
  protected void deactivateController() {

  }
}
