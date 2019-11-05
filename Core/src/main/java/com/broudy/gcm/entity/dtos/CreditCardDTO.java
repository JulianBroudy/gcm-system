package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.time.LocalDate;
import java.util.Objects;

/**
 * TODO provide a summary to CreditCardDTO class!!!!!
 * <p>
 * Created on the 7th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CreditCardDTO extends AbstractDTO {

  private static final long serialVersionUID = 7757049214079233619L;

  private int ID;
  private String accountsEmail;
  private String cardholdersFullName;
  private String cardholdersID;
  private String ccNumber;
  private LocalDate expirationDate;
  private String cvv;

  public CreditCardDTO() {
    super(DTOType.CREDIT_CARD);
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
   * Gets the ID.
   *
   * @return the value of the ID.
   */
  public int getID() {
    return ID;
  }

  /**
   * Sets the ID.
   *
   * @param ID is the $field.typeName's new value.
   */
  public void setID(int ID) {
    this.ID = ID;
  }

  public String getAccountsEmail() {
    return accountsEmail;
  }

  public void setAccountsEmail(String accountsEmail) {
    this.accountsEmail = accountsEmail;
  }

  public String getCardholdersFullName() {
    return cardholdersFullName;
  }

  public void setCardholdersFullName(String cardholdersFullName) {
    this.cardholdersFullName = cardholdersFullName;
  }

  public String getCardholdersID() {
    return cardholdersID;
  }

  public void setCardholdersID(String cardholdersID) {
    this.cardholdersID = cardholdersID;
  }

  public String getCcNumber() {
    return ccNumber;
  }

  public void setCcNumber(String ccNumber) {
    this.ccNumber = ccNumber;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  @Override
  public String render() {
    return this.getCcNumber();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CreditCardDTO)) {
      return false;
    }
    CreditCardDTO that = (CreditCardDTO) o;
    return getAccountsEmail().equals(that.getAccountsEmail()) && getCardholdersFullName()
        .equals(that.getCardholdersFullName()) && getCardholdersID().equals(that.getCardholdersID())
        && getCcNumber().equals(that.getCcNumber()) && getExpirationDate()
        .equals(that.getExpirationDate()) && getCvv().equals(that.getCvv()) /*&&
        getRequest() == that.getRequest()*/;
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(getAccountsEmail(), getCardholdersFullName(), getCardholdersID(), getCcNumber(),
            getExpirationDate(), getCvv()/*, getRequest()*/);
  }

  @Override
  public String toString() {
    return "CreditCardDTO{" + "accountsEmail='" + accountsEmail + '\'' + ", cardholdersFullName='"
        + cardholdersFullName + '\'' + ", cardholdersID='" + cardholdersID + '\'' + ", ccNumber='"
        + ccNumber + '\'' + ", expirationDate=" + expirationDate + ", cvv='" + cvv + '\'' + "} "
        + super.toString();
  }
}
