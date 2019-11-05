package com.broudy.gcm.control.repos;

import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.UserClassification;
import com.broudy.gcm.entity.dtos.MapDTO;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * TODO provide a summary to MapsRepository class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class MapsRepository extends BeanRepository<MapDTO> {

  private final UsersRepository usersRepository;
  private final CitiesRepository citiesRepository;

  private final ListProperty<MapDTO> detachedMaps;
  private List<MapDTO> attachedMapsBackup;
  private List<MapDTO> detachedMapsBackup;
  private MapDTO DEFAULT_MAP;

  /**
   * Constructs the basis of any DTORepository.
   * Initializes the {@link #bean}'s property and the {@link #superBean} with the passed dto as well
   * as initializes {@link #beansList}.
   * Constructor calls {@link #setupInitialBean} and {@link #startInternalInitialization()}.
   */
  @Inject
  public MapsRepository(UsersRepository usersRepository, CitiesRepository citiesRepository) {
    super(new MapDTO());
    this.usersRepository = usersRepository;
    this.citiesRepository = citiesRepository;
    detachedMaps = new SimpleListProperty<>(FXCollections.observableArrayList());
    attachedMapsBackup = new ArrayList<>();
    detachedMapsBackup = new ArrayList<>();
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
        resetDefaultMap();
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
   * @param emptyMapDTO is the passed DTO that would be set as the specific DEFAULT_DTO in
   *     subclass.
   */
  @Override
  protected <M extends MapDTO> void setupInitialBean(M emptyMapDTO) {
    this.DEFAULT_MAP = emptyMapDTO;
    resetDefaultMap();
  }

  private void resetDefaultMap() {
    logger.trace(TextColors.PURPLE.colorThis("resetDefaultMap") + " was just invoked!");
    DEFAULT_MAP.setID(-2);
    DEFAULT_MAP.setName("");
    DEFAULT_MAP.setVersion(0);
    DEFAULT_MAP.setState(State.DEFAULT);
    setBean(DEFAULT_MAP);
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
   * Sets the beansList ObservableList.
   *
   * @param beansList is the beansList's new value.
   */
  @Override
  public void setBeansList(ObservableList<? super MapDTO> beansList) {

    attachedMapsBackup = (List<MapDTO>) beansList;
    super.setBeansList(FXCollections.observableArrayList(beansList));


    /*
    List<MapDTO> mapsList = (List<MapDTO>) beansList;

    final int currentlySelectedcityID = citiesRepository.getBean().getID();

    unattachedMapsBackup.clear();
    attachedMapsBackup.clear();
    for (MapDTO map : mapsList) {
      if (map.getCityID() == currentlySelectedcityID) {
        attachedMapsBackup.add(map);
      } else {
        unattachedMapsBackup.add(map);
      }
    }

    *//*

    List<MapDTO> mapsToBeRemoved = new ArrayList<>();
    for (MapDTO map : mapsList) {
      // if(mapsList.stream().anyMatch(mapS->mapS.getPreviousID()==map.getID()))
      //   mapsToBeRemoved.add(map);
      int mapsPreviousID = map.getPreviousID();
      if (mapsPreviousID != -1) {
        mapsToBeRemoved.addAll(
            mapsList.stream().filter(mapUnderTest -> mapUnderTest.getID() == mapsPreviousID)
                .collect(Collectors.toList()));
      }
    }
    mapsList.removeAll(mapsToBeRemoved);

    availableMapsBackup = mapsList.stream().filter(map -> map.getCityID() == 0)
        .collect(Collectors.toList());
    attachedMapsBackup = mapsList.stream().filter(map -> map.getCityID() != 0)
        .collect(Collectors.toList());*//*

    unattachedMapsBackup.sort((map1, map2) -> map1.getName().compareToIgnoreCase(map2.getName()));
    attachedMapsBackup.sort((map1, map2) -> map1.getName().compareToIgnoreCase(map2.getName()));

    setUnattachedMapsList(FXCollections.observableArrayList(unattachedMapsBackup));
    super.setBeansList(FXCollections.observableArrayList(attachedMapsBackup));

    logger.info("BeansRepository: {}", this.getClass().hashCode());
    logger.info("{}'s beansList has been set to: {}", bean.getClass().getSimpleName(),
        getBeansList());*/
  }

  /**
   * Gets the detachedMaps' ObservableList.
   *
   * @return the value of the detachedMaps.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getDetachedMapsList() {
    logger.info("{}'s beansList has been retrieved.", bean.get().getClass().getSimpleName());
    return (ObservableList<RenderableDTO>) detachedMaps.get();
  }

  /**
   * Sets the detachedMaps' ObservableList.
   *
   * @param detachedMapsList is the detachedMaps' new value.
   */
  public void setDetachedMapsList(ObservableList<? super MapDTO> detachedMapsList) {

    detachedMapsBackup = (List<MapDTO>) detachedMapsList;
    detachedMaps.set(FXCollections.observableArrayList(detachedMapsBackup));
    logger.info("{}'s unattached beansList has been set to: {}", bean.getClass().getSimpleName(),
        detachedMaps);
  }

  /**
   * Gets the availableMaps' ListProperty.
   *
   * @return the availableMaps' ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> detachedMapsListProperty() {
    return (ListProperty<RenderableDTO>) detachedMaps;
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
   * This method sends the server a request to fetch all the maps that are attached to the currently
   * selected city in {@link CitiesRepository}.
   * In addition, it will fetch all available maps, i.e., NOT attached to any city.
   * See {@link Inquiry#MAP_OF_CITY_FETCH_ALL}.
   */
  public void requestMapsOfCity() {
    logger.trace(TextColors.PURPLE.colorThis("requestMapsOfCity") + " was just invoked!");
    MapDTO map = bean.get().getDeepCopy();
    map.setCityID(citiesRepository.getBean().getID());
    if (usersRepository.getBean().getClassification() == UserClassification.CUSTOMER) {
      sendInquiry(map, Inquiry.MAP_OF_CITY_FETCH_ALL);
    } else {
      sendInquiry(map, Inquiry.EMPLOYEE_MAP_OF_CITY_FETCH_ALL);
    }
  }

  /**
   * This method sends the server a request to update the attached and unattached maps' lists, also
   * known as {@link #beansList} and {@link #detachedMaps}.
   */
  public void requestMapsSave() {
    logger.trace(TextColors.PURPLE.colorThis("requestMapsSave") + " was just invoked!");
    final List<MapDTO> toBeUnattachedMaps = getDetachedMapsList();
    final List<MapDTO> toBeAttachedMaps = getBeansList();

    final int currentlySelectedCitysID = citiesRepository.getBean().getID();

    for (MapDTO map : toBeAttachedMaps) {
      if (!attachedMapsBackup.contains(map)) {
        map.setState(State.LOCKED);
        map.setCityID(currentlySelectedCitysID);
        sendInquiry(map, Inquiry.MAP_SAVE);
      }
    }

    for (MapDTO map : toBeUnattachedMaps) {
      if (!detachedMapsBackup.contains(map)) {
        map.setState(State.LOCKED);
        map.setCityID(0);
        sendInquiry(map, Inquiry.MAP_SAVE);
      }
    }

  }

  public void requestAttachedMapsSaving() {
    logger.trace(TextColors.PURPLE.colorThis("requestAttachedMapsSaving") + " was just invoked!");

    /* All the maps attached to the currently selected city before saving it are automatically duplicated.
     * The things left to do are to detach any maps that shouldn't be there as well as attach newly attached maps...*/
    int currentlySelectedCityID = citiesRepository.getBean().getID();
    List<MapDTO> mapsToAttach = new ArrayList<>();
    for (MapDTO map : beansList) {
      if (!attachedMapsBackup.contains(map)) {
        mapsToAttach.add(map);
      }
    }
    ClientsInquiry<MapDTO> mapInquiry = ClientsInquiry.of(MapDTO.class);
    MapDTO mapWithCityID = new MapDTO();
    mapWithCityID.setCityID(currentlySelectedCityID);
    mapInquiry.setTheDTO(mapWithCityID);
    mapInquiry.setListOfDTOs(mapsToAttach);
    mapInquiry.setInquiry(Inquiry.MAP_ATTACH_MAPS_TO_CITY);
    sendInquiry(mapInquiry);

    // ClientsInquiry<MapDTO> mapInquiry = ClientsInquiry.of(MapDTO.class);
    // mapInquiry.setExtraParameters(citiesRepository.getBean().getID());
    // mapInquiry.setInquiry(Inquiry.MAP_SAVE_ATTACHED_MAPS);
    // mapInquiry.setListOfDTOs(
    //     beansList.get().stream().filter(map -> !attachedMapsBackup.contains(map))
    //         .collect(Collectors.toList()));
    // GCMClient.getInstance().send(mapInquiry);
  }


  public void requestDetachedMapsSaving() {
    logger.trace(TextColors.PURPLE.colorThis("requestDetachedMapsSaving") + " was just invoked!");

    /* All the maps attached to the currently selected city before saving it are automatically duplicated.
     * The things left to do are to detach any maps that shouldn't be there as well as attach newly attached maps...*/
    List<MapDTO> mapsToDetach = new ArrayList<>();
    for (MapDTO map : attachedMapsBackup) {
      if (!beansList.contains(map)) {
        mapsToDetach.add(map);
      }
    }
    ClientsInquiry<MapDTO> mapInquiry = ClientsInquiry.of(MapDTO.class);
    mapInquiry.setTheDTO(new MapDTO());
    mapInquiry.setListOfDTOs(mapsToDetach);
    mapInquiry.setInquiry(Inquiry.MAP_DETACH_MAPS_FROM_CITY);
    sendInquiry(mapInquiry);
  }

  /**
   * This method saves the map's new sites list and tours list.
   */
  public void requestMapSitesSave() {
    logger.trace(TextColors.PURPLE.colorThis("requestMapSitesSave") + " was just invoked!");
    sendInquiry(Inquiry.MAP_ADD_SITE);
  }


  public void requestMapSiteRemoval() {
    logger.trace(TextColors.PURPLE.colorThis("requestMapSiteRemoval") + " was just invoked!");
    sendInquiry(Inquiry.MAP_REMOVE_SITES);
  }

  public void requestDetachedMaps() {
    logger.trace(TextColors.PURPLE.colorThis("requestDetachedMaps") + " was just invoked!");
    sendInquiry(Inquiry.MAP_DETACHED_FETCH_ALL);
  }

  /*
   *   ____                           _                       __  __      _   _               _
   *  / ___|___  _ ____   _____ _ __ (_) ___ _ __   ___ ___  |  \/  | ___| |_| |__   ___   __| |___
   * | |   / _ \| '_ \ \ / / _ \ '_ \| |/ _ \ '_ \ / __/ _ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
   * | |__| (_) | | | \ V /  __/ | | | |  __/ | | | (_|  __/ | |  | |  __/ |_| | | | (_) | (_| \__ \
   *  \____\___/|_| |_|\_/ \___|_| |_|_|\___|_| |_|\___\___| |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
   *
   */

  public boolean mapsWereChanged() {
    if (attachedMapsBackup.size() != beansList.size() || detachedMapsBackup.size() != detachedMaps
        .size()) {
      return true;
    }
    return (!(attachedMapsBackup.containsAll(beansList) || beansList.containsAll(attachedMapsBackup)
        || detachedMapsBackup.containsAll(detachedMaps)));
  }

  /**
   * Calculates number of maps that will be updated if saving city in current state.
   */
  public int getNumberOfMapsChanges() {
    logger.trace(TextColors.PURPLE.colorThis("getNumberOfMapsChanges") + " was just invoked!");
    return Math.abs(detachedMapsBackup.size() - detachedMaps.size());
  }

  public void requestRepoReset() {
    logger.trace(TextColors.PURPLE.colorThis("requestRepoReset") + " was just invoked!");
    resetDefaultMap();
    setBeansList(FXCollections.emptyObservableList());
  }

  protected void sendInquiry(Inquiry inquiry) {
    sendInquiry(bean.get(), inquiry);
  }

  protected void sendInquiry(MapDTO mapToAttach, Inquiry inquiry) {
    final ClientsInquiry<MapDTO> mapInquiry = ClientsInquiry.of(MapDTO.class);
    mapInquiry.setTheDTO(mapToAttach);
    mapInquiry.setInquiry(inquiry);
    GCMClient.getInstance().send(mapInquiry);
  }

}
