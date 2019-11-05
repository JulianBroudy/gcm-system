package com.broudy.gcm.boundary.enhanced.fxmlrelated;

import com.broudy.utils.TextColors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This controller provides an option to link and unlink it from program's current flow.
 * It also provides an option to initialize it one-time when it is loaded for the first time in case
 * it is a singleton.
 * <p>
 * Created on the 1st of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public abstract class EnhancedFXMLController {

  protected boolean linked = false;
  private Logger logger = LogManager.getLogger(this.getClass());
  private boolean initialized = false;

  /**
   * All the one-time initializations should be implemented in this method.
   */
  public abstract void initializeEnhancedController();

  /**
   * This method is called by the {@link EnhancedFXMLLoader} after all previous-interconnected
   * controllers have been constructed, injected & activated.
   * This method activates the controller IFF the controller is NOT currently activate.
   */
  final void link() {
    if (!this.linked) {
      if (!this.initialized) {
        initializeEnhancedController();
        this.initialized = true;
        logger.trace(TextColors.CYAN.colorThis(this.getClass().getSimpleName())
            + " was initialized for the first & last time.");
      }
      activateController();
      this.linked = true;
      logger.trace(
          TextColors.CYAN.colorThis(this.getClass().getSimpleName()) + " was " + TextColors.GREEN
              .colorThis("LINKED"));
    }
  }

  /**
   * This method should be used in order to "suspend" an {@link EnhancedFXMLController} if needed.
   * <p>
   * For example:
   * <br>In some occasions, switching between controllers might require unbinding some of
   * the first controller's controls or removing some of its Listeners.
   */
  final void unlink() {
    if (this.linked) {
      deactivateController();
      this.linked = false;
      logger.trace(
          TextColors.CYAN.colorThis(this.getClass().getSimpleName()) + " was " + TextColors.RED
              .colorThis("UNLINKED"));

    }
  }

  /**
   * This method should implement all the steps that turn an initialized & "unlinked" controller
   * into a "linked" one.
   * <p>
   * By default this method calls {@link #activateValidators()}, {@link #activateListeners()} ()},
   * {@link #activateListeners()} ()} and {@link #activateEventHandlers()} ()} in this order.
   */
  protected abstract void activateController();

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  protected abstract void deactivateController();

  /**
   * Activates all needed bindings.
   */
  protected void activateBindings() {
  }

  /**
   * Activates all needed listeners.
   */
  protected void activateListeners() {
  }

  /**
   * Activates all needed validators.
   */
  protected void activateValidators() {
  }

  /**
   * Activates all needed event handlers.
   */
  protected void activateEventHandlers() {
  }

}
