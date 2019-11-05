package com.broudy.gcm.boundary.fxmlControllers.welcome;

import animatefx.animation.Shake;
import com.broudy.gcm.boundary.FXMLView;
import com.broudy.gcm.boundary.enhanced.controls.FillingForm;
import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.StageManager;
import com.broudy.gcm.control.repos.CreditCardsRepository;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Manages the third SignUpVIEW.
 * Validates the credit card holder's: full name; id as well as credit card's expiration
 * date & cvv inputs.
 * <p>
 * Created on the 7th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SignUpForm3Controller extends EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(SignUpForm3Controller.class);

  /* Constants */
  private final String NOT_EMPTY_COLOR = "yellow";
  private final String EMPTY_AND_FOCUSED_COLOR = "blue";
  private final String EMPTY_AND_UNFOCUSED_COLOR = "grey-light";

  /* Guice injected fields */
  private final CreditCardsRepository creditCardsRepository;
  @Inject
  private StageManager stageManager;
  @Inject
  private FillingForm fillingForm;

  /* Other variables */
  private RegexValidator fullNameValidator = new RegexValidator();
  private RegexValidator idValidator = new RegexValidator();
  private RegexValidator creditCardNumberValidator = new RegexValidator();
  private RegexValidator cvvValidator = new RegexValidator();
  private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();

  @FXML
  private VBox signUp3VB;
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
  private JFXButton backToStep2BTN;
  @FXML
  private JFXButton signUpBTN;

  @Inject
  public SignUpForm3Controller(CreditCardsRepository creditCardsRepository) {
    this.creditCardsRepository = creditCardsRepository;
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
        .prepareEnhancedTextInputControl(fullNameHB, fullNameTF, fullNameICON, NOT_EMPTY_COLOR,
            EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);
    RenderingsStyler.prepareEnhancedTextInputControl(idHB, idTF, idICON, NOT_EMPTY_COLOR,
        EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);
    RenderingsStyler.prepareEnhancedTextInputControl(ccHB, ccTF, ccICON, NOT_EMPTY_COLOR,
        EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);
    RenderingsStyler.prepareEnhancedTextInputControl(cvvHB, cvvTF, cvvICON, NOT_EMPTY_COLOR,
        EMPTY_AND_FOCUSED_COLOR, EMPTY_AND_UNFOCUSED_COLOR);

    System.out.println(expirationDP.getValue());

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
    creditCardsRepository.getSuperBean().
        bindBidirectional("cardholdersFullName", fullNameTF.textProperty());
    creditCardsRepository.getSuperBean().
        bindBidirectional("cardholdersID", idTF.textProperty());
    creditCardsRepository.getSuperBean().
        bindBidirectional("ccNumber", ccTF.textProperty());
    creditCardsRepository.getSuperBean().
        bindBidirectional("cvv", cvvTF.textProperty());
    creditCardsRepository.getSuperBean().
        bindBidirectional("expirationDate", expirationDP.valueProperty(), LocalDate.class);
  }

  /**
   * Initializes all needed validators.
   */
  public void activateValidators() {

    // Set patterns
    fullNameValidator.setRegexPattern("^[A-Za-z]+\\s[A-Za-z]+(\\s)*$");
    idValidator.setRegexPattern("[0-9]{9}");
    creditCardNumberValidator.setRegexPattern("[0-9]{16}");
    cvvValidator.setRegexPattern("[0-9]{3}");

    // Set appropriate messages
    fullNameValidator.setMessage("Wrong Full Name pattern");
    idValidator.setMessage("Wrong ID format");
    creditCardNumberValidator.setMessage("Wrong number format");
    cvvValidator.setMessage("3 digits only");
    requiredFieldValidator.setMessage("Required field");

    // Add Validators to control
    fullNameTF.getValidators().addAll(requiredFieldValidator, fullNameValidator);
    idTF.getValidators().addAll(requiredFieldValidator, idValidator);
    ccTF.getValidators().addAll(requiredFieldValidator, creditCardNumberValidator);
    cvvTF.getValidators().addAll(requiredFieldValidator, cvvValidator);
  }

  /**
   * Initializes all needed listeners.
   */
  public void activateListeners() {
    fullNameTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        fullNameHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        fullNameHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!fullNameTF.validate()) {
          shakeNode(true, fullNameTF);
        }
      }
    });

    idTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        idHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        idHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!idTF.validate()) {
          shakeNode(true, idTF);
        }
      }
    });

    ccTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        ccHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        ccHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!ccTF.validate()) {
          shakeNode(true, ccTF);
        }
      }
    });

    cvvTF.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        cvvHB.getStyleClass().add("hbox-sign-in-form-focused");
      } else {
        cvvHB.getStyleClass().remove("hbox-sign-in-form-focused");
        if (!cvvTF.validate()) {
          shakeNode(true, cvvTF);
        }
      }
    });

    expirationDP.valueProperty().addListener(((observable, oldValue, newValue) -> {
      if (newValue != null && newValue.isBefore(LocalDate.now())) {
        Shake shake = new Shake(expirationDP);
        shake.play();
        expirationDP.setValue(oldValue);
      }
    }));
  }

  /**
   * Initializes all needed event handlers.
   */
  public void activateEventHandlers() {
    //TODO: clean this up
    signUpBTN.setOnMouseClicked(e -> {
      if (!fullNameTF.validate() || !ccTF.validate() || !idTF.validate() || !cvvTF.validate()
          || expirationDP.getValue().isBefore(LocalDate.now())) {
        shakeNode(fullNameTF.validate(), fullNameTF);
        shakeNode(ccTF.validate(), ccTF);
        shakeNode(idTF.validate(), idTF);
        shakeNode(cvvTF.validate(), cvvTF);
        shakeNode(expirationDP.getValue().isBefore(LocalDate.now()), expirationDP);
      } else {
        //everything is valid send request
        creditCardsRepository.createCC();
      }
    });

    backToStep2BTN.setOnMouseClicked(e -> {
      fillingForm.loadForm(FXMLView.SIGN_UP_FORM_2);
    });

  }

  private void shakeNode(Boolean shakeIT, Node node) {
    if (shakeIT) {
      Shake shake = new Shake(node);
      shake.play();
    }
  }

}
