package com.broudy.gcm.boundary.enhanced.fxmlrelated;

import com.broudy.gcm.boundary.FXMLView;
import com.broudy.utils.TextColors;
import com.google.inject.Injector;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This enhanced loader will use Guice's Injector to instantiate all inter-connected controllers if
 * there are any, then it will link them one by one.
 * <p>
 * Created on the 4th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EnhancedFXMLLoader extends FXMLLoader {

  private static final Logger logger = LogManager.getLogger(EnhancedFXMLLoader.class);

  private static Injector injector;
  final Set<Object> controllers;


  public EnhancedFXMLLoader(final FXMLView fxmlView) {
    super();

    if (injector == null) {
      throw new NullPointerException("Injector cannot be NULL!");
    }
    setLocation(getClass().getResource(fxmlView.getPath()));
    setBuilderFactory(new EnhancedFXMLBuilder(injector));

    controllers = new HashSet<>();

    // IoC container
    setControllerFactory(clazz -> {
      final Object instance = injector.getInstance(clazz);
      logger.trace("Got " + TextColors.BLUE.colorThis("{}") + " instance.",
          instance.getClass().getSimpleName() + " " + instance.hashCode());
      if (instance != null) {
        controllers.add(instance);
      }
      return instance;
    });
  }

  /**
   * Sets the injector to be used.
   */
  public static void setInjector(Injector injector) {
    logger.debug("Setting injector ");
    EnhancedFXMLLoader.injector = injector;
  }


  @Override
  public <T> T load() throws IOException {
    final T loaded = super.load();

    logger.debug("#load currentControllers: " + controllers.stream()
        .map(controller -> controller.getClass().getSimpleName() + " " + controller.hashCode())
        .collect(Collectors.joining()));

    for (Object controller : controllers) {
      if (controller instanceof EnhancedFXMLController) {
        logger.debug("Linking " + TextColors.BLUE.colorThis("{}") + "...",
            () -> controller.getClass().getSimpleName() + controller.hashCode());
        ((EnhancedFXMLController) controller).link();
      }
    }
    return loaded;
  }

}
