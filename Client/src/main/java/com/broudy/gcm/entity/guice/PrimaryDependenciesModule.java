package com.broudy.gcm.entity.guice;

import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.CreditCardsRepository;
import com.broudy.gcm.control.repos.MapsRepository;
import com.broudy.gcm.control.repos.PurchasesRepository;
import com.broudy.gcm.control.repos.SitesRepository;
import com.broudy.gcm.control.repos.ToursRepository;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class configures the application's various dependency bindings.
 * TODO provide a more in-depth summary to CitiesRepository class!!!!!
 * <p>
 * Created on the 2nd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class PrimaryDependenciesModule extends AbstractModule {

  private static Logger logger = LogManager.getLogger(PrimaryDependenciesModule.class);

  private final Stage primaryStage;

  public PrimaryDependenciesModule(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.initStyle(StageStyle.TRANSPARENT);

  }

  @Override
  protected void configure() {
    bind(Stage.class).toInstance(primaryStage);
    bind(StageManager.class).in(Singleton.class);


    // bind(UsersRepository.class).asEagerSingleton();
    // bind(CitiesRepository.class).asEagerSingleton();
    // bind(MapsRepository.class).asEagerSingleton();
    // bind(PurchasesRepository.class).asEagerSingleton();
    // bind(MapsRepository.class).in(Singleton.class);
    // bind(PurchasesRepository.class).in(Singleton.class);

    bind(UsersRepository.class).in(Singleton.class);
    bind(CitiesRepository.class).in(Singleton.class);
    bind(MapsRepository.class).in(Singleton.class);
    bind(SitesRepository.class).in(Singleton.class);
    bind(ToursRepository.class).in(Singleton.class);
    bind(CreditCardsRepository.class).in(Singleton.class);
    bind(PurchasesRepository.class).in(Singleton.class);

    requestStaticInjection(RenderingsStyler.class);
  }


}
