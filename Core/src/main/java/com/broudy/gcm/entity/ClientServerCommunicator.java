package com.broudy.gcm.entity;

import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * An abstract class which provides the basic functionality needed for client-server communication.
 * <p>
 * Created on the 25th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public abstract class ClientServerCommunicator<DTO extends AbstractDTO> implements Serializable {

  private final Class<DTO> dtoClass;

  DTO theDTO;
  List<DTO> listOfDTOs;
  private List<?> extraParameters;


  ClientServerCommunicator(Class<DTO> dtoClass) {
    this.dtoClass = dtoClass;
    this.theDTO = null;
    this.listOfDTOs = new ArrayList<>();
    this.extraParameters = new ArrayList<>();
  }


  /**
   * Gets the theDTO.
   *
   * @return theDTO's value.
   */
  public DTO getTheDTO() {
    return theDTO;
  }

  /**
   * Sets the theDTO.
   *
   * @param theDTO is the theDTO's new value.
   */
  public void setTheDTO(DTO theDTO) {
    this.theDTO = theDTO;
  }

  /**
   * Gets the listOfDTOs.
   *
   * @return listOfDTOs's value.
   */
  public List<DTO> getListOfDTOs() {
    return listOfDTOs;
  }

  /**
   * Sets the listOfDTOs.
   *
   * @param listOfDTOs is the listOfDTOs's new value.
   */
  public void setListOfDTOs(List<DTO> listOfDTOs) {
    this.listOfDTOs = listOfDTOs;
  }

  /**
   * Gets the extraParameters.
   *
   * @return extraParameters's value.
   */
  public List<?> getExtraParameters() {
    return Objects.requireNonNull(extraParameters);
  }

  public <COMMUNICATOR extends ClientServerCommunicator<DTO>> COMMUNICATOR setExtraParameters(
      Object... objects) {
    if (objects.length > 0) {
      extraParameters = new ArrayList<>(Arrays.asList(objects));
    }
    return (COMMUNICATOR) this;
  }

  public <COMMUNICATOR extends ClientServerCommunicator<DTO>> COMMUNICATOR setExtraParameters(
      List<?> list) {
    extraParameters = list;
    return (COMMUNICATOR) this;
  }

  /**
   * Gets the extraParameters.
   *
   * @return extraParameters's value.
   */
  public <E> E getExtraParameterAt(int statementIndex) {
    if (Objects.requireNonNull(extraParameters).isEmpty()) {
      return null;
    } else {
      return (E) extraParameters.get(statementIndex - 1);
    }
  }

  @Override
  public String toString() {
    return "ClientServerCommunicator{" + "theDTO=" + theDTO + '}';
  }

  /**
   * Gets the dtoClass.
   *
   * @return dtoClass's value.
   */
  public Class<DTO> getDtoClass() {
    return dtoClass;
  }

  interface TransferableMessage extends Serializable {

  }

}
