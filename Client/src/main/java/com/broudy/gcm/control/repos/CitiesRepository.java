package com.broudy.gcm.control.repos;

import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.UserClassification;
import com.broudy.gcm.entity.dtos.CityDTO;
import com.broudy.gcm.entity.dtos.EmployeeDTO;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Manages all user related matters.
 * Accepts {@see CityDTO} and all its subclasses.
 * TODO provide a more in-depth summary to CitiesRepository class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see CityDTO
 */
public class CitiesRepository extends BeanRepository<CityDTO> {

  private final UsersRepository usersRepository;

  /**
   * This list does not contain any duplicates. To be more specific, it contains each city only once
   * with its latest {@link com.broudy.gcm.entity.State}. i.e., each city that has its {@link
   * com.broudy.gcm.entity.interfaces.IStatable#previousID} set to -1.
   * This list gets updated on every {@link #setBeansList} call.
   */
  private final ListProperty<CityDTO> cleanedUpCities;
  private final ListProperty<CityDTO> employeesWorkspace;
  private final ListProperty<CityDTO> cityApprovalRequests;

  private CityDTO DEFAULT_CITY;

  /**
   * Constructs the basis of any DTORepository.
   * Initializes the {@link #bean}'s property and the {@link #superBean} with the passed dto as well
   * as initializes {@link #beansList}.
   * Constructor calls {@link #setupInitialBean} and {@link #startInternalInitialization()}.
   */
  @Inject
  public CitiesRepository(UsersRepository usersRepository) {
    super(new CityDTO());
    this.usersRepository = usersRepository;
    cleanedUpCities = new SimpleListProperty<>(FXCollections.observableArrayList());
    employeesWorkspace = new SimpleListProperty<>(FXCollections.observableArrayList());
    cityApprovalRequests = new SimpleListProperty<>(FXCollections.observableArrayList());
  }

