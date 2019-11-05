package com.broudy.gcm.boundary.fxmlControllers.customer;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.PurchasesRepository;
import com.broudy.gcm.control.services.renderings.CityRenderingsService;
import com.broudy.gcm.control.services.renderings.CityRenderingsService.CityCatalogRendering;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * This class controls the customers' catalog browsing experience.
 * <p>
 * Created on the 24th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CustomerCatalogBrowserController<RIS extends Renderable & IStatable> extends
    EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(CustomerCatalogBrowserController.class);

  private final CitiesRepository citiesRepository;
  private final PurchasesRepository purchasesRepository;
  private final SceneSwitcheroo sceneSwitcheroo;

  private final CityRenderingsService cityRenderingsService;
  private ConcurrentHashMap<RIS, CityCatalogRendering> renderedCities;

  @FXML
  private TilePane citiesTP;
  @FXML
  private JFXComboBox<String> searchByCB;
  @FXML
  private CustomTextField searchCTF;

  @Inject
  public CustomerCatalogBrowserController(CitiesRepository citiesRepository,
      PurchasesRepository purchasesRepository, SceneSwitcheroo sceneSwitcheroo,
      CityRenderingsService cityRenderingsService) {
    this.citiesRepository = citiesRepository;
    this.purchasesRepository = purchasesRepository;
    this.sceneSwitcheroo = sceneSwitcheroo;
    this.cityRenderingsService = cityRenderingsService;
  }


  @Override
  public void initializeEnhancedController() {
  }

  @Override
  protected void activateController() {
    sceneSwitcheroo.loadDrawerView(FXMLView.CUSTOMER_CITY_EXPLORER);

    purchasesRepository.requestCustomerActivePurchases();

    searchByCB.getItems().addAll("City", "Site", "Description");
    searchCTF.disableProperty().bind(searchByCB.valueProperty().isNull());
    activateListeners();

    citiesRepository.requestAllCities();
  }

  @Override
  protected void deactivateController() {

  }

  @Override
  protected void activateListeners() {
    citiesRepository.beansListProperty()
        .addListener((ListChangeListener<? super Renderable>) listChange -> {
          while (listChange.next()) {
            renderCities(listChange);
            logger.debug(TextColors.CYAN.colorThis("New mapSitesList: {}"), listChange.getList());
          }
        });

    searchByCB.valueProperty()
        .addListener((observable, oldValue, newValue) -> filterCities(searchCTF.getText()));

    // Filer rendered cities according to selection in searchBy ComboBox.
    searchCTF.textProperty()
        .addListener((observable, oldValue, newValue) -> filterCities(newValue));
  }

  /**
   * This method renders all available cities.
   */
  private void renderCities(Change<? extends Renderable> listChange) {
    renderedCities = cityRenderingsService.generateRenderingsOfCityCatalogPreviews();
    citiesTP.getChildren().clear();
    if (renderedCities.isEmpty()) {
      Label noAvailableCitiesLBL = new Label("No available cities");
      noAvailableCitiesLBL.getStyleClass().add("label-30");
      noAvailableCitiesLBL.setOpacity(0.4);
      citiesTP.getChildren().add(noAvailableCitiesLBL);
    } else {
      citiesTP.getChildren().addAll(renderedCities.values());
      List<CityCatalogRendering> visibleRenderings = renderedCities.values().stream()
          .filter(Node::isVisible).collect(Collectors.toList());
      for (int i = 0; i < visibleRenderings.size(); i++) {
        visibleRenderings.get(i).setStyleClass(i);
      }
    }
  }

  /**
   * This method filters (hides & shows) the cities based on text typed into the search box as well
   * as the search by
   * attribute.
   */
  private void filterCities(String queriedText) {
    if (!searchCTF.isDisable()) {
      switch (searchByCB.getValue()) {
        case "City": {
          renderedCities.values()
              .forEach(renderedCity -> renderedCity.filterByCityName(queriedText));
          break;
        }
        case "Site": {
          renderedCities.values()
              .forEach(renderedCity -> renderedCity.filterBySiteName(queriedText));
          break;
        }
        case "Description": {
          renderedCities.values()
              .forEach(renderedCity -> renderedCity.filterByDescription(queriedText));
          break;
        }
      }
    } else {
      //render back all cities
      renderedCities.values().forEach(renderedCity -> {
        if (!renderedCity.isVisible()) {
          renderedCity.setVisible(true);
        }
      });
    }
    List<CityCatalogRendering> visibleRenderings = renderedCities.values().stream()
        .filter(Node::isVisible).collect(Collectors.toList());
    for (int i = 0; i < visibleRenderings.size(); i++) {
      visibleRenderings.get(i).setStyleClass(i);
    }
  }

}
