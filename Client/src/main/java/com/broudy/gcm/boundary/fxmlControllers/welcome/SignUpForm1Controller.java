package com.broudy.gcm.boundary.fxmlControllers.welcome;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.FillingForm;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the first SignUpVIEW.
 * Validates the email & password input.
 * <p>
 * Created on the 6th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SignUpForm1Controller extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(SignUpForm1Controller.class);

  /* Constants */
  private final String NOT_EMPTY_COLOR = "yellow";
  private final String EMPTY_AND_FOCUSED_COLOR = "blue";
  private final String EMPTY_AND_UNFOCUSED_COLOR = "grey-light";

  /* Guice injected fields */
  private final UsersRepository usersRepository;
  @Inject
  private StageManager stageManager;
  @Inject
  private FillingForm fillingForm;

  /* Other variables */
  private RegexValidator emailFieldValidator = new RegexValidator();
  private RegexValidator passwordFieldValidator = new RegexValidator();
  private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();

  /* FXML injected elements */
  @FXML
  private VBox signUp1VB;
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
  private HBox rePasswordHB;
  @FXML
  private JFXPasswordField rePasswordPF;
  @FXML
  private FontAwesomeIconView rePasswordICON;
  @FXML
  private JFXButton openStep2BTN;

  @Inject
  public SignUpForm1Controller(UsersRepository usersRepository) {
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

    RenderingsStyler.prepareEnhancedTextInputControl(rePasswordHB, rePasswordPF, rePasswordICON,
        NOT_EMPTY_COLOR, EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

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

  }


  /**
   * Initializes all needed bindings.
   */
  public void activateBindings() {
    usersRepository.getSuperBean().
        bindBidirectional("email", emailTF.textProperty());
    usersRepository.getSuperBean().
        bindBidirectional("password", passwordPF.textProperty());
    // CreditCardsManager.getInstance().getSuperBean().
    //     bindBidirectional("accountEmail",emailTF.textProperty());
  }

  /**
   * Initializes all needed validators.
   */
  public void activateValidators() {
    // Set patterns
    emailFieldValidator.setRegexPattern(
        "^[a-zA-Z0-9_.+-]+@(?:(?:[a-zA-Z0-9-]+\\.)?[a-zA-Z]+\\.)?(GCM|gcm|gmail|yahoo|hotmail|live|Broude|s.braude|broude|braude|G6|g6)\\.(com|co.il|ac.il)$");
    passwordFieldValidator.setRegexPattern(".{8,}");

    // Set appropriate messages
    requiredFieldValidator.setMessage("Required field");
    emailFieldValidator.setMessage("Illegal email address");
    passwordFieldValidator.setMessage("Your password must be at least 8 characters long.");

    // Add validators to text input controls
    emailTF.getValidators().addAll(requiredFieldValidator, emailFieldValidator);
    passwordPF.getValidators().addAll(requiredFieldValidator, passwordFieldValidator);
    rePasswordPF.getValidators().addAll(requiredFieldValidator, passwordFieldValidator);
  }

  /**
   * Initializes all needed listeners.
   */
  public void activateListeners() {

    emailTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        emailHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        emailHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!emailTF.validate()) {
          shakeNode(true, emailTF);
        }
      }
    });

    passwordPF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        passwordHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        passwordHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!passwordPF.validate()) {
          shakeNode(true, passwordPF);
        }
      }
    });

    rePasswordPF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        rePasswordHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        rePasswordHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!rePasswordPF.validate() || !(rePasswordPF.textProperty().getValue()
            .equals(passwordPF.textProperty().getValue()))) {
          shakeNode(true, rePasswordPF);
        }
      }
    });
  }

  /**
   * Initializes all needed event handlers.
   */
  public void activateEventHandlers() {
    openStep2BTN.setOnMouseClicked(e -> openStep2Clicked());
  }

  private void openStep2Clicked() {
    if (!emailTF.validate() || !passwordPF.validate() || !rePasswordPF.validate()) {
      shakeNode(true, emailTF);
      shakeNode(true, rePasswordPF);
      shakeNode(true, passwordPF);
    } else {
      fillingForm.loadForm(FXMLView.SIGN_UP_FORM_2);
    }
  }

  private void shakeNode(Boolean shakeIT, Node node) {
    if (shakeIT) {
      Shake shake = new Shake(node);
      shake.play();
    }
  }


}
