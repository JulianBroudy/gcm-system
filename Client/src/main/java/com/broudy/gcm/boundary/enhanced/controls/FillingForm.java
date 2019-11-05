package com.broudy.gcm.boundary.enhanced.controls;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.control.StageManager;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXDrawer;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// TODO: generify, think of a better implementation, one without drawer, etc...

/**
 * Acts as a convenience class for various filling forms.
 * <p>
 * Created on the 6th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class FillingForm extends AnchorPane {

  private static final Logger logger = LogManager.getLogger(FillingForm.class);

  private final StageManager stageManager;


  private JFXDrawer drawer;
  private FXMLView currenltyActiveForm;

  @Inject
  public FillingForm(StageManager stageManager) {
    this.stageManager = stageManager;
    setPrefHeight(600);
    setPrefWidth(440);
    currenltyActiveForm = null;
    getStylesheets()
        .add(this.getClass().getResource("/css/DefaultStyleSheet.css").toExternalForm());
    getStyleClass().add("background-transparent");
  }

  /**
   * This method loads the passed {@see FXMLView}, uses drawer effect if it has been integrated.
   *
   * @param fxmlView the fxml file to load.
   */
  public void loadForm(FXMLView fxmlView) {
    if (drawer != null) {
      switchWithDrawerEffect(fxmlView);
    } else {
      if (currenltyActiveForm != fxmlView) {
        currenltyActiveForm = fxmlView;
        stageManager.switchSubScene(fxmlView, this);
      } else {
        new Shake(this).play();
      }
    }
  }

  /**
   * Integrates the passed {@see JFXDrawer} for a nicer switching effect.
   *
   * @param drawer the drawer to integrate.
   */
  public void integrateDrawer(JFXDrawer drawer) {
    this.drawer = drawer;
    Platform.runLater(() -> {
      this.drawer.setSidePane(this);
      Pane pane = new Pane();
      pane.getStyleClass().add("background-transparent");
      this.drawer.setContent();
    });
  }

  /**
   * Loads the root node of passed FXMLView into this node.
   *
   * @param fxmlView scene to be loaded.
   */
  private void switchWithDrawerEffect(FXMLView fxmlView) {
    if (currenltyActiveForm != fxmlView) {
      currenltyActiveForm = fxmlView;
      drawer.setOnDrawerClosed(closed -> {
        stageManager.switchSubScene(fxmlView, this);
        drawer.open();
      });
      drawer.close();
    } else {
      new Shake(drawer).play();
    }
  }

  /**
   * This method provides a way of setting initial formView.
   * The difference between this method and {@link #loadForm} is that this method will not shake
   * the UI if it is already set.
   *
   * @param fxmlView the form to be set.
   */
  public void setInitialForm(FXMLView fxmlView) {
    currenltyActiveForm = null;
    loadForm(fxmlView);
  }
}


