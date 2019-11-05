package com.broudy.gcm.boundary.fxmlControllers.employee;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.EventHandlingService;
import com.broudy.gcm.entity.UserClassification;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class controls the switching between employees' views.
 * <p>
 * Created on the 8th of August, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeSidePaneController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(EmployeeSidePaneController.class);

  private final SceneSwitcheroo sceneSwitcheroo;
  private final UsersRepository usersRepository;
  private final EmployeeMyAccountController employeeMyAccountController;
  private final EmployeeMyWorkspaceController employeeMyWorkspaceController;
  private final EmployeeCitiesEditorController employeeCitiesEditorController;
  private final EmployeeMapsEditorController employeeMapsEditorController;
  private final EmployeeRequestsViewerController employeeRequestsViewerController;

  /* Guice injected fields */
  @Inject
  private StageManager stageManager;
  @Inject
  private EventHandlingService eventHandlingService;

  /* FXML injected elements */
  @FXML
  private JFXButton myAccountBTN;
  @FXML
  private JFXButton myWorkspaceBTN;
  @FXML
  private JFXButton citiesEditorBTN;
  @FXML
  private JFXButton mapsEditorBTN;
  @FXML
  private JFXButton pricesEditorBTN;
  @FXML
  private JFXButton requestsViewerBTN;
  @FXML
  private JFXButton reportsGeneratorBTN;
  @FXML
  private JFXButton helpBTN;
  @FXML
  private JFXButton logOutBTN;

  @Inject
  public EmployeeSidePaneController(SceneSwitcheroo sceneSwitcheroo,
      UsersRepository usersRepository, EmployeeMyAccountController employeeMyAccountController,
      EmployeeMyWorkspaceController employeeMyWorkspaceController,
      EmployeeCitiesEditorController employeeCitiesEditorController,
      EmployeeMapsEditorController employeeMapsEditorController,
      EmployeeRequestsViewerController employeeRequestsViewerController) {
    this.sceneSwitcheroo = sceneSwitcheroo;
    this.usersRepository = usersRepository;
    this.employeeMyAccountController = employeeMyAccountController;
    this.employeeMyWorkspaceController = employeeMyWorkspaceController;
    this.employeeCitiesEditorController = employeeCitiesEditorController;
    this.employeeMapsEditorController = employeeMapsEditorController;
    this.employeeRequestsViewerController = employeeRequestsViewerController;
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
  @Override
  public void activateEventHandlers() {

    myAccountBTN.setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.EMPLOYEE_MY_ACCOUNT));
    myWorkspaceBTN.setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.EMPLOYEE_MY_WORKSPACE));
    citiesEditorBTN.setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.EMPLOYEE_CITIES_EDITOR));
    mapsEditorBTN.setOnAction(click -> sceneSwitcheroo.loadView(FXMLView.EMPLOYEE_MAPS_EDITOR));
    requestsViewerBTN.setOnAction(click -> {
      if (usersRepository.getBean().getClassification() == UserClassification.CONTENT_EDITOR) {
        sceneSwitcheroo.loadView(FXMLView.EMPLOYEE_UNAUTHORIZED);
      } else {
        sceneSwitcheroo.loadView(FXMLView.EMPLOYEE_REQUESTS_VIEWER);
      }
    });
    helpBTN.setOnAction(eventHandlingService::helpButtonHandler);
    logOutBTN.setOnAction(eventHandlingService::logOutButtonHandler);
  }


}
