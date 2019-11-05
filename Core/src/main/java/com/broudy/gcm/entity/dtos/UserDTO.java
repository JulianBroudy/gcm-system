package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.UserClassification;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.util.Objects;

/**
 * <p>
 * TODO provide a summary to UserDTO class!!!!!
 * <p>
 * Created on the 7th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class UserDTO extends AbstractDTO {

  private static final long serialVersionUID = 7586266655080195633L;

  private String email;
  private String password;

  private String firstName;
  private String lastName;
  private String username;
  private String phoneNumber;

  private UserClassification classification;
  private boolean online;

  public UserDTO() {
    super(DTOType.USER);
  }


  public void deepCopyTo(UserDTO userToCopyTo) {
    userToCopyTo.setEmail(getEmail());
    userToCopyTo.setPassword(getPassword());
    userToCopyTo.setFirstName(getFirstName());
    userToCopyTo.setLastName(getLastName());
    userToCopyTo.setUsername(getUsername());
    userToCopyTo.setPhoneNumber(getPhoneNumber());
    userToCopyTo.setClassification(getClassification());
    userToCopyTo.setOnline(isOnline());

    // userToCopyTo.setRequest(getRequest());
  }

  public UserDTO getDeepCopy() {
    final UserDTO deepCopyOfUser = new UserDTO();
    deepCopyTo(deepCopyOfUser);
    return deepCopyOfUser;
  }

  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */

  /**
   * Gets the email
   *
   * @return the value of the email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email
   *
   * @param email's new value.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the password
   *
   * @return the value of the password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password
   *
   * @param password's new value.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the firstName
   *
   * @return the value of the firstName.
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the firstName
   *
   * @param firstName's new value.
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the lastName
   *
   * @return the value of the lastName.
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the lastName
   *
   * @param lastName's new value.
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the username
   *
   * @return the value of the username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username
   *
   * @param username's new value.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the phoneNumber
   *
   * @return the value of the phoneNumber.
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the phoneNumber
   *
   * @param phoneNumber's new value.
   */
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * Gets the classification
   *
   * @return the value of the classification.
   */
  public UserClassification getClassification() {
    return classification;
  }

  /**
   * Sets the classification
   *
   * @param classification's new value.
   */
  public void setClassification(UserClassification classification) {
    this.classification = classification;
  }

  /**
   * Gets the online
   *
   * @return the value of the online.
   */
  public boolean isOnline() {
    return online;
  }

  /**
   * Sets the online
   *
   * @param online's new value.
   */
  public void setOnline(boolean online) {
    this.online = online;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserDTO)) {
      return false;
    }
    UserDTO userDTO = (UserDTO) o;
    return isOnline() == userDTO.isOnline() && getEmail().equals(userDTO.getEmail())
        && getPassword().equals(userDTO.getPassword()) && Objects
        .equals(getFirstName(), userDTO.getFirstName()) && Objects
        .equals(getLastName(), userDTO.getLastName()) && Objects
        .equals(getUsername(), userDTO.getUsername()) && Objects
        .equals(getPhoneNumber(), userDTO.getPhoneNumber()) && getClassification() == userDTO
        .getClassification() /*&&
        getRequest().equals(userDTO.getRequest())*/;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getEmail(), getPassword(), getFirstName(), getLastName(), getUsername(),
        getPhoneNumber(), getClassification(), isOnline()/*, getRequest()*/);
  }

  @Override
  public String toString() {
    return "UserDTO{" + "email='" + email + '\'' + ", password='" + password + '\''
        + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", username='"
        + username + '\'' + ", phoneNumber='" + phoneNumber + '\'' + ", classification="
        + classification + ", online=" + online + "} " + super.toString();
  }

  @Override
  public String render() {
    return this.getFirstName() + this.getLastName();
  }


}
