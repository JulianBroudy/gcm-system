package com.broudy.gcm.control.repos;

import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.CoordinatesDTO;
import com.broudy.gcm.entity.dtos.SiteCategory;
import com.broudy.gcm.entity.dtos.SiteDTO;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.sothawo.mapjfx.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Manages all user related matters.
 * Accepts {@see SiteDTO} and all its subclasses.
 * TODO provide a more in-depth summary to SitesRepository class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see SiteDTO
 */
public class SitesRepository extends BeanRepository<SiteDTO> {

  private static int countOfNewSites = -1;
  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;
  private final ToursRepository toursRepository;
  /**
   * This list contains sites that are attached to {@link MapsRepository#bean}.
   * This list gets updated on every {@link #setBeansList} call.
   */
  private final ListProperty<SiteDTO> sitesOfMap;
  private final ListProperty<SiteDTO> sitesOfTour;
  private final ListProperty<SiteDTO> sitesAvailableForTour;
  private SiteDTO DEFAULT_SITE;

  /**
   * Constructs the basis of any DTORepository.
   * Initializes the {@link #bean}'s property and the {@link #superBean} with the passed dto as well
   * as initializes {@link #beansList}.
   * Constructor calls {@link #setupInitialBean} and {@link #startInternalInitialization()}.
   */
  @Inject
  public SitesRepository(CitiesRepository citiesRepository, MapsRepository mapsRepository,
      ToursRepository toursRepository) {
    super(new SiteDTO());
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.toursRepository = toursRepository;
    this.sitesOfMap = new SimpleListProperty<>(FXCollections.observableArrayList());
    this.sitesOfTour = new SimpleListProperty<>(FXCollections.observableArrayList());
    this.sitesAvailableForTour = new SimpleListProperty<>(FXCollections.observableArrayList());
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
        resetDefaultSite();
      }
      logger.debug(newValue);
      getSuperBean().setBean(bean.get());
    });
  }

  /**
   * This method is called from within the {@link #BeanRepository} constructor.
   * It is used to initialize subclasses' private specific DEFAULT_DTO with the <code>theDTO</code>
   * passed to the constructor.
   *
   * @param emptySiteDTO is the passed DTO that would be set as the specific DEFAULT_DTO in
   *     subclass.
   */
  @Override
  protected <S extends SiteDTO> void setupInitialBean(S emptySiteDTO) {
    this.DEFAULT_SITE = emptySiteDTO;
    resetDefaultSite();
  }

  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */

  private void resetDefaultSite() {
    logger.trace(TextColors.PURPLE.colorThis("resetDefaultSite") + " was just invoked!");
    DEFAULT_SITE.setID(-2);
    DEFAULT_SITE.setName("");
    DEFAULT_SITE.setDescription("");
    DEFAULT_SITE.setRecommendedVisitDuration("");
    DEFAULT_SITE.setCategory(new SiteCategory());
    DEFAULT_SITE.setState(State.DEFAULT);
    setBean(DEFAULT_SITE);
  }

  /**
   * Sets the beansList ObservableList.
   *
   * @param beansList is the beansList's new value.
   */
  @Override
  public void setBeansList(ObservableList<? super SiteDTO> beansList) {
    super.setBeansList(beansList);
    logger.info("BeansRepository: {}", this.getClass().hashCode());
    logger.info("{}'s beansList has been set to: {}", bean.getClass().getSimpleName(),
        getBeansList());
    requestSitesOfMap();
  }


  /**
   * Gets the sitesOfMap's ObservableList.
   *
   * @return the value of the sitesOfMap.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getSitesOfMap() {
    logger.info("{}'s beansList has been retrieved.", bean.get().getClass().getSimpleName());
    return (ObservableList<RenderableDTO>) sitesOfMap.get();
  }

  /**
   * Sets the sitesOfMap's ObservableList.
   *
   * @param allSitesOfMap is the sitesOfMap new value.
   */
  public void setSitesOfMap(ObservableList<? super SiteDTO> allSitesOfMap) {
    sitesOfMap.set((ObservableList<SiteDTO>) allSitesOfMap);
    logger.info("{}'s unattached beansList has been set to: {}", bean.getClass().getSimpleName(),
        sitesOfMap);
  }


  /**
   * Gets the sitesOfMap's ListProperty.
   *
   * @return the sitesOfMap's ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> sitesOfMapList() {
    return (ListProperty<RenderableDTO>) sitesOfMap;
  }


  /**
   * Gets the sitesOfTour's ObservableList.
   *
   * @return the value of the sitesOfTour.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getSitesOfTour() {
    logger.info("{}'s beansList has been retrieved.", bean.get().getClass().getSimpleName());
    return (ObservableList<RenderableDTO>) sitesOfTour.get();
  }

  /**
   * Sets the sitesOfTour's ObservableList.
   *
   * @param allSitesOfTour is the sitesOfTour new value.
   */
  public void setSitesOfTour(ObservableList<? super SiteDTO> allSitesOfTour) {
    sitesOfTour.set((ObservableList<SiteDTO>) allSitesOfTour);
    logger.info("{}'s unattached beansList has been set to: {}", bean.getClass().getSimpleName(),
        sitesOfTour);
  }


  /**
   * Gets the sitesOfTour's ListProperty.
   *
   * @return the sitesOfTour's ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> sitesOfTourList() {
    return (ListProperty<RenderableDTO>) sitesOfTour;
  }
///

  /**
   * Gets the sitesAvailableForTour's ObservableList.
   *
   * @return the value of the sitesAvailableForTour.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getSitesAvailableForTour() {
    logger.info("{}'s sitesAvailableForTour has been retrieved.",
        bean.get().getClass().getSimpleName());
    return (ObservableList<RenderableDTO>) sitesAvailableForTour.get();
  }

  /**
   * Sets the sitesAvailableForTour's ObservableList.
   *
   * @param allSitesAvailableForTour is the sitesAvailableForTour new value.
   */
  public void setSitesAvailableForTour(ObservableList<? super SiteDTO> allSitesAvailableForTour) {
    sitesAvailableForTour.set((ObservableList<SiteDTO>) allSitesAvailableForTour);
    logger.info("{}'s sitesAvailableForTour has been set to: {}", bean.getClass().getSimpleName(),
        sitesAvailableForTour);
  }

  /**
   * Gets the sitesAvailableForTour's ListProperty.
   *
   * @return the sitesAvailableForTour's ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> sitesAvailableForTourList() {
    return (ListProperty<RenderableDTO>) sitesAvailableForTour;
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
   * This method sets the {@link #bean} with a newly created site.
   */
  public void requestNewSitePlaceholder(Coordinate center) {
    if (bean.get().getState() != State.NEW) {
      logger.trace(TextColors.PURPLE.colorThis("requestNewSitePlaceholder") + " was just invoked!");
      final SiteDTO newSitePlaceholder = new SiteDTO();
      newSitePlaceholder.setID(-1);
      newSitePlaceholder.setCityID(citiesRepository.getBean().getID());
      newSitePlaceholder.setName("Enter name for new site...");
      newSitePlaceholder.setDescription("Enter description for new site...");
      newSitePlaceholder.setRecommendedVisitDuration("Enter duration...");
      newSitePlaceholder.setCategory(new SiteCategory());
      newSitePlaceholder.setCoordinates(center);
      CoordinatesDTO coordinatesDTO = newSitePlaceholder.getCoordinatesDTO();
      // TODO move coordinate a little bit from center
      newSitePlaceholder.setState(State.NEW);
      setBean(newSitePlaceholder);
    }
  }

  public void requestRepoReset() {
    logger.trace(TextColors.PURPLE.colorThis("requestRepoReset") + " was just invoked!");
    resetDefaultSite();
  }

  public void requestSitesOfMap() {
    logger.trace(TextColors.PURPLE.colorThis("requestSitesOfMap") + " was just invoked!");
    List<Integer> listOfSiteIDs = mapsRepository.getBean().getSites();
    List<SiteDTO> sitesOfSelectedMap = beansList.stream()
        .filter(site -> listOfSiteIDs.contains(site.getID())).collect(Collectors.toList());
    setSitesOfMap(FXCollections.observableArrayList(sitesOfSelectedMap));
    logger.info("sitesOfMap has been set to: {}", getSitesOfMap());
  }

  public void requestSitesOfTour() {
    logger.trace(TextColors.PURPLE.colorThis("requestSitesOfTour") + " was just invoked!");
    List<Integer> listOfSiteIDs = toursRepository.getBean().getSites();
    List<SiteDTO> sitesOfSelectedTour = new ArrayList<>();
    List<SiteDTO> filteredSitesOfMap = new ArrayList<>();
    for (SiteDTO site : sitesOfMap.get()) {
      if (listOfSiteIDs.contains(site.getID())) {
        sitesOfSelectedTour.add(site);
      } else {
        filteredSitesOfMap.add(site);
      }
    }
    setSitesAvailableForTour(FXCollections.observableArrayList(filteredSitesOfMap));
    setSitesOfTour(FXCollections.observableArrayList(sitesOfSelectedTour));
    logger.info("setSitesAvailableForTour has been set to: {}", getSitesAvailableForTour());
    logger.info("setSitesOfTour has been set to: {}", getSitesOfTour());
  }

  public void requestSitesOfCity() {
    logger.trace(TextColors.PURPLE.colorThis("requestSitesOfCity") + " was just invoked!");
    ClientsInquiry<SiteDTO> siteInquiry = ClientsInquiry.of(SiteDTO.class);
    SiteDTO siteRequest = new SiteDTO();
    siteRequest.setCityID(citiesRepository.getBean().getID());
    siteInquiry.setTheDTO(siteRequest);
    siteInquiry.setInquiry(Inquiry.SITE_OF_CITY_FETCH_ALL);
    sendInquiry(siteInquiry);
  }


  /**
   * This method sends the server a request to create or update currently selected site.
   * Created/Updated site gets a {@link com.broudy.gcm.entity.State#LOCKED} state. i.e., it is not
   * released to the public before before an employee with {@link com.broudy.gcm.entity.UserClassification}
   * of CONTENT_MANAGER and up approves the release of city this site's map belongs to.
   */
  public void requestSiteSaving() {
    logger.trace(TextColors.PURPLE.colorThis("requestSiteSaving") + " was just invoked!");
    ClientsInquiry<SiteDTO> siteInquiry = ClientsInquiry.of(SiteDTO.class);
    siteInquiry.setTheDTO(bean.get());
    if (bean.get().getState() == State.NEW) {
      siteInquiry.setExtraParameters(mapsRepository.getBean().getID());
      siteInquiry.setInquiry(Inquiry.SITE_SAVE_AND_LOCK);
    } else {
      siteInquiry.setInquiry(Inquiry.SITE_SAVE);
    }
    sendInquiry(siteInquiry);
  }

  public void requestSiteDeletion() {
    logger.trace(TextColors.PURPLE.colorThis("requestSiteDeletion") + " was just invoked!");
    sendInquiry(Inquiry.SITE_DELETE);
  }

  @Override
  protected void sendInquiry(SiteDTO siteToAttach, Inquiry inquiry) {
    final ClientsInquiry<SiteDTO> siteInquiry = ClientsInquiry.of(SiteDTO.class);
    siteInquiry.setTheDTO(siteToAttach);
    siteInquiry.setInquiry(inquiry);
    GCMClient.getInstance().send(siteInquiry);
  }

}