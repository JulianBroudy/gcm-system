package com.broudy.gcm.boundary;

/**
 * This class centralizes all different views/scenes that might be loaded during program's life.
 * <p>
 * Created on the 4th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public enum FXMLView {

  CLIENT_STARTUP {
    @Override
    public String getPath() {
      return "/views/ClientInitializingVIEW.fxml";
    }
  }, SPLASH_SCREEN {
    @Override
    public String getPath() {
      return "/views/SplashScreenVIEW.fxml";
    }
  }, WELCOME_SCREEN {
    @Override
    public String getPath() {
      return "/views/welcome/WelcomeScreenVIEW.fxml";
    }
  }, SIGN_IN_FORM {
    @Override
    public String getPath() {
      return "/views/welcome/SignInFormVIEW.fxml";
    }
  }, SIGN_UP_FORM_1 {
    @Override
    public String getPath() {
      return "/views/welcome/SignUpForm1VIEW.fxml";
    }
  }, SIGN_UP_FORM_2 {
    @Override
    public String getPath() {
      return "/views/welcome/SignUpForm2VIEW.fxml";
    }
  }, SIGN_UP_FORM_3 {
    @Override
    public String getPath() {
      return "/views/welcome/SignUpForm3VIEW.fxml";
    }
  }, MAIN_CONTAINER {
    @Override
    public String getPath() {
      return "/views/MainContainerVIEW.fxml";
    }
  }, SCENE_SWITCHEROO {
    @Override
    public String getPath() {
      return "/views/SceneSwitcherooVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Welcome to GCM";
    }
  }, JUST_BROWSE_SIDE_PANE {
    @Override
    public String getPath() {
      return "/views/JustBrowseSidePaneVIEW.fxml";
    }
  }, JUST_BROWSING {
    @Override
    public String getPath() {
      return "/views/JustBrowsingVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Catalog Browser";
    }
  }, EMPLOYEE_SIDE_PANE {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeSidePaneVIEW.fxml";
    }
  }, EMPLOYEE_CITIES_EDITOR {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeCitiesEditorVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Cities Editor";
    }
  }, EMPLOYEE_MY_ACCOUNT {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeMyAccountVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "My Account";
    }
  }, EMPLOYEE_MY_WORKSPACE {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeMyWorkspaceVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "My Workspace";
    }

  }, EMPLOYEE_MAPS_EDITOR {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeMapsEditorVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Maps Editor";
    }

  }, EMPLOYEE_REQUESTS_VIEWER {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeRequestsViewerVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Requests Viewer";
    }

  }, EMPLOYEE_CITY_EXPLORER {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeCityExplorerVIEW.fxml";
    }
  }, EMPLOYEE_UNAUTHORIZED {
    @Override
    public String getPath() {
      return "/views/employee/EmployeeUnauthorizedVIEW.fxml";
    }
  }, CUSTOMER_SIDE_PANE {
    @Override
    public String getPath() {
      return "/views/customer/CustomerSidePaneVIEW.fxml";
    }
  }, CUSTOMER_MY_ACCOUNT {
    @Override
    public String getPath() {
      return "/views/customer/CustomerMyAccountVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "My Account";
    }

  }, CUSTOMER_CATALOG_BROWSING {
    @Override
    public String getPath() {
      return "/views/customer/CustomerCatalogBrowserVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Catalog Browser";
    }

  }, CUSTOMER_ACTIVE_SUBSCRIPTIONS {
    @Override
    public String getPath() {
      return "/views/customer/CustomerActiveSubscriptionsVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Active Subscriptions";
    }

  }, CUSTOMER_PURCHASE_HISTORY {
    @Override
    public String getPath() {
      return "/views/customer/CustomerPurchaseHistoryVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Purchase History";
    }

  }, CUSTOMER_CITY_EXPLORER {
    @Override
    public String getPath() {
      return "/views/customer/CustomerCityExplorerVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Purchase History";
    }

  };

  // abstract String getTitle();

  abstract public String getPath();

  public String getTitle() {
    return "";
  }

}
