package com.broudy.gcm.boundary.fxmlControllers.welcome;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the SignInVIEW.
 * Validates input before sending to server.
 * <p>
 * Created on the 6th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SignInFormController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(SignInFormController.class);

  /* Constants */
  private final String NOT_EMPTY_COLOR = "yellow";
  private final String EMPTY_AND_FOCUSED_COLOR = "pink";
  private final String EMPTY_AND_UNFOCUSED_COLOR = "blue-light";

  /* Guice injected fields */
  private final UsersRepository usersRepository;
  @Inject
  private StageManager stageManager;

  /* Other variables */
  private RegexValidator emailFieldValidator = new RegexValidator();
  private RegexValidator passwordFieldValidator = new RegexValidator();
  private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();

  /* FXML injected elements */
  @FXML
  private VBox signInVB;
  @FXML
  private HBox emailHB;
  @FXML
  private JFXTextField emailTF;
  @FXML
  private FontAwesomeIconView emailICON;
  @FXML
  private HBox passwordHB;
  @FXML
  private JFXPasswordField passwordPF;
  @FXML
  private FontAwesomeIconView passwordICON;
  @FXML
  private Label forgotPasswordBTN;
  @FXML
  private JFXButton signInBTN;

  @Inject
  public SignInFormController(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }


  /**
   * All the one-time initializations should be implemented in this method.
   */
  @Override
  public void initializeEnhancedController() {

  }

  /**
   * This method should implement all the steps that turn an initialized & "unlinked" controller
   * into a "linked" one.
   */
  @Override
  protected void activateController() {
    RenderingsStyler.prepareEnhancedTextInputControl(emailHB, emailTF, emailICON, NOT_EMPTY_COLOR,
        EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

    RenderingsStyler
        .prepareEnhancedTextInputControl(passwordHB, passwordPF, passwordICON, NOT_EMPTY_COLOR,
            EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

    activateBindings();
    activateValidators();
    activateListeners();
    activateEventHandlers();
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {
    RenderingsStyler.deactivateEnhancedTextInputControl(emailTF, passwordPF);

    usersRepository.getSuperBean().
        unBindBidirectional("email", emailTF.textProperty());
    usersRepository.getSuperBean().
        unBindBidirectional("password", passwordPF.textProperty());
  }

  /**
   * Initializes all needed bindings.
   */
  public void activateBindings() {
    usersRepository.getSuperBean().
        bindBidirectional("email", emailTF.textProperty());
    usersRepository.getSuperBean().
        bindBidirectional("password", passwordPF.textProperty());
  }

  /**
   * Initializes all needed validators.
   */
  public void activateValidators() {
    // set Patterns
    emailFieldValidator.setRegexPattern(
        "^[a-zA-Z0-9_.+-]+@(?:(?:[a-zA-Z0-9-]+\\.)?[a-zA-Z]+\\.)?(GCM|gcm|gmail|yahoo|hotmail|live|Broude|s.braude|broude|braude|G6|g6)\\.(com|co.il|ac.il)$");
    passwordFieldValidator.setRegexPattern(".{8,}");

    // set appropriate messages
    emailFieldValidator.setMessage("Invalid email address");
    passwordFieldValidator.setMessage("At least 8 characters long");
    requiredFieldValidator.setMessage("Required field");

    // add Validators to control
    emailTF.getValidators().addAll(requiredFieldValidator, emailFieldValidator);
    passwordPF.getValidators().addAll(requiredFieldValidator, passwordFieldValidator);
  }

  /**
   * Initializes all needed listeners.
   */
  public void activateListeners() {
    emailTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue && !emailTF.validate()) {
        shakeNode(true, emailTF);
      }
    });

    passwordPF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue && !passwordPF.validate()) {
        shakeNode(true, passwordPF);
      }
    });
  }

  /**
   * Initializes all needed event handlers.
   */
  public void activateEventHandlers() {
    signInBTN.setOnMouseClicked(e -> signInClicked());

    //TODO delete login shortcut
    forgotPasswordBTN.setOnMouseClicked(e -> {
      usersRepository.getBean().setEmail("Julian@Broude.co.il");
      usersRepository.getBean().setPassword("74107410");
      usersRepository.requestLoginAuthentication();
    });
  }

  private void shakeNode(Boolean shakeIT, Node node) {
    if (shakeIT) {
      Shake shake = new Shake(node);
      shake.play();
    }
  }

  private void signInClicked() {
    if (emailTF.validate() && passwordPF.validate()) {
      usersRepository.requestLoginAuthentication();
    }
  }

}
