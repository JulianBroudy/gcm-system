package com.broudy.gcm.control.repos;

import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.TourDTO;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import java.util.ArrayList;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Manages all user related matters.
 * Accepts {@see SiteDTO} and all its subclasses.
 * TODO provide a more in-depth summary to ToursRepository class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see TourDTO
 */
public class ToursRepository extends BeanRepository<TourDTO> {

  private static int countOfNewSites = -1;
  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;
  private final ListProperty<TourDTO> toursOfMap;
  /**
   * This list contains sites that are attached to {@link MapsRepository#bean}.
   * This list gets updated on every {@link #setBeansList} call.
   */
  private TourDTO DEFAULT_TOUR;


  /**
   * Constructs the basis of any DTORepository.
   * Initializes the {@link #bean}'s property and the {@link #superBean} with the passed dto as well
   * as initializes {@link #beansList}.
   * Constructor calls {@link #setupInitialBean} and {@link #startInternalInitialization()}.
   */
  @Inject
  public ToursRepository(CitiesRepository citiesRepository, MapsRepository mapsRepository) {
    super(new TourDTO());
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.toursOfMap = new SimpleListProperty<TourDTO>(FXCollections.observableArrayList());
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
        resetDefaultTour();
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
   * @param emptyTourDTO is the passed DTO that would be set as the specific DEFAULT_DTO in
   *     subclass.
   */
  @Override
  protected <T extends TourDTO> void setupInitialBean(T emptyTourDTO) {
    this.DEFAULT_TOUR = emptyTourDTO;
    resetDefaultTour();
  }


  private void resetDefaultTour() {
    logger.trace(TextColors.PURPLE.colorThis("resetDefaultTour") + " was just invoked!");
    DEFAULT_TOUR.setID(-2);
    DEFAULT_TOUR.setMapID(-1);
    DEFAULT_TOUR.setSites(new ArrayList<>());
    DEFAULT_TOUR.setPreviousID(-1);
    DEFAULT_TOUR.setState(State.DEFAULT);
    setBean(DEFAULT_TOUR);
  }

  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */

  // /**
  //  * Sets the beansList ObservableList.
  //  *
  //  * @param beansList is the beansList's new value.
  //  */
  // @Override
  // public void setBeansList(ObservableList<? super TourDTO> beansList) {
  //   super.setBeansList(beansList);
  //   logger.info("BeansRepository: {}", this.getClass().hashCode());
  //   logger.info("{}'s beansList has been set to: {}", bean.getClass().getSimpleName(),
  //       toursOfMap);
  //   requestToursOfMap();
  // }

  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getToursOfMap() {
    return (ObservableList<RenderableDTO>) toursOfMap.get();
  }

  public void setToursOfMap(ObservableList<? super TourDTO> toursOfMap) {
    this.toursOfMap.set((ObservableList<TourDTO>) toursOfMap);
  }

  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> toursOfMapProperty() {
    return (ListProperty<RenderableDTO>) toursOfMap;
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
  public void requestNewTourPlaceholder() {
    if (bean.get().getState() != State.NEW) {
      logger.trace(TextColors.PURPLE.colorThis("requestNewTourPlaceholder") + " was just invoked!");
      final TourDTO newTourPlaceholder = new TourDTO();
      newTourPlaceholder.setID(-1);
      newTourPlaceholder.setMapID(citiesRepository.getBean().getID());
      newTourPlaceholder.setState(State.NEW);
      setBean(newTourPlaceholder);
    }
  }

  public void requestRepoReset() {
    logger.trace(TextColors.PURPLE.colorThis("requestRepoReset") + " was just invoked!");
    resetDefaultTour();
  }

  public void requestToursOfMap() {
    logger.trace(TextColors.PURPLE.colorThis("requestToursOfMap") + " was just invoked!");
    ClientsInquiry<TourDTO> siteInquiry = ClientsInquiry.of(TourDTO.class);
    TourDTO tourRequest = new TourDTO();
    siteInquiry.setTheDTO(tourRequest);
    siteInquiry.setExtraParameters(mapsRepository.getBean().getID());
    siteInquiry.setInquiry(Inquiry.TOUR_OF_MAP_FETCH_ALL);
    sendInquiry(siteInquiry);
  }

  /**
   * This method sends the server a request to create or update currently selected site.
   * Created/Updated site gets a {@link State#LOCKED} state. i.e., it is not
   * released to the public before before an employee with {@link com.broudy.gcm.entity.UserClassification}
   * of CONTENT_MANAGER and up approves the release of city this site's map belongs to.
   */
  public void requestTourSaving() {
    logger.trace(TextColors.PURPLE.colorThis("requestSiteSaving") + " was just invoked!");
    ClientsInquiry<TourDTO> tourInquiry = ClientsInquiry.of(TourDTO.class);
    tourInquiry.setTheDTO(bean.get());
    if (bean.get().getState() == State.NEW) {
      tourInquiry.setExtraParameters(mapsRepository.getBean().getID());
      tourInquiry.setInquiry(Inquiry.TOUR_SAVE_AND_LOCK);
    } else {
      tourInquiry.setInquiry(Inquiry.TOUR_SAVE);
    }
    sendInquiry(tourInquiry);
  }

  public void requestTourDeletion() {
    logger.trace(TextColors.PURPLE.colorThis("requestSiteDeletion") + " was just invoked!");
    sendInquiry(Inquiry.TOUR_DELETE);
  }

  @Override
  protected void sendInquiry(TourDTO toutToAttach, Inquiry inquiry) {
    final ClientsInquiry<TourDTO> tourInquiry = ClientsInquiry.of(TourDTO.class);
    tourInquiry.setTheDTO(toutToAttach);
    tourInquiry.setInquiry(inquiry);
    GCMClient.getInstance().send(tourInquiry);
  }
}