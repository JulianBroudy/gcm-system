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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the second SignUpVIEW.
 * Validates the first name, last name, username & phone number input.
 * <p>
 * Created on the 6th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SignUpForm2Controller extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(SignUpForm2Controller.class);

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
  private RegexValidator nameValidator = new RegexValidator();
  private RegexValidator phoneNumberValidator = new RegexValidator();
  private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
  private StringProperty phoneNumberTextProperty = new SimpleStringProperty("");

  /* FXML injected elements */
  @FXML
  private VBox signUp2VB;
  @FXML
  private HBox firstNameHB;
  @FXML
  private JFXTextField firstNameTF;
  @FXML
  private FontAwesomeIconView firstNameICON;
  @FXML
  private HBox lastNameHB;
  @FXML
  private JFXTextField lastNameTF;
  @FXML
  private FontAwesomeIconView lastNameICON;
  @FXML
  private HBox usernameHB;
  @FXML
  private JFXTextField usernameTF;
  @FXML
  private FontAwesomeIconView usernameICON;
  @FXML
  private HBox phoneNumberPrefixHB;
  @FXML
  private JFXComboBox<String> phoneNumberPrefixCB;
  @FXML
  private HBox phoneNumberHB;
  @FXML
  private JFXTextField phoneNumberTF;
  @FXML
  private FontAwesomeIconView phoneNumberICON;
  @FXML
  private JFXButton backToStep1BTN;
  @FXML
  private JFXButton openStep3BTN;

  @Inject
  public SignUpForm2Controller(UsersRepository usersRepository) {
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
    RenderingsStyler
        .prepareEnhancedTextInputControl(firstNameHB, firstNameTF, firstNameICON, NOT_EMPTY_COLOR,
            EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);
    RenderingsStyler
        .prepareEnhancedTextInputControl(lastNameHB, lastNameTF, lastNameICON, NOT_EMPTY_COLOR,
            EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);
    RenderingsStyler
        .prepareEnhancedTextInputControl(usernameHB, usernameTF, usernameICON, NOT_EMPTY_COLOR,
            EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);
    RenderingsStyler.prepareEnhancedTextInputControl(phoneNumberHB, phoneNumberTF, phoneNumberICON,
        NOT_EMPTY_COLOR, EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

    phoneNumberPrefixCB.getItems().addAll("050", "052", "054", "058");

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
    usersRepository.getSuperBean().unBindBidirectional("firstName", firstNameTF.textProperty());
    usersRepository.getSuperBean().unBindBidirectional("lastName", lastNameTF.textProperty());
    usersRepository.getSuperBean().unBindBidirectional("username", usernameTF.textProperty());
    usersRepository.getSuperBean().unBindBidirectional("phoneNumber", phoneNumberTextProperty);
    phoneNumberTextProperty.unbind();
  }


  /**
   * Initializes all needed bindings.
   */
  public void activateBindings() {
    phoneNumberTextProperty.bind(Bindings.createStringBinding(() ->
            phoneNumberPrefixCB.valueProperty().isNotNull().get() && phoneNumberTF.textProperty()
                .isNotNull().get() ? phoneNumberPrefixCB.valueProperty().get() + phoneNumberTF
                .textProperty().get() : "", phoneNumberPrefixCB.valueProperty(),
        phoneNumberTF.textProperty()));

    // phoneNumberTextProperty
    //     .bind(phoneNumberPrefixCB.valueProperty().asString().concat(phoneNumberTF.textProperty()));

    //Todo: fix exception when pressing back from SignUpForm3
    usersRepository.getSuperBean().bindBidirectional("firstName", firstNameTF.textProperty());
    usersRepository.getSuperBean().bindBidirectional("lastName", lastNameTF.textProperty());
    usersRepository.getSuperBean().bindBidirectional("username", usernameTF.textProperty());
    usersRepository.getSuperBean().bindBidirectional("phoneNumber", phoneNumberTextProperty);
  }

  /**
   * Initializes all needed validators.
   */
  public void activateValidators() {
    // Set patterns
    nameValidator.setRegexPattern("^[a-zA-Z]+$");
    phoneNumberValidator.setRegexPattern("[0-9]{7}");

    // Set appropriate messages
    nameValidator.setMessage("Letters only");
    phoneNumberValidator.setMessage("Must be 7 digits");
    requiredFieldValidator.setMessage("Required field");

    // Add Validators to control
    firstNameTF.getValidators().addAll(requiredFieldValidator, nameValidator);
    lastNameTF.getValidators().addAll(requiredFieldValidator, nameValidator);
    usernameTF.getValidators().add(requiredFieldValidator);
    phoneNumberPrefixCB.getValidators().add(requiredFieldValidator);
    phoneNumberTF.getValidators().addAll(requiredFieldValidator, phoneNumberValidator);
  }

  /**
   * Initializes all needed listeners.
   */
  public void activateListeners() {
    phoneNumberPrefixCB.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        phoneNumberPrefixHB.getStyleClass().add("container-line-blue");
      } else {
        if (!phoneNumberPrefixCB.validate()) {
          shakeNode(true, phoneNumberPrefixCB);
        }
        phoneNumberPrefixHB.getStyleClass().remove("container-line-blue");
      }
    });

    firstNameTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        firstNameHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        firstNameHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!firstNameTF.validate()) {
          shakeNode(true, firstNameTF);
        }
      }

    });

    lastNameTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        lastNameHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        lastNameHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!lastNameTF.validate()) {
          shakeNode(true, lastNameTF);
        }
      }
    });

    usernameTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        usernameHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        usernameHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!usernameTF.validate()) {
          shakeNode(true, usernameTF);
        }
      }
    });

    phoneNumberTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        if (!phoneNumberTF.validate()) {
          shakeNode(true, phoneNumberTF);
        }
      }
    });
  }

  /**
   * Initializes all needed event handlers.
   */
  public void activateEventHandlers() {
    openStep3BTN.setOnMouseClicked(e -> {
      if (!firstNameTF.validate() || !lastNameTF.validate() || !usernameTF.validate()
          || !phoneNumberPrefixCB.validate() || !phoneNumberTF.validate()) {
        shakeNode(firstNameTF.validate(), firstNameTF);
        shakeNode(lastNameTF.validate(), lastNameTF);
        shakeNode(usernameTF.validate(), usernameTF);
        shakeNode(phoneNumberPrefixCB.validate(), phoneNumberPrefixCB);
        shakeNode(phoneNumberTF.validate(), phoneNumberTF);
      } else {
        fillingForm.loadForm(FXMLView.SIGN_UP_FORM_3);
      }
    });
    backToStep1BTN.setOnMouseClicked(e -> fillingForm.loadForm(FXMLView.SIGN_UP_FORM_1));
  }

  private void shakeNode(Boolean shakeIT, Node node) {
    if (shakeIT) {
      Shake shake = new Shake(node);
      shake.play();
    }
  }

}
