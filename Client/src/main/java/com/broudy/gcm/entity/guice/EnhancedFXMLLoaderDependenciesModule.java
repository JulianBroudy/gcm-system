package com.broudy.gcm.entity.guice;

import com.broudy.gcm.boundary.enhanced.controls.FillingForm;
import com.broudy.gcm.boundary.enhanced.controls.SceneSwitcheroo;
import com.broudy.gcm.boundary.fxmlControllers.ClientInitializingController;
import com.broudy.gcm.boundary.fxmlControllers.MainContainerController;
import com.broudy.gcm.boundary.fxmlControllers.customer.CustomerCatalogBrowserController;
import com.broudy.gcm.boundary.fxmlControllers.customer.CustomerSidePaneController;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeCitiesEditorController;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeCityExplorerController;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeMapsEditorController;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeMyAccountController;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeMyWorkspaceController;
import com.broudy.gcm.boundary.fxmlControllers.employee.EmployeeRequestsViewerController;
import com.broudy.gcm.boundary.fxmlControllers.welcome.SignInFormController;
import com.broudy.gcm.boundary.fxmlControllers.welcome.SignUpForm1Controller;
import com.broudy.gcm.boundary.fxmlControllers.welcome.SignUpForm2Controller;
import com.broudy.gcm.boundary.fxmlControllers.welcome.SignUpForm3Controller;
import com.broudy.gcm.boundary.fxmlControllers.welcome.WelcomeScreenController;
import com.broudy.gcm.control.services.renderings.CityRenderingsService;
import com.broudy.gcm.control.services.renderings.MapViewRenderingsService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class configures all <code>EnhancedFXMLController</code>'s bindings.
 * <p>
 * TODO provide a more in-depth summary to CitiesRepository class!!!!!
 * <p>
 * Created on the 4th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController
 */
public class EnhancedFXMLLoaderDependenciesModule extends AbstractModule {

  private static Logger logger = LogManager.getLogger(EnhancedFXMLLoaderDependenciesModule.class);

  @Override
  protected void configure() {
    logger.traceEntry("configure()");

    // MapBinder<FXMLView, EnhancedFXMLController> viewToControllerBinder = MapBinder
    //     .newMapBinder(binder(), FXMLView.class, EnhancedFXMLController.class);
    //
    // viewToControllerBinder.addBinding(FXMLView.CLIENT_STARTUP)
    //     .to(ClientInitializingController.class).in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.WELCOME_SCREEN).to(WelcomeScreenController.class)
    //     .in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.SIGN_IN_FORM).to(SignInFormController.class)
    //     .in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.SIGN_UP_FORM_1).to(SignUpForm1Controller.class)
    //     .in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.SIGN_UP_FORM_2).to(SignUpForm2Controller.class)
    //     .in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.SIGN_UP_FORM_3).to(SignUpForm3Controller.class)
    //     .in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.MAIN_CONTAINER).to(MainContainerController.class)
    //     .in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.EMPLOYEE_SIDE_PANE)
    //     .to(EmployeeSidePaneController.class).in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.EMPLOYEE_MY_ACCOUNT)
    //     .to(EmployeeMyAccountController.class).in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.EMPLOYEE_MY_WORKSPACE)
    //     .to(EmployeeMyWorkspaceController.class).in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.EMPLOYEE_CITIES_EDITOR)
    //     .to(EmployeeCitiesEditorController.class).in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.EMPLOYEE_MAPS_EDITOR)
    //     .to(EmployeeMapsEditorController.class).in(Singleton.class);
    // viewToControllerBinder.addBinding(FXMLView.EMPLOYEE_REQUESTS_VIEWER)
    //     .to(EmployeeRequestsViewerController.class).in(Singleton.class);

    bind(ClientInitializingController.class).asEagerSingleton();
    bind(WelcomeScreenController.class).asEagerSingleton();/*in(Singleton.class)*/
    bind(SignInFormController.class).asEagerSingleton();
    bind(SignUpForm1Controller.class).asEagerSingleton();
    bind(SignUpForm2Controller.class).asEagerSingleton();
    bind(SignUpForm3Controller.class).asEagerSingleton();
    bind(MainContainerController.class).asEagerSingleton();/*.in(Singleton.class);*/
    bind(EmployeeMyAccountController.class).in(Singleton.class);
    bind(EmployeeMyWorkspaceController.class).in(Singleton.class);
    bind(EmployeeCitiesEditorController.class).in(Singleton.class);
    bind(EmployeeMapsEditorController.class).in(Singleton.class);
    bind(EmployeeRequestsViewerController.class).in(Singleton.class);
    bind(EmployeeCityExplorerController.class).in(Singleton.class);
    // TODO: cleanup EnhancedFXMLLoaderDependenciesModule

    bind(FillingForm.class).asEagerSingleton();
    bind(SceneSwitcheroo.class).in(Singleton.class);

    bind(CustomerSidePaneController.class).in(Singleton.class);
    bind(CustomerCatalogBrowserController.class).in(Singleton.class);

    bind(MapViewRenderingsService.class).in(Singleton.class);
    bind(CityRenderingsService.class).in(Singleton.class);
  }


}
