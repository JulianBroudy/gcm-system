package com.broudy.gcm.control.services.renderings;

import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.CityDTO;
import com.broudy.gcm.entity.dtos.MapDTO;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXListCell;
import de.jensd.fx.glyphs.GlyphIcon;
import java.util.HashMap;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Convenience class that offers useful methods that change renderings' visuals.
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class RenderingsStyler {

  private static final String ICON_STYLE_CLASS = "glyph-icon-";
  private static final String CONTAINER_STYLE_CLASS = "container-line-";
  /**
   * In order to keep bindings alive for cases where the calling class does not store the
   * StringProperty
   * returned by {@link #prepareEnhancedTextInputControl}. Also in order to unbind and
   */
  private static final HashMap<TextInputControl, StringProperty> activeSFSF = new HashMap<>();

  private static final DropShadow dropShadow = new DropShadow();

  @Inject
  private static CitiesRepository citiesRepository;

  static {
    dropShadow.setRadius(10);
    dropShadow.setWidth(21);
    dropShadow.setHeight(21);
    dropShadow.setOffsetX(-2);
    dropShadow.setOffsetY(4);
    dropShadow.setColor(Color.BLACK);
  }

  /**
   * Gets the dropShadow.
   *
   * @return dropShadow's new value.
   */
  public static DropShadow getDropShadow() {
    return dropShadow;
  }

  /**
   * Goes through all the necessary steps to initialize an EnhancedTextField.
   * EnhancedTextField is built from a Pane (preferably an HBox), a TextInputControl & a GlyphIcon.
   * <p>
   * Important! CSS file must contain classes whose names are a concatenation of this class's
   * constant strings {@see #ICON_STYLE_CLASS}, {@see #ICON_STYLE_CLASS} and the passed colors.
   *
   * @param container a Pane which has the capability of holding more than 1 component.
   * @param textInputControl the TextField.
   * @param glyphIcon an icon (de.jensd.fx.glyphs) by Jens Deters.
   * @param notEmptyColor the color to be applied when the TextField is not empty.
   * @param focusedColor the color to be applied when the TextField is empty but focused.
   * @param unfocusedColor the color to be applied when the TextField is empty and unfocused.
   *
   * @return StringProperty that must be stored in order to keep the binding active.
   */
  public static StringProperty prepareEnhancedTextInputControl(final Pane container,
      final TextInputControl textInputControl, final GlyphIcon glyphIcon,
      final String notEmptyColor, final String focusedColor, final String unfocusedColor) {

    StringProperty iconStyleClassProperty = RenderingsStyler
        .createIconStyleClassBindings(textInputControl, notEmptyColor, focusedColor,
            unfocusedColor);

    RenderingsStyler
        .createContainerColorsSwitcheroo(container, textInputControl, focusedColor, unfocusedColor);

    /* Check if a StringProperty for this textInputControl has been created already:
     * If so --> done (other UI components are already bound to this StringProperty).
     * Else --> create the needed bindings. */
    if (!activeSFSF.containsKey(textInputControl)) {
      RenderingsStyler.createIconColorsSwitcheroo(iconStyleClassProperty, glyphIcon);
      activeSFSF.put(textInputControl, iconStyleClassProperty);
    }

    return iconStyleClassProperty;
  }

  /**
   * Creates a StringBinding that matches the correct icon's style class based on passed colors.
   *
   * @param textInputControl the control which the stringProperty is dependent on.
   * @param notEmptyColor the color to apply when the textInputControl is not empty.
   * @param focusedColor the color to apply when the textInputControl empty & focused.
   * @param unfocusedColor the color to apply when the textInputControl empty & unfocused.
   */
  private static StringProperty createIconStyleClassBindings(TextInputControl textInputControl,
      String notEmptyColor, String focusedColor, String unfocusedColor) {

    String whenNotEmptyApply = ICON_STYLE_CLASS + notEmptyColor;
    String whenEmptyAndFocused = ICON_STYLE_CLASS + focusedColor;
    String whenEmptyAndUnfocused = ICON_STYLE_CLASS + unfocusedColor;

    /* Check if a StringProperty for this textInputControl has been created already:
     * If so --> unbind it and use it.
     * Else --> create a new one. */
    StringProperty stringProperty;
    if (activeSFSF.containsKey(textInputControl)) {
      stringProperty = activeSFSF.get(textInputControl);
      stringProperty.unbind();
    } else {
      stringProperty = new SimpleStringProperty();
    }

    stringProperty.bind(
        Bindings.when(textInputControl.textProperty().isNotEmpty()).then(whenNotEmptyApply)
            .otherwise(Bindings.when(textInputControl.focusedProperty()).then(whenEmptyAndFocused)
                .otherwise(whenEmptyAndUnfocused)));

    return stringProperty;
  }

  /**
   * Adds a listener that keeps the passed icon's style classes in sync with the iconStyleClass.
   *
   * @param iconStyleClass the StringProperty to set.
   * @param fieldIcon the icon to add the listener to.
   */
  private static void createIconColorsSwitcheroo(StringProperty iconStyleClass,
      GlyphIcon fieldIcon) {
    iconStyleClass.addListener((observable, oldValue, newValue) -> {
      fieldIcon.getStyleClass().clear();
      fieldIcon.getStyleClass().add(newValue);
    });
  }

  /**
   * Adds a listener that sets the right colors to the passed container.
   *
   * @param container HBox to alter its color.
   * @param textInputControl the TextField the HBox's color is dependant on.
   */
  private static void createContainerColorsSwitcheroo(Pane container,
      TextInputControl textInputControl, String focusedColor, String unfocusedColor) {

    String whenTextFieldIsFocused = CONTAINER_STYLE_CLASS + focusedColor;
    String whenTextFieldIsNotFocused = CONTAINER_STYLE_CLASS + unfocusedColor;

    textInputControl.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        container.getStyleClass().clear();
        container.getStyleClass().add(whenTextFieldIsFocused);
      } else {
        container.getStyleClass().clear();
        container.getStyleClass().add(whenTextFieldIsNotFocused);
      }
    });
  }

  /**
   * Convenience method.
   * Disables or enables all passed nodes.
   *
   * @param disable {@code true} to disable, {@code false} to enable.
   * @param nodes nodes to toggle their disable property.
   */
  public static void toggleDisableProperty(boolean disable, Node... nodes) {
    for (Node node : nodes) {
      node.setDisable(disable);
    }
  }

  public static void applyStyleClass(String styleClass, Node... nodes) {
    for (Node node : nodes) {
      node.getStyleClass().add(styleClass);
    }
  }

  public static void replace(String thisStyleClass, String withThisStyleClass, Node... nodes) {
    for (Node node : nodes) {
      node.getStyleClass().removeIf(Predicate.isEqual(thisStyleClass));
      node.getStyleClass().add(withThisStyleClass);
    }
  }

  public static void removeStyleClass(String styleClass, Node... nodes) {
    for (Node node : nodes) {
      node.getStyleClass().remove(styleClass);
    }
  }

  public static void deactivateEnhancedTextInputControl(TextInputControl... textInputControls) {
    for (int i = 0; i < textInputControls.length; i++) {
      activeSFSF.remove(textInputControls[i]);
    }

  }

  /**
   * This class centralizes all and every CellFactory implementation that could possibly be needed.
   */
  public static class EngineeredCellFactories {

    public static ListCell<Renderable> callLV(ListView<Renderable> renderingsListView) {
      return new JFXListCell<Renderable>() {
        @Override
        protected void updateItem(Renderable item, boolean empty) {
          super.updateItem(item, empty);
          this.setVisible(item != null || !empty);
          this.setManaged(false);
          // if(item==null || empty)
          // {
          //   this.setGraphic(null);
          // }
          setText(empty ? "" : item.render());
        }
      };
    }

    public static ListCell<Renderable> callStatableLV(ListView<Renderable> renderingsListView) {
      return new JFXListCell<Renderable>() {
        @Override
        protected void updateItem(Renderable item, boolean empty) {
          super.updateItem(item, empty);
          boolean visible;
          // this.setManaged(false);
          this.setVisible(visible = (item != null && !empty));
          if (visible) {
            State itemsState = ((IStatable) item).getState();
            boolean lockCell = itemsState != State.APPROVED;
            setDisable(lockCell);
            setOpacity(lockCell ? 0.3 : 1);
            setText(item.render());
          } else {
            setText("");
          }
          // this.setManaged(false);
        }
      };
    }


    public static <RIS extends Renderable & IStatable> ListCell<RIS> callRLV(
        ListView<RIS> renderingsListView) {
      return new JFXListCell<RIS>() {
        @Override
        protected void updateItem(RIS item, boolean empty) {
          super.updateItem(item, empty);
          this.setVisible(item != null || !empty);
          this.setManaged(false);
          // if(item==null || empty)
          // {
          //   this.setGraphic(null);
          // }
          setText(empty ? "" : item.render());
        }
      };
    }

    public static <RIS extends Renderable & IStatable> ListCell<RIS> callRISLV(
        ListView<RIS> renderingsListView) {
      return new JFXListCell<RIS>() {
        @Override
        protected void updateItem(RIS item, boolean empty) {
          super.updateItem(item, empty);
          boolean visible;
          // this.setManaged(false);
          this.setVisible(visible = (item != null && !empty));
          if (visible) {
            State itemsState = ((IStatable) item).getState();
            boolean lockCell = itemsState != State.APPROVED;
            setDisable(lockCell);
            setOpacity(lockCell ? 0.3 : 1);
            setText(item.render());
          } else {
            setText("");
          }
          // this.setManaged(false);
        }
      };
    }

    public static <MAP extends Renderable & IStatable> ListCell<MAP> callRISMapLV(
        ListView<MAP> renderingsListView) {
      return (ListCell<MAP>) new JFXListCell<MapDTO>() {
        @Override
        protected void updateItem(MapDTO map, boolean empty) {
          super.updateItem(map, empty);
          boolean visible;
          this.setVisible(visible = (map != null && !empty));
          if (visible) {
            State itemsState = ((IStatable) map).getState();
            boolean lockCell =
                itemsState != State.APPROVED && citiesRepository.getEmployeesWorkspace().stream()
                    .noneMatch(city -> map.getCityID() == (((CityDTO) city).getID()));
            setDisable(lockCell);
            setOpacity(lockCell ? 0.3 : 1);
            setText(map.render());
          } else {
            setText("");
          }
        }
      };
    }


    public static <RIS extends Renderable & IStatable> TreeCell<RIS> callCTV(
        TreeView<RIS> risTreeView) {
      return new CheckBoxTreeCell<RIS>() {
        @Override
        public void updateItem(RIS item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty ? "" : item.render());
        }
      };
    }

    public static <RIS extends Renderable & IStatable> ListCell<RIS> callRISCLV(
        ListView<RIS> risListView) {
      return new CheckBoxListCell<RIS>() {
        @Override
        public void updateItem(RIS item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null) {
            setText(null);
          } else {
            setText(empty ? "" : item.render());
          }
        }
      };
    }

    public static <RIS extends Renderable & IStatable> ListCell<RIS> callRISCLVN(
        ListView<RIS> risListView) {
      return new CheckBoxListCell<RIS>() {
        @Override
        public void updateItem(RIS item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null) {
            setText(null);
          } else {
            setText(empty ? "" : item.render());
          }
        }
      };

    }

    /*public static <Renderable> ListCell<Renderable> callIStatableMapLV(
        ListView<? extends Renderable> renderingsListView) {
      return (ListCell<? extends Renderable>) new JFXListCell<MapDTO>() {
        @Override
        protected void updateItem(MapDTO item, boolean empty) {
          super.updateItem(item, empty);
          boolean visible;
          this.setVisible(visible = (item != null && !empty));
          if (visible) {
            State itemsState = ((IStatable) item).getState();
            boolean lockCell = (itemsState != State.APPROVED && citiesRepository
                .getEmployeesWorkspace().stream()
                .noneMatch(city -> item.getCityID() == (((CityDTO) city).getID())));
            setDisable(lockCell);
            setOpacity(lockCell ? 0.3 : 1);
            setText(item.render());
          } else {
            setText("");
          }
        }
      };
    }*/
  }

}
