package com.broudy.gcm.boundary.enhanced.fxmlrelated;

import com.broudy.gcm.boundary.enhanced.controls.FillingForm;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.google.inject.Injector;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

/**
 * Inversion of Control container.
 * Enhanced FXMLBuilder which integrates Guice's Dependency Injection framework.
 * An instance of a custom class will be provided by Guice's Injector.
 * <p>
 * Created on the 4th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EnhancedFXMLBuilder implements BuilderFactory {

  private final Injector injector;
  private final BuilderFactory DEFAULT_BUILDER_FACTORY;


  public EnhancedFXMLBuilder(final Injector inj) {
    super();
    injector = inj;
    DEFAULT_BUILDER_FACTORY = new JavaFXBuilderFactory();
  }

  /**
   * Checks if passed type is one that should be provided by Guice's Injector.
   *
   * @return default builder or instance provided by Guice's Injector.
   */
  @Override
  public Builder<?> getBuilder(final Class<?> type) {
    if (type == FillingForm.class || type == SceneSwitcheroo.class) {
      return () -> injector.getInstance(type);
    }
    return DEFAULT_BUILDER_FACTORY.getBuilder(type);
  }
}
