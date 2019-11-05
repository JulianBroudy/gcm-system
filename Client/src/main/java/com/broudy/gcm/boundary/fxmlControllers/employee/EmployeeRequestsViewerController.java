package com.broudy.gcm.boundary.fxmlControllers.employee;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.renderings.CityRenderingsService;
import com.broudy.gcm.control.services.renderings.CityRenderingsService.CityApprovalRequestRendering;
import com.broudy.gcm.entity.interfaces.IStatable;
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
 * This class controls managing requests by > CONTENT_EDITOR.
 * <p>
 * Created on the 10th of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeRequestsViewerController<RIS extends Renderable & IStatable> extends
    EnhancedFXMLController {

  private final static Logger logger = LogManager.getLogger(EmployeeRequestsViewerController.class);

  /* Guice injected fields */
  private final UsersRepository usersRepository;
  private final CitiesRepository citiesRepository;
  private final CityRenderingsService cityRenderingsService;
  private final SceneSwitcheroo sceneSwitcheroo;

  /* Other variables */
  private ListChangeListener<Renderable> citiesListListener;
  private ConcurrentHashMap<RIS, CityApprovalRequestRendering> citiesRenderings;

  @FXML
  private TilePane citiesTP;

  @Inject
  public EmployeeRequestsViewerController(UsersRepository usersRepository,
      CitiesRepository citiesRepository, CityRenderingsService cityRenderingsService,
      SceneSwitcheroo sceneSwitcheroo) {
    this.usersRepository = usersRepository;
    this.citiesRepository = citiesRepository;
    this.cityRenderingsService = cityRenderingsService;
    this.sceneSwitcheroo = sceneSwitcheroo;
  }

  /**
   * All the one-time initializations should be implemented in this method.
   */
  @Override
  public void initializeEnhancedController() {
    citiesListListener = listChange -> {
      while (listChange.next()) {
        renderRequests(listChange);
      }
    };
  }


  /**
   * This method should implement all the steps that turn an initialized & "unlinked" controller
   * into a "linked" one.
   */
  @Override
  protected void activateController() {
    sceneSwitcheroo.loadDrawerView(FXMLView.EMPLOYEE_CITY_EXPLORER);
    citiesRepository.employeesWorkspaceProperty().addListener(citiesListListener);

    citiesRepository.requestCityApprovalRequests();
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {
    citiesRepository.employeesWorkspaceProperty().removeListener(citiesListListener);
  }


  private void renderRequests(Change<? extends Renderable> listChange) {
    // TODO remove listChange parameter or implement a process more tuned to ListChange for city renderings
    citiesRenderings = cityRenderingsService.generateRenderingsOfCityApprovalRequests();
    citiesTP.getChildren().clear();
    if (citiesRenderings.isEmpty()) {
      Label noPendingRequestsLBL = new Label("No pending requests at the moment.");
      noPendingRequestsLBL.getStyleClass().add("label-30");
      noPendingRequestsLBL.setOpacity(0.4);
      citiesTP.getChildren().add(noPendingRequestsLBL);
    } else {
      citiesTP.getChildren().addAll(citiesRenderings.values());
    }
  }

}
