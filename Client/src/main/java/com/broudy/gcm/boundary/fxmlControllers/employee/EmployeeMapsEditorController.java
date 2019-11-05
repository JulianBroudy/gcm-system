package com.broudy.gcm.boundary.fxmlControllers.employee;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.MapsRepository;
import com.broudy.gcm.control.repos.SitesRepository;
import com.broudy.gcm.control.repos.ToursRepository;
import com.broudy.gcm.control.services.EventHandlingService;
import com.broudy.gcm.control.services.renderings.MapViewRenderingsService;
import com.broudy.gcm.control.services.renderings.MapViewRenderingsService.SiteRendering;
import com.broudy.gcm.control.services.renderings.MapViewRenderingsService.TourRendering;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.Extent;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import com.sun.javafx.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.MaskerPane;


/**
 * This class controls city's editing.
 * <p>
 * Created on the 26th of July, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeMapsEditorController<RIS extends Renderable & IStatable> extends
    EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(EmployeeMapsEditorController.class);

  /* Guice injected fields */
  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;
  private final SitesRepository sitesRepository;
  private final ToursRepository toursRepository;
  private final MapViewRenderingsService mapViewRenderingsService;
  /* Other variables */
  private final BooleanProperty cityEditingIsPermitted;
  private final BooleanProperty siteSelectionIsPermitted;
  private final BooleanProperty tourEditingIsPermitted;
  private final BooleanProperty mapIsBeingAnimated;
  private final ObjectProperty<RIS> selectedSite;
  private final ChangeListener<Boolean> mapViewInitializationListener;

  @Inject
  private EventHandlingService eventHandlingService;
  private MapViewRenderingsService.SiteRendering newSite;
  private Coordinate selectedMapCenterCoordinates;

  private ConcurrentHashMap<RIS, SiteRendering> renderedSitesOfMap;
  private ConcurrentHashMap<RIS, TourRendering> renderedToursOfMap;
  private ListChangeListener<Renderable> siteRenderingsListener;
  private ListChangeListener<Renderable> tourRenderingsListener;
  private ChangeListener<RIS> citiesCBChangeListener;
  private ChangeListener<RIS> toursCBChangeListener;


  /* FXML injected elements */
  @FXML
  private MapView mapView;
  @FXML
  private Accordion accordion;
  @FXML
  private TitledPane quickGuideTP;
  @FXML
  private JFXButton shallTheEditingBeginBTN;
  @FXML
  private TitledPane chooserTP;
  @FXML
  private HBox citiesHB;
  @FXML
  private JFXComboBox<RIS> citiesCB;
  @FXML
  private Label cityCreateLBL;
  @FXML
  private JFXButton cityCreateBTN;
  @FXML
  private HBox mapsHB;
  @FXML
  private JFXComboBox<RIS> mapsCB;
  @FXML
  private Label mapAttachLBL;
  @FXML
  private JFXButton mapAttachBTN;
  @FXML
  private JFXButton citySwitcherooBTN;
  @FXML
  private TitledPane sitesEditorTP;
  @FXML
  private JFXButton siteCreateBTN;
  @FXML
  private HBox siteNameHB;
  @FXML
  private JFXTextField siteNameTF;
  @FXML
  private JFXToggleButton siteLockTB;
  @FXML
  private HBox siteCategoryHB;
  @FXML
  private JFXComboBox<Renderable> siteCategoryCB;
  @FXML
  private HBox siteVisitDurationHB;
  @FXML
  private JFXTextField siteVisitDurationTF;
  @FXML
  private JFXToggleButton siteAccessibilityTB;
  @FXML
  private HBox siteDescriptionHB;
  @FXML
  private JFXTextArea siteDescriptionTA;
  @FXML
  private HBox siteLatitudeHB;
  @FXML
  private JFXTextField siteLatitudeTF;
  @FXML
  private HBox siteLongitudeHB;
  @FXML
  private JFXTextField siteLongitudeTF;
  @FXML
  private JFXButton siteDeleteBTN;
  @FXML
  private JFXButton siteSaveBTN;
  @FXML
  private TitledPane toursEditorTP;
  @FXML
  private JFXButton tourCreateBTN;
  @FXML
  private HBox toursHB;
  @FXML
  private JFXComboBox<RIS> toursCB;
  @FXML
  private JFXToggleButton tourLockTB;
  @FXML
  private JFXListView<RIS> mapAvailableSitesLV;
  @FXML
  private JFXButton siteDetachBTN;
  @FXML
  private JFXButton siteAttachBTN;
  @FXML
  private JFXListView<RIS> tourAttachedSitesLV;
  @FXML
  private JFXButton tourDeleteBTN;
  @FXML
  private JFXButton tourSaveBTN;
  @FXML
  private JFXButton saveMapSnapshotBTN;
  @FXML
  private JFXToggleButton mapTypeTB;
  @FXML
  private MaskerPane maskerPane;


  @Inject
  public EmployeeMapsEditorController(CitiesRepository citiesRepository,
      MapsRepository mapsRepository, SitesRepository sitesRepository,
      ToursRepository toursRepository, MapViewRenderingsService mapViewRenderingsService) {
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.sitesRepository = sitesRepository;
    this.toursRepository = toursRepository;
    this.mapViewRenderingsService = mapViewRenderingsService;
    this.renderedSitesOfMap = new ConcurrentHashMap<>();
    this.renderedToursOfMap = new ConcurrentHashMap<>();
    this.cityEditingIsPermitted = new SimpleBooleanProperty(false);
    this.siteSelectionIsPermitted = new SimpleBooleanProperty(false);
    this.tourEditingIsPermitted = new SimpleBooleanProperty(false);
    this.mapIsBeingAnimated = new SimpleBooleanProperty(false);
    this.selectedSite = new SimpleObjectProperty<>();
    // Israel coordinates
    this.selectedMapCenterCoordinates = new Coordinate(31.494261815532752, 35.1947021484375);
    this.mapViewInitializationListener = (observable, oldValue, newValue) -> {
      if (newValue) {
        logger.trace("Map has been initialized. Finishing up...");
        String bingMapsApiKey = "jRBBC3c6oCO3ebBAITlD~SCTs0-piL0zBf3eqp61CiA~All99rEJA33I8lQpEffyBcbCUZRDs1aZrem6CfTA5m5svwt_JU07CF-fSd1dyXsf";
        mapView.setBingMapsApiKey(bingMapsApiKey);
        mapView.setMapType(MapType.BINGMAPS_AERIAL);
        mapView.setZoom(8);
        // if (citiesRepository.getBean().getState() == State.DEFAULT) {
        if (sitesRepository.getBeansList().isEmpty()) {
          mapView.setCenter(selectedMapCenterCoordinates);
        }
        mapView.setAnimationDuration(1000);
        mapViewRenderingsService.setMapView(mapView);
        sitesRepository.beansListProperty().addListener(siteRenderingsListener);
        toursRepository.beansListProperty().addListener(tourRenderingsListener);
        showMaskerPane(2);
        logger.trace("Finished.");
      }
    };
  }

  /**
   * All the one-time initializations should be implemented in this method.
   */
  @Override
  public void initializeEnhancedController() {
    citiesCBChangeListener = (observable, oldValue, newValue) -> {
      if (newValue != null && newValue.getState() != State.DEFAULT) {
        mapsRepository.requestRepoReset();
        mapsRepository.requestMapsOfCity();
        sitesRepository.requestRepoReset();
        sitesRepository.requestSitesOfCity();
        if (!cityEditingIsPermitted.get()) {
          citySwitcherooBTN.getStyleClass().add("button-rainbow-blue-pink-yellow-hover");
          Shake shakeAnimation = new Shake(citySwitcherooBTN);
          PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.3));
          pauseTransition.setOnFinished(finished -> citySwitcherooBTN.getStyleClass()
              .remove("button-rainbow-blue-pink-yellow-hover"));
          shakeAnimation.setOnFinished(finished -> pauseTransition.play());
          shakeAnimation.play();
        }
      }
    };

    toursCBChangeListener = (observable, oldValue, newValue) -> {
      if (newValue != null) {
        if (!confirmSiteSwitch()) {
          Platform.runLater(() -> toursCB.setValue(oldValue));
        }
      }
    };

    siteRenderingsListener = listChange -> {
      while (listChange.next()) {
        renderSites(listChange);
        logger.debug(TextColors.CYAN.colorThis("New mapSitesList: {}"), listChange.getList());
      }
    };

    tourRenderingsListener = listChange -> {
      while (listChange.next()) {
        renderTours(listChange);
        // setMapExtent(listChange.getList());
        logger.debug(TextColors.CYAN.colorThis("New mapToursList: {}"), listChange.getList());
      }
    };
  }

  /**
   * This method should implement all the steps that turn an initialized & "unlinked" controller
   * into a "linked" one.
   */
  @Override
  protected void activateController() {
    showMaskerPaneWithProgressBar("Loading...");

    citiesRepository.requestRepoReset();
    mapsRepository.requestRepoReset();
    sitesRepository.requestRepoReset();
    toursRepository.requestRepoReset();
    citiesRepository.requestEmployeeWorkspace();
    citiesRepository.requestAllCities();

    citiesCB.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    citiesCB.setButtonCell(RenderingsStyler.EngineeredCellFactories.callRLV(null));

    mapsCB.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRISMapLV);
    mapsCB.setButtonCell(RenderingsStyler.EngineeredCellFactories.callRISMapLV(null));

    siteCategoryCB.setCellFactory(RenderingsStyler.EngineeredCellFactories::callLV);
    siteCategoryCB.setButtonCell(RenderingsStyler.EngineeredCellFactories.callLV(null));

    toursCB.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    toursCB.setButtonCell(RenderingsStyler.EngineeredCellFactories.callRLV(null));

    mapAvailableSitesLV.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    tourAttachedSitesLV.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);

    activateBindings();
    activateListeners();
    activateValidators();
    activateEventHandlers();
    experiment();
    accordion.setExpandedPane(quickGuideTP);
    Platform.runLater(() -> mapView.initialize());
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {
    mapView.initializedProperty().removeListener(mapViewInitializationListener);
    sitesRepository.beansListProperty().removeListener(siteRenderingsListener);
    toursRepository.beansListProperty().removeListener(tourRenderingsListener);

    citiesCB.itemsProperty().unbindBidirectional(citiesRepository.beansListProperty());
    citiesCB.valueProperty().unbindBidirectional(citiesRepository.beanProperty());
    citiesCB.valueProperty().removeListener(citiesCBChangeListener);

    mapsCB.itemsProperty().unbind();
    mapsCB.valueProperty().unbindBidirectional(mapsRepository.beanProperty());

    cityEditingIsPermitted.unbind();
    cityEditingIsPermitted.set(false);

    mapView.disableProperty().unbind();
    mapView.opacityProperty().unbind();

    selectedSite.unbindBidirectional(sitesRepository.beanProperty());
    siteLatitudeTF.textProperty().unbind();
    siteLongitudeTF.textProperty().unbind();
    sitesRepository.beansListProperty().removeListener(siteRenderingsListener);
    sitesRepository.requestRepoReset();

    toursCB.valueProperty().unbindBidirectional(toursRepository.beanProperty());
    toursCB.itemsProperty().unbindBidirectional(toursRepository.beansListProperty());

    mapView.opacityProperty().unbind();
    mapView.disableProperty().unbind();

    sitesRepository.getSitesOfTour().clear();
    sitesRepository.getSitesAvailableForTour().clear();
    sitesRepository.getSitesOfMap().clear();
    sitesRepository.getBeansList().clear();
    citiesRepository.requestBeanReset();
    mapView.close();
  }

  /**
   * Activates all needed bindings.
   */
  @Override
  public void activateBindings() {
    // Bind cities' ComboBox's items list and selected item to CitiesRepository's properties.
    citiesCB.itemsProperty().bindBidirectional(citiesRepository.beansListProperty());
    citiesCB.valueProperty().bindBidirectional(citiesRepository.beanProperty());

    // Create binding which determines if current employee is permitted to edit currently selected city.
    cityEditingIsPermitted.bind(Bindings.createBooleanBinding(
        () -> citiesCB != null && citiesCB.getValue() != null && citiesRepository
            .getEmployeesWorkspace().contains(citiesCB.getValue())
            && citiesCB.getValue().getState() != State.PENDING_APPROVAL, citiesCB.valueProperty()));

    // Change City & Map Chooser's main button text based on selected city's state.
    citySwitcherooBTN.disableProperty().bind(Bindings.createBooleanBinding(
        () -> citiesCB.getValue() == null || citiesCB.getValue().getState() == State.DEFAULT,
        citiesCB.valueProperty()));
    citySwitcherooBTN.textProperty().bind(Bindings.createStringBinding(
        () -> cityEditingIsPermitted.get() ? "Remove from Workspace" : citiesCB.getValue() != null
            && citiesCB.getValue().getState() == State.PENDING_APPROVAL ? "Cancel Approval Request"
            : "Add City to My Workspace", cityEditingIsPermitted));
    cityCreateLBL.opacityProperty().bindBidirectional(cityCreateBTN.opacityProperty());

    // Bind maps' ComboBox's items list and selected item to MapsRepository's properties.
    mapsCB.itemsProperty().bind(mapsRepository.beansListProperty());
    mapsCB.valueProperty().bindBidirectional(mapsRepository.beanProperty());
    // Disable maps' ComboBox selection when city editing is prohibited.
    mapsCB.disableProperty().bind(cityEditingIsPermitted.not());

    mapAttachLBL.opacityProperty().bindBidirectional(mapAttachBTN.opacityProperty());
    final BooleanBinding mapIsSelected = Bindings
        .createBooleanBinding(() -> mapsCB.getValue().getState() != State.DEFAULT,
            mapsCB.valueProperty());

    // Create binding which determines if currently displayed sites are selectable.
    siteSelectionIsPermitted.bind(cityEditingIsPermitted.and(mapIsSelected));
    sitesEditorTP.disableProperty().bind(siteSelectionIsPermitted.not());

    // Bind this.selectedSite to the SitesRepository's bean.
    selectedSite.bindBidirectional(sitesRepository.beanProperty());

    // Bind SitesRepository's bean attributes to relevant GUI components.
    sitesRepository.getSuperBean().bindBidirectional("name", siteNameTF.textProperty());
    sitesRepository.getSuperBean()
        .bindBidirectional("category", siteCategoryCB.valueProperty(), Renderable.class);
    sitesRepository.getSuperBean()
        .bindBidirectional("recommendedVisitDuration", siteVisitDurationTF.textProperty());
    sitesRepository.getSuperBean()
        .bindBidirectional("accessible", siteAccessibilityTB.selectedProperty());
    sitesRepository.getSuperBean()
        .bindBidirectional("description", siteDescriptionTA.textProperty());

    siteNameTF.disableProperty().bind(siteLockTB.selectedProperty());
    siteCategoryCB.disableProperty().bind(siteLockTB.selectedProperty());
    siteVisitDurationTF.disableProperty().bind(siteLockTB.selectedProperty());
    siteDescriptionTA.disableProperty().bind(siteLockTB.selectedProperty());
    siteAccessibilityTB.disableProperty().bind(siteLockTB.selectedProperty());
    siteLatitudeTF.disableProperty().bind(siteLockTB.selectedProperty());
    siteLongitudeTF.disableProperty().bind(siteLockTB.selectedProperty());
    siteDeleteBTN.disableProperty().bind(siteLockTB.selectedProperty().or(Bindings
        .createBooleanBinding(() -> selectedSite.get().getState() == State.NEW, selectedSite)));
    siteSaveBTN.disableProperty().bind(siteLockTB.selectedProperty());

    // Bind tours' ComboBox's items list and selected item to ToursRepository's properties.
    toursCB.valueProperty().bindBidirectional(toursRepository.beanProperty());
    toursCB.itemsProperty().bindBidirectional(toursRepository.beansListProperty());
    // Create binding which determines if currently displayed sites are selectable.
    tourEditingIsPermitted.bind(cityEditingIsPermitted.and(mapIsSelected));
    toursEditorTP.disableProperty().bind(tourEditingIsPermitted.not());

    final BooleanBinding tourIsSelected = Bindings.createBooleanBinding(
        () -> toursCB.getValue() != null && toursCB.getValue().getState() != State.DEFAULT,
        toursCB.valueProperty());

    mapAvailableSitesLV.disableProperty().bind(tourIsSelected.not());
    tourAttachedSitesLV.disableProperty().bind(tourIsSelected.not());
    mapAvailableSitesLV.itemsProperty()
        .bindBidirectional(sitesRepository.sitesAvailableForTourList());
    tourAttachedSitesLV.itemsProperty().bindBidirectional(sitesRepository.sitesOfTourList());

    siteAttachBTN.disableProperty().bind(Bindings
        .createBooleanBinding(() -> mapAvailableSitesLV.getItems().isEmpty(),
            mapAvailableSitesLV.itemsProperty()));
    siteDetachBTN.disableProperty().bind(Bindings
        .createBooleanBinding(() -> tourAttachedSitesLV.getItems().isEmpty(),
            tourAttachedSitesLV.itemsProperty()));

    // Disable MapView and add a transparency effect when city editing or site selection is prohibited.
    mapView.disableProperty().bind(cityEditingIsPermitted.not().or(siteSelectionIsPermitted.not()));
    mapView.opacityProperty().bind(Bindings.when(citySwitcherooBTN.disableProperty()).then(0.3D)
        .otherwise(Bindings.when(cityEditingIsPermitted.not()).then(0.6D)
            .otherwise(Bindings.when(siteSelectionIsPermitted.not()).then(0.8D).otherwise(1D))));

  }

  /**
   * Activates all needed listeners.
   */
  @Override
  public void activateListeners() {
    // Watch the MapView's initialized property to finish initialization.
    mapView.initializedProperty().addListener(mapViewInitializationListener);

    AtomicBoolean ignoreExpansion = new AtomicBoolean(false);
    AtomicInteger numberOfExpansionsToIgnore = new AtomicInteger();
    // Give a nicer accordion effect.
    accordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
      logger.trace(
          TextColors.RED.colorThis("accordion.expandedPaneProperty listener was activated."));
      logger.trace(TextColors.RED.colorThis("oldValue = {}, newValue = {}"), oldValue, newValue);
      if (numberOfExpansionsToIgnore.get() == 0) {
        logger.trace(TextColors.RED.colorThis("NOT ignored"));
        ArrayList<TitledPane> titledPanes = new ArrayList<>(accordion.getPanes());
        if (oldValue != null) {
          if (titledPanes.stream().noneMatch(TitledPane::isExpanded)) {
            final int indexOfPaneBeingCollapsed = titledPanes.indexOf(oldValue);
            Platform.runLater(() -> {
              accordion.setExpandedPane(indexOfPaneBeingCollapsed == 0 ? titledPanes.get(1)
                  : titledPanes.get(indexOfPaneBeingCollapsed - 1));
            });
          }
          // Check whether there are any unsaved changes...
          if (!confirmSiteSwitch()) {
            numberOfExpansionsToIgnore.set(3);
            Platform.runLater(() -> {
              accordion.setExpandedPane(oldValue);
            });
          } else {
            if (oldValue == toursEditorTP) {
              toursRepository.requestRepoReset();
              sitesRepository.requestSitesOfTour();
              tourLockTB.setSelected(true);
            } else if (oldValue == sitesEditorTP) {
              // Check whether there are any unsaved changes...
              // Lock siteLockTB when "Sites Editor" TitledPane is collapsed.
              // sitesRepository.requestRepoReset();
              toursRepository.requestRepoReset();
              sitesRepository.requestSitesOfTour();
              siteLockTB.setSelected(true);
            }
          }
        } else if (newValue != null) {
          if (newValue == toursEditorTP) {
            toursRepository.requestRepoReset();
            toursRepository.requestToursOfMap();
          } else if (newValue == sitesEditorTP) {
            sitesRepository.requestRepoReset();
          }
        }
      } else {
        logger.trace(TextColors.RED.colorThis("IGNORED"));
        numberOfExpansionsToIgnore.getAndDecrement();
      }
    });

    // Change MapView's type with the toggle button.
    mapTypeTB.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != oldValue) {
        if (newValue) {
          mapView.setMapType(MapType.BINGMAPS_AERIAL);
        } else {
          mapView.setMapType(MapType.BINGMAPS_ROAD);
        }
        for (TourRendering tour : renderedToursOfMap.values()) {
          if (tour.isVisible()) {
            tour.refreshCoordinates(mapView);
          }
        }
      }
    });

    // Refresh maps' ComboBox when a different city is picked assuming it is not locked to this employee.
    citiesCB.valueProperty().addListener(citiesCBChangeListener);

    // Open "Sites Editor" TitledPane after selecting a map.
    mapsCB.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (!sitesEditorTP.isExpanded() && newValue.getState() != State.DEFAULT) {
        toursRepository.requestRepoReset();
        sitesRepository.requestSitesOfMap();
        sitesRepository.requestRepoReset();
        setMapExtent(sitesRepository.sitesOfMapList());
        sitesEditorTP.setExpanded(true);
      }
    });

    selectedSite.addListener((observable, oldValue, newValue) -> {
      if (oldValue.getState() != State.DEFAULT) {
        // Switch from "selected" rendering to "unselected" rendering.
        if (oldValue.getState() == State.NEW) {
          if (renderedSitesOfMap.containsKey(oldValue)) {
            renderedSitesOfMap.get(oldValue).detachFrom(mapView);
            renderedSitesOfMap.remove(oldValue);
          }
        } else if (renderedSitesOfMap.containsKey(oldValue)) {
          renderedSitesOfMap.get(oldValue).setSelected(false, mapView);
        }
      }
      siteLatitudeTF.textProperty().unbind();
      siteLongitudeTF.textProperty().unbind();
      siteLatitudeTF.clear();
      siteLongitudeTF.clear();
      if (newValue.getState() != State.DEFAULT) {
        if (maskerPane.isVisible()) {
          maskerPane.setVisible(false);
        }
        final boolean newSiteOption = !renderedSitesOfMap.containsKey(newValue);
        // Switch from "unselected" rendering to "selected" rendering.
        if (newSiteOption) {
          newSite = mapViewRenderingsService.generateSiteRendering(newValue);
          renderedSitesOfMap.put(newValue, newSite);
        }
        renderedSitesOfMap.get(newValue).setSelected(true, mapView);
        // Unlock site if this is a new site because clicking "New Site" entails entering new data.
        siteLockTB.setSelected(!newSiteOption);
        // Since mapJFX library doesn't yet provide coordinates' longitude & latitude setters it is not
        // possible to use a BeanPathAdaptor, instead manually bind SitesRepository's bean's coordinates
        // to the relevant GUI components.
        siteLatitudeTF.textProperty().bind(Bindings.createStringBinding(
            () -> renderedSitesOfMap.get(newValue).getRenderedMarker().getPosition().getLatitude()
                .toString(),
            renderedSitesOfMap.get(newValue).getRenderedMarker().positionProperty()));
        siteLongitudeTF.textProperty().bind(Bindings.createStringBinding(
            () -> renderedSitesOfMap.get(newValue).getRenderedMarker().getPosition().getLongitude()
                .toString(),
            renderedSitesOfMap.get(newValue).getRenderedMarker().positionProperty()));
        // Bring newly rendered site to front.
        renderedSitesOfMap.values().stream().filter(rendering -> rendering.isVisible() && !rendering
            .equals(renderedSitesOfMap.get(newValue)) && (Math.abs(
            rendering.getPosition().getLongitude() - renderedSitesOfMap.get(newValue).getPosition()
                .getLongitude()) < 0.0009 || Math.abs(
            rendering.getPosition().getLatitude() - renderedSitesOfMap.get(newValue).getPosition()
                .getLatitude()) < 0.0009)).forEach(rendering -> {
          rendering.detachFrom(mapView);
          rendering.attachTo(mapView);
        });
      }
    });

    toursCB.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        if (newValue.getState() != State.DEFAULT) {
          boolean skip = false;
          if (selectedSite.get().getState() != State.DEFAULT) {
            if (!confirmSiteSwitch()) {

              Platform.runLater(() -> toursCB.setValue(oldValue));
              skip = true;
              // sitesRepository.requestSitesOfMap();
            }
          }
          if (!skip) {
            sitesRepository.requestRepoReset();
            sitesRepository.requestSitesOfTour();
            final List<RIS> sitesOfTour = sitesRepository.getSitesOfTour();
            renderedToursOfMap.values()
                .forEach(tourRendering -> tourRendering.refreshCoordinates(mapView));
            if (!sitesOfTour.isEmpty()) {
              setMapExtent(sitesRepository.getSitesOfTour());
            }
          }
        }
      }

    });

  }

  private void experiment() {
    //  TODO: delete method
    sitesEditorTP.expandedProperty().addListener((observable, oldValue, newValue) -> {
      logger.trace(TextColors.RED.colorThis("sitesEditorTP.expandedProperty was activated"));
      if (newValue) {
        logger.trace(TextColors.RED.colorThis("newValue = true"));
      } else {
        logger.trace(TextColors.RED.colorThis("newValue = false"));
      }
    });

    toursEditorTP.expandedProperty().addListener((observable, oldValue, newValue) -> {
      logger.trace(TextColors.RED.colorThis("toursEditorTP.expandedProperty was activated"));
      if (newValue) {
        logger.trace(TextColors.RED.colorThis("newValue = true"));
      } else {
        logger.trace(TextColors.RED.colorThis("newValue = false"));
      }
    });
  }

  /**
   * Activates all needed validators.
   */
  @Override
  public void activateValidators() {

  }

  /**
   * Activates all needed event handlers.
   */
  @Override
  public void activateEventHandlers() {
    shallTheEditingBeginBTN.setOnAction(clicked -> accordion.setExpandedPane(chooserTP));

    citySwitcherooBTN.setOnAction(clicked -> {
      if (!cityEditingIsPermitted.get()) {
        if (citiesRepository.getBean().getState() == State.PENDING_APPROVAL) {
          // If city IS in employee's workspace but editing is prohibited, i.e., city's state is PENDING_APPROVAL.
          final ButtonType cancelRequest = new ButtonType("Cancel Request", ButtonData.YES);
          final ButtonType keepRequest = new ButtonType("Keep", ButtonData.NO);
          Alert alert = new Alert(AlertType.CONFIRMATION,
              "Press \"Cancel Request\" to regain control over " + citiesRepository.getBean()
                  .getName() + "'s maps."
                  + "\nPress \"Keep\" to close this dialog and keep the request.", cancelRequest,
              keepRequest);
          alert.setTitle("Hold on a second...");
          alert.setHeaderText("You are about to withdraw your request!");
          alert.showAndWait();
          if (alert.getResult().equals(cancelRequest)) {
            citiesCB.valueProperty().removeListener(citiesCBChangeListener);
            showMaskerPaneWithProgressBar("Cancelling...");
            EventHandlingService.setHideProcessFlag(true);
            eventHandlingService
                .cancelApprovalRequestButtonHandler(clicked, citiesRepository.getBean());
          }
          showMaskerPaneWithProgressBar("Cancelling...");
        } else {
          // If city is NOT in employee's workspace., i.e., city's state != LOCKED.
          final ButtonType LOCK = new ButtonType("Lock", ButtonData.YES);
          Alert alert = new Alert(AlertType.CONFIRMATION,
              "Press \"Add\" to lock " + citiesRepository.getBean().getName()
                  + " and add it to your workspace." + "\nPress \"Cancel\" to go back.", LOCK,
              ButtonType.CANCEL);
          alert.setTitle("Hold on a second...");
          alert.setHeaderText("This city is not in your workspace yet!");
          alert.showAndWait();
          if (alert.getResult().equals(LOCK)) {
            citiesCB.valueProperty().removeListener(citiesCBChangeListener);
            showMaskerPaneWithProgressBar("Locking...");
            EventHandlingService.setHideProcessFlag(true);
            citiesRepository.requestCitySaving();
          }
        }
      } else {
        // If city IS in employee's workspace., i.e., city's state == LOCKED.
        final ButtonType DISCARD = new ButtonType("Discard City", ButtonData.YES);
        Alert alert = new Alert(AlertType.CONFIRMATION,
            "Press \"Discard City\" to unlock " + citiesRepository.getBean().getName()
                + " and remove it from your workspace." + "\nPress \"Cancel\" to go back.", DISCARD,
            ButtonType.CANCEL);
        alert.setTitle("Hold on a second...");
        alert.setHeaderText(
            "You are about to discard all and any progress you made to " + citiesRepository
                .getBean().getName() + "!\nThis operation is UNDOABLE!");
        alert.showAndWait();
        if (alert.getResult().equals(DISCARD)) {
          showMaskerPaneWithProgressBar("Unlocking...");
          EventHandlingService.setHideProcessFlag(true);
          eventHandlingService.deleteLockedCityButtonHandler(clicked, citiesRepository.getBean());
        }
      }
    });

    mapView.setOnMouseEntered(event -> mapTypeTB.setOpacity(0.6D));
    mapView.setOnMouseExited(event -> mapTypeTB.setOpacity(1D));

    // Show label when hovering over site's locator icon.
    mapView.addEventHandler(MarkerEvent.MARKER_ENTERED, event -> {
      event.consume();
      logger.trace("MARKER_ENTERED");
      SiteRendering found;
      for (Entry<RIS, SiteRendering> entry : renderedSitesOfMap.entrySet()) {
        if (entry.getValue().getRenderedMarker().getId().equals(event.getMarker().getId())) {
          System.out.println("\n\n\n\n" + entry.getValue());
          entry.getValue().setLabelVisibility(true);
          System.out.println("\n\n\n\n" + entry.getValue() + "\n\n\n\n");
        }
      }
     /* renderedSitesOfMap.values().stream()
          .filter(siteRendering -> {
            return siteRendering.getRenderedMarker().equals(event.getMarker());
          })
          .findFirst().ifPresent(siteRendering -> {
        System.out.println("\n\n\n\n"+siteRendering);
        siteRendering.setLabelVisibility(true);
      });*/
    });

    // Hide label when NOT hovering over site's locator icon.
    mapView.addEventHandler(MarkerEvent.MARKER_EXITED, event -> {
      event.consume();
      logger.trace("MARKER_EXITED");
      renderedSitesOfMap.values().stream()
          .filter(siteRendering -> siteRendering.getRenderedMarker().equals(event.getMarker()))
          .findFirst().ifPresent(siteRendering -> siteRendering.setLabelVisibility(false));
    });

    // Set focus on site when clicking map's marker. i.e., set selectedSite to the marker's site.
    mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
      event.consume();
      logger.trace("MARKER_CLICKED");
      System.out.println("\n\nMarker: " + event.getMarker());
      System.out.println("\n\nMarkerHash: " + event.getMarker().hashCode());
      if (mapIsBeingAnimated.get()) {
        new Shake(mapView).play();
      } else {
        final RIS clickedSite = renderedSitesOfMap.entrySet().stream()
            .filter(entry -> entry.getValue().getRenderedMarker().equals(event.getMarker()))
            .findAny().map(Entry::getKey).orElseThrow(NullPointerException::new);
        // If clicking a an already selected site -> deselect it...
        if (clickedSite.equals(selectedSite.get())) {
          sitesRepository.requestRepoReset();
          if (renderedSitesOfMap.get(clickedSite).isPartOfTour()) {
            mapView.setExtent(mapViewRenderingsService.getEnhancedExtent(
                (Collection<? extends Coordinate>) renderedToursOfMap.get(toursCB.getValue())
                    .getCoordinatesStream().collect(Collectors.toList())));
          }
        } else {
          if (confirmSiteSwitch()) {
            // Set selected site to the site's SiteRendering the marker belongs to.
            selectedSite.set(clickedSite);
            if (renderedSitesOfMap.get(clickedSite).isPartOfTour()) {
              renderedToursOfMap.get(toursCB.getValue()).refreshCoordinates(mapView);
            }
          }
        }
      }
    });

    // Check if site is focused in order to move it's locator icon.
    mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
      event.consume();
      logger.trace("MAP_CLICKED");
      logger.trace(event.getCoordinate());
      if (mapIsBeingAnimated.get()) {
        new Shake(mapView).play();
      } else if (mapsRepository.getBean().getState() == State.DEFAULT) {
        mapsCB.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {
          @Override
          public void invalidated(Observable observable) {
            maskerPane.setVisible(false);
            mapsCB.getSelectionModel().selectedItemProperty().removeListener(this);
          }
        });
        maskerPane.setText("Select Map!");
        maskerPane.setProgressVisible(false);
        maskerPane.setVisible(true);
      } else if (selectedSite.get().getState() == State.DEFAULT) {
        maskerPane.setProgressVisible(false);
        showMaskerPane("Select Site!", 2, finished -> {
          maskerPane.setProgressVisible(true);
          maskerPane.setVisible(false);
        });
        new Shake(maskerPane).play();
      } else if (renderedSitesOfMap.entrySet().stream()
          .anyMatch(entry -> sitesRepository.getSitesOfMap().contains(entry.getKey()))) {
        if (siteLockTB.isSelected() && !toursEditorTP.isExpanded()) {
          siteLockTB.selectedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
              maskerPane.setVisible(false);
              siteLockTB.selectedProperty().removeListener(this);
            }
          });
          maskerPane.setText("Unlock Site!");
          maskerPane.setVisible(true);
        } else {
          final Coordinate newClickCoordinates = event.getCoordinate().normalize();
          logger.trace("MAP_CLICKED at: " + newClickCoordinates);
          final Coordinate positionBeforeChange = renderedSitesOfMap.get(selectedSite.get())
              .getPosition();
          mapView.setCenter(positionBeforeChange);
          if (toursEditorTP.isExpanded()) {
            changeSitePosition(renderedSitesOfMap.get(selectedSite.get()), newClickCoordinates);
            System.out.println("Pos before change: " + positionBeforeChange);
            System.out.println(
                "Pos after change: " + renderedSitesOfMap.get(selectedSite.get()).getPosition());
          } else {
            changeSitePosition(renderedSitesOfMap.get(selectedSite.get()), newClickCoordinates);

          }
        }
      }
    });
    //else if (toursEditorTP.isExpanded()) {
    //         final Coordinate newClickCoordinates = event.getCoordinate().normalize();
    //         logger.trace("MAP_CLICKED at: " + newClickCoordinates);
    //         changeSitePosition(renderedSitesOfMap.get(selectedSite.get()), newClickCoordinates);
    //
    //       }
    siteCreateBTN.setOnAction(clicked -> {
      if (selectedSite.get().getState() != State.NEW) {
        if (mapsCB.valueProperty().get().getState() != State.DEFAULT && confirmSiteSwitch()) {
          showMaskerPane("Constructing...", 1, finished -> {
            // mapsCB.valueProperty().unbindBidirectional(mapsRepository.beanProperty());
            sitesRepository.requestNewSitePlaceholder(mapView.getCenter());
            maskerPane.setVisible(false);
          });
          siteLockTB.setSelected(false);
        }
      }
    });

    siteSaveBTN.setOnAction(clicked -> {
      if (selectedSite.get().getState() == State.NEW || renderedSitesOfMap.get(selectedSite.get())
          .hasUnsavedChanges()) {
        final ButtonType CONFIRM = new ButtonType("Save", ButtonData.YES);
        Alert alert = new Alert(AlertType.CONFIRMATION,
            "Press \"Save\" to save the changes in the database."
                + "\nPress \"Cancel\" to go back.", CONFIRM, ButtonType.CANCEL);
        alert.setTitle("Hold on a second...");
        alert.setHeaderText("You are about to save the changes!");
        alert.showAndWait();
        if (alert.getResult().equals(CONFIRM)) {
          maskerPane.setText("Saving...");
          maskerPane.setProgressVisible(true);
          maskerPane.setVisible(true);
          sitesRepository.requestSiteSaving();
        }
      }
    });

    siteDeleteBTN.setOnAction(clicked -> {
      final ButtonType CONFIRM = new ButtonType("Delete", ButtonData.YES);
      Alert alert = new Alert(AlertType.CONFIRMATION,
          "Press \"Delete\" to permanently delete this site." + "\nPress \"Cancel\" to go back.",
          CONFIRM, ButtonType.CANCEL);
      alert.setTitle("Hold on a second...");
      alert.setHeaderText("You are about to permanently delete this site!");
      alert.showAndWait();
      if (alert.getResult().equals(CONFIRM)) {
        maskerPane.setText("Deleting...");
        maskerPane.setProgressVisible(true);
        maskerPane.setVisible(true);
        sitesRepository.requestSiteDeletion();
      }
    });

    // Handle moving items from availableMapsLV to attachedMapsLV and vice versa.
    siteDetachBTN.setOnMouseClicked(click -> moveSites(tourAttachedSitesLV, mapAvailableSitesLV));
    siteAttachBTN.setOnMouseClicked(click -> moveSites(mapAvailableSitesLV, tourAttachedSitesLV));
  }

  /**
   * This method prompts the user for a confirmation alert if there are any unsaved changes to the
   * currently selected site.
   *
   * @return true - if there are no unsaved changes or there are and the user confirmed the switch.
   *     false - the user canceled the switching.
   */
  private boolean confirmSiteSwitch() {

    final SiteRendering renderedSiteBeforeChange = renderedSitesOfMap.get(selectedSite.get());
    final AtomicBoolean confirmSwitch = new AtomicBoolean(true);

    final State sitesState = selectedSite.get().getState();

    if (sitesState != State.DEFAULT && (sitesState == State.NEW || renderedSiteBeforeChange
        .hasUnsavedChanges())) {
      final ButtonType CONFIRM = new ButtonType("Discard Unsaved Changes", ButtonData.RIGHT);
      Alert alert = new Alert(AlertType.CONFIRMATION,
          "Press \"Cancel\" to go back and save site.\nPress \"Discard Unsaved Changes\" to continue without saving.",
          CONFIRM, ButtonType.CANCEL);
      alert.setTitle("Hold on a second...");
      alert.setHeaderText("Seems like you the site has some unsaved changes!");
      alert.showAndWait().ifPresent(buttonType -> confirmSwitch.set(buttonType.equals(CONFIRM)));
      if (confirmSwitch.get()) {
        changeSitePosition(renderedSiteBeforeChange, renderedSiteBeforeChange.getOriginalPosition(),
            true);
      }
    }
    return confirmSwitch.get();
  }

  /**
   * This method detaches from map sites that shouldn't be rendered anymore and attaches needed
   * ones.
   *
   * @param listOfSitesToBeRendered the change on the list of sites to be rendered.
   */
  private void renderSites(Change<? extends Renderable> listOfSitesToBeRendered) {
    // TODO implement a process more tuned to ListChange for site renderings
    // Remove sites' renderings from the map's view
    renderedSitesOfMap.forEach((renderable, siteRendering) -> siteRendering.detachFrom(mapView));
    // Generate new sites' renderings
    renderedSitesOfMap = mapViewRenderingsService.generateMapSitesRenderings();
    // Add sites' renderings to the map's view
    renderedSitesOfMap.forEach((renderable, siteRendering) -> siteRendering.attachTo(mapView));
    setMapExtent(listOfSitesToBeRendered.getList());
  }

  private void renderTours(Change<? extends Renderable> listOfToursToBeRendered) {
    // Remove tours' renderings from the map's view
    renderedToursOfMap.forEach((renderable, tourRendering) -> tourRendering.detachFrom(mapView));
    // Generate new tours' renderings
    renderedToursOfMap = mapViewRenderingsService.generateMapToursRenderings();
    // Add tours' renderings to the map's view
    renderedToursOfMap.forEach((renderable, tourRendering) -> tourRendering.attachTo(mapView));
  }

  private void setMapExtent(List<? extends Renderable> list) {

    List<Coordinate> renderedSitesCoordinates = renderedSitesOfMap.entrySet().stream()
        .filter(entry -> list.contains(entry.getKey())).map(Entry::getValue)
        .map(SiteRendering::getPosition).collect(Collectors.toList());

    if (renderedSitesCoordinates.size() < 1) {
      mapView.setCenter(selectedMapCenterCoordinates);
    } else if (renderedSitesCoordinates.size() == 1) {
      mapView.setCenter(renderedSitesCoordinates.get(0));
      mapView.setZoom(13);
    } else {
      Extent allSitesCoordinates = mapViewRenderingsService
          .getEnhancedExtent(renderedSitesCoordinates);
      mapView.setExtent(allSitesCoordinates);
      if (renderedSitesOfMap.values().stream()
          .anyMatch(siteRendering -> siteRendering.isVisible())) {
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
        pauseTransition.setOnFinished(finished -> {
          if (mapView.getZoom() > 16) {
            mapView.setZoom(16);
          }
        });
        pauseTransition.play();
      }

      logger.info("MapView's center changed from: {} to {}", selectedMapCenterCoordinates,
          selectedMapCenterCoordinates = mapView.getCenter());
    }
  }


  private void changeSitePosition(SiteRendering siteRendering, Coordinate newPosition) {
    changeSitePosition(siteRendering, newPosition, false);
  }

  private void changeSitePosition(SiteRendering siteRendering, Coordinate newPosition,
      final boolean rollBack) {
    mapIsBeingAnimated.set(true);
    // Animate the marker to the new position.
    final Coordinate initialCoordinates = siteRendering.getPosition();
    // If moved site is part of a rendered tour -> refresh rendered tour.
    final TourRendering tourRendering;
    if (siteRendering.isPartOfTour()) {
      tourRendering = renderedToursOfMap.get(toursCB.getValue());
    } else {
      tourRendering = null;
    }
    final ChangeListener<Coordinate> coordinateChangeListener;
    if (!rollBack) {
      if (siteRendering.isPartOfTour()) {
        coordinateChangeListener = (observable, oldValue, newValue) -> {
          mapView.setCenter(newValue);
          tourRendering.refreshCoordinates(mapView);
        };
      } else {
        coordinateChangeListener = (observable, oldValue, newValue) -> mapView.setCenter(newValue);
      }
    } else if (siteRendering.isPartOfTour()) {
      coordinateChangeListener = (observable, oldValue, newValue) -> tourRendering
          .refreshCoordinates(mapView);
    } else {
      coordinateChangeListener = (observable, oldValue, newValue) -> {
      };
    }

    final double previousZoomLevel = mapView.getZoom();
    final float distanceInKilometers = 100 * Point2D
        .distance(initialCoordinates.getLatitude().floatValue(),
            initialCoordinates.getLongitude().floatValue(), newPosition.getLatitude().floatValue(),
            newPosition.getLongitude().floatValue());

    System.out.println("\n\n Distance: " + distanceInKilometers * 1000);

    // Calculate how many seconds should the transition's duration be
    // while ideally it should take 1 second to move site's position 500 meters.

    // distanceInKilometers * 1000 * 2 is the formula after simplifying:
    // distanceInKilometers * 1000 = distanceInMeters
    // distanceInMeters / 500 = numberOfSecondsTheDurationShouldTake
    // numberOfSecondsTheDurationShouldTake * 1000 = numberOfSecondsTheDurationShouldTakeInMilliseconds
    final float secondsPer500Meters = distanceInKilometers * 1000;

    final Transition transition = new Transition() {
      private final Double oldPositionLatitude = initialCoordinates.getLatitude();
      private final Double oldPositionLongitude = initialCoordinates.getLongitude();
      private final double deltaLatitude = newPosition.getLatitude() - oldPositionLatitude;
      private final double deltaLongitude = newPosition.getLongitude() - oldPositionLongitude;

      {
        setCycleDuration(Duration.millis(/*rollBack ? 1000 : secondsPer500Meters*/1000));
        // setCycleDuration(Duration.seconds(distance / 0.003378520));
        setOnFinished(event -> {
          System.out.println("\n\nBEFORE siteRendering.setPosition(newPosition);:");

          System.out.println("Original coordinates: " + siteRendering.getOriginalPosition());
          System.out.println("Current coordinates: " + siteRendering.getPosition());

          siteRendering.setPosition(newPosition);
          System.out.println("\nAFTER siteRendering.setPosition(newPosition);:");
          System.out.println("Original coordinates: " + siteRendering.getOriginalPosition());
          System.out.println("Current coordinates: " + siteRendering.getPosition());
          siteRendering.positionProperty().removeListener(coordinateChangeListener);
          mapView.setAnimationDuration(300);
          if (!rollBack && !siteRendering.isPartOfTour()) {
            mapView.setZoom(previousZoomLevel);
          } else {
            if (rollBack) {
              siteRendering.rollBack();
            }
            if (siteRendering.isPartOfTour()) {
              mapView.setExtent(mapViewRenderingsService.getEnhancedExtent(
                  (Collection<? extends Coordinate>) tourRendering.getCoordinatesStream()
                      .collect(Collectors.toList())));
            } /*else if (selectedSite.get().getState() == State.DEFAULT) {
              setMapExtent(sitesRepository.getSitesOfMap());
            }*/
          }
          renderedSitesOfMap.values().stream().filter(
              rendering -> rendering.isVisible() && !rendering.equals(siteRendering) && (
                  Math.abs(rendering.getPosition().getLongitude() - newPosition.getLongitude())
                      < 0.0009
                      || Math.abs(rendering.getPosition().getLatitude() - newPosition.getLatitude())
                      < 0.0009)).forEach(rendering -> {
            rendering.detachFrom(mapView);
            rendering.attachTo(mapView);
          });

          PauseTransition pause = new PauseTransition(Duration.millis(300));
          pause.setOnFinished(finished -> {
            mapView.setAnimationDuration(1000);
            mapIsBeingAnimated.set(false);
          });
          pause.play();
        });
      }

      @Override
      protected void interpolate(double v) {
        final double latitude = initialCoordinates.getLatitude() + v * deltaLatitude;
        final double longitude = initialCoordinates.getLongitude() + v * deltaLongitude;
        siteRendering.setPosition(new Coordinate(latitude, longitude));
      }
    };
    if (!rollBack) {
      mapView.setZoom(tourRendering != null ? 14 : 16);
    }
    PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000));
    pauseTransition.setOnFinished(finished -> {
      siteRendering.positionProperty().addListener(coordinateChangeListener);
      mapView.setAnimationDuration(1);
      transition.play();
    });
    pauseTransition.play();
  }

  /**
   * A convenience method for reducing duplicate code.
   * This method moves ListView items between 2 ListViews.
   *
   * @param fromList the ListView from which to move selected items.
   * @param toList the ListView which receives moved items.
   */
  private void moveSites(ListView<RIS> fromList, ListView<RIS> toList) {
    ObservableList<RIS> selectedItems = fromList.getSelectionModel().getSelectedItems();
    RIS toursCBValue = toursCB.getValue();
    for (Entry<RIS, TourRendering> tourEntry : renderedToursOfMap.entrySet()) {
      if (Objects.equals(tourEntry.getKey(), toursCBValue)) {
        if (fromList == mapAvailableSitesLV) {
          tourEntry.getValue().addSites(selectedItems);
        } else {
          tourEntry.getValue().removeSites(selectedItems);
        }
        toList.getItems().addAll(selectedItems);
        fromList.getItems().removeAll(selectedItems);
        tourEntry.getValue().refreshCoordinates(mapView);
        break;
      }
    }
    logger.info("Sites have been moved from {} to {}", fromList, toList);
    logger.trace("Moved sites are: {}", selectedItems);
    logger.debug("Available sites: {}", sitesRepository.getSitesAvailableForTour());
    logger.debug("Attached sites: {}", sitesRepository.getSitesOfTour());
  }

  /*
   *   ____                           _                       __  __      _   _               _
   *  / ___|___  _ ____   _____ _ __ (_) ___ _ __   ___ ___  |  \/  | ___| |_| |__   ___   __| |___
   * | |   / _ \| '_ \ \ / / _ \ '_ \| |/ _ \ '_ \ / __/ _ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
   * | |__| (_) | | | \ V /  __/ | | | |  __/ | | | (_|  __/ | |  | |  __/ |_| | | | (_) | (_| \__ \
   *  \____\___/|_| |_|\_/ \___|_| |_|_|\___|_| |_|\___\___| |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
   *
   */

  private void showMaskerPaneWithProgressBar(final String textToDisplay) {
    maskerPane.setText(textToDisplay);
    maskerPane.setProgressVisible(true);
    maskerPane.setVisible(true);
  }

  private void showMaskerPane(final double seconds) {
    showMaskerPane(seconds, event -> maskerPane.setVisible(false));
  }

  private void showMaskerPane(final double seconds,
      final EventHandler<ActionEvent> onFinishedHandler) {
    final PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
    pause.setOnFinished(onFinishedHandler);
    maskerPane.setVisible(true);
    pause.play();
  }

  public void showMaskerPane(final String textToDisplay, final double seconds,
      final EventHandler<ActionEvent> onFinishedHandler) {
    maskerPane.setText(textToDisplay);
    showMaskerPane(seconds, onFinishedHandler);
  }

  /**
   * Convenience method to display passed String and switch {@link #maskerPane} off after a second.
   */
  public void finishedProcessing(String textToDisplay) {
    maskerPane.setText(textToDisplay);
    showMaskerPane(1);
  }

  /**
   * Convenience method to display "All done :)" and switch {@link #maskerPane} off after a second.
   */
  public void finishedProcessing() {
    finishedProcessing("All done :)");
  }

  /**
   * Convenience method to allow adding the listener from {@link com.broudy.gcm.control.services.EventHandlingService}.
   */
  public void listenToCitiesCBChanges() {
    if (linked && citiesCB != null) {
      if (maskerPane.isVisible()) {
        finishedProcessing();
      }
      citiesCB.valueProperty().addListener(citiesCBChangeListener);
      if (!citiesCB.getSelectionModel().isEmpty()) {
        final RIS selectedCity = citiesCB.getSelectionModel().getSelectedItem();
        citiesCB.getSelectionModel().clearSelection();
        citiesCB.getSelectionModel().select(selectedCity);
      }
    }
  }

  public void removedCityFromWorkspace() {
    showMaskerPane("Removed :)", 1, finished -> {
      citiesCB.getSelectionModel().clearSelection();
      maskerPane.setVisible(false);
    });
  }

  public void savedSite() {
    showMaskerPane("Saved :)", 1, finished -> {
      sitesRepository.requestRepoReset();
      maskerPane.setVisible(false);
    });
  }

  public void editSpecificCity() {
    maskerPane.visibleProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
          Boolean newValue) {
        // chooserTP.setExpanded(true);
        accordion.setExpandedPane(chooserTP);
        PauseTransition pause = new PauseTransition(Duration.millis(700));
        pause.setOnFinished(finished -> {
          showMapComboBox();
          maskerPane.visibleProperty().removeListener(this);
        });
        pause.play();
      }
    });
  }

  public void showMapComboBox() {
    mapsCB.show();
  }
}
