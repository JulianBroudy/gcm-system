package com.broudy.gcm.boundary.enhanced.controls;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.fxmlControllers.MainContainerController;
import com.broudy.gcm.control.StageManager;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXDrawer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class introduces a new improved way of switching between scenes with ease as well as
 * integrating a JFXDrawer into the magic.
 * <p>
 * Created on the 9th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see JFXDrawer
 */
public class SceneSwitcheroo extends AnchorPane {

  private static final Logger logger = LogManager.getLogger(MainContainerController.class);

  private final StageManager stageManager;
  private JFXDrawer drawer;
  private StringProperty windowTitle;

  private FXMLView currentlyVisibleView;
  private FXMLView currentlyVisibleViewInDrawer;

  @Inject
  public SceneSwitcheroo(StageManager stageManager) {
    this.stageManager = stageManager;
    setPrefHeight(700);
    setPrefWidth(1150);
    getStylesheets()
        .add(this.getClass().getResource("/css/DefaultStyleSheet.css").toExternalForm());
    getStyleClass().add("background-switcheroo-main-pane-white");
    resetView();
    windowTitle = new SimpleStringProperty();
  }

  /**
   * This method loads the passed {@see FXMLView} into the {@see JFXDrawer}'s content pane.
   *
   * @param fxmlView the fxml file to load.
   */
  public void loadView(FXMLView fxmlView) {
    if (currentlyVisibleView != fxmlView) {
      currentlyVisibleView = fxmlView;
      stageManager.switchSubScene(fxmlView, this);
      Platform.runLater(() -> windowTitle.set(fxmlView.getTitle()));
      if (drawer.isOpened()) {
        hideDrawerView();
      }
    }
  }

  /**
   * This method loads the passed {@see FXMLView} into the drawer.
   *
   * @param fxmlView the fxml file to load.
   */
  public void loadDrawerView(FXMLView fxmlView) {
    if (currentlyVisibleViewInDrawer != fxmlView) {
      currentlyVisibleViewInDrawer = fxmlView;
      drawer.setSidePane(stageManager.getRootNodeOf(fxmlView));
    }
  }

  /**
   * This method shows the integrated drawer.
   */
  public void showDrawerView() {
    drawer.open();
  }

  /**
   * This method hides the integrated drawer.
   */
  public void hideDrawerView() {
    drawer.close();
  }


  /**
   * Integrates the passed {@see JFXDrawer} for a nicer switching effect.
   *
   * @param drawer the drawer to integrate.
   */
  public void integrateDrawer(JFXDrawer drawer) {
    this.drawer = drawer;
    AnchorPane root = stageManager.getRootNodeOf(FXMLView.SCENE_SWITCHEROO);
    getChildren().addAll(root.getChildren());
    this.windowTitle.set(FXMLView.SCENE_SWITCHEROO.getTitle());
    this.drawer.setContent(this);
    this.drawer.setAlignment(Pos.CENTER);
  }

  /**
   * Integrates the passed {@see JFXDrawer} for a nicer switching effect.
   *
   * @param drawer the drawer to integrate.
   * @param windowTitleLBL the label to be changed to view's title.
   */
  public void integrateDrawer(JFXDrawer drawer, Label windowTitleLBL) {
    integrateDrawer(drawer);
    windowTitleLBL.textProperty().bind(windowTitle);
  }

  /**
   * This method provides a way of setting initial drawer view.
   * The difference between this method and {@link #loadDrawerView} is that this method will not
   * shake the UI if it is already set.
   *
   * @param fxmlView the drawer view to be set.
   */
  public void setInitialView(FXMLView fxmlView) {
    resetView();
    loadView(fxmlView);
  }

  /**
   * This method resets the currently visible view.
   */
  public void resetView() {
    this.currentlyVisibleView = null;
  }


  /**
   * Gets the currentlyVisibleView.
   *
   * @return currentlyVisibleView's value.
   */
  public FXMLView getCurrentlyVisibleView() {
    return currentlyVisibleView;
  }
}
