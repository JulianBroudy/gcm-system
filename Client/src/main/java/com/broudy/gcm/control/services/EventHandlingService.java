package com.broudy.gcm.control.services;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeCitiesEditorController;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeMapsEditorController;
import com.broudy.gcm.boundary.fxmlControllers.welcome.WelcomeScreenController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.CreditCardsRepository;
import com.broudy.gcm.control.repos.MapsRepository;
import com.broudy.gcm.control.repos.PurchasesRepository;
import com.broudy.gcm.control.repos.SitesRepository;
import com.broudy.gcm.control.repos.ToursRepository;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.UserClassification;
import com.broudy.gcm.entity.dtos.CityDTO;
import com.broudy.gcm.entity.dtos.MapDTO;
import com.broudy.gcm.entity.dtos.PurchaseDTO;
import com.broudy.gcm.entity.dtos.SiteDTO;
import com.broudy.gcm.entity.dtos.TourDTO;
import com.broudy.gcm.entity.dtos.UserDTO;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class manages everything that has to do with receiving or sending messages from and to the
 * Server.
 * <p>
 * Created on the 11th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EventHandlingService {

  private static final Logger logger = LogManager.getLogger(EventHandlingService.class);
  public static CityDTO savedCity;
  private static boolean hideProcessFlag;
  private static boolean pressedFromCityExplorer;

  /* Repositories */
  private final UsersRepository usersRepository;
  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;
  private final SitesRepository sitesRepository;
  private final ToursRepository toursRepository;
  private final PurchasesRepository purchasesRepository;
  private final CreditCardsRepository creditCardsRepository;
  /* Enhanced FXML Controllers */
  private final WelcomeScreenController welcomeScreenController;
  private final EmployeeCitiesEditorController employeeCitiesEditorController;
  private final SceneSwitcheroo sceneSwitcheroo;
  @Inject
  private StageManager stageManager;

  @Inject
  public EventHandlingService(UsersRepository usersRepository, CitiesRepository citiesRepository,
      MapsRepository mapsRepository, SitesRepository sitesRepository,
      ToursRepository toursRepository, PurchasesRepository purchasesRepository,
      CreditCardsRepository creditCardsRepository, WelcomeScreenController welcomeScreenController,
      EmployeeCitiesEditorController employeeCitiesEditorController,
      SceneSwitcheroo sceneSwitcheroo) {
    this.usersRepository = usersRepository;
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.sitesRepository = sitesRepository;
    this.toursRepository = toursRepository;
    this.purchasesRepository = purchasesRepository;
    this.creditCardsRepository = creditCardsRepository;
    this.welcomeScreenController = welcomeScreenController;
    this.employeeCitiesEditorController = employeeCitiesEditorController;
    this.sceneSwitcheroo = sceneSwitcheroo;
    hideProcessFlag = false;
    pressedFromCityExplorer = false;
  }

  /**
   * Sets the hideProcessFlag.
   *
   * @param hideProcessFlag is the hideProcessFlag's new value.
   */
  public static void setHideProcessFlag(boolean hideProcessFlag) {
    EventHandlingService.hideProcessFlag = hideProcessFlag;
  }

  /**
   * Sets the pressedFromCityExplorer.
   *
   * @param pressedFromCityExplorer is the pressedFromCityExplorer's new value.
   */
  public static void setPressedFromCityExplorer(boolean pressedFromCityExplorer) {
    EventHandlingService.pressedFromCityExplorer = pressedFromCityExplorer;
  }

  public void helpButtonHandler(ActionEvent helpClicked) {
    logger.trace("\"Help\" button was clicked.");
    helpClicked.consume();
  }

  public void donateButtonHandler(ActionEvent donateClicked) {
    logger.trace("\"Donate\" button was clicked.");
    donateClicked.consume();
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Donate");
    alert.setHeaderText("The thought is all that matters.");
    alert.setContentText("Thank you for being a good human being :)");
    alert.showAndWait();
  }

  public void contactUsButtonHandler(ActionEvent contactUsClicked) {
    logger.trace("\"Contact Us\" button was clicked.");
    contactUsClicked.consume();
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Contact Us");
    alert.setHeaderText("contact-support@GCM.com");
    alert.setContentText("Please, feel free to contact us & we'll get back to you ASAP!");
    alert.showAndWait();
  }

  public void logOutButtonHandler(ActionEvent logOutClicked) {
    logger.trace("\"Log Out\" button was clicked.");
    logOutClicked.consume();
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Are you sure?");
    alert.setHeaderText("Log out?");
    alert.setContentText(
        "You are about to log out of the system." + "\n" + "Any unsaved progress will be lost.");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      usersRepository.requestUserSignOut();
    }
  }

  public void minimizeButtonHandler(ActionEvent minimizeClicked) {
    logger.trace("\"Minimize\" button was clicked.");
    minimizeClicked.consume();
    stageManager.setIconified(true);
  }

  public void exitButtonHandler(ActionEvent exitClicked) {
    logger.trace("\"Exit\" button was clicked.");
    exitClicked.consume();
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Exit");
    alert.setHeaderText("Are you sure?");
    alert.setContentText(
        "You are about to exit the system." + "\n" + "Any unsaved progress will be lost.");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      if (usersRepository.getBean().isOnline()) {
        usersRepository.requestSystemExit();
      } else {
        stageManager.cleanUpAndExit();
      }
    }
  }

  public void editCityButtonHandler(ActionEvent editCityClicked, CityDTO clickedCity) {
    logger.trace("\"Request Approval\" button was clicked.");
    editCityClicked.consume();
    citiesRepository.setBean(clickedCity);
    citiesRepository.requestCityRejectedToLocked();
  }

  public void requestApprovalButtonHandler(ActionEvent requestApprovalClicked,
      CityDTO clickedCity) {
    logger.trace("\"Request Approval\" button was clicked.");
    requestApprovalClicked.consume();
    citiesRepository.setBean(clickedCity);
    citiesRepository.requestCityApprovalRequest();
  }

  public void cancelApprovalRequestButtonHandler(ActionEvent actionEvent, CityDTO clickedCity) {
    logger.trace("\"Request Approval\" button was clicked.");
    actionEvent.consume();
    citiesRepository.setBean(clickedCity);
    citiesRepository.requestApprovalRequestCancellation();
  }

  public void deleteLockedCityButtonHandler(ActionEvent deleteLockedCityClicked,
      CityDTO clickedCity) {
    logger.trace("\"Request Approval\" button was clicked.");
    deleteLockedCityClicked.consume();
    citiesRepository.setBean(clickedCity);
    citiesRepository.requestCityDeletion();
  }

  public void rejectApprovalRequestButtonHandler(ActionEvent rejectClicked, CityDTO clickedCity) {
    logger.trace("\"Reject Approval Request\" button was clicked.");
    rejectClicked.consume();
    citiesRepository.setBean(clickedCity);
    citiesRepository.requestCityApprovalRejection();
  }

  public void approveApprovalRequestButtonHandler(ActionEvent approveClicked, CityDTO clickedCity) {
    logger.trace("\"Reject Approval Request\" button was clicked.");
    approveClicked.consume();
    citiesRepository.setBean(clickedCity);
    citiesRepository.requestCityApprovalApprove();
  }

  public void rejectFromCityExplorerApprovalRequestButtonHandler(ActionEvent rejectClicked,
      CityDTO clickedCity) {
    pressedFromCityExplorer = true;
    rejectApprovalRequestButtonHandler(rejectClicked, clickedCity);
  }

  public void approveFromCityExplorerApprovalRequestButtonHandler(ActionEvent approveClicked,
      CityDTO clickedCity) {
    pressedFromCityExplorer = true;
    approveApprovalRequestButtonHandler(approveClicked, clickedCity);
  }

  public void clearCityFromWorkspaceButtonHandler(ActionEvent clearCityClicked,
      CityDTO renderedCity) {
    logger.trace("\"Clear City Request\" button was clicked.");
    clearCityClicked.consume();
    citiesRepository.setBean(renderedCity);
    citiesRepository.requestRemovalOfCityFromWorkspace();
  }

  public void registerButtonHandler(ActionEvent registerClicked) {
    logger.trace("\"Register\" button was clicked.");
    registerClicked.consume();
    Task<Void> showRegistrationForm = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        stageManager.switchScene(FXMLView.WELCOME_SCREEN);
        return null;
      }
    };
    showRegistrationForm.setOnSucceeded(succeeded -> {
      welcomeScreenController.showRegistrationForm();
    });
    showRegistrationForm.run();
  }

  public void extendSubscriptionButtonHandler(ActionEvent extendSubscriptionClicked) {
    logger.trace("\"Extend Subscription\" button was clicked.");
    extendSubscriptionClicked.consume();

  }

  abstract class ServerResponseHandler<DTO extends AbstractDTO> extends Service<Void> {

    protected final Logger logger = LogManager.getLogger(this.getClass());

    final ServersResponse<DTO> serversResponse;

    public ServerResponseHandler(ServersResponse<DTO> serversResponse) {
      this.serversResponse = serversResponse;
      logger.trace(
          TextColors.YELLOW.colorThis(serversResponse.getResponse()) + " response was received!");

      setHandlers();
    }

    protected abstract void setHandlers();

  }

  public class UserRelatedResponseHandler extends ServerResponseHandler<UserDTO> {

    public UserRelatedResponseHandler(ServersResponse<UserDTO> serversResponse) {
      super(serversResponse);
    }

    @Override
    protected void setHandlers() {
      switch (serversResponse.getResponse()) {
        case USER_AUTHENTICATION_FAILED: {
          setOnScheduled(scheduled -> {
            logger.trace("USER_AUTHENTICATION_FAILED");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Authentication failed");
            alert.setContentText("E-mail or password are incorrect, please retry...");
            alert.showAndWait();
          });
          break;
        }
        case USER_SIGN_IN: {
          setOnScheduled(scheduled -> usersRepository.setBean(serversResponse.getTheDTO()));
          break;
        }
        case USER_SIGN_UP_SUCCESSFUL: {
          setOnScheduled(scheduled -> {
            logger.trace("USER_SIGN_UP_SUCCESSFUL");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Congratulations!");
            alert.setHeaderText("Welcome to GCM");
            alert.setContentText("You have been successfully signed-up, redirecting...");
            alert.showAndWait();
            welcomeScreenController.showSignInForm();
          });
          break;
        }
        case USER_SIGN_UP_FAIL: {
          setOnScheduled(scheduled -> {
            logger.trace("USER_SIGN_UP_FAIL");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Oops!");
            alert.setContentText("Sign-up failed, please retry...");
            alert.showAndWait();
          });
          break;
        }
        case USER_IS_ALREADY_ONLINE: {
          setOnScheduled(scheduled -> {
            logger.trace("USER_IS_ALREADY_ONLINE");
            final ButtonType LOGIN_HERE = new ButtonType("Login Here", ButtonData.YES);
            Alert alert = new Alert(AlertType.CONFIRMATION,
                "Click \"Login Here\" to logout everywhere and login here instead.."
                    + "\nClick \"Cancel\" to go back.", LOGIN_HERE, ButtonType.CANCEL);
            alert.setTitle("Oopsy Daisy!");
            alert.setHeaderText("Seems like you are already logged-in to our system!");
            alert.showAndWait();
            if (alert.getResult().equals(LOGIN_HERE)) {
              usersRepository.requestLogOtherSessionsOutAndLoginHere();
            }
          });
          break;
        }
        case USER_SIGN_OUT_SUCCESSFUL: {
          setOnScheduled(scheduled -> {
            logger.trace("USER_SIGN_OUT_SUCCESSFUL");
            usersRepository.setBean(serversResponse.getTheDTO());
          });
          break;
        }
        case USER_FORCE_SIGN_OUT: {
          setOnScheduled(scheduled -> usersRepository.requestBeanReset());
          setOnSucceeded(succeeded -> {
            logger.trace("USER_FORCE_SIGN_OUT");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Oopsy Daisy!");
            alert.setHeaderText("You were logged out of the system.");
            alert.setContentText("Seems like you chose to sign-in on another device...");
            alert.showAndWait();
          });
          break;
        }
      }
    }

    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {

        @Override
        protected Void call() throws Exception {
          switch (serversResponse.getResponse()) {
            case USER_SIGN_IN: {
              logger.trace("USER_SIGN_IN");
              if (serversResponse.getTheDTO().getClassification() != UserClassification.CUSTOMER) {
                citiesRepository.requestEmployeeWorkspace();
              }
              stageManager.switchScene(FXMLView.MAIN_CONTAINER);
              break;
            }
            case USER_SIGN_OUT_SUCCESSFUL: {
              stageManager.switchScene(FXMLView.WELCOME_SCREEN);
              welcomeScreenController.showSignInForm();
              break;
            }
            case USER_FORCE_SIGN_OUT: {
              // usersRepository.requestBeanReset();
              stageManager.switchScene(FXMLView.WELCOME_SCREEN);
              break;
            }
            case EXIT_SYSTEM: {
              stageManager.cleanUpAndExit();
              break;
            }
          }
          return null;
        }
      };
    }
  }

  public class CityRelatedResponseHandler extends ServerResponseHandler<CityDTO> {

    public CityRelatedResponseHandler(ServersResponse<CityDTO> serversResponse) {
      super(serversResponse);
    }

    @Override
    protected void setHandlers() {
      switch (serversResponse.getResponse()) {
        case CITY_FETCHED_ONE: {
          setOnScheduled(scheduled -> {
            logger.trace("CITY_FETCHED_ONE");
            //TODO CITY_FETCHED_ONE response
          });
          break;
        }
        case CITY_FETCHED_ALL: {
          setOnScheduled(scheduled -> {
            logger.trace("CITY_FETCHED_ALL");
            citiesRepository
                .setBeansList(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
        case EMPLOYEE_CITY_FETCHED_ALL: {
          setOnScheduled(scheduled -> {
            logger.trace("EMPLOYEE_CITY_FETCHED_ALL");
            citiesRepository
                .setBeansList(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
        case EMPLOYEE_WORKSPACE_FETCHED_ALL:{
          setOnSucceeded(succeeded -> {
            logger.trace("EMPLOYEE_WORKSPACE_FETCHED_ALL");
            citiesRepository.setEmployeesWorkspace(
                FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
            if (hideProcessFlag) {
              CityDTO selectedCity = citiesRepository.getBean();
              selectedCity.setState(com.broudy.gcm.entity.State.LOCKED);
              citiesRepository.requestBeanReset();
              citiesRepository.setBean(selectedCity);
              final EmployeeMapsEditorController enhancedController = stageManager
                  .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR);
              enhancedController.finishedProcessing("Cancelled :)");
              enhancedController.showMapComboBox();
              hideProcessFlag = false;
            }
          });
          break;
        }
        case EMPLOYEE_CITY_APPROVAL_REQUEST_FETCHED_ALL: {
          if (sceneSwitcheroo.getCurrentlyVisibleView() == FXMLView.EMPLOYEE_REQUESTS_VIEWER) {
            setOnSucceeded(succeeded -> {
              logger.trace("EMPLOYEE_WORKSPACE_FETCHED_ALL");
              citiesRepository.setEmployeesWorkspace(
                  FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
              if (hideProcessFlag) {
                CityDTO selectedCity = citiesRepository.getBean();
                selectedCity.setState(com.broudy.gcm.entity.State.LOCKED);
                citiesRepository.requestBeanReset();
                citiesRepository.setBean(selectedCity);
                final EmployeeMapsEditorController enhancedController = stageManager
                    .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR);
                enhancedController.finishedProcessing("Cancelled :)");
                enhancedController.showMapComboBox();
                hideProcessFlag = false;
              }
            });
          }
          break;
        }
        case CITY_SAVED: {
          setOnScheduled(scheduled -> {
            logger.trace("CITY_SAVED");
            final CityDTO savedCity = serversResponse.getTheDTO();
            List<CityDTO> citiesList = citiesRepository.getBeansList();
            List<CityDTO> employeeWorkspace = citiesRepository.getEmployeesWorkspace();
            citiesList.removeIf(city -> city.getID() == savedCity.getID());
            employeeWorkspace.removeIf(city -> city.getID() == savedCity.getID());
            citiesList.add(savedCity);
            employeeWorkspace.add(savedCity);
            citiesList.sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));
            employeeWorkspace
                .sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));
            citiesRepository
                .setEmployeesWorkspace(FXCollections.observableArrayList(employeeWorkspace));
            citiesRepository.setBean(savedCity);
            citiesRepository.setBeansList(FXCollections.observableArrayList(citiesList));
          });
          setOnSucceeded(succeeded -> {
            if (hideProcessFlag) {
              ((EmployeeMapsEditorController) stageManager
                  .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR)).showMapComboBox();
              hideProcessFlag = false;
            }
          });
          break;
        }
        case CITY_SAVED_AND_LOCKED: {
          savedCity = serversResponse.getTheDTO();
          setOnScheduled(scheduled -> {
            logger.trace("CITY_SAVED_AND_LOCKED");
            List<CityDTO> citiesList = new ArrayList<>(citiesRepository.getBeansList());
            List<CityDTO> employeeWorkspace = new ArrayList<>(
                citiesRepository.getEmployeesWorkspace());

            citiesList.add(savedCity);
            employeeWorkspace.add(savedCity);

            citiesList.removeIf(city -> city.getID() == savedCity.getPreviousID());
            // employeeWorkspace.removeIf(city -> city.getID() == savedCity.getPreviousID());

            citiesList.sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));
            employeeWorkspace
                .sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));

            citiesRepository.setBeansList(FXCollections.observableArrayList(citiesList));
            citiesRepository
                .setEmployeesWorkspace(FXCollections.observableArrayList(employeeWorkspace));
            citiesRepository.setBean(savedCity);
          });
          setOnSucceeded(succeeded -> {
            if (hideProcessFlag) {
              ((EmployeeMapsEditorController) stageManager
                  .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR)).showMapComboBox();
              hideProcessFlag = false;
            }
          });
          break;
        }
        case CITY_DELETED: {
          setOnScheduled(scheduled -> {
            EmployeeMapsEditorController employeeMapsEditorController = stageManager
                .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR);
            employeeMapsEditorController.removedCityFromWorkspace();
          });
          break;
        }
        case CITY_APPROVAL_REQUESTED: {
          setOnScheduled(scheduled -> {
            logger.trace("CITY_APPROVAL_REQUESTED");
            List<CityDTO> citiesList = new ArrayList<>(citiesRepository.getBeansList());
            List<CityDTO> employeeWorkspace = new ArrayList<>(
                citiesRepository.getEmployeesWorkspace());

            citiesList.add(savedCity);
            employeeWorkspace.add(savedCity);

            citiesList.removeIf(city -> city.getID() == savedCity.getPreviousID());
            // employeeWorkspace.removeIf(city -> city.getID() == savedCity.getPreviousID());

            citiesList.sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));
            employeeWorkspace
                .sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));

            citiesRepository.setBeansList(FXCollections.observableArrayList(citiesList));
            citiesRepository
                .setEmployeesWorkspace(FXCollections.observableArrayList(employeeWorkspace));
            citiesRepository.setBean(savedCity);
          });
          break;
        }
        case CITY_APPROVAL_REQUEST_CANCELLED: {
          setOnScheduled(scheduled -> {
            // citiesRepository.setEmployeesWorkspace(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
            // employeeMapsEditorController.finishedProcessing("Cancelled :)");
          });
          break;
        }
        case CITY_APPROVED_APPROVAL_REQUEST:
        case CITY_REJECTED_APPROVAL_REQUEST: {
          setOnScheduled(scheduled -> {
            citiesRepository.getEmployeesWorkspace().remove(serversResponse.getTheDTO());
            if (pressedFromCityExplorer) {
              sceneSwitcheroo.hideDrawerView();
              pressedFromCityExplorer = false;
            }
          });
          break;
        }
        case CITY_READY_FOR_EDITING: {
          setOnSucceeded(succeeded -> {
            citiesRepository.setBean(serversResponse.getTheDTO());
            ((EmployeeMapsEditorController) stageManager
                .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR)).editSpecificCity();
          });
          break;
        }
        case CITY_UNLOCKED_FROM_EMPLOYEE: {
          setOnScheduled(scheduled -> {
            citiesRepository.getEmployeesWorkspace().remove(serversResponse.getTheDTO());
          });
          break;
        }
        case PUSH_WORKSPACE:{
          setOnSucceeded(succeeded->{
          logger.trace("EMPLOYEE_WORKSPACE_FETCHED_ALL");
          citiesRepository.getEmployeesWorkspace().clear();
          citiesRepository.setEmployeesWorkspace(
              FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
      }
    }

    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          switch (serversResponse.getResponse()) {
            case EMPLOYEE_CITY_FETCHED_ALL: {
              break;
            }
            case CITY_SAVED:
            case CITY_SAVED_AND_LOCKED: {
              /* Database will automatically create new LOCKED maps under the new city's ID ...
               *  The only thing needed now is to update the attached maps*/
              mapsRepository.requestAttachedMapsSaving();
              break;
            }
            case CITY_READY_FOR_EDITING: {
              citiesRepository.requestBeanReset();
              sceneSwitcheroo.loadView(FXMLView.EMPLOYEE_MAPS_EDITOR);
              break;
            }
            case PUSH_WORKSPACE:
            case EMPLOYEE_WORKSPACE_FETCHED_ALL:
            case EMPLOYEE_CITY_APPROVAL_REQUEST_FETCHED_ALL: {
              serversResponse.getListOfDTOs()
                  .sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));
              break;
            }
          }
          return null;
        }
      };
    }
  }

  public class MapRelatedResponseHandler extends ServerResponseHandler<MapDTO> {

    public MapRelatedResponseHandler(ServersResponse<MapDTO> serversResponse) {
      super(serversResponse);
    }

    @Override
    protected void setHandlers() {
      switch (serversResponse.getResponse()) {
        case MAP_OF_CITY_FETCHED_ALL: {
          setOnSucceeded(succeeded -> {
            logger.trace("MAP_OF_CITY_FETCHED_ALL");
            mapsRepository
                .setBeansList(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
        case EMPLOYEE_MAP_OF_CITY_FETCHED_ALL: {
          setOnSucceeded(succeeded -> {
            logger.trace("EMPLOYEE_MAP_OF_CITY_FETCHED_ALL");
            mapsRepository
                .setBeansList(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
        case MAP_DETACHED_FETCHED_ALL: {
          setOnSucceeded(succeeded -> {
            logger.trace("MAP_DETACHED_FETCHED_ALL");
            mapsRepository.setDetachedMapsList(
                FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
        case MAP_ATTACHED_MAPS_TO_CITY: {
          setOnSucceeded(succeeded -> {
            logger.trace("MAP_ATTACHED_MAPS_TO_CITY");
          });
          break;
        }
        case MAP_DETACHED_MAPS_FROM_CITY: {
          setOnSucceeded(succeeded -> {
            logger.trace("MAP_DETACHED_MAPS_FROM_CITY");
            employeeCitiesEditorController.listenToCitiesCBChanges();
            ((EmployeeMapsEditorController) stageManager
                .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR)).listenToCitiesCBChanges();
          });
          break;
        }
      }
    }

    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          switch (serversResponse.getResponse()) {
            case EMPLOYEE_MAP_OF_CITY_FETCHED_ALL: {
              mapsRepository.requestDetachedMaps();
              break;
            }
            case MAP_DETACHED_FETCHED_ALL: {
              break;
            }
            case MAP_ATTACHED_MAPS_TO_CITY: {
              mapsRepository.requestDetachedMapsSaving();
              break;
            }
            case MAP_DETACHED_MAPS_FROM_CITY: {
              mapsRepository.requestMapsOfCity();
              break;
            }
          }
          return null;
        }
      };
    }
  }

  public class TourRelatedResponseHandler extends ServerResponseHandler<TourDTO> {

    public TourRelatedResponseHandler(ServersResponse<TourDTO> serversResponse) {
      super(serversResponse);
    }

    @Override
    protected void setHandlers() {
      switch (serversResponse.getResponse()) {
        case TOUR_OF_MAP_FETCHED_ALL: {
          setOnScheduled(scheduled -> {
            toursRepository
                .setBeansList(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
      }
    }

    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          switch (serversResponse.getResponse()) {
            case TOUR_OF_MAP_FETCHED_ALL: {
              break;
            }
          }
          return null;
        }
      };
    }
  }


  public class SiteRelatedResponseHandler extends ServerResponseHandler<SiteDTO> {

    public SiteRelatedResponseHandler(ServersResponse<SiteDTO> serversResponse) {
      super(serversResponse);
    }

    @Override
    protected void setHandlers() {
      switch (serversResponse.getResponse()) {
        case SITE_OF_CITY_FETCHED_ALL: {
          setOnScheduled(scheduled -> {
            logger.trace("SITE_OF_CITY_FETCHED_ALL");
            sitesRepository
                .setBeansList(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
        case SITE_SAVED_AND_LOCKED: {
          setOnScheduled(scheduled -> {
            mapsRepository.getBean().getSites().add(serversResponse.getTheDTO().getID());
            sitesRepository.getBeansList().add(serversResponse.getTheDTO());
            sitesRepository.requestSitesOfMap();
            sitesRepository.setBean(serversResponse.getTheDTO());
            ((EmployeeMapsEditorController) stageManager
                .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR)).finishedProcessing("Saved :)");
          });
          break;
        }
        case SITE_SAVED: {
          setOnScheduled(scheduled -> {
            logger.trace("SITE_SAVED");
            List<SiteDTO> sitesList = new ArrayList<>(sitesRepository.getBeansList());
            sitesList.removeIf(site -> site.getID() == serversResponse.getTheDTO().getID());
            sitesList.add(serversResponse.getTheDTO());
            sitesRepository.setBeansList(FXCollections.observableArrayList(sitesList));
            ((EmployeeMapsEditorController) stageManager
                .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR)).savedSite();
            sitesRepository.setBean(serversResponse.getTheDTO());
          });
          break;
        }
        case SITE_DELETED: {
          setOnScheduled(scheduled -> {
            sitesRepository.requestRepoReset();
            sitesRepository.getSitesOfMap()
                .removeIf(site -> ((SiteDTO) site).getID() == serversResponse.getTheDTO().getID());
          });
          setOnSucceeded(succeeded -> {
            ((EmployeeMapsEditorController) stageManager
                .getControllerFor(FXMLView.EMPLOYEE_MAPS_EDITOR)).finishedProcessing("Deleted :)");
          });
          break;
        }
      }
    }

    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          switch (serversResponse.getResponse()) {
            case SITE_OF_CITY_FETCHED_ALL: {
              // sitesRepository.requestSitesOfTour();
              break;
            }
            case SITE_DELETED: {
              mapsRepository.getBean().getSites()
                  .remove(Integer.valueOf(serversResponse.getTheDTO().getID()));
              break;
            }
          }
          return null;
        }
      };
    }
  }

  public class PurchaseRelatedResponseHandler extends ServerResponseHandler<PurchaseDTO> {

    public PurchaseRelatedResponseHandler(ServersResponse<PurchaseDTO> serversResponse) {
      super(serversResponse);
    }

    @Override
    protected void setHandlers() {
      switch (serversResponse.getResponse()) {
        case PURCHASE_FETCHED_ALL_ACTIVE: {
          setOnScheduled(scheduled -> {
            logger.trace("PURCHASE_FETCHED_ALL_ACTIVE");
            purchasesRepository
                .setBeansList(FXCollections.observableArrayList(serversResponse.getListOfDTOs()));
          });
          break;
        }
        case PURCHASE_SUBSCRIPTION_EXTENDED: {
          setOnScheduled(scheduled -> {
            logger.trace("PURCHASE_SUBSCRIPTION_EXTENDED");
            Optional<? extends Renderable> purchaseToRemove = purchasesRepository.getBeansList()
                .stream().filter(
                    purchase -> ((PurchaseDTO) purchase).getID() == serversResponse.getTheDTO()
                        .getID()).findFirst();
            purchasesRepository.getBeansList().remove(purchaseToRemove.get());
            purchasesRepository.getBeansList().add(serversResponse.getTheDTO());
            // Optional<? extends Renderable> purchaseToSwap = purchasesRepository.getBeansList()
            //     .stream().filter(
            //         purchase -> ((PurchaseDTO) purchase).getID() == serversResponse.getTheDTO()
            //             .getID()).findFirst();
            // purchaseToSwap.ifPresent(
            //     purchase -> serversResponse.getTheDTO().deepCopyTo((PurchaseDTO) purchase));
          });
          break;
        }
        case PURCHASE_SUCCESSFUL_NEW_SUBSCRIPTION: {
          setOnScheduled(scheduled -> {
            logger.trace("PURCHASE_SUCCESSFUL_NEW_SUBSCRIPTION");
            purchasesRepository.getBeansList().add(serversResponse.getTheDTO());
          });
          break;
        }
      }
    }

    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          switch (serversResponse.getResponse()) {

          }
          return null;
        }
      };
    }
  }
}
