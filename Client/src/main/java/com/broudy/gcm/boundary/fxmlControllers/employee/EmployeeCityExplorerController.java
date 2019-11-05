package com.broudy.gcm.boundary.fxmlControllers.employee;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.StageManager;
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
import com.broudy.gcm.entity.dtos.MapDTO;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleButton;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.event.MarkerEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class controls the viewing of a city by an employee.
 * <p>
 * Created on the 29th of August, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeCityExplorerController<RIS extends Renderable & IStatable> extends
    EnhancedFXMLController {

  private final static Logger logger = LogManager.getLogger(EmployeeCityExplorerController.class);

  private final Coordinate ISRAEL_CENTER = new Coordinate(31.494261815532752, 35.1947021484375);

  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;
  private final SitesRepository sitesRepository;
  private final ToursRepository toursRepository;
  private final SceneSwitcheroo sceneSwitcheroo;
  private final MapViewRenderingsService mapViewRenderingsService;
  private final BooleanProperty mapIsBeingAnimated;
  private final ChangeListener<Boolean> mapViewInitializationListener;
  private final ListChangeListener<? super Renderable> sitesBeansListListener;
  private final ListChangeListener<? super Renderable> toursBeansListListener;

  /* Guice injected fields */
  @Inject
  private StageManager stageManager;
  private ConcurrentHashMap<RIS, SiteRendering> renderedSitesOfMap;
  private ConcurrentHashMap<RIS, TourRendering> renderedToursOfMap;

  @FXML
  private MapView mapView;
  @FXML
  private JFXListView<RIS> mapsLV;
  @FXML
  private JFXListView<RIS> sitesLV;
  @FXML
  private JFXListView<RIS> toursLV;
  @FXML
  private JFXButton rejectBTN;
  @FXML
  private JFXButton approveBTN;
  @FXML
  private JFXToggleButton mapTypeTB;
  @FXML
  private JFXButton backBTN;
  @Inject
  private EventHandlingService eventHandlingService;

  @Inject
  public EmployeeCityExplorerController(CitiesRepository citiesRepository,
      MapsRepository mapsRepository, SitesRepository sitesRepository,
      ToursRepository toursRepository, SceneSwitcheroo sceneSwitcheroo,
      MapViewRenderingsService mapViewRenderingsService) {
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.sitesRepository = sitesRepository;
    this.toursRepository = toursRepository;
    this.sceneSwitcheroo = sceneSwitcheroo;
    this.mapViewRenderingsService = mapViewRenderingsService;
    this.renderedSitesOfMap = new ConcurrentHashMap<>();
    this.renderedToursOfMap = new ConcurrentHashMap<>();
    this.mapIsBeingAnimated = new SimpleBooleanProperty(false);
    this.mapViewInitializationListener = (observable, oldValue, newValue) -> {
      if (newValue) {
        logger.trace("Map has been initialized. Finishing up...");
        String bingMapsApiKey = "jRBBC3c6oCO3ebBAITlD~SCTs0-piL0zBf3eqp61CiA~All99rEJA33I8lQpEffyBcbCUZRDs1aZrem6CfTA5m5svwt_JU07CF-fSd1dyXsf";
        mapView.setBingMapsApiKey(bingMapsApiKey);
        mapView.setMapType(MapType.BINGMAPS_AERIAL);
        mapView.setCenter(ISRAEL_CENTER);
        mapView.setZoom(8);
        mapView.setAnimationDuration(1000);
        mapViewRenderingsService.setMapView(mapView);
        logger.trace("Finished.");
      }
    };
    this.sitesBeansListListener = listChange -> {
      while (listChange.next()) {
        renderSites((List<RIS>) listChange.getList());
        logger.debug(TextColors.CYAN.colorThis("New mapSitesList: {}"), listChange.getList());
      }
    };
    this.toursBeansListListener = listChange -> {
      while (listChange.next()) {
        renderTours((List<RIS>) listChange.getList());
        logger.debug(TextColors.CYAN.colorThis("New mapToursList: {}"), listChange.getList());
      }
    };
  }

  @Override
  public void initializeEnhancedController() {
  }

  @Override
  protected void activateController() {

    mapsLV.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    mapsLV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    sitesLV.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    sitesLV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    toursLV.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    toursLV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    activateBindings();
    activateListeners();
    activateEventHandlers();

    mapView.initialize();
    // Check all check boxes
  }

  @Override
  protected void deactivateController() {
    mapView.initializedProperty().removeListener(mapViewInitializationListener);
    sitesRepository.beansListProperty().removeListener(sitesBeansListListener);
    toursRepository.beansListProperty().removeListener(toursBeansListListener);

    mapsLV.itemsProperty().unbind();
    sitesLV.itemsProperty().unbind();
    toursLV.itemsProperty().unbind();
    mapsLV.disableProperty().unbind();
    sitesLV.disableProperty().unbind();
    toursLV.disableProperty().unbind();

    mapView.close();
  }

  @Override
  protected void activateBindings() {
    mapsLV.itemsProperty().bind(mapsRepository.beansListProperty());
    sitesLV.itemsProperty().bind(sitesRepository.sitesOfMapList());
    toursLV.itemsProperty().bind(toursRepository.beansListProperty());

    mapsLV.disableProperty().bind(mapIsBeingAnimated);
    sitesLV.disableProperty().bind(mapIsBeingAnimated);
    toursLV.disableProperty().bind(mapIsBeingAnimated);
  }

  @Override
  protected void activateListeners() {
    // Watch the MapView's initialized property to finish initialization.
    mapView.initializedProperty().addListener(mapViewInitializationListener);

    mapsLV.getSelectionModel().selectedItemProperty()
        .addListener((observable, wasSelected, selected) -> {
          if (selected != null) {
            sitesLV.getSelectionModel().clearSelection();
            mapsRepository.setBean((MapDTO) selected);
            // toursRepository.requestRepoReset();
            sitesRepository.requestSitesOfMap();
            toursRepository.requestToursOfMap();
            setMapExtent(sitesRepository.sitesOfMapList());
          }
        });

    sitesRepository.beansListProperty().addListener(sitesBeansListListener);

    sitesLV.getSelectionModel().selectedItemProperty()
        .addListener((observable, wasSelected, selected) -> {
          if (wasSelected != null) {
            logger.info("wasSelected != null: {}", wasSelected);
            SiteRendering wasRendered = renderedSitesOfMap.get(wasSelected);
            mapView.removeMarker(wasRendered.getRenderedMarker());
            wasRendered.setSelected(false);
            wasRendered.setLabelVisibility(false);
            mapView.addMarker(wasRendered.getRenderedMarker());
          }
          if (selected != null && selected.getState() != State.DEFAULT) {
            logger.info("selected != null: {}", selected);
            SiteRendering siteToBeRendered = renderedSitesOfMap.get(selected);
            mapView.removeMarker(siteToBeRendered.getRenderedMarker());
            siteToBeRendered.setSelected(true);
            siteToBeRendered.setLabelVisibility(true);
            mapView.addMarker(siteToBeRendered.getRenderedMarker());
            setCenterAndZoom(siteToBeRendered.getPosition(), 15);

            for (Entry<RIS, SiteRendering> siteEntry : renderedSitesOfMap.entrySet()) {
              if (!siteEntry.getKey().equals(selected)) {
                SiteRendering renderingUnderTest = siteEntry.getValue();
                if (renderingUnderTest.isSelected()) {
                  mapView.removeMarker(renderingUnderTest.getRenderedMarker());
                  renderingUnderTest.setSelected(false);
                  renderingUnderTest.setLabelVisibility(false);
                  mapView.addMarker(renderingUnderTest.getRenderedMarker());
                } else if ((Math.abs(
                    renderingUnderTest.getPosition().getLongitude() - siteToBeRendered.getPosition()
                        .getLongitude()) < 0.0009 || Math.abs(
                    renderingUnderTest.getPosition().getLatitude() - siteToBeRendered.getPosition()
                        .getLatitude()) < 0.0009)) {
                  renderingUnderTest.detachFrom(mapView);
                  renderingUnderTest.attachTo(mapView);
                }
              }
            }

            // Bring newly rendered site to front.
            /*renderedSitesOfMap.values().stream().filter(
                rendering -> rendering.isVisible() && !rendering.equals(siteToBeRendered) && (
                    Math.abs(rendering.getPosition().getLongitude() - siteToBeRendered.getPosition()
                        .getLongitude()) < 0.0009 || Math.abs(
                        rendering.getPosition().getLatitude() - siteToBeRendered.getPosition()
                            .getLatitude()) < 0.0009)).forEach(rendering -> {
            });*/
          }
        });

    toursRepository.beansListProperty().addListener(toursBeansListListener);

    toursLV.getSelectionModel().selectedItemProperty()
        .addListener((observable, wasSelected, selected) -> {
          if (selected != null && selected.getState() != State.DEFAULT) {
            List<Coordinate> newExtent = new ArrayList<>();
            renderedToursOfMap.get(selected).refreshCoordinates(mapView);
            List<SiteRendering> sitesInTour = renderedToursOfMap.get(selected)
                .getSiteRenderingsInTour();
            System.out.println("\n\n");
            logger.info("SITES: {}", sitesInTour);

            for (SiteRendering siteRendering : renderedSitesOfMap.values()) {
              if (sitesInTour.contains(siteRendering)) {
                logger.info("tour contains this: {}", siteRendering);
                if (!siteRendering.isSelected()) {
                  mapView.removeMarker(siteRendering.getRenderedMarker());
                  siteRendering.setSelected(true);
                  siteRendering.setLabelVisibility(true);
                  mapView.addMarker(siteRendering.getRenderedMarker());
                  logger.info("wasn't selected, now is: {}", siteRendering);
                }
                newExtent.add(siteRendering.getPosition());
                logger.info("added to extent: {}", siteRendering);
              } else {
                logger.info("tour DOESNT contain this: {}", siteRendering);
                if (siteRendering.isSelected()) {
                  mapView.removeMarker(siteRendering.getRenderedMarker());
                  siteRendering.setSelected(false);
                  siteRendering.setLabelVisibility(false);
                  mapView.addMarker(siteRendering.getRenderedMarker());
                  logger.info("wasn selected, now is NOT: {}", siteRendering);
                } else if (sitesInTour.stream().anyMatch(siteToBeRendered -> Math.abs(
                    siteRendering.getPosition().getLongitude() - siteToBeRendered.getPosition()
                        .getLongitude()) < 0.0009 || Math.abs(
                    siteRendering.getPosition().getLatitude() - siteToBeRendered.getPosition()
                        .getLatitude()) < 0.0009)) {
                  siteRendering.detachFrom(mapView);
                  siteRendering.attachTo(mapView);
                }
              }
            }
            setExtent(newExtent);
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
          tour.refreshCoordinates(mapView);
        }
      }
    });
  }

  @Override
  protected void activateEventHandlers() {
    mapView.setOnMouseEntered(event -> mapTypeTB.setOpacity(0.6D));
    mapView.setOnMouseExited(event -> mapTypeTB.setOpacity(1D));

    approveBTN.setOnAction(click -> eventHandlingService
        .approveFromCityExplorerApprovalRequestButtonHandler(click, citiesRepository.getBean()));
    rejectBTN.setOnAction(click -> eventHandlingService
        .rejectFromCityExplorerApprovalRequestButtonHandler(click, citiesRepository.getBean()));

    backBTN.setOnAction(click -> {
      mapView.setCenter(ISRAEL_CENTER);
      mapView.setZoom(8);
      sceneSwitcheroo.hideDrawerView();
    });

    mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
      Optional<Entry<RIS, SiteRendering>> clickedSite = renderedSitesOfMap.entrySet().stream()
          .filter(entry -> entry.getValue().getRenderedMarker().equals(event.getMarker()))
          .findFirst();
      if (clickedSite.isPresent()) {
        sitesLV.getSelectionModel().select(clickedSite.get().getKey());
      }
      System.out.println("\n\nMarker Clicked: " + event.getMarker().getPosition());
    });
  }

  private void renderSites(List<RIS> sitesToBeRendered) {
    // TODO implement a process more tuned to ListChange for site renderings
    // Remove sites' renderings from the map's view
    renderedSitesOfMap.forEach((renderable, siteRendering) -> siteRendering.detachFrom(mapView));
    // Generate new sites' renderings
    renderedSitesOfMap = mapViewRenderingsService.generateMapSitesRenderings();
    // Add sites' renderings to the map's view
    renderedSitesOfMap.forEach((renderable, siteRendering) -> {
      siteRendering.visibleProperty().unbind();
      siteRendering.visibleProperty().bind(Bindings
          .createBooleanBinding(() -> sitesRepository.sitesOfMapList().contains(renderable),
              sitesRepository.sitesOfMapList()));
      siteRendering.attachTo(mapView);
    });
  }

  private void renderTours(List<RIS> list) {
    // Remove tours' renderings from the map's view
    renderedToursOfMap.forEach((renderable, tourRendering) -> tourRendering.detachFrom(mapView));
    // Generate new tours' renderings
    renderedToursOfMap = mapViewRenderingsService.generateMapToursRenderings();
    // Add tours' renderings to the map's view
    renderedToursOfMap.forEach((renderable, tourRendering) -> {
      tourRendering.visibleProperty().unbind();
      tourRendering.setVisible(true);
      tourRendering.attachTo(mapView);
    });
  }


  private void setMapExtent(List<RIS> list) {
    List<Coordinate> renderedSitesCoordinates = renderedSitesOfMap.entrySet().stream()
        .filter(entry -> list.contains(entry.getKey())).map(Entry::getValue)
        .map(SiteRendering::getPosition).collect(Collectors.toList());
    if (renderedSitesCoordinates.size() < 1) {
      setExtent(renderedSitesOfMap.values().stream().map(SiteRendering::getPosition)
          .collect(Collectors.toList()));
    } else if (renderedSitesCoordinates.size() == 1) {
      mapView.setCenter(renderedSitesCoordinates.get(0));
      mapView.setZoom(13);
    } else {
      setExtent(renderedSitesCoordinates);
    }
  }


  private void setExtent(List<Coordinate> newExtent) {
    if (mapIsBeingAnimated.get()) {
      new Shake(mapView).play();
    } else {
      mapIsBeingAnimated.set(true);
      PauseTransition pause = new PauseTransition(Duration.millis(1000));
      pause.setOnFinished(finished -> {
        mapIsBeingAnimated.set(false);
      });
      mapView.setExtent(mapViewRenderingsService.getEnhancedExtent(newExtent));
      pause.play();
    }
  }

  private void setCenterAndZoom(Coordinate newCenter, double zoomLevel) {
    if (mapIsBeingAnimated.get()) {
      new Shake(mapView).play();
    } else {
      mapIsBeingAnimated.set(true);
      PauseTransition pause = new PauseTransition(Duration.millis(1000));
      pause.setOnFinished(finished -> {
        mapIsBeingAnimated.set(false);
      });
      mapView.setCenter(newCenter);
      mapView.setZoom(zoomLevel);
      pause.play();
    }
  }


}
