package com.broudy.gcm.boundary.fxmlControllers.customer;

import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.MapsRepository;
import com.broudy.gcm.control.repos.SitesRepository;
import com.broudy.gcm.control.repos.ToursRepository;
import com.broudy.gcm.control.services.renderings.MapViewRenderingsService;
import com.broudy.gcm.control.services.renderings.MapViewRenderingsService.SiteRendering;
import com.broudy.gcm.control.services.renderings.MapViewRenderingsService.TourRendering;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.broudy.gcm.control.services.renderings.RenderingsStyler.EngineeredCellFactories;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.Extent;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.CheckTreeView;

/**
 * This class controls the program's flow when a customer views a city.
 * <p>
 * Created on the 29th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CustomerCityExplorerController<RIS extends Renderable & IStatable> extends
    EnhancedFXMLController {

  private final static Logger logger = LogManager.getLogger(CustomerSidePaneController.class);

  private final Coordinate ISRAEL_CENTER = new Coordinate(31.494261815532752, 35.1947021484375);
  private final MapsRepository mapsRepository;
  private final SitesRepository sitesRepository;
  private final ToursRepository toursRepository;
  private final SceneSwitcheroo sceneSwitcheroo;
  private final MapViewRenderingsService mapViewRenderingsService;
  private final FileChooser fileChooser;

  private final ChangeListener<Boolean> mapViewInitializationListener;
  private final ListChangeListener<? super Renderable> sitesBeansListListener;
  private final ListChangeListener<? super Renderable> sitesOfMapListListener;
  private final ListChangeListener<? super Renderable> toursBeansListListener;

  /* Guice injected fields */
  @Inject
  private StageManager stageManager;
  private CheckBoxTreeItem<RIS> sitesRootDummy;
  private ConcurrentHashMap<RIS, SiteRendering> renderedSitesOfMap;
  private ConcurrentHashMap<RIS, CheckBoxTreeItem<RIS>> renderedSitesCheckBoxItems;
  private ConcurrentHashMap<RIS, TourRendering> renderedToursOfMap;


  @FXML
  private MapView mapView;
  @FXML
  private JFXComboBox<RIS> mapsCB;
  @FXML
  private CheckTreeView<RIS> sitesCTV;
  @FXML
  private CheckListView<RIS> toursCLV;
  @FXML
  private JFXButton downloadSnapshotBTN;
  @FXML
  private JFXToggleButton mapTypeTB;
  @FXML
  private JFXButton backBTN;

  @Inject
  public CustomerCityExplorerController(MapsRepository mapsRepository,
      SitesRepository sitesRepository, ToursRepository toursRepository,
      SceneSwitcheroo sceneSwitcheroo, MapViewRenderingsService mapViewRenderingsService) {
    this.mapsRepository = mapsRepository;
    this.sitesRepository = sitesRepository;
    this.toursRepository = toursRepository;
    this.sceneSwitcheroo = sceneSwitcheroo;
    this.mapViewRenderingsService = mapViewRenderingsService;
    this.renderedSitesOfMap = new ConcurrentHashMap<>();
    this.renderedSitesCheckBoxItems = new ConcurrentHashMap<>();
    this.renderedToursOfMap = new ConcurrentHashMap<>();
    fileChooser = new FileChooser();
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
        sitesCTV.getCheckModel().getCheckedItems()
            .addListener((ListChangeListener<? super TreeItem<RIS>>) listChange -> {
              System.out.println(sitesCTV.getCheckModel().getCheckedItems());
              while (listChange.next()) {
                if (listChange.wasRemoved()) {
                  System.out.println("removed: " + listChange.getRemoved());
                  List<RIS> removedSites = listChange.getRemoved().stream().map(TreeItem::getValue)
                      .collect(Collectors.toList());
                  for (Entry<RIS, TourRendering> entry : renderedToursOfMap.entrySet().stream()
                      .filter(entry -> entry.getValue().visibleProperty().get())
                      .collect(Collectors.toList())) {
                    System.out.println("Checking tour: " + entry.getKey());
                    for (RIS removedSite : removedSites) {
                      System.out.println("Checking site: " + removedSite);
                      if (entry.getValue()
                          .containsSiteRendering(renderedSitesOfMap.get(removedSite))) {
                        System.out.println("it contains!!.. before:  " + toursCLV.getCheckModel()
                            .getCheckedIndices());
                        toursCLV.getCheckModel().clearCheck(entry.getKey());
                        // System.out.println("it contains!!.. after:  " +toursCLV.getCheckModel().getCheckedIndices());
                        break;
                      }
                    }
                  }
                }
                setMapExtent(listChange.getList().stream().map(TreeItem::getValue)
                    .collect(Collectors.toList()));
              }
            });
        logger.trace("Finished.");
      }
    };
    this.sitesBeansListListener = listChange -> {
      while (listChange.next()) {
        renderSites((List<RIS>) listChange.getList());
        logger.debug(TextColors.CYAN.colorThis("New mapSitesList: {}"), listChange.getList());
      }
    };
    this.sitesOfMapListListener = listChange -> {
      while (listChange.next()) {
        fillList((List<RIS>) listChange.getList());
        logger.debug(TextColors.CYAN.colorThis("New filledList: {}"), listChange.getList());
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
    sitesRootDummy = new CheckBoxTreeItem<>();
    // Initialize FileChooser
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
    fileChooser.setTitle("Choose location for map's snapshot...");
  }

  @Override
  protected void activateController() {
    mapsCB.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    mapsCB.setButtonCell(RenderingsStyler.EngineeredCellFactories.callRLV(null));

    // Create a dummy root to hide first level of tree
    sitesCTV.setRoot(sitesRootDummy);
    // Hide root level in order to have same level categories
    sitesCTV.setShowRoot(false);
    sitesCTV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    sitesCTV.setCellFactory(EngineeredCellFactories::callCTV);

    toursCLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    // Set the renderings cell factory
    // toursCLV.setCellFactory(EngineeredCellFactories::callRISCLVN);
    toursCLV
        .setCellFactory(listView -> new CheckBoxListCell<RIS>(toursCLV::getItemBooleanProperty) {
          @Override
          public void updateItem(RIS item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : item.render());
          }
        });
    // toursCLV.setCheckModel();

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
    sitesRepository.sitesOfMapList().removeListener(sitesOfMapListListener);
    toursRepository.beansListProperty().removeListener(toursBeansListListener);

    sitesCTV.disableProperty().unbind();
    toursCLV.disableProperty().unbind();
    toursCLV.itemsProperty().unbind();
    mapView.close();
  }

  @Override
  protected void activateBindings() {
    mapsCB.itemsProperty().bindBidirectional(mapsRepository.beansListProperty());
    mapsCB.valueProperty().bindBidirectional(mapsRepository.beanProperty());

    sitesCTV.disableProperty().bind(
        Bindings.createBooleanBinding(() -> mapsCB.getValue() == null, mapsCB.valueProperty()));

    toursCLV.disableProperty().bind(
        Bindings.createBooleanBinding(() -> mapsCB.getValue() == null, mapsCB.valueProperty()));
    toursCLV.itemsProperty().bind(toursRepository.beansListProperty());
  }

  @Override
  protected void activateListeners() {
    // Watch the MapView's initialized property to finish initialization.
    mapView.initializedProperty().addListener(mapViewInitializationListener);

    mapsCB.valueProperty().addListener((observable, oldValue, newValue) -> {
      sitesRepository.requestSitesOfMap();
      toursRepository.requestToursOfMap();
    });

    sitesRepository.beansListProperty().addListener(sitesBeansListListener);
    sitesRepository.sitesOfMapList().addListener(sitesOfMapListListener);

    toursRepository.beansListProperty().addListener(toursBeansListListener);

    // Change MapView's type with the toggle button.
    mapTypeTB.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != oldValue) {
        if (newValue) {
          mapView.setMapType(MapType.BINGMAPS_AERIAL);
        } else {
          mapView.setMapType(MapType.BINGMAPS_ROAD);
        }
      }
    });
  }


  @Override
  protected void activateEventHandlers() {
    mapView.setOnMouseEntered(event -> mapTypeTB.setOpacity(0.6D));
    mapView.setOnMouseExited(event -> mapTypeTB.setOpacity(1D));

    downloadSnapshotBTN.setOnAction(click -> downloadSnapshot());

    backBTN.setOnAction(click -> {
      mapView.setCenter(ISRAEL_CENTER);
      mapView.setZoom(8);
      sceneSwitcheroo.hideDrawerView();
    });
  }

  private void renderSites(List<RIS> sitesToBeRendered) {
    // TODO implement a process more tuned to ListChange for site renderings
    // Remove sites' renderings from the map's view
    renderedSitesOfMap.forEach((renderable, siteRendering) -> siteRendering.detachFrom(mapView));
    // Generate new sites' renderings
    renderedSitesOfMap = mapViewRenderingsService.generateMapSitesRenderings();

    // Add sites' renderings to the map's view
    renderedSitesOfMap.forEach((renderable, siteRendering) -> siteRendering.attachTo(mapView));
    sitesCTV.getCheckModel().checkAll();
  }

  private void fillList(List<RIS> listOfSitesToFill) {
    // Clean existing sites from CTV
    sitesRootDummy.getChildren().clear();
    renderedSitesCheckBoxItems.clear();
    renderedSitesOfMap.forEach((renderable, siteRendering) -> {
      siteRendering.visibleProperty().unbind();
      siteRendering.getLabelVisibility().unbind();
      if (listOfSitesToFill.contains(renderable)) {
        CheckBoxTreeItem<RIS> siteItem = new CheckBoxTreeItem<>(renderable);
        // Bind the checkbox's checked property to the site's generated rendering visibility
        siteRendering.visibleProperty().bind(siteItem.selectedProperty());
        siteRendering.getLabelVisibility().bind(siteRendering.visibleProperty());
        renderedSitesCheckBoxItems.putIfAbsent(renderable, siteItem);
        sitesRootDummy.getChildren().add(siteItem);
      } else {
        siteRendering.setVisible(false);
        siteRendering.setLabelVisibility(false);
      }
    });
    sitesRootDummy.setExpanded(true);
    sitesCTV.getCheckModel().checkAll();
  }


  private void renderTours(List<RIS> list) {
    // Remove tours' renderings from the map's view
    renderedToursOfMap.forEach((renderable, tourRendering) -> tourRendering.detachFrom(mapView));
    // Generate new tours' renderings
    renderedToursOfMap = mapViewRenderingsService.generateMapToursRenderings();

    // Add tours' renderings to the map's view
    renderedToursOfMap.forEach((renderable, tourRendering) -> {
      tourRendering.attachTo(mapView);
      // tourRendering.setVisible(true);
      tourRendering.visibleProperty().bind(Bindings.createBooleanBinding(
          () -> toursCLV.getCheckModel().getCheckedItems().contains(renderable)/* {
        if (toursCLV.getCheckModel().getCheckedItems().contains(renderable)) {
          return tourRendering.getSiteRenderingsInTour().stream()
              .noneMatch(site -> !((SiteRendering) site).isVisible());
        } else {
          return false;
        }
      }*/, toursCLV.getCheckModel().getCheckedItems()));
      tourRendering.visibleProperty().addListener((observable, wasVisible, isVisible) -> {
        if (isVisible && !wasVisible) {
          for (Entry<RIS, SiteRendering> siteEntry : renderedSitesOfMap.entrySet().stream()
              .filter(entry -> !entry.getValue().isVisible()).collect(Collectors.toList())) {
            if (tourRendering.containsSiteRendering(siteEntry.getValue())) {
              renderedSitesCheckBoxItems.get(siteEntry.getKey()).setSelected(true);
              // sitesCTV.getCheckModel()
              //     .check(;);
            }

          }
        }
      });
    });
  }


  private void setMapExtent(List<RIS> list) {
    List<Coordinate> renderedSitesCoordinates = renderedSitesOfMap.entrySet().stream()
        .filter(entry -> list.contains(entry.getKey())).map(Entry::getValue)
        .map(SiteRendering::getPosition).collect(Collectors.toList());
    if (renderedSitesCoordinates.size() < 1) {
      mapView.setExtent(mapViewRenderingsService.getEnhancedExtent(
          renderedSitesOfMap.values().stream().map(SiteRendering::getPosition)
              .collect(Collectors.toList())));
    } else if (renderedSitesCoordinates.size() == 1) {
      mapView.setCenter(renderedSitesCoordinates.get(0));
      mapView.setZoom(13);
    } else {
      Extent allSitesCoordinates = mapViewRenderingsService
          .getEnhancedExtent(renderedSitesCoordinates);
      mapView.setExtent(allSitesCoordinates);
    }
  }


  private void downloadSnapshot() {
    // Set FileChooser's initial file name
    fileChooser.setInitialFileName(mapsRepository.getBean().render());

    // Take snapshot of map's view
    WritableImage image = mapView.snapshot(new SnapshotParameters(), null);

    // Open save as dialog
    File file = fileChooser.showSaveDialog(stageManager.getPrimaryStage());

    // Save snapshot as ".png" file
    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    } catch (IOException e) {
      // TODO: handle exception!!!!!!!!!!!!!!!!!!!!!!
    }
  }


}
