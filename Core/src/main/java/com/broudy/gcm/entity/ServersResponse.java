package com.broudy.gcm.entity;

import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A "messenger" class providing needed functionality to send responses to client.
 * <p>
 * Created on the 19th of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ServersResponse<DTO extends AbstractDTO> extends
    ClientServerCommunicator<DTO> implements Serializable {

  private final static Logger logger = LogManager.getLogger(ServersResponse.class);

  private Response response;

  /**
   * Private constructor; use the factory method instead
   */
  private ServersResponse(Class<DTO> dtoClass) {
    super(dtoClass);
    this.response = Response.UNASSIGNED;
  }

  /**
   * Factory method
   */
  public static <DTO extends AbstractDTO> ServersResponse<DTO> of(Class<DTO> dtoClass) {
    return new ServersResponse<>(dtoClass);
  }
//
//  /**
//   * Gets the theDTO.
//   *
//   * @return theDTO's value.
//   */
//  public DTO getTheDTO() {
//    if (theDTO == null) {
//      throw new NullPointerException("The " + getResponsesDTOType() + " is null!");
//    }
//    return theDTO;
//  }
//
//  /**
//   * Gets the listOfDTOs.
//   *
//   * @return listOfDTOs's value.
//   */
//  public List<DTO> getListOfDTOs() {
//    if (listOfDTOs == null) {
//      throw new NullPointerException("The listOf" + getResponsesDTOType() + "s is null!");
//    }
//    return listOfDTOs;
//  }

  /**
   * Gets the response.
   *
   * @return response's value.
   */
  public Response getResponse() {
    return response;
  }

  /**
   * Sets the response.
   *
   * @param response is the response's new value.
   */
  public void setResponse(Response response) {
    this.response = response;
  }

  /**
   * Gets the response's {@link DTOType}.
   *
   * @return response's DTOType value.
   */
  public DTOType getResponsesDTOType() {
    return DTOType.valueOf(
        getDtoClass().getSimpleName().substring(0, getDtoClass().getSimpleName().length() - 3)
            .toUpperCase());
  }


  public enum Response implements TransferableMessage, Serializable {
    INITIAL_STATE, USER_AUTHENTICATION_FAILED, USER_SIGN_IN, USER_SIGN_UP_SUCCESSFUL, USER_SIGN_UP_FAIL, CC_NEW_CUSTOMER_SUCCESSFUL, CC_NEW_CUSTOMER_FAILED, SUCCESSFUL_ONESHOTDEAL_CREATION, FAILED_ONESHOTDEAL_CREATION, SUCCESSFUL_SUBSCRIPTION_CREATION, FAILED_SUBSCRIPTION_CREATION, SUBSCRIPTION_NOT_EXTENDED, SUBSCRIPTION_SUCCESSFULLY_EXTENDED, USER_IS_ALREADY_ONLINE, USER_SIGN_OUT_SUCCESSFUL, USER_SIGN_IN_SUCCESSFUL, USER_STATUS_WAS_SET_TO_OFFLINE, CITY_SAVED_AND_LOCKED, MAP_SAVED, MAP_SITE_SAVED, SITE_SAVED_AND_LOCKED, MAP_FETCHED_ONE, MAP_SITES_ADDED, SITE_SAVED, COORDINATES_SAVED, COORDINATES_SAVED_AND_LOCKED, SITE_DELETED, MAP_SITES_REMOVED, USER_FORCE_SIGN_OUT, CITY_SAVED, EMPLOYEE_WORKSPACE_FETCHED_ALL, CITY_APPROVAL_REQUEST_CANCELLED, CITY_APPROVAL_REQUESTED, CITY_DELETED, SITE_OF_CITY_DELETED, EMPLOYEE_CITY_APPROVAL_REQUEST_FETCHED_ALL, UNASSIGNED, USER_EMPLOYEE_ID_FETCHED, CITY_FETCHED_ONE, EMPLOYEE_CITY_FETCHED_ALL, CITY_FETCHED_ALL, CITY_MAPS_COUNT_FETCHED, CITY_UNLOCKED_FROM_EMPLOYEE, EXIT_SYSTEM, MAP_OF_CITY_FETCHED_ALL, EMPLOYEE_MAP_OF_CITY_FETCHED_ALL, MAP_FETCHED_SITES_LIST, MAP_SAVED_ATTACHED_MAPS, MAP_DETACHED_FETCHED_ALL, MAP_ATTACHED_MAPS_TO_CITY, MAP_DETACHED_MAPS_FROM_CITY, COORDINATES_FETCHED_ONE, SITE_OF_CITY_FETCHED_ALL, TOUR_OF_MAP_FETCHED_ALL, PURCHASE_FETCHED_ALL_ACTIVE, PURCHASE_SUBSCRIPTION_EXTENDED, PURCHASE_SUCCESSFUL_NEW_SUBSCRIPTION, CITY_REJECTED_APPROVAL_REQUEST, CITY_APPROVED_APPROVAL_REQUEST, CITY_READY_FOR_EDITING, USER_SIGN_EMPLOYEE_IN
  }

}