  /**
   * This is the last method called from within the {@link #BeanRepository} constructor.
   * <p>
   * Should implement any and all initialization needs.
   */
  @Override
  protected void startInternalInitialization() {
    // Bind bean to superBean
    bean.addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        resetDefaultCity();
        logger.trace("City became null");
        getSuperBean().setBean(DEFAULT_CITY);
      } else {
        logger.debug(newValue);
        getSuperBean().setBean(bean.get());
      }
    });
  }

  /**
   * This method is called from within the {@link #BeanRepository} constructor.
   * It is used to initialize subclasses' private specific DEFAULT_DTO with the <code>theDTO</code>
   * passed to the constructor.
   *
   * @param emptyCityDTO is the passed DTO that would be set as the specific DEFAULT_DTO in
   *     subclass.
   */
  @Override
  protected <C extends CityDTO> void setupInitialBean(C emptyCityDTO) {
    this.DEFAULT_CITY = emptyCityDTO;
    resetDefaultCity();
  }

  private void resetDefaultCity() {
    logger.trace(TextColors.PURPLE.colorThis("resetDefaultCity") + " was just invoked!");
    DEFAULT_CITY.setID(-2);
    DEFAULT_CITY.setName("");
    DEFAULT_CITY.setDescription("");
    DEFAULT_CITY.setNumberOfMaps(0);
    DEFAULT_CITY.setCollectionVersion(0D);
    DEFAULT_CITY.setState(State.DEFAULT);
    setBean(DEFAULT_CITY);
  }

  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */


  /**
   * Gets the cleanedUpCities' ObservableList.
   *
   * @return the value of the cleanedUpCities.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getCleanedUpCitiesList() {
    logger.info("{}'s beansList has been retrieved.", bean.get().getClass().getSimpleName());
    return (ObservableList<RenderableDTO>) cleanedUpCities.get();
  }

  /**
   * Sets the cleanedUpCities' ObservableList.
   *
   * @param approvedCitiesList is the cleanedUpCities' new value.
   */
  public void setCleanedUpCitiesList(ObservableList<? super CityDTO> approvedCitiesList) {
    cleanedUpCities.set((ObservableList<CityDTO>) approvedCitiesList);
    logger.info("{}'s unattached beansList has been set to: {}", bean.getClass().getSimpleName(),
        cleanedUpCities);
  }

  /**
   * Gets the cleanedUpCities' ListProperty.
   *
   * @return the cleanedUpCities' ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> cleanedUpCitiesList() {
    return (ListProperty<RenderableDTO>) cleanedUpCities;
  }


  /**
   * Gets the employeesWorkspace ObservableList.
   *
   * @return the value of the employeesWorkspace.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getEmployeesWorkspace() {
    logger.info("{}'s beansList has been retrieved.", DEFAULT_CITY.getClass().getSimpleName());
    return (ObservableList<RenderableDTO>) employeesWorkspace.get();
  }

  /**
   * Sets the employeesWorkspace ObservableList.
   *
   * @param employeesWorkspace is the employeesWorkspace new value.
   */
  public void setEmployeesWorkspace(ObservableList<? super CityDTO> employeesWorkspace) {
    this.employeesWorkspace.set((ObservableList<CityDTO>) employeesWorkspace);
    logger.info("{}'s unattached beansList has been set to: {}", CityDTO.class, employeesWorkspace);
  }

  /**
   * Gets the employeesWorkspace ListProperty.
   *
   * @return the employeesWorkspace ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> employeesWorkspaceProperty() {
    return (ListProperty<RenderableDTO>) employeesWorkspace;
  }

  /**
   * Gets the cityApprovalRequests ObservableList.
   *
   * @return the value of the cityApprovalRequests.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getCityApprovalRequests() {
    logger.info("{}'s beansList has been retrieved.", bean.get().getClass().getSimpleName());
    return (ObservableList<RenderableDTO>) cityApprovalRequests.get();
  }

  /**
   * Sets the cityApprovalRequests ObservableList.
   *
   * @param cityApprovalRequests is the cityApprovalRequests new value.
   */
  public void setCityApprovalRequests(ObservableList<? super CityDTO> cityApprovalRequests) {
    this.cityApprovalRequests.set((ObservableList<CityDTO>) cityApprovalRequests);
    logger.info("{}'s unattached beansList has been set to: {}", bean.getClass().getSimpleName(),
        cityApprovalRequests);
  }

  /**
   * Gets the cityApprovalRequests ListProperty.
   *
   * @return the cityApprovalRequests ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> cityApprovalRequests() {
    return (ListProperty<RenderableDTO>) cityApprovalRequests;
  }

  /*
   *     ____ _ _            _         ___                   _      _
   *    / ___| (_) ___ _ __ | |_ ___  |_ _|_ __   __ _ _   _(_)_ __(_) ___  ___
   *   | |   | | |/ _ \ '_ \| __/ __|  | || '_ \ / _` | | | | | '__| |/ _ \/ __|
   *   | |___| | |  __/ | | | |_\__ \  | || | | | (_| | |_| | | |  | |  __/\__ \
   *    \____|_|_|\___|_| |_|\__|___/ |___|_| |_|\__, |\__,_|_|_|  |_|\___||___/
   *                                                |_|
   */

  /**
   * This method sends the server a request to fetch all the cities available in GCM's system.
   */
  public void requestAllCities() {
    logger.trace(TextColors.PURPLE.colorThis("requestAllCities") + " was just invoked!");
    if (usersRepository.getBean().getClassification() == UserClassification.CUSTOMER) {
      sendInquiry(Inquiry.CITY_FETCH_ALL);
    } else {
      sendInquiry(Inquiry.EMPLOYEE_CITY_FETCH_ALL);
    }
  }

  /**
   * This method sets the {@link #bean} with a newly created city.
   */
  public void requestNewCityPlaceholder() {
    logger.trace(TextColors.PURPLE.colorThis("requestNewCityPlaceholder") + " was just invoked!");
    CityDTO newCityPlaceholder = new CityDTO();
    newCityPlaceholder.setID(-1);
    newCityPlaceholder.setName("Enter name for new city...");
    newCityPlaceholder.setDescription("Enter description for new city...");
    newCityPlaceholder.setCollectionVersion(1.0D);
    // TODO: fix "resetDefaultCity" method! or delete...
    setBean(newCityPlaceholder);
  }

  /**
   * This method sends the server a request to create or update currently selected city.
   * Created/Updated city gets a {@link com.broudy.gcm.entity.State#LOCKED} state. i.e., it is not
   * released to the public before an employee with {@link com.broudy.gcm.entity.UserClassification}
   * of CONTENT_MANAGER and up approves it.
   */
  public void requestCitySaving() {
    logger.trace(TextColors.PURPLE.colorThis("requestCitySaving") + " was just invoked!");
    State stateOfCity = bean.get().getState();
    if (stateOfCity == State.APPROVED || stateOfCity == State.NEW) {
      sendInquiry(Inquiry.CITY_SAVE_AND_LOCK);
    } else {
      sendInquiry(Inquiry.CITY_SAVE);
    }
  }

  public void requestRepoReset() {
    logger.trace(TextColors.PURPLE.colorThis("requestRepoReset") + " was just invoked!");
    resetDefaultCity();
    // requestAllCities();
  }

  public void requestCityApprovalRequest() {
    logger.trace(TextColors.PURPLE.colorThis("requestCityApproval") + " was just invoked!");
    ClientsInquiry<CityDTO> cityInquiry = ClientsInquiry.of(CityDTO.class);
    cityInquiry.setExtraParameters(((EmployeeDTO) usersRepository.getBean()).getEmployeeID());
    cityInquiry.setTheDTO(bean.get());
    cityInquiry.setInquiry(Inquiry.CITY_REQUEST_APPROVAL);
    // sendInquiry(Inquiry.CITY_REQUEST_APPROVAL);
    sendInquiry(cityInquiry);
  }

  public void requestCityApprovalRequests() {
    logger.trace(TextColors.PURPLE.colorThis("requestCityApprovalRequests") + " was just invoked!");
    sendInquiry(Inquiry.EMPLOYEE_CITY_APPROVAL_REQUEST_FETCH_ALL);
  }

  public void requestApprovalRequestCancellation() {
    logger.trace(
        TextColors.PURPLE.colorThis("requestApprovalRequestCancellation") + " was just invoked!");
    ClientsInquiry<CityDTO> cityInquiry = ClientsInquiry.of(CityDTO.class);
    cityInquiry.setExtraParameters(((EmployeeDTO) usersRepository.getBean()).getEmployeeID());
    cityInquiry.setTheDTO(bean.get());
    cityInquiry.setInquiry(Inquiry.CITY_CANCEL_APPROVAL_REQUEST);
    sendInquiry(cityInquiry);
    // sendInquiry(Inquiry.CITY_REQUEST_APPROVAL);
    // sendInquiry(cityInquiry);
    // sendInquiry(Inquiry.CITY_CANCEL_APPROVAL_REQUEST);
  }

  public void requestCityApprovalRejection() {
    logger
        .trace(TextColors.PURPLE.colorThis("requestCityApprovalRejection") + " was just invoked!");
    sendInquiry(Inquiry.CITY_REJECT_APPROVAL_REQUEST);
  }

  public void requestCityApprovalApprove() {
    logger.trace(TextColors.PURPLE.colorThis("requestCityApprovalApprove") + " was just invoked!");
    sendInquiry(Inquiry.CITY_APPROVE_APPROVAL_REQUEST);
  }


  public void requestCityRejectedToLocked() {
    logger.trace(TextColors.PURPLE.colorThis("requestCityRejectedToLocked") + " was just invoked!");
    sendInquiry(Inquiry.CITY_REJECTED_TO_LOCKED);
  }

  public void requestCityDeletion() {
    logger.trace(TextColors.PURPLE.colorThis("requestCityDeletion") + " was just invoked!");
    ClientsInquiry<CityDTO> cityInquiry = ClientsInquiry.of(CityDTO.class);
    cityInquiry.setExtraParameters(((EmployeeDTO) usersRepository.getBean()).getEmployeeID());
    cityInquiry.setTheDTO(bean.get());
    cityInquiry.setInquiry(Inquiry.CITY_DELETE_ONE);
    sendInquiry(cityInquiry);
    // sendInquiry(Inquiry.CITY_DELETE_ONE);
  }

  public void requestEmployeeWorkspace() {
    logger.trace(TextColors.PURPLE.colorThis("requestEmployeeWorkspace") + " was just invoked!");
    // CustomDTORequestWrapper workspaceRequest = new CustomDTORequestWrapper();
    // workspaceRequest.addParemeter(((EmployeeDTO) usersRepository.getBean()).getEmployeeID());
    // workspaceRequest.setRequest(Inquiry.EMPLOYEE_WORKSPACE_FETCH_ALL);
    // GCMClient.getInstance().send(workspaceRequest);
    ClientsInquiry<CityDTO> cityInquiry = ClientsInquiry.of(CityDTO.class)
        .setExtraParameters(((EmployeeDTO) usersRepository.getBean()).getEmployeeID());
    cityInquiry.setInquiry(Inquiry.EMPLOYEE_WORKSPACE_FETCH_ALL);
    cityInquiry.setTheDTO(new CityDTO());
    sendInquiry(cityInquiry);
  }


  public void requestRemovalOfCityFromWorkspace() {
    logger.trace(
        TextColors.PURPLE.colorThis("requestRemovalOfCityFromWorkspace") + " was just invoked!");
    sendInquiry(Inquiry.CITY_UNLOCK_FROM_EMPLOYEE);
  }


  /*
   *   ____                           _                       __  __      _   _               _
   *  / ___|___  _ ____   _____ _ __ (_) ___ _ __   ___ ___  |  \/  | ___| |_| |__   ___   __| |___
   * | |   / _ \| '_ \ \ / / _ \ '_ \| |/ _ \ '_ \ / __/ _ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
   * | |__| (_) | | | \ V /  __/ | | | |  __/ | | | (_|  __/ | |  | |  __/ |_| | | | (_) | (_| \__ \
   *  \____\___/|_| |_|\_/ \___|_| |_|_|\___|_| |_|\___\___| |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
   *
   */

  protected void sendInquiry(CityDTO cityToAttach, Inquiry inquiry) {
    final ClientsInquiry<CityDTO> cityInquiry = ClientsInquiry.of(CityDTO.class);
    cityInquiry.setTheDTO(cityToAttach);
    cityInquiry.setInquiry(inquiry);
    GCMClient.getInstance().send(cityInquiry);
  }


  public void requestBeanReset() {
    resetDefaultCity();
  }

}
