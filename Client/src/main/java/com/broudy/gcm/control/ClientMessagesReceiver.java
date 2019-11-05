package com.broudy.gcm.control;

import com.broudy.gcm.control.services.EventHandlingService;
import com.broudy.gcm.entity.ServersResponse;
import com.broudy.gcm.entity.dtos.CityDTO;
import com.broudy.gcm.entity.dtos.MapDTO;
import com.broudy.gcm.entity.dtos.PurchaseDTO;
import com.broudy.gcm.entity.dtos.SiteDTO;
import com.broudy.gcm.entity.dtos.TourDTO;
import com.broudy.gcm.entity.dtos.UserDTO;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.google.inject.Inject;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for intercepting messages sent by the server.
 * It matches each message to its repository.
 * <p>
 * Note: the class implements Observer because of a mandatory requirement to use
 * OCSF (Object Client Server Framework) given by my professors.
 * TODO provide a more in-depth summary to ClientMessagesReceiver class!!!!!
 * <p>
 * Created on the 4th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ClientMessagesReceiver implements Observer {

  private static final Logger logger = LogManager.getLogger(ClientMessagesReceiver.class);

  private final GCMClient gcmClient;

  // @Inject
  private final EventHandlingService eventHandlingService;

  @Inject
  public ClientMessagesReceiver(EventHandlingService eventHandlingService) {
    this.eventHandlingService = eventHandlingService;
    this.gcmClient = GCMClient.getInstance();
    gcmClient.addObserver(this);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void update(Observable o, Object msg) {

    if (msg != null) {
      ServersResponse<? extends AbstractDTO> serversResponse = (ServersResponse<? extends AbstractDTO>) msg;
      switch (serversResponse.getResponsesDTOType()) {

        case USER: {
          eventHandlingService.new UserRelatedResponseHandler(
              (ServersResponse<UserDTO>) serversResponse).start();
          break;
        }

        case CITY: {
          eventHandlingService.new CityRelatedResponseHandler(
              (ServersResponse<CityDTO>) serversResponse).start();
          break;
        }

        case MAP: {
          eventHandlingService.new MapRelatedResponseHandler(
              (ServersResponse<MapDTO>) serversResponse).start();
          break;
        }

        case TOUR: {
          eventHandlingService.new TourRelatedResponseHandler(
              (ServersResponse<TourDTO>) serversResponse).start();
          break;
        }

        case SITE: {
          eventHandlingService.new SiteRelatedResponseHandler(
              (ServersResponse<SiteDTO>) serversResponse).start();
          break;
        }

        case PURCHASE: {
          eventHandlingService.new PurchaseRelatedResponseHandler(
              (ServersResponse<PurchaseDTO>) serversResponse).start();
          break;
        }

        // case xxxx: {
        //   new Thread(eventHandlingService.new xxxxx(serversResponse)).start();
        //   break;
        // }
        // case xxxx: {
        //   new Thread(eventHandlingService.new xxxxx(serversResponse)).start();
        //   break;
        // }
        // case xxxx: {
        //   new Thread(eventHandlingService.new xxxxx(serversResponse)).start();
        //   break;
        // }
        // case xxxx: {
        //   new Thread(eventHandlingService.new xxxxx(serversResponse)).start();
        //   break;
        // }
        // case xxxx: {
        //   new Thread(eventHandlingService.new xxxxx(serversResponse)).start();
        //   break;
        // }

        default: {
          logger.error("Oh oh, unknown message has been received:");
        }

      }

      System.out.println(msg);
     /* if (msg instanceof List<?>) {
        handleDTOList((List<? extends AbstractDTO>) msg);
        List<UserDTO> list = new ArrayList<>();
      } else {
        handleDTO((AbstractDTO) msg);
      }*/
    } else {
      logger.traceExit("NULL message has been received.");
    }
  }


  /**
   * Updates the bean in the right repository.
   */
  private void handleDTO(AbstractDTO theDTO) {
/*
    logger.traceEntry(theDTO.toString());
    // usersRepository.setBean(theDTO);

    *//* Make new thread to manage processing answer of type AbstractDTO *//*
    Runnable manage = () -> {

      logger.trace(
          TextColors.GREEN.colorThis(theDTO.getDTOType().name() + " with " + theDTO.getRequest())
              + " was just received!");

      ServersCommands serversAnswer = (ServersCommands) theDTO.getRequest();

      switch (theDTO.getDTOType()) {

        case USER: {
          Platform.runLater(() -> {
            switch (serversAnswer) {
              case USER_AUTHENTICATION_FAILED: {
                logger.debug("USER_AUTHENTICATION_FAILED");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Authentication failed");
                alert.setContentText("E-mail or password are incorrect, please retry...");
                alert.showAndWait();
                break;
              }
              case USER_SIGN_IN: {
                logger.debug("USER_SIGN_IN");
                if (((UserDTO) theDTO).getClassification() != UserClassification.CUSTOMER) {
                  usersRepository.setBean((UserDTO) theDTO);
                  citiesRepository.requestEmployeeWorkspace();
                }
                controllersManager.getStageManager().switchScene(FXMLView.MAIN_CONTAINER);
                // welcomeScreenController.unlink();
                // signInFormController.unlink();
                break;
              }
              case USER_SIGN_UP_SUCCESSFUL: {
                logger.debug("USER_SIGN_UP_SUCCESSFUL");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Congratulations!");
                alert.setHeaderText("Welcome to GCM");
                alert.setContentText("You have been successfully signed-up, redirecting...");
                alert.showAndWait();
                controllersManager.getWelcomeScreenController().showSignIn();
                break;
              }
              case USER_SIGN_UP_FAIL: {
                logger.debug("USER_SIGN_UP_FAIL");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Oops!");
                alert.setContentText("Sign-up failed, please retry...");
                alert.showAndWait();
                break;
              }
              case USER_IS_ALREADY_ONLINE: {
                logger.debug("USER_IS_ALREADY_ONLINE");
                Alert alert = new Alert(AlertType.CONFIRMATION, "SWITCH TO HERE", ButtonType.OK);
                alert.setTitle("Oopsy Daisy!");
                alert.setHeaderText("Seems like you are already connected to our system!");
                alert.setContentText(
                    "Click \"SWITCH TO HERE\" to disconnect prior connections and sign-in.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                  //  TODO implement logging other connections out and signing in here.
                  usersRepository.getBean()
                      .setRequest(ClientsInquiries.USER_DISCONNECT_OTHER_CONNECTIONS);
                  GCMClient.getInstance().send(usersRepository.getBean());
                }
                break;
              }
              case USER_SIGN_OUT_SUCCESSFUL: {
                logger.debug("USER_SIGN_OUT_SUCCESSFUL");
              *//*if (exitRequested) {
                stageManager.cleanUpAndExit();
              } else {
                // TODO close active controllers...
                stageManager.switchScene(FXMLView.WELCOME_SCREEN);
                signInFormController.resetFields();
                // mainContainerController.unlink();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Oopsy Daisy!");
                alert.setHeaderText("You were logged out of the system.");
                alert.setContentText("Seems like you chose to sign-in on another device...");
                alert.showAndWait();*//*

                // welcomeScreenController.showSignIn();
                break;
              }

              case USER_FORCE_SIGN_OUT: {
                // TODO cleanup user force logout
                logger.debug("USER_FORCE_SIGN_OUT_SUCCESSFUL");
              *//*if (exitRequested) {
                stageManager.cleanUpAndExit();
              } else {
                // TODO close active controllers...
                stageManager.switchScene(FXMLView.WELCOME_SCREEN);
                signInFormController.resetFields();
              }*//*
                break;
              }
              default: {
                usersRepository.setBean((UserDTO) theDTO);
                break;
              }
            }
          });
          break;
        }

        case CITY: {
          Platform.runLater(() -> citiesRepository.setBean((CityDTO) theDTO));
          break;
        }

        case MAP: {
          Platform.runLater(() -> mapsRepository.setBean((MapDTO) theDTO));
          break;
        }

        case SITE: {
          Platform.runLater(() -> sitesRepository.setBean((SiteDTO) theDTO));
          break;
        }

        case CREDIT_CARD: {
          Platform.runLater(() -> creditCardsRepository.setBean((CreditCardDTO) theDTO));
          break;
        }

        case REQUEST_WRAPPER: {
          Platform.runLater(() -> {
            switch ((ServersCommands) theDTO.getRequest()) {
              case EMPLOYEE_WORKSPACE_FETCHED: {
                final List<CityDTO> workspaceCities = (List<CityDTO>) ((CustomDTORequestWrapper) theDTO)
                    .getReceivedMessage();
                if (workspaceCities != null) {
                  citiesRepository
                      .setEmployeesWorkspace(FXCollections.observableArrayList(workspaceCities));
                }
                break;
              }
              case EMPLOYEE_CITY_APPROVAL_REQUEST_FETCHED_ALL: {
                final List<CityDTO> approvalRequests = (List<CityDTO>) ((CustomDTORequestWrapper) theDTO)
                    .getReceivedMessage();
                if (approvalRequests != null) {
                  citiesRepository
                      .setCityApprovalRequests(FXCollections.observableArrayList(approvalRequests));
                }
                break;
              }
            }
          });
          break;
        }

        default: {
          logger.error("Oh oh, unknown message has been received: {}", () -> theDTO);
        }

      }
    };
    new Thread(manage).start();*/
  }

  /**
   * Updates the list in the right repository.
   */
  private void handleDTOList(List<? extends AbstractDTO> theList) {
/*
    Runnable manage = () -> {

      // TODO? create a global transferring object which contains list/single dto?
      if (!theList.isEmpty()) {

        logger.trace(TextColors.GREEN.colorThis(
            theList.get(0).getDTOType().name() + " LIST with " + theList.get(0).getRequest())
            + " was just received!");
        switch (theList.get(0).getDTOType()) {

          case USER: {
            Platform.runLater(
                () -> usersRepository.setBeansList(FXCollections.observableArrayList(theList)));
            break;
          }

          case CITY: {
            Platform.runLater(
                () -> citiesRepository.setBeansList(FXCollections.observableArrayList(theList)));
            // () -> citiesRepository.setBeansList(observableList));
            break;
          }

          case MAP: {
            Platform.runLater(
                () -> mapsRepository.setBeansList(FXCollections.observableArrayList(theList)));
            break;
          }

          case SITE: {
            Platform.runLater(
                () -> sitesRepository.setBeansList(FXCollections.observableArrayList(theList)));
            break;
          }

          default: {
            logger.error("Oh oh, unknown message has been received: {}", () -> theList);
          }
        }
      }
    };
    new Thread(manage).start();*/
  }


}