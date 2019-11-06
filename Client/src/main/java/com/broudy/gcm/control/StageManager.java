package com.broudy.gcm.control;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLLoader;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.IEnhancedFXMLControllersLinker.EnhancedFXMLControllersLinker;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

;

/**
 * TODO provide a summary to StageManagerN class!!!!!
 * <p>
 * Created on the 12th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class StageManager extends EnhancedFXMLControllersLinker {

  private static final Logger logger = LogManager.getLogger(StageManager.class);

  @Inject
  private Stage primaryStage;


  public StageManager() {
    logger.debug("No-Arg constructor: " + this);
    logger.debug("No-Arg constructor, current stage: " + primaryStage);
  }

  /**
   * The only method that should be used to switch the current scene, i.e., load a different
   * {@see FXMLView} onto the currently shown {@see Stage}.
   *
   * @param fxmlView is the {@see FXMLView} to be loaded instead of currently active scene.
   */
  public void switchScene(final FXMLView fxmlView) {
    logger.traceEntry("switching scene to {}", () -> fxmlView);
    Parent fxmlViewRootNode = load(fxmlView);

    // Delete 2 next lines in order to make stage undraggable
    StageDragger stageDragger = new StageDragger();
    stageDragger.makeDraggable(fxmlViewRootNode);
    show(fxmlViewRootNode);
  }

  /**
   * The only method that should be used to switch one of the current sub-scenes, i.e., load a
   * different {@see FXMLView} onto one of the currently shown {@see Scene}s.
   *
   * @param fxmlView is the {@see FXMLView} to be loaded.
   * @param subSceneRootNode is the {@see Parent} node to load onto.
   */
  public <ROOT extends Pane> void switchSubScene(final FXMLView fxmlView,
      final ROOT subSceneRootNode) {
    Platform.runLater(() -> {
      Parent fxmlViewRootNode = load(fxmlView);
      try {
        subSceneRootNode.getChildren().clear();
        subSceneRootNode.getChildren().setAll(fxmlViewRootNode);
        subSceneRootNode.getStyleClass().add("background-transparent");
      } catch (ClassCastException cce) {
        logger.error(cce);
      }
    });
  }

  public <ROOT extends Parent> ROOT getRootNodeOf(final FXMLView fxmlView) {
    return (ROOT) load(fxmlView);
  }

  private void show(final Parent rootNode) {
    Scene scene = prepareScene(rootNode);
    Platform.runLater(() -> {
      primaryStage.setScene(scene);
      primaryStage.sizeToScene();
      primaryStage.centerOnScreen();

      logger.debug("#show StageManager: " + this);
      logger.debug("#show PrimaryStage: " + primaryStage);
      try {
        primaryStage.show();
      } catch (Exception exception) {
        logger.error(exception);
      }
    });
  }

  private Scene prepareScene(Parent rootNode) {
    Scene scene = new Scene(rootNode);
    scene.setRoot(rootNode);
    scene.setFill(Color.TRANSPARENT);
    return scene;
  }

  /**
   * This method loads the passed {@see FXMLView} after all its dependencies if any are resolved by
   * the {@see boundary.EnhancedFXMLLoader} and returns the top-most node of the FXML's hierarchy.
   *
   * @param fxmlView the {@see FXMLView} to be loaded.
   *
   * @return the root node {@see Parent} of the loaded {@see FXMLView}.
   */
  private Parent loadViewNodeHierarchy(FXMLView fxmlView) {
    Parent rootNode = null;
    EnhancedFXMLLoader enhancedLoader = new EnhancedFXMLLoader(fxmlView);
    try {
      rootNode = enhancedLoader.load();
      Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
    } catch (Exception exception) {
      exception.printStackTrace();
      logger.error(exception.getMessage());
    }
    return logger.traceExit(rootNode);
  }

  /**
   * This is a hook method that is called by the {@link #load(FXMLView)} method before
   * iterating over the {@link EnhancedFXMLController}s. All the controllers that are mapped to
   * the {@link FXMLView}s contained in the returned Set <b>will not</b> be unlinked.
   *
   * @return a Set of FXMLViews to skip when unlinking.
   */
  @Override
  public ConcurrentHashMap<FXMLView, ArrayList<FXMLView>> initializeViewToListOfViewsToSkipMap() {
    final ConcurrentHashMap<FXMLView, ArrayList<FXMLView>> viewsToSkip = new ConcurrentHashMap<>();

    final ArrayList<FXMLView> whenSwitchingToWelcomeScreenORSignIn = new ArrayList<>();
    whenSwitchingToWelcomeScreenORSignIn.add(FXMLView.WELCOME_SCREEN);
    viewsToSkip.put(FXMLView.WELCOME_SCREEN, whenSwitchingToWelcomeScreenORSignIn);
    whenSwitchingToWelcomeScreenORSignIn.add(FXMLView.SIGN_IN_FORM);
    viewsToSkip.put(FXMLView.SIGN_IN_FORM, whenSwitchingToWelcomeScreenORSignIn);

    // final ArrayList<FXMLView> whenSwitchingToSignIn = new ArrayList<>();
    // whenSwitchingToSignIn.add(FXMLView.WELCOME_SCREEN);
    // whenSwitchingToSignIn.add(FXMLView.SIGN_UP_FORM_1);

    final ArrayList<FXMLView> whenSwitchingToSignUp1 = new ArrayList<>();
    whenSwitchingToSignUp1.add(FXMLView.WELCOME_SCREEN);
    whenSwitchingToSignUp1.add(FXMLView.SIGN_UP_FORM_2);
    whenSwitchingToSignUp1.add(FXMLView.SIGN_UP_FORM_3);
    viewsToSkip.put(FXMLView.SIGN_UP_FORM_1, whenSwitchingToSignUp1);

    final ArrayList<FXMLView> whenSwitchingToSignUp2 = new ArrayList<>();
    whenSwitchingToSignUp2.add(FXMLView.WELCOME_SCREEN);
    whenSwitchingToSignUp2.add(FXMLView.SIGN_IN_FORM);
    whenSwitchingToSignUp2.add(FXMLView.SIGN_UP_FORM_1);
    whenSwitchingToSignUp2.add(FXMLView.SIGN_UP_FORM_3);
    viewsToSkip.put(FXMLView.SIGN_UP_FORM_2, whenSwitchingToSignUp2);

    final ArrayList<FXMLView> whenSwitchingToSignUp3 = new ArrayList<>();
    whenSwitchingToSignUp3.add(FXMLView.WELCOME_SCREEN);
    whenSwitchingToSignUp3.add(FXMLView.SIGN_IN_FORM);
    whenSwitchingToSignUp3.add(FXMLView.SIGN_UP_FORM_1);
    whenSwitchingToSignUp3.add(FXMLView.SIGN_UP_FORM_2);
    viewsToSkip.put(FXMLView.SIGN_UP_FORM_3, whenSwitchingToSignUp3);


    final ArrayList<FXMLView> whenSwitchingToJustBrowseSidePane = new ArrayList<>();
    whenSwitchingToJustBrowseSidePane.add(FXMLView.CUSTOMER_CATALOG_BROWSING);
    whenSwitchingToJustBrowseSidePane.add(FXMLView.CUSTOMER_CITY_EXPLORER);
    whenSwitchingToJustBrowseSidePane.add(FXMLView.WELCOME_SCREEN);
    whenSwitchingToJustBrowseSidePane.add(FXMLView.MAIN_CONTAINER);
    // whenSwitchingToJustBrowseCatalog.add(FXMLView.SIGN_UP_FORM_1);
    // whenSwitchingToJustBrowseCatalog.add(FXMLView.SIGN_UP_FORM_2);
    viewsToSkip.put(FXMLView.JUST_BROWSE_SIDE_PANE, whenSwitchingToJustBrowseSidePane);

    final ArrayList<FXMLView> whenSwitchingToCityExplorer = new ArrayList<>();
    whenSwitchingToCityExplorer.add(FXMLView.CUSTOMER_CATALOG_BROWSING);
    whenSwitchingToCityExplorer.add(FXMLView.JUST_BROWSE_SIDE_PANE);
    whenSwitchingToCityExplorer.add(FXMLView.WELCOME_SCREEN);
    whenSwitchingToCityExplorer.add(FXMLView.MAIN_CONTAINER);
    viewsToSkip.put(FXMLView.CUSTOMER_CITY_EXPLORER, whenSwitchingToCityExplorer);

    final ArrayList<FXMLView> whenSwitchingToCustomerCatalogBrowsing = new ArrayList<>();
    whenSwitchingToCustomerCatalogBrowsing.add(FXMLView.CUSTOMER_CITY_EXPLORER);
    whenSwitchingToCustomerCatalogBrowsing.add(FXMLView.JUST_BROWSE_SIDE_PANE);
    whenSwitchingToCustomerCatalogBrowsing.add(FXMLView.WELCOME_SCREEN);
    whenSwitchingToCustomerCatalogBrowsing.add(FXMLView.MAIN_CONTAINER);
    viewsToSkip.put(FXMLView.CUSTOMER_CATALOG_BROWSING, whenSwitchingToCustomerCatalogBrowsing);

    // whenSwitchingToJustBrowseCatalog.add(FXMLView.SIGN_UP_FORM_1);
    // whenSwitchingToJustBrowseCatalog.add(FXMLView.SIGN_UP_FORM_2);

    final ArrayList<FXMLView> whenSwitchingToMainContainer = new ArrayList<>();
    // whenSwitchingToMainContainer.add(FXMLView.NOTIFOCATIONS?);
    whenSwitchingToMainContainer.add(FXMLView.JUST_BROWSE_SIDE_PANE);
    viewsToSkip.put(FXMLView.MAIN_CONTAINER, whenSwitchingToMainContainer);

    final ArrayList<FXMLView> whenSwitchingToEmployeeViews = new ArrayList<>();
    whenSwitchingToEmployeeViews.add(FXMLView.MAIN_CONTAINER);
    viewsToSkip.put(FXMLView.EMPLOYEE_SIDE_PANE, whenSwitchingToEmployeeViews);

    whenSwitchingToEmployeeViews.add(FXMLView.EMPLOYEE_SIDE_PANE);
    viewsToSkip.put(FXMLView.EMPLOYEE_MY_ACCOUNT, whenSwitchingToEmployeeViews);
    viewsToSkip.put(FXMLView.EMPLOYEE_MY_WORKSPACE, whenSwitchingToEmployeeViews);
    viewsToSkip.put(FXMLView.EMPLOYEE_CITIES_EDITOR, whenSwitchingToEmployeeViews);
    viewsToSkip.put(FXMLView.EMPLOYEE_MAPS_EDITOR, whenSwitchingToEmployeeViews);
    viewsToSkip.put(FXMLView.EMPLOYEE_REQUESTS_VIEWER, whenSwitchingToEmployeeViews);
    viewsToSkip.put(FXMLView.EMPLOYEE_CITY_EXPLORER, whenSwitchingToEmployeeViews);
    //
    // final ArrayList<FXMLView> whenSwitchingToJustBrowse = new ArrayList<>();
    // whenSwitchingToJustBrowse.add(FXMLView.JUST_BROWSE_SIDE_PANE);
    // viewsToSkip.put(FXMLView.EMPLOYEE_SIDE_PANE, whenSwitchingToEmployeeViews);


    /*
    final ArrayList<FXMLView> whenSwitchingToCustomerViews = new ArrayList<>();
    whenSwitchingToCustomerViews.add(FXMLView.MAIN_CONTAINER);
    viewsToSkip.put(FXMLView.CUSTOMER_SIDE_PANE, whenSwitchingToCustomerViews);

    whenSwitchingToCustomerViews.add(FXMLView.CUSTOMER_SIDE_PANE);
    viewsToSkip.put(FXMLView.CUSTOMER_MY_ACCOUNT, whenSwitchingToCustomerViews);
    viewsToSkip.put(FXMLView.CUSTOMER_CATALOG_BROWSING, whenSwitchingToCustomerViews);
    viewsToSkip.put(FXMLView.CUSTOMER_ACTIVE_SUBSCRIPTIONS, whenSwitchingToCustomerViews);
    viewsToSkip.put(FXMLView.CUSTOMER_PURCHASE_HISTORY, whenSwitchingToCustomerViews);*/

    return viewsToSkip;
  }

  // Convenience methods..................................................................

  // public EnhancedFXMLController getControllerFor(FXMLView fxmlView) {
  //   return enhancedFXMLControllers.get(fxmlView);
  // }

  public <EC extends EnhancedFXMLController> EC getControllerFor(FXMLView fxmlView) {
    return (EC) enhancedFXMLControllers.get(fxmlView);
  }


  public void setMaximized(boolean maximize) {
    primaryStage.setMaximized(maximize);
  }

  public void setFullScreen(boolean fullScreen) {
    primaryStage.setFullScreen(fullScreen);
  }

  public void setIconified(boolean minimize) {
    primaryStage.setIconified(minimize);
  }

  @Override
  public String toString() {
    return Integer.toHexString(hashCode());
  }

  public void cleanUpAndExit() {
    try {
      GCMClient.getInstance().closeConnection();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Platform.runLater(() -> {
      primaryStage.close();
      Platform.exit();
      System.exit(0);
    });
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  /**
   *
   */
  private class StageDragger {

    private double xPos = 0;
    private double yPos = 0;

    private void makeDraggable(Parent fxmlViewRootNode) {

      Objects.requireNonNull(fxmlViewRootNode).setOnMousePressed(e -> {
        xPos = e.getSceneX();
        yPos = e.getSceneY();
      });

      fxmlViewRootNode.setOnMouseDragged(e -> {
        primaryStage.setOpacity(0.7f);
        primaryStage.setX(e.getScreenX() - xPos);
        primaryStage.setY(e.getScreenY() - yPos);
      });

      fxmlViewRootNode.setOnMouseReleased(e -> primaryStage.setOpacity(1f));
    }

  }
}
