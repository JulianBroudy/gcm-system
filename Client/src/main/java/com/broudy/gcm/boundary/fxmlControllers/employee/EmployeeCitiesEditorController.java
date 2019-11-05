package com.broudy.gcm.boundary.fxmlControllers.employee;

import com.broudy.gcm.boundary.enhanced.fxmlrelated.EnhancedFXMLController;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.MapsRepository;
import com.broudy.gcm.control.services.renderings.RenderingsStyler;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.emojione.EmojiOneView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import java.math.BigDecimal;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO *FEATURE* add option to delete city if (current user > content editor)

/**
 * This class controls the editing of a city.
 * <p>
 * Created on the 11th of August, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeCitiesEditorController<RIS extends Renderable & IStatable> extends
    EnhancedFXMLController {

  private static final Logger logger = LogManager.getLogger(EmployeeCitiesEditorController.class);

  /* Constants */
  private final String PURPLE_NOT_EMPTY_COLOR = "purple";
  private final String PURPLE_EMPTY_AND_FOCUSED_COLOR = "purple-light";
  private final String PURPLE_EMPTY_AND_UNFOCUSED_COLOR = "purple-light";
  private final String BLUE_NOT_EMPTY_COLOR = "blue";
  private final String BLUE_EMPTY_AND_FOCUSED_COLOR = "blue-light";
  private final String BLUE_EMPTY_AND_UNFOCUSED_COLOR = "blue-light";

  /* Guice injected fields */
  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;

  /* Other variables */
  private final BooleanProperty lockedCity;
  private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
  private RegexValidator cityNameValidator = new RegexValidator();
  private ChangeListener<RIS> citiesCBChangeListener;

  /* FXML injected elements */
  @FXML
  private JFXComboBox<RIS> citiesCB;
  @FXML
  private JFXButton createCityBTN;
  @FXML
  private HBox cityNameHB;
  @FXML
  private JFXTextField cityNameTF;
  @FXML
  private EmojiOneView cityNameICON;
  @FXML
  private HBox versionHB;
  @FXML
  private JFXTextField versionTF;
  @FXML
  private FontAwesomeIconView versionICON;
  @FXML
  private HBox descriptionHB;
  @FXML
  private JFXTextArea descriptionTA;
  @FXML
  private MaterialIconView descriptionICON;
  @FXML
  private JFXListView<RIS> availableMapsLV;
  @FXML
  private JFXButton unattachBTN;
  @FXML
  private JFXButton attachBTN;
  @FXML
  private JFXListView<RIS> attachedMapsLV;
  @FXML
  private JFXButton saveBTN;

  @Inject
  public EmployeeCitiesEditorController(CitiesRepository citiesRepository,
      MapsRepository mapsRepository) {
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.lockedCity = new SimpleBooleanProperty();
    this.citiesCBChangeListener = (observable, oldValue, newValue) -> {
      if (newValue != null) {
        if (newValue.getState() != State.DEFAULT) {
          mapsRepository.requestMapsOfCity();
          if (newValue.getState() == State.NEW) {
            recolorTextInputControls(BLUE_NOT_EMPTY_COLOR, BLUE_EMPTY_AND_FOCUSED_COLOR,
                BLUE_EMPTY_AND_UNFOCUSED_COLOR, PURPLE_NOT_EMPTY_COLOR);
            cityNameTF.layout();
            cityNameTF.requestFocus();
            cityNameTF.selectAll();
          } else {
            recolorTextInputControls(PURPLE_NOT_EMPTY_COLOR, PURPLE_EMPTY_AND_FOCUSED_COLOR,
                PURPLE_EMPTY_AND_UNFOCUSED_COLOR, BLUE_NOT_EMPTY_COLOR);
            citiesCB.requestFocus();
          }
        }
      }
    };

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
    citiesRepository.requestRepoReset();
    citiesRepository.requestAllCities();
    mapsRepository.requestDetachedMaps();

    citiesCB.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRLV);
    citiesCB.setButtonCell(RenderingsStyler.EngineeredCellFactories.callRLV(null));

    availableMapsLV.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRISMapLV);
    attachedMapsLV.setCellFactory(RenderingsStyler.EngineeredCellFactories::callRISMapLV);
    availableMapsLV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    attachedMapsLV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    activateBindings();
    activateValidators();
    activateListeners();
    activateEventHandlers();

    recolorTextInputControls(PURPLE_NOT_EMPTY_COLOR, PURPLE_EMPTY_AND_FOCUSED_COLOR,
        PURPLE_EMPTY_AND_UNFOCUSED_COLOR, BLUE_NOT_EMPTY_COLOR);
  }

  /**
   * This method should implement all the steps that turn a "linked" controller into an "unlinked"
   * one.
   */
  @Override
  protected void deactivateController() {
    logger.trace("closing " + TextColors.PURPLE.colorThis(getClass().getSimpleName()) + "...");
    citiesCB.valueProperty().removeListener(citiesCBChangeListener);
    citiesCB.itemsProperty().unbindBidirectional(citiesRepository.beansListProperty());
    citiesCB.valueProperty().unbindBidirectional(citiesRepository.beanProperty());
    citiesRepository.getSuperBean().unBindBidirectional("name", cityNameTF.textProperty());
    citiesRepository.getSuperBean()
        .unBindBidirectional("collectionVersion", versionTF.textProperty());
    citiesRepository.getSuperBean()
        .unBindBidirectional("description", descriptionTA.textProperty());
    attachedMapsLV.itemsProperty().unbindBidirectional(mapsRepository.beansListProperty());
    availableMapsLV.itemsProperty().unbindBidirectional(mapsRepository.detachedMapsListProperty());
    lockedCity.unbind();
    lockedCity.set(true);
    logger.trace(TextColors.GREEN.colorThis(getClass().getSimpleName()) + " has been closed.");
  }

  /**
   * Initializes all needed bindings.
   */
  @Override
  public void activateBindings() {
    // Bind cities' ComboBox's items list and selected item to CitiesRepository's properties.
    citiesCB.itemsProperty().bindBidirectional(citiesRepository.beansListProperty());
    citiesCB.valueProperty().bindBidirectional(citiesRepository.beanProperty());

    // Bind CitiesRepository's superBean fields to VIEW's text fields.
    citiesRepository.getSuperBean().bindBidirectional("name", cityNameTF.textProperty());
    citiesRepository.getSuperBean()
        .bindBidirectional("collectionVersion", versionTF.textProperty());
    citiesRepository.getSuperBean().bindBidirectional("description", descriptionTA.textProperty());

    // Bind attached maps' ListView's items to MapsRepository's beans list.
    attachedMapsLV.itemsProperty().bindBidirectional(mapsRepository.beansListProperty());
    // Bind available maps' ListView's items to MapsRepository's available maps list.
    availableMapsLV.itemsProperty().bindBidirectional(mapsRepository.detachedMapsListProperty());

    // Create CitiesRepository bean's state.
    lockedCity.bind(Bindings.createBooleanBinding(() -> {
      if (citiesCB.getValue() == null) {
        return true;
      }
      State stateOfSelectedCity = citiesCB.getValue().getState();
      return (stateOfSelectedCity != State.APPROVED && !citiesRepository.getEmployeesWorkspace()
          .contains(citiesCB.getValue())) && stateOfSelectedCity != State.NEW;
    }, citiesRepository.beanProperty(), citiesRepository.employeesWorkspaceProperty()));

    // Bind controls to CitiesRepository bean's state.
    cityNameTF.disableProperty().bind(lockedCity);
    descriptionTA.disableProperty().bind(lockedCity);
    attachedMapsLV.disableProperty().bind(lockedCity);
    availableMapsLV.disableProperty().bind(lockedCity);
    // Bind attach button's disable property to having selection in available maps ListView
    // & to CitiesRepository bean's state.
    attachBTN.disableProperty().bind(Bindings.createBooleanBinding(
        () -> lockedCity.get() || availableMapsLV.getSelectionModel().getSelectedItems().isEmpty(),
        lockedCity, availableMapsLV.getSelectionModel().getSelectedItems()));
    // Bind unattach button's disable property to having selection in attached maps ListView
    // & to CitiesRepository bean's state.
    unattachBTN.disableProperty().bind(Bindings.createBooleanBinding(
        () -> lockedCity.get() || attachedMapsLV.getSelectionModel().getSelectedItems().isEmpty(),
        lockedCity, attachedMapsLV.getSelectionModel().getSelectedItems()));
  }

  /**
   * Initializes all needed listeners.
   */
  @Override
  public void activateListeners() {

    // Handle "Choose city" ComboBox clicks.
    // TODO replace citiesRepository.beanProperty() with citiesCB.valueProperty() and make sure all works correctly.
    citiesCB.valueProperty().addListener(citiesCBChangeListener);

    // Forbid white space prefix & digits in city's name.
    cityNameTF.textProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue != null) {
        if (newValue.contentEquals(" ") || newValue.matches(".*\\d.*")/*!newValue
            .matches("^([A-Za-z]|-|')+(\\s[A-Za-z]|-)*$") TODO finish regex*/) {
          if (newValue.isEmpty()) {
            cityNameTF.clear();
          } else {
            cityNameTF.setText(oldValue);
          }
        } else if (!oldValue.isEmpty()) {
          /* Show "Required field!" message only if user erased name and
           * NOT when typing illegal character in the beginning of the city's name. */
          cityNameTF.validate();
        }
      }
    });

    descriptionTA.textProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue != null) {
        if (newValue.contentEquals(" ")) {
          descriptionTA.clear();
        } else {
          descriptionTA.validate();
        }
      }
    });
  }

  /**
   * Initializes all needed validators.
   */
  @Override
  public void activateValidators() {
    // Set validators messages.
    requiredFieldValidator.setMessage("Required field!");
    cityNameValidator.setMessage("Illegal characters were entered!");

    // TODO implement validator?
    // Set regex patterns.
    cityNameValidator.setRegexPattern("^([a-z]|[A-z])+(\\s?([a-z]|[A-z]))*$");

    // Add validators to controls.
    cityNameTF.getValidators().add(requiredFieldValidator);
    descriptionTA.getValidators().add(requiredFieldValidator);
  }

  /**
   * Initializes all needed event handlers.
   */
  @Override
  public void activateEventHandlers() {
    // Handle "Create a new city" Button clicks.
    createCityBTN.setOnAction(clicked -> citiesRepository.requestNewCityPlaceholder());

    // Handle traversing IS-tatable Renderable ListViews with "UP" & "DOWN" keys.
    availableMapsLV
        .setOnKeyPressed(pressed -> handleListViewArrowKeysTraversal(pressed, availableMapsLV));
    attachedMapsLV
        .setOnKeyPressed(pressed -> handleListViewArrowKeysTraversal(pressed, availableMapsLV));

    // Handle moving items from availableMapsLV to attachedMapsLV and vice versa.
    unattachBTN.setOnMouseClicked(click -> moveMaps(attachedMapsLV, availableMapsLV));
    attachBTN.setOnMouseClicked(click -> moveMaps(availableMapsLV, attachedMapsLV));

    // Handle "Save" Button click.
    saveBTN.setOnAction(clicked -> {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setHeaderText(null);

      if (!cityNameTF.validate()) {
        alert.setTitle("City without a name?");
        alert.setContentText("Please provide a name...");
        alert.showAndWait();
      } else if (!descriptionTA.validate()) {
        alert.setTitle("You lazy ass!");
        alert.setContentText("How about a description?");
        alert.showAndWait();
      } else if (attachedMapsLV.getItems().isEmpty()) {
        alert.setTitle("City without maps?");
        alert.setContentText("Please attach at least 1 map.");
        alert.showAndWait();
      } else if (!mapsRepository.mapsWereChanged()) {
        alert.setTitle("Hmmm...");
        alert.setContentText("Maps were not changed!");
        alert.showAndWait();
      } else {
        BigDecimal currentVersionNumber;
        currentVersionNumber = BigDecimal.valueOf(Double.valueOf(versionTF.getText()));
        versionTF.setText(String.valueOf(currentVersionNumber.add(BigDecimal.valueOf(0.1D))));
        citiesCB.valueProperty().removeListener(citiesCBChangeListener);
        citiesRepository.requestCitySaving();
      }
    });
  }

  private void recolorTextInputControls(String notEmptyColor, String emptyAndFocusedColor,
      String emptyAndUnfocusedColor, String colorToRemove) {

    String textField = "jfx-text-field-black-";
    String listView = "jfx-list-view-";
    String button = "button-fillable-";

    RenderingsStyler
        .replace(textField + colorToRemove, textField + notEmptyColor, cityNameTF, versionTF,
            descriptionTA);
    RenderingsStyler.replace(listView + colorToRemove, listView + notEmptyColor, availableMapsLV,
        attachedMapsLV);
    RenderingsStyler
        .replace(button + colorToRemove, button + notEmptyColor, attachBTN, unattachBTN);

    RenderingsStyler
        .prepareEnhancedTextInputControl(cityNameHB, cityNameTF, cityNameICON, notEmptyColor,
            emptyAndFocusedColor, emptyAndUnfocusedColor);
    RenderingsStyler
        .prepareEnhancedTextInputControl(versionHB, versionTF, versionICON, notEmptyColor,
            emptyAndFocusedColor, emptyAndUnfocusedColor);
    RenderingsStyler.prepareEnhancedTextInputControl(descriptionHB, descriptionTA, descriptionICON,
        notEmptyColor, emptyAndFocusedColor, emptyAndUnfocusedColor);

    logger.info(cityNameTF.getStyleClass());

    versionTF.setDisable(false);
    cityNameTF.requestFocus();
    descriptionTA.requestFocus();
    versionTF.requestFocus();
    versionTF.setDisable(true);
  }

  /*
   *   ____                           _                       __  __      _   _               _
   *  / ___|___  _ ____   _____ _ __ (_) ___ _ __   ___ ___  |  \/  | ___| |_| |__   ___   __| |___
   * | |   / _ \| '_ \ \ / / _ \ '_ \| |/ _ \ '_ \ / __/ _ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
   * | |__| (_) | | | \ V /  __/ | | | |  __/ | | | (_|  __/ | |  | |  __/ |_| | | | (_) | (_| \__ \
   *  \____\___/|_| |_|\_/ \___|_| |_|_|\___|_| |_|\___\___| |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
   *
   */

  /**
   * A convenience method for reducing duplicate code.
   * This method moves ListView items between 2 ListViews.
   *
   * @param fromList the ListView from which to move selected items.
   * @param toList the ListView which receives moved items.
   */
  private void moveMaps(ListView<RIS> fromList, ListView<RIS> toList) {
    ObservableList<RIS> selectedItems = fromList.getSelectionModel().getSelectedItems();
    if (selectedItems.stream().noneMatch(renderable -> renderable.getState() == State.LOCKED)) {
      toList.getItems().addAll(selectedItems);
      fromList.getItems().removeAll(selectedItems);
    }
    logger.info("Maps have been moved from {} to {}", fromList, toList);
    logger.trace("Moved maps are: {}", selectedItems);
    logger.debug("Available maps: {}", mapsRepository.getDetachedMapsList());
    logger.debug("Attached maps: {}", mapsRepository.getBeansList());
  }

  /**
   * This method handles "UP" & "DOWN" KeyEvents.
   * This method checks whether the new selection is {@link State#APPROVED}. If not, it selects the
   * first selection that is {@code APPROVED} depending on the arrow pressed. This method takes into
   * account that the last or first item in list might not be {@code APPROVED} and the selection
   * comes from the other end.
   *
   * @param pressed is the {@link KeyEvent}.
   * @param listView is the {@link ListView}.
   */
  private void handleListViewArrowKeysTraversal(KeyEvent pressed, ListView<RIS> listView) {
    Renderable currentlySelectedItem = listView.getSelectionModel().getSelectedItem();
    logger.trace("Currently Selected ITEM: {}", currentlySelectedItem);
    if (currentlySelectedItem != null
        && ((IStatable) currentlySelectedItem).getState() != State.APPROVED) {
      List<RIS> availableMaps = listView.getItems();
      int currentSelectionIndex = listView.getSelectionModel().getSelectedIndex();
      Platform.runLater(() -> {
        listView.getSelectionModel().clearSelection(currentSelectionIndex);
      });
      int traversalDirection;
      switch (pressed.getCode()) {
        case UP: {
          traversalDirection = -1;
          break;
        }
        case DOWN: {
          traversalDirection = +1;
          break;
        }
        default: {
          traversalDirection = 0;
          Platform.runLater(() -> listView.getSelectionModel().select(0));
        }
      }
      if (traversalDirection != 0) {

        logger.trace("map index {}: {}", currentSelectionIndex,
            listView.getItems().get(currentSelectionIndex));
        for (int i = 1; i <= availableMaps.size(); i++) {
          int indexToTest = ((currentSelectionIndex + i * traversalDirection) % availableMaps.size()
              + availableMaps.size()) % availableMaps.size();
          logger.trace("\n" + "availableMaps:{}\n\nindexToTest:{}+{}*{}={}\nget:{}",
              availableMaps.size(), currentSelectionIndex, i, traversalDirection, indexToTest,
              indexToTest % availableMaps.size());
          if (availableMaps.get(indexToTest).getState() == State.APPROVED) {
            Platform.runLater(() -> listView.getSelectionModel().select(indexToTest));
            break;
          }
        }
      }
    }
  }


  public void listenToCitiesCBChanges() {
    if (linked && citiesCB != null) {
      logger.trace(TextColors.RED.colorThis("bound citiesCB"));
      citiesCB.valueProperty().addListener(citiesCBChangeListener);
      if (!citiesCB.getSelectionModel().isEmpty()) {
        final RIS selectedCity = citiesCB.getSelectionModel().getSelectedItem();
        citiesCB.getSelectionModel().clearSelection();
        citiesCB.getSelectionModel().select(selectedCity);
      }
    }
  }

}
