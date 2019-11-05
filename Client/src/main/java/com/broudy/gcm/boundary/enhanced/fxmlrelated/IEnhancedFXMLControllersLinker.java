package com.broudy.gcm.boundary.enhanced.fxmlrelated;

import com.broudy.gcm.boundary.FXMLView;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides the functionality needed to have singleton enhanced controllers that can be
 * linked and unlinked when switching between different FXMLViews.
 * <p>
 * Created on the 14th of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public interface IEnhancedFXMLControllersLinker {

  ConcurrentHashMap<FXMLView, EnhancedFXMLController> enhancedFXMLControllers = new ConcurrentHashMap<>();

  /**
   * This method <b>must</b> always be called in order to switch a stage or a scene, its purpose is
   * to {@link EnhancedFXMLController#unlink()} all {@link EnhancedFXMLController}s that should be
   * unlinked.
   *
   * @param fxmlView is the FXMLView to be loaded.
   */
  Parent load(FXMLView fxmlView);

  /**
   * TODO provide a summary to EnhancedFXMLControllersLinker class!!!!!
   * <p>
   * Created on the 17th of September, 2019.
   *
   * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
   */
  public abstract class EnhancedFXMLControllersLinker implements IEnhancedFXMLControllersLinker {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private ConcurrentHashMap<FXMLView, ArrayList<FXMLView>> viewsToSkip;

    public EnhancedFXMLControllersLinker() {
      viewsToSkip = initializeViewToListOfViewsToSkipMap();
    }

    /**
     * This method <b>must</b> always be called in order to switch a stage or a scene, its purpose
     * is
     * to call {@link EnhancedFXMLController#unlink} on all {@link EnhancedFXMLController}s that
     * are currently linked before showing to the passed fxmlView.
     * <p>
     * This method loops over all {@link #enhancedFXMLControllers} and checks if:
     * <ul><li>The currently looped controller's {@link FXMLView} does <b> not</b> exist in the
     *         {@link Set} returned by the hook method {@link #initializeViewToListOfViewsToSkipMap} &&</li></ul>
     * <ul><li>The currently looped controller != null</li></ul>
     * Unlink currently looped controller.
     * <p>
     * By default, the Set will contain all {@link FXMLView}s that were showed at least once through this class.
     *
     * @param fxmlView is the FXMLView to be loaded.
     */
    @Override
    public final Parent load(final FXMLView fxmlView) {
      ArrayList<EnhancedFXMLController> controllersToUnlink = new ArrayList<>();
      ArrayList<EnhancedFXMLController> controllersToLink = new ArrayList<>();
      for (Entry<FXMLView, EnhancedFXMLController> entry : enhancedFXMLControllers.entrySet()) {

      }
      enhancedFXMLControllers.forEach((view, controller) -> {
        if (!(viewsToSkip.getOrDefault(fxmlView, new ArrayList<>()).contains(view)
            || view == fxmlView || controller == null)) {
          controller.unlink();
        }
      });
      final EnhancedFXMLLoader enhancedLoader = new EnhancedFXMLLoader(fxmlView);
      final Parent parent = loadViewNodeHierarchy(fxmlView, enhancedLoader);
      final EnhancedFXMLController enhancedFXMLController = enhancedLoader.getController();
      if (enhancedFXMLController != null) {
        enhancedFXMLControllers.putIfAbsent(fxmlView, enhancedFXMLController);
      }
      return parent;
    }

    /**
     * This method <b>must not</b> be called directly.
     * This method <b>must</b> be implemented in order to provide a way of actually showing the
     * {@link FXMLView} passed to {@link #load(FXMLView)}.
     *
     * @param fxmlView is the FXMLView that will be passed by the {@link #load(FXMLView)}
     *     method.
     */
    private Parent loadViewNodeHierarchy(final FXMLView fxmlView,
        final EnhancedFXMLLoader enhancedFXMLLoader) {
      Parent rootNode = null;
      try {
        rootNode = enhancedFXMLLoader.load();
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
     * the {@link FXMLView}s contained in the returned Map <b>will not</b> be unlinked.
     *
     * @return a Map of FXMLViews to skip when unlinking.
     */
    public abstract ConcurrentHashMap<FXMLView, ArrayList<FXMLView>> initializeViewToListOfViewsToSkipMap();

  }


}
