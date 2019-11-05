package com.broudy.gcm.control.services.renderings;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.MapsRepository;
import com.broudy.gcm.control.repos.PurchasesRepository;
import com.broudy.gcm.control.repos.SitesRepository;
import com.broudy.gcm.control.repos.ToursRepository;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.EventHandlingService;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.CityDTO;
import com.broudy.gcm.entity.dtos.PurchaseDTO;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is the go-to class regarding all city-related renderings.
 * <p>
 * Created on the 1st of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CityRenderingsService<RIS extends Renderable & IStatable> {

  private static final Logger logger = LogManager.getLogger(CityRenderingsService.class);

  private final UsersRepository usersRepository;
  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;
  private final SitesRepository sitesRepository;
  private final ToursRepository toursRepository;
  private final PurchasesRepository purchasesRepository;

  private final SceneSwitcheroo sceneSwitcheroo;


  @Inject
  private EventHandlingService eventHandlingService;

  private ConcurrentHashMap<RIS, CityCatalogRendering> currenltyRenderedCitiesCatalog;


  @Inject
  public CityRenderingsService(UsersRepository usersRepository, CitiesRepository citiesRepository,
      MapsRepository mapsRepository, SitesRepository sitesRepository,
      ToursRepository toursRepository, PurchasesRepository purchasesRepository,
      SceneSwitcheroo sceneSwitcheroo) {
    this.usersRepository = usersRepository;
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.sitesRepository = sitesRepository;
    this.toursRepository = toursRepository;
    this.purchasesRepository = purchasesRepository;
    this.sceneSwitcheroo = sceneSwitcheroo;
  }

  public ConcurrentHashMap<Renderable, CityWorkspaceRendering> generateRenderingsOfCityInWorkspace() {
    ConcurrentHashMap<Renderable, CityWorkspaceRendering> hashMap = new ConcurrentHashMap<>();

    List<CityDTO> renderableCities = citiesRepository.getEmployeesWorkspace();
    // List<CityDTO> renderableCities = citiesRepository.getBeansList();

    CityWorkspaceRendering cityRendering;
    // Loop over all sites to be rendered
    for (CityDTO city : renderableCities) {

      // Create rendering from renderable and store it in HashMap
      cityRendering = new CityWorkspaceRendering(city);
      // bindVisibilityToCityExistenceInEmployeesWorkspaceList(cityRendering, city);
      hashMap.put(city, cityRendering);
    }
    return hashMap;
  }

  private void bindVisibilityToCityExistenceInEmployeesWorkspaceList(
      CityWorkspaceRendering cityRendering, CityDTO city) {
    cityRendering.visibleProperty().bind(Bindings.createBooleanBinding(
        () -> citiesRepository.getEmployeesWorkspace().stream()
            .anyMatch(workspaceCity -> ((CityDTO) workspaceCity).getID() == city.getID()),
        citiesRepository.employeesWorkspaceProperty(), citiesRepository.beansListProperty()));
    cityRendering.managedProperty().bind(cityRendering.visibleProperty());
  }

  public ConcurrentHashMap<RIS, CityApprovalRequestRendering> generateRenderingsOfCityApprovalRequests() {

    ConcurrentHashMap<RIS, CityApprovalRequestRendering> hashMap = new ConcurrentHashMap<>();

    List<CityDTO> renderableCities = citiesRepository.getEmployeesWorkspace();
    // List<CityDTO> renderableCities = citiesRepository.getBeansList();

    CityApprovalRequestRendering cityRequestRendering;
    // Loop over all sites to be rendered
    for (CityDTO city : renderableCities) {

      // Create rendering from renderable and store it in HashMap
      cityRequestRendering = new CityApprovalRequestRendering(city);
      // bindVisibilityToCityExistenceInEmployeesWorkspaceList(cityRendering, city);
      hashMap.put((RIS) city, cityRequestRendering);
    }
    return hashMap;
  }


  public ConcurrentHashMap<RIS, CityCatalogRendering> generateRenderingsOfCityCatalogPreviews() {
    ConcurrentHashMap<RIS, CityCatalogRendering> cities = new ConcurrentHashMap<>();
    List<CityDTO> renderableCities = citiesRepository.getBeansList();

    CityCatalogRendering cityCatalogRendering;
    // Loop over all sites to be rendered
    for (CityDTO city : renderableCities) {

      // Create rendering from renderable and store it in HashMap
      cityCatalogRendering = generateCityRendering(city);
      // bindVisibilityToSiteExistenceInSitesOfMapList(cityRendering, city);
      cities.put((RIS) city, cityCatalogRendering);
    }
    currenltyRenderedCitiesCatalog = cities;
    return cities;
  }

  public CityCatalogRendering generateCityRendering(Renderable city) {
    if (!(city instanceof CityDTO)) {
      throw new ClassCastException();
    }
    CityCatalogRendering cityCatalogRendering = new CityCatalogRendering((CityDTO) city);
    return logger.traceExit(TextColors.GREEN.colorThis("Generated") + " rendering of: {}",
        cityCatalogRendering);
  }

  public class CityWorkspaceRendering extends StackPane {

    private final HBox mainContainer;
    private final Label cityName;
    private final Label numberOfMaps;
    private final Label numberOfSites;
    private final Label status;

    private CityDTO renderedCity;

    /**
     * Creates an AnchorPane layout.
     */
    public CityWorkspaceRendering(CityDTO cityToBeRendered) {
      this.renderedCity = cityToBeRendered;
      mainContainer = new HBox();
      cityName = new Label(cityToBeRendered.getName());
      numberOfMaps = new Label(String.valueOf(cityToBeRendered.getNumberOfMaps()));
      numberOfSites = new Label(String.valueOf(cityToBeRendered.getNumberOfSites()));
      status = new Label(cityToBeRendered.getState().name().replace("_", " "));
      buildLayout();
    }

    private void buildLayout() {

      setAlignment(Pos.CENTER);

      BorderPane cityNameBP = new BorderPane(cityName);
      cityNameBP.setMinWidth(225);
      cityNameBP.setMaxWidth(225);
      cityName.getStyleClass().add("label-24");
      HBox.setHgrow(cityNameBP, Priority.NEVER);

      BorderPane numberOfMapsBP = new BorderPane(numberOfMaps);
      numberOfMapsBP.setMinWidth(125);
      numberOfMapsBP.setMaxWidth(125);
      HBox.setHgrow(numberOfMapsBP, Priority.NEVER);

      BorderPane numberOfSitesBP = new BorderPane(numberOfSites);
      numberOfSitesBP.setMinWidth(125);
      numberOfSitesBP.setMaxWidth(125);
      HBox.setHgrow(numberOfSitesBP, Priority.NEVER);

      BorderPane statusBP = new BorderPane(status);
      statusBP.setMinWidth(215);
      statusBP.setMaxWidth(215);
      status.setWrapText(true);
      status.setTextAlignment(TextAlignment.CENTER);
      HBox.setHgrow(statusBP, Priority.NEVER);

      JFXButton actionBTN = new JFXButton();
      actionBTN.setMinWidth(300);
      actionBTN.setMaxWidth(300);
      actionBTN.setPrefHeight(100);
      if (renderedCity.getState() == State.LOCKED) {
        actionBTN.getStyleClass().addAll("button-map-workspace-request-approval");
        actionBTN.setText("Request Approval");
        actionBTN.setOnAction(requestApprovalClicked -> eventHandlingService
            .requestApprovalButtonHandler(requestApprovalClicked, renderedCity));
      } else if (renderedCity.getState() == State.REJECTED) {
        actionBTN.getStyleClass().addAll("button-map-workspace-edit");
        actionBTN.setText("Edit City");
        actionBTN.setOnAction(editCityClicked -> eventHandlingService
            .editCityButtonHandler(editCityClicked, renderedCity));
      } else if (renderedCity.getState() == State.PENDING_APPROVAL) {
        actionBTN.getStyleClass().addAll("button-map-workspace-cancel");
        actionBTN.setText("Cancel Request");
        actionBTN.setOnAction(cancelApprovalRequestClicked -> eventHandlingService
            .cancelApprovalRequestButtonHandler(cancelApprovalRequestClicked, renderedCity));
      } else {
        actionBTN.getStyleClass().addAll("button-map-workspace-clear");
        actionBTN.setText("Clear");
        actionBTN.setOnAction(clearCityClicked -> eventHandlingService
            .clearCityFromWorkspaceButtonHandler(clearCityClicked, renderedCity));
      }
      actionBTN.getStyleClass().add("label-bold");
      VBox actionsVB = new VBox(actionBTN);
      actionsVB.setMinWidth(300);
      actionsVB.setMaxWidth(300);
      actionsVB.setAlignment(Pos.CENTER);
      VBox.setVgrow(actionBTN, Priority.NEVER);
      HBox.setHgrow(actionsVB, Priority.NEVER);

      mainContainer.setPrefWidth(1040);
      mainContainer.setPrefHeight(100);
      mainContainer.setPadding(new Insets(0, 0, 0, 50));
      mainContainer.setAlignment(Pos.CENTER);
      mainContainer.getStyleClass().addAll("tile-map-workspace-background", "label-22");
      mainContainer.setEffect(RenderingsStyler.getDropShadow());
      RenderingsStyler
          .applyStyleClass("text-drop-shadow-effect", cityName, numberOfMaps, numberOfSites,
              status);
      if (renderedCity.getState() == State.REJECTED) {
        RenderingsStyler.applyStyleClass("label-text-white", cityName, numberOfMaps, numberOfSites);
        status.setTextFill(Color.web("#ff2636"));
      } else {
        RenderingsStyler
            .applyStyleClass("label-text-white", cityName, numberOfMaps, numberOfSites, status);
      }
      mainContainer.getChildren()
          .addAll(cityNameBP, numberOfMapsBP, numberOfSitesBP, statusBP, actionsVB);

      JFXButton deleteBTN = new JFXButton("Delete");
      StackPane.setAlignment(deleteBTN, Pos.TOP_LEFT);
      deleteBTN.getStyleClass().addAll("button-map-workspace-discard", "label-16");
      // deleteBTN.setMinWidth(50);
      deleteBTN.setMinHeight(70);
      // deleteBTN.setMaxHeight(70);
      if (renderedCity.getState() == State.PENDING_APPROVAL
          || renderedCity.getState() == State.APPROVED) {
        deleteBTN.setDisable(true);
      } else {
        deleteBTN.setOnAction(deleteLockedCityClicked -> eventHandlingService
            .deleteLockedCityButtonHandler(deleteLockedCityClicked, renderedCity));
      }
      getChildren().addAll(mainContainer, deleteBTN);
    }
  }

  public class CityApprovalRequestRendering extends StackPane {

    private final HBox mainContainer;
    private final Label cityName;
    private final Label numberOfMaps;
    private final Label numberOfSites;
    private final Label status;
    private final JFXButton viewMapBTN;

    private CityDTO renderedCity;

    /**
     * Creates an AnchorPane layout.
     */
    public CityApprovalRequestRendering(CityDTO cityToBeRendered) {
      this.renderedCity = cityToBeRendered;
      mainContainer = new HBox();
      cityName = new Label(cityToBeRendered.getName());
      numberOfMaps = new Label(String.valueOf(cityToBeRendered.getNumberOfMaps()));
      numberOfSites = new Label(String.valueOf(cityToBeRendered.getNumberOfSites()));
      status = new Label(cityToBeRendered.getState().name().replace("_", " "));
      buildLayout();
      viewMapBTN = new JFXButton("View Map");
    }

    private void buildLayout() {

      setAlignment(Pos.CENTER);

      BorderPane cityNameBP = new BorderPane(cityName);
      cityNameBP.setMinWidth(225);
      cityNameBP.setMaxWidth(225);
      cityName.getStyleClass().add("label-24");
      HBox.setHgrow(cityNameBP, Priority.NEVER);

      BorderPane numberOfMapsBP = new BorderPane(numberOfMaps);
      numberOfMapsBP.setMinWidth(125);
      numberOfMapsBP.setMaxWidth(125);
      HBox.setHgrow(numberOfMapsBP, Priority.NEVER);

      BorderPane numberOfSitesBP = new BorderPane(numberOfSites);
      numberOfSitesBP.setMinWidth(125);
      numberOfSitesBP.setMaxWidth(125);
      HBox.setHgrow(numberOfSitesBP, Priority.NEVER);

      JFXButton approveBTN = new JFXButton();
      approveBTN.setMinWidth(500);
      approveBTN.setMaxWidth(500);
      approveBTN.setPrefHeight(100);

      approveBTN.getStyleClass().addAll("button-map-workspace-approve"/*, "label-bold"*/);
      approveBTN.setText("Approve");
      approveBTN.setOnAction(approveClicked -> eventHandlingService
          .approveApprovalRequestButtonHandler(approveClicked, renderedCity));
      HBox.setHgrow(approveBTN, Priority.NEVER);

      HBox approveHB = new HBox(approveBTN);
      approveHB.setMinWidth(515);
      approveHB.setMaxWidth(515);
      approveHB.setAlignment(Pos.CENTER_RIGHT);
      HBox.setHgrow(approveHB, Priority.NEVER);

      mainContainer.setPrefWidth(1040);
      mainContainer.setPrefHeight(100);
      mainContainer.setPadding(new Insets(0, 0, 0, 50));
      mainContainer.setAlignment(Pos.CENTER);
      mainContainer.getStyleClass().addAll("tile-map-workspace-background", "label-22");
      mainContainer.setEffect(RenderingsStyler.getDropShadow());
      RenderingsStyler
          .applyStyleClass("text-drop-shadow-effect", cityName, numberOfMaps, numberOfSites,
              status);
      RenderingsStyler
          .applyStyleClass("label-text-white", cityName, numberOfMaps, numberOfSites, status);
      mainContainer.getChildren().addAll(cityNameBP, numberOfMapsBP, numberOfSitesBP, approveHB);
      StackPane.setAlignment(mainContainer, Pos.CENTER);

      JFXButton rejectBTN = new JFXButton();
      rejectBTN.getStyleClass().addAll("button-map-workspace-reject", "label-22");
      rejectBTN.setText("Reject");
      rejectBTN.setMinWidth(200);
      rejectBTN.setMaxWidth(200);
      rejectBTN.setPrefHeight(100);
      // rejectBTN.setPadding(new Insets(0,5,0,0));
      rejectBTN.setOnAction(rejectClicked -> eventHandlingService
          .rejectApprovalRequestButtonHandler(rejectClicked, renderedCity));
      StackPane.setAlignment(rejectBTN, Pos.CENTER_RIGHT);

      JFXButton viewMap = new JFXButton("View Map");
      viewMap.setMinWidth(100);
      viewMap.setMinHeight(100);
      viewMap.getStyleClass().add("button-map-workspace-view-map");
      viewMap.setOnAction(click -> {
        citiesRepository.setBean(renderedCity);
        mapsRepository.requestRepoReset();
        sitesRepository.requestRepoReset();
        toursRepository.requestRepoReset();
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            mapsRepository.requestMapsOfCity();
            sitesRepository.requestSitesOfCity();
            sceneSwitcheroo.showDrawerView();
            return null;
          }
        }.run();
      });
      StackPane.setAlignment(viewMap, Pos.CENTER_LEFT);
      getChildren().addAll(mainContainer, rejectBTN, viewMap);
    }
  }

  // /**
  //  * TODO provide a summary to SiteRendering class!!!!!
  //  * <p>
  //  * Created on the 03th of September, 2019.
  //  *
  //  * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
  //  */
  // public class CityRendering<CITY extends CityDTO> {
  //
  //   private final CITY renderedCity;
  //
  //   public CityRendering(CITY renderedCity) {
  //     this.renderedCity = renderedCity;
  //   }
  // }

  public class CityCatalogRendering extends VBox {

    private final CityDTO renderedCity;
    private final Label cityName;
    private final Label releaseDate;
    private final Label numberOfMaps;
    private final Label numberOfSites;
    private final Label description;
    private final BooleanProperty active;
    private final VBox buttonsContainer;
    private final StringProperty styleClass;
    private final Label expiresOn;

    public CityCatalogRendering(CityDTO cityToBeRendered) {
      this.renderedCity = cityToBeRendered;
      cityName = new Label(cityToBeRendered.getName());
      expiresOn = new Label();
      releaseDate = new Label("Release Date: " + cityToBeRendered.getReleaseDate().toString());
      numberOfMaps = new Label("Number of Maps: " + cityToBeRendered.getNumberOfMaps());
      numberOfSites = new Label("Number of Sites: " + cityToBeRendered.getNumberOfSites());
      description = new Label(cityToBeRendered.getDescription());
      description.setWrapText(true);
      buttonsContainer = new VBox();
      active = new SimpleBooleanProperty(false);

      managedProperty().bind(visibleProperty());

      active.bind(Bindings.createBooleanBinding(() -> purchasesRepository.getBeansList().stream()
              .anyMatch(purchase -> ((PurchaseDTO) purchase).getCityID() == renderedCity.getID()),
          purchasesRepository.beansListProperty()));
      active.addListener((observable, wasActive, isActive) -> {
        transformRendering(wasActive, isActive);
        System.out.println("reattaching Appropriate Buttons");
      });
      buttonsContainer.disableProperty().bind(Bindings
          .createBooleanBinding(() -> !usersRepository.getBean().isOnline(),
              usersRepository.beanProperty()));

      styleClass = new SimpleStringProperty();
      styleClass.addListener((observable, oldValue, newValue) -> {
        if (oldValue != null) {
          getStyleClass().remove(oldValue);
        }
        if (newValue != null && !getStyleClass().contains(newValue)) {
          getStyleClass().add(newValue);
        }
      });
      buildLayout();
    }

    private void buildLayout() {

      setAlignment(Pos.TOP_CENTER);
      setPrefHeight(340);
      setPrefWidth(520);
      setPadding(new Insets(50, 30, 50, 30));
      setSpacing(10);
      setFillWidth(true);
      setEffect(RenderingsStyler.getDropShadow());
      TilePane.setAlignment(this, Pos.CENTER);

      // Set city name
      cityName.setWrapText(true);
      cityName.getStyleClass().addAll("label-28", "label-city-preview-title");
      getChildren().add(cityName);

      expiresOn.getStyleClass().addAll("label-20", "label-text-white");

      Region region = new Region();
      region.setMaxHeight(30);
      VBox.setVgrow(region, Priority.ALWAYS);
      getChildren().add(region);

      Region region1 = new Region();
      region1.setMaxHeight(10);
      VBox.setVgrow(region1, Priority.ALWAYS);

      Region region2 = new Region();
      region2.setMaxHeight(10);
      VBox.setVgrow(region2, Priority.ALWAYS);

      Region region3 = new Region();
      region3.setMaxHeight(Double.MAX_VALUE);
      VBox.setVgrow(region3, Priority.ALWAYS);

      VBox detailsContainer = new VBox(releaseDate, region1, numberOfMaps, region2, numberOfSites,
          region3, description);
      // detailsContainer.setSpacing(10);
      detailsContainer.setFillWidth(true);
      detailsContainer.setMaxHeight(Double.MAX_VALUE);
      VBox.setVgrow(detailsContainer, Priority.ALWAYS);
      VBox.setMargin(detailsContainer, new Insets(0, 20, 0, 20));
      getChildren().add(detailsContainer);

      Region region4 = new Region();
      region4.setMaxHeight(Double.MAX_VALUE);
      VBox.setVgrow(region4, Priority.ALWAYS);
      getChildren().add(region4);

      buttonsContainer.setFillWidth(true);
      buttonsContainer.setSpacing(10);
      VBox.setVgrow(buttonsContainer, Priority.NEVER);
      transformRendering(!active.get(), active.get());
      getChildren().add(buttonsContainer);

      detailsContainer.getStyleClass().addAll("label-18", "label-text-white");
    }

    private void transformRendering(boolean wasActive, boolean isActive) {

      if (isActive) {
        String expirationDate = purchasesRepository.getBeansList().stream()
            .filter(purchase -> ((PurchaseDTO) purchase).getCityID() == renderedCity.getID())
            .findFirst().map(purchase -> ((PurchaseDTO) purchase).getExpirationDate().toString())
            .get();
        expiresOn.setText("Expires on: " + expirationDate);
        if (!getChildren().contains(expiresOn)) {
          getChildren().add(1, expiresOn);
        } else {
          new Shake(expiresOn).play();
        }
        if (!wasActive) {
          buttonsContainer.getChildren().clear();
          JFXButton extendSubscriptionBTN = new JFXButton("Extend Subscription");
          extendSubscriptionBTN.setRipplerFill(Color.WHITE);
          extendSubscriptionBTN.setMaxWidth(Double.MAX_VALUE);
          extendSubscriptionBTN.setOnAction(click -> {
            Optional<Renderable> relevantPurchase = purchasesRepository.getBeansList().stream()
                .filter(purchase -> ((PurchaseDTO) purchase).getCityID() == renderedCity.getID())
                .findFirst();
            if (relevantPurchase.isPresent()) {
              purchasesRepository.setBean((PurchaseDTO) relevantPurchase.get());
              purchasesRepository.requestSubscriptionExtension();
            }
          });
          VBox.setVgrow(extendSubscriptionBTN, Priority.ALWAYS);
          // extendSubscriptionBTN.setOnAction(eventHandlingService::extendSubscriptionButtonHandler);

          JFXButton downloadBTN = new JFXButton("Download");
          downloadBTN.setRipplerFill(Color.WHITE);
          downloadBTN.setMaxWidth(Double.MAX_VALUE);
          HBox.setHgrow(downloadBTN, Priority.ALWAYS);

          JFXButton viewBTN = new JFXButton("View");
          viewBTN.setRipplerFill(Color.WHITE);
          viewBTN.setMaxWidth(Double.MAX_VALUE);
          viewBTN.setOnAction(clicked -> {
            citiesRepository.setBean(renderedCity);
            mapsRepository.requestRepoReset();
            toursRepository.requestRepoReset();
            new Task<Void>() {
              @Override
              protected Void call() throws Exception {
                mapsRepository.requestMapsOfCity();
                sitesRepository.requestSitesOfCity();
                sceneSwitcheroo.showDrawerView();
                return null;
              }
            }.run();
          });
          HBox.setHgrow(viewBTN, Priority.ALWAYS);

          HBox hbox = new HBox(downloadBTN, viewBTN);
          hbox.setFillHeight(true);
          VBox.setVgrow(hbox, Priority.ALWAYS);

          RenderingsStyler
              .applyStyleClass("experimental-button10", extendSubscriptionBTN, downloadBTN,
                  viewBTN);
          buttonsContainer.getChildren().addAll(extendSubscriptionBTN, hbox);
        }
      } else if (wasActive) {
        getChildren().remove(expiresOn);
        buttonsContainer.getChildren().clear();

        JFXButton subscribeBTN = new JFXButton("Subscribe");
        subscribeBTN.setRipplerFill(Color.WHITE);
        subscribeBTN.setMaxWidth(Double.MAX_VALUE);
        subscribeBTN.setOnAction(click -> {
          citiesRepository.setBean(renderedCity);
          purchasesRepository.requestNewSubscription();
        });
        VBox.setVgrow(subscribeBTN, Priority.ALWAYS);

        JFXButton oneTimePurchaseBTN = new JFXButton("One Time Purchase");
        oneTimePurchaseBTN.setRipplerFill(Color.WHITE);
        oneTimePurchaseBTN.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(oneTimePurchaseBTN, Priority.ALWAYS);

        RenderingsStyler.applyStyleClass("experimental-button10", subscribeBTN, oneTimePurchaseBTN);
        buttonsContainer.getChildren().addAll(subscribeBTN, oneTimePurchaseBTN);
      }

    }


    public void filterByCityName(String queriedCityName) {
      if (renderedCity.getName().toLowerCase().contains(queriedCityName.toLowerCase())) {
        if (!isVisible()) {
          setVisible(true);
        }
      } else {
        if (isVisible()) {
          setVisible(false);
        }
      }
      // setVisible(renderedCity.getName().toLowerCase().contains(queriedCityName.toLowerCase()));
    }

    public void filterBySiteName(String queriedSiteName) {
      if (renderedCity.getListOfSites().stream()
          .anyMatch(site -> site.toLowerCase().contains(queriedSiteName.toLowerCase()))) {
        if (!isVisible()) {
          setVisible(true);
        }
      } else {
        if (isVisible()) {
          setVisible(false);
        }
      }
      // setVisible(renderedCity.getListOfSites().stream()
      //     .anyMatch(site -> site.toLowerCase().contains(queriedSiteName.toLowerCase())));
    }


    public void filterByDescription(String queriedDescription) {
      if (renderedCity.getDescription().toLowerCase().contains(queriedDescription.toLowerCase())) {
        if (!isVisible()) {
          setVisible(true);
        }
      } else {
        if (isVisible()) {
          setVisible(false);
        }
      }
      // setVisible(renderedCity.getDescription().toLowerCase().contains(queriedDescription.toLowerCase()));
    }

    public void setStyleClass(String styleClass) {
      this.styleClass.set(styleClass);
    }

    public void setStyleClass(int currentIndex) {
      String styleClass = "";
      switch (currentIndex % 3) {
        case 0: {
          styleClass = "tile-leftmost-background";
          break;
        }
        case 1: {
          styleClass = "tile-middle-background";
          break;
        }
        case 2: {
          styleClass = "tile-rightmost-background";
          break;
        }
      }
      this.styleClass.set(styleClass);
    }
  }

}