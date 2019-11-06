package com.broudy.gcm.boundary.fxmlControllers;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.EventHandlingService;
import com.broudy.gcm.entity.UserClassification;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerNextArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is the base of the application after a user is logged in (customer or employee).
 * Loads the SceneSwitcheroo and integrates the JFXDrawer into it.
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class MainContainerController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(MainContainerController.class);

  /* Guice injected fields */
  private final UsersRepository usersRepository;
  private final SceneSwitcheroo sceneSwitcheroo;
  /* Other variables */
  // private final ObjectProperty<IRequest> usersActivity;
  @Inject
  private StageManager stageManager;
  @Inject
  private EventHandlingService eventHandlingService;
  private HamburgerNextArrowBasicTransition sidePaneAnimation;

  /* FXML injected elements */
  @FXML
  private JFXDrawer switchableViewDRAWER;
  @FXML
  private JFXDrawer sidePaneDRAWER;
  @FXML
  private Label windowTitleLBL;
  @FXML
  private JFXHamburger menuToggleBTN;
  @FXML
  private JFXButton minimizeBTN;
  @FXML
  private JFXButton exitBTN;

  @Inject
  public MainContainerController(UsersRepository usersRepository, SceneSwitcheroo sceneSwitcheroo) {
    this.usersRepository = usersRepository;
    this.sceneSwitcheroo = sceneSwitcheroo;
    // usersActivity = new SimpleObjectProperty<>();
  }

  /**
   * This method should implement all the steps that turn an initialized & "unlinked" controller
   * into a "linked" one.
   */
  @Override
  protected void activateController() {
    sidePaneAnimation = new HamburgerNextArrowBasicTransition(menuToggleBTN);
    sidePaneAnimation.setRate(-1);

    activateBindings();
    activateListeners();
    activateEventHandlers();

    sceneSwitcheroo.integrateDrawer(switchableViewDRAWER, windowTitleLBL);
    refreshVIEWS();
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {
  }

  /**
   * All the one-time initializations should be implemented in this method.
   */
  @Override
  public void initializeEnhancedController() {
    /*activateController();*/
  }

  /**
   * Initializes all needed bindings.
   */
  public void activateBindings() {
    // usersRepository.getSuperBean().bindBidirectional("request", usersActivity, IRequest.class);
  }

  /**
   * Initializes all needed listeners.
   */
  public void activateListeners() {
    // usersActivity.addListener((observable, oldValue, newValue) -> {
    //   if (newValue != null) {
    //     refreshVIEWS();
    //   }
    // });
  }

  /**
   * Initializes all needed event handlers.
   */
  public void activateEventHandlers() {
    menuToggleBTN.setOnMouseClicked(e -> toggleHamburger());

    minimizeBTN.setOnAction(eventHandlingService::minimizeButtonHandler);
    exitBTN.setOnAction(eventHandlingService::exitButtonHandler);
  }

  /**
   * This method refreshes the initially showed {@link FXMLView} based on user's current
   * state. i.e., loads customer's view, employee's view or the "Just Browsing" view.
   * TODO change doc
   */
  private void refreshVIEWS() {
    sceneSwitcheroo.resetView();
    if (usersRepository.getBean().isOnline()) {
      sidePaneDRAWER.setSidePane(stageManager.getRootNodeOf(
          usersRepository.getBean().getClassification() == UserClassification.CUSTOMER
              ? FXMLView.CUSTOMER_SIDE_PANE : FXMLView.EMPLOYEE_SIDE_PANE));
    } else {
      sidePaneDRAWER.setSidePane(stageManager.getRootNodeOf(FXMLView.JUST_BROWSE_SIDE_PANE));
    }
    /*switch (currentCommand) {
      case USER_SIGN_IN: {
        break;
      }
      case USER_SIGN_EMPLOYEE_IN: {
        sidePaneDRAWER.setSidePane(stageManager.getRootNodeOf(FXMLView.EMPLOYEE_SIDE_PANE));
        break;
      }
      default: {
        sidePaneDRAWER.setSidePane(stageManager.getRootNodeOf(FXMLView.JUST_BROWSE_SIDE_PANE));
        sceneSwitcheroo.setInitialView(FXMLView.JUST_BROWSING);
      }
    }*/
    toggleHamburger();
  }

  /**
   * Plays the hamburger animation as well as toggles the side pane.
   */
  private void toggleHamburger() {
    sidePaneAnimation.setRate(sidePaneAnimation.getRate() * -1);
    sidePaneAnimation.play();
    if (sidePaneDRAWER.isOpened()) {
      sidePaneDRAWER.close();
    } else {
      sidePaneDRAWER.open();
    }
  }
}
