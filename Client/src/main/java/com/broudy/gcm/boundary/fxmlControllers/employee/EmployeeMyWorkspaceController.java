package com.broudy.gcm.boundary.fxmlControllers.employee;

import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.renderings.CityRenderingsService;
import com.broudy.gcm.control.services.renderings.CityRenderingsService.CityWorkspaceRendering;
import com.broudy.gcm.entity.interfaces.Renderable;
import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class controls the employee's workspace.
 * <p>
 * Created on the 9th of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeMyWorkspaceController extends EnhancedFXMLController {

  private final static Logger logger = LogManager.getLogger(EmployeeMyWorkspaceController.class);

  /* Guice injected fields */
  private final UsersRepository usersRepository;
  private final CitiesRepository citiesRepository;
  private final CityRenderingsService cityRenderingsService;

  /* Other variables */
  private ListChangeListener<Renderable> workspaceListListener;
  private ConcurrentHashMap<Renderable, CityWorkspaceRendering> citiesRenderings;

  @FXML
  private TilePane citiesTP;

  @Inject
  public EmployeeMyWorkspaceController(UsersRepository usersRepository,
      CitiesRepository citiesRepository, CityRenderingsService cityRenderingsService) {
    this.usersRepository = usersRepository;
    this.citiesRepository = citiesRepository;
    this.cityRenderingsService = cityRenderingsService;
  }

  /**
   * All the one-time initializations should be implemented in this method.
   */
  @Override
  public void initializeEnhancedController() {
    workspaceListListener = listChange -> {
      while (listChange.next()) {
        renderCities(listChange);
      }
    };
    // citiesRepository.beansListProperty().addListener(workspaceListListener);
  }


  /**
   * This method should implement all the steps that turn an initialized & "unlinked" controller
   * into a "linked" one.
   */
  @Override
  protected void activateController() {
    citiesRepository.employeesWorkspaceProperty().addListener(workspaceListListener);

    citiesRepository.requestEmployeeWorkspace();
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {
    citiesRepository.employeesWorkspaceProperty().removeListener(workspaceListListener);
  }


  private void renderCities(Change<? extends Renderable> listChange) {
    // TODO remove listChange parameter or implement a process more tuned to ListChange for city renderings
    citiesRenderings = cityRenderingsService.generateRenderingsOfCityInWorkspace();
    citiesTP.getChildren().clear();
    if (citiesRenderings.isEmpty()) {
      Label emptyWorkspaceLBL = new Label("Your workspace is empty");
      emptyWorkspaceLBL.getStyleClass().add("label-30");
      emptyWorkspaceLBL.setOpacity(0.4);
      citiesTP.getChildren().add(emptyWorkspaceLBL);
    } else {
      citiesTP.getChildren().addAll(citiesRenderings.values());
    }
  }

}
