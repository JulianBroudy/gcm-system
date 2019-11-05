package com.broudy.gcm.boundary.fxmlControllers.employee;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class controls employee's account information.
 * <p>
 * Created on the 2nd of July, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeMyAccountController extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(EmployeeMyAccountController.class);
  /* Constants */
  private final String NOT_EMPTY_COLOR = "pink";
  private final String EMPTY_AND_FOCUSED_COLOR = "blue";
  private final String EMPTY_AND_UNFOCUSED_COLOR = "blue-light";

  private final UsersRepository usersRepository;
  /* Other variables */
  private RegexValidator nameValidator = new RegexValidator();
  private RegexValidator phoneNumberValidator = new RegexValidator();
  private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
  private StringProperty phoneNumberTextProperty = new SimpleStringProperty("");

  @FXML
  private Accordion accordion;
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
  private HBox fullNameHB;
  @FXML
  private JFXTextField fullNameTF;
  @FXML
  private FontAwesomeIconView fullNameICON;
  @FXML
  private HBox idHB;
  @FXML
  private JFXTextField idTF;
  @FXML
  private FontAwesomeIconView idICON;
  @FXML
  private HBox ccHB;
  @FXML
  private JFXTextField ccTF;
  @FXML
  private FontAwesomeIconView ccICON;
  @FXML
  private HBox expirationDateHB;
  @FXML
  private JFXDatePicker expirationDP;
  @FXML
  private HBox cvvHB;
  @FXML
  private JFXTextField cvvTF;
  @FXML
  private FontAwesomeIconView cvvICON;
  @FXML
  private JFXButton saveBTN;

  @Inject
  public EmployeeMyAccountController(UsersRepository usersRepository) {
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

    RenderingsStyler
        .prepareEnhancedTextInputControl(passwordHB, passwordPF, passwordICON, NOT_EMPTY_COLOR,
            EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);
    RenderingsStyler.prepareEnhancedTextInputControl(rePasswordHB, rePasswordPF, rePasswordICON,
        NOT_EMPTY_COLOR, EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

    phoneNumberPrefixCB.getItems().addAll("050", "052", "054", "058");

    activateBindings();
    activateValidators();
    activateListeners();
    activateEventHandlers();

    phoneNumberPrefixCB.setValue(phoneNumberTextProperty.get().substring(0, 3));
    phoneNumberTF.setText(phoneNumberTextProperty.get().substring(3));
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
  }

  @Override
  protected void activateBindings() {
    usersRepository.getSuperBean().bindBidirectional("firstName", firstNameTF.textProperty());
    usersRepository.getSuperBean().bindBidirectional("lastName", lastNameTF.textProperty());
    usersRepository.getSuperBean().bindBidirectional("username", usernameTF.textProperty());
    usersRepository.getSuperBean().bindBidirectional("phoneNumber", phoneNumberTextProperty);
  }

  /**
   * Initializes all needed validators.
   */
  @Override
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
    accordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
      ArrayList<TitledPane> titledPanes = new ArrayList<>(accordion.getPanes());
      if (oldValue != null) {
        if (titledPanes.stream().noneMatch(TitledPane::isExpanded)) {
          final int indexOfPaneBeingCollapsed = titledPanes.indexOf(oldValue);
          Platform.runLater(() -> {
            accordion.setExpandedPane(indexOfPaneBeingCollapsed == 0 ? titledPanes.get(1)
                : titledPanes.get(indexOfPaneBeingCollapsed - 1));
          });
        }
      }
    });

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

  @Override
  protected void activateEventHandlers() {
    saveBTN.setOnMouseClicked(e -> {
      if (!firstNameTF.validate() || !lastNameTF.validate() || !usernameTF.validate()
          || !phoneNumberPrefixCB.validate() || !phoneNumberTF.validate()) {
        shakeNode(firstNameTF.validate(), firstNameTF);
        shakeNode(lastNameTF.validate(), lastNameTF);
        shakeNode(usernameTF.validate(), usernameTF);
        shakeNode(phoneNumberPrefixCB.validate(), phoneNumberPrefixCB);
        shakeNode(phoneNumberTF.validate(), phoneNumberTF);
      } else {
        usersRepository.requestUserInformationSaving();
      }
    });
  }

  private void shakeNode(Boolean shakeIT, Node node) {
    if (shakeIT) {
      Shake shake = new Shake(node);
      shake.play();
    }
  }
}
