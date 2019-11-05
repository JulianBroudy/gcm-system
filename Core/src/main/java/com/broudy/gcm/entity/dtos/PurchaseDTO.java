package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * TODO provide a summary to PurchaseDTO class!!!!!
 * <p>
 * Created on the 7th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */

public class PurchaseDTO extends AbstractDTO {

  private int ID;
  private Date purchaseDate;
  private int cityID;
  private String customerEmail;
  private boolean extended;
  private Date expirationDate;

  public PurchaseDTO() {
    super(DTOType.PURCHASE);
  }

  public void deepCopyTo(PurchaseDTO purchaseToCopyTo) {
    purchaseToCopyTo.setID(getID());
    purchaseToCopyTo.setCustomerEmail(getCustomerEmail());
    purchaseToCopyTo.setCityID(getCityID());
    purchaseToCopyTo.setPurchaseDate(getPurchaseDate());
    purchaseToCopyTo.setExpirationDate(getExpirationDate());
    purchaseToCopyTo.setExtended(isExtended());
  }

  public PurchaseDTO getDeepCopy() {
    final PurchaseDTO deepCopyOfPurchase = new PurchaseDTO();
    deepCopyTo(deepCopyOfPurchase);
    return deepCopyOfPurchase;
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
   * @return ID's value.
   */
  public int getID() {
    return ID;
  }

  /**
   * Sets the ID.
   *
   * @param ID is the ID's new value.
   */
  public void setID(int ID) {
    this.ID = ID;
  }

  /**
   * Gets the purchaseDate.
   *
   * @return purchaseDate's value.
   */
  public Date getPurchaseDate() {
    return purchaseDate;
  }

  /**
   * Sets the purchaseDate.
   *
   * @param purchaseDate is the purchaseDate's new value.
   */
  public void setPurchaseDate(Date purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  /**
   * Gets the cityID.
   *
   * @return cityID's value.
   */
  public int getCityID() {
    return cityID;
  }

  /**
   * Sets the cityID.
   *
   * @param cityID is the cityID's new value.
   */
  public void setCityID(int cityID) {
    this.cityID = cityID;
  }

  /**
   * Gets the customerEmail.
   *
   * @return customerEmail's value.
   */
  public String getCustomerEmail() {
    return customerEmail;
  }

  /**
   * Sets the customerEmail.
   *
   * @param customerEmail is the customerEmail's new value.
   */
  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }

  /**
   * Gets the extended.
   *
   * @return extended's value.
   */
  public boolean isExtended() {
    return extended;
  }

  /**
   * Sets the extended.
   *
   * @param extended is the extended's new value.
   */
  public void setExtended(boolean extended) {
    this.extended = extended;
  }

  /**
   * Gets the expirationDate.
   *
   * @return expirationDate's value.
   */
  public Date getExpirationDate() {
    return expirationDate;
  }

  /**
   * Sets the expirationDate.
   *
   * @param expirationDate is the expirationDate's new value.
   */
  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PurchaseDTO)) {
      return false;
    }
    PurchaseDTO that = (PurchaseDTO) o;
    return getID() == that.getID() && Objects.equals(getExpirationDate(), that.getExpirationDate());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getID(), getExpirationDate());
  }
}
