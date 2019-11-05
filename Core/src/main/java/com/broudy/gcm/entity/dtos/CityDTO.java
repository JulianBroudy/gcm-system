package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.broudy.gcm.entity.interfaces.IStatable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * TODO provide a summary to CityDTO class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CityDTO extends AbstractDTO implements IStatable {

  private static final long serialVersionUID = 7610109282497360462L;

  private int ID;
  private String name;
  private double collectionVersion;
  private int numberOfMaps;
  private int numberOfSites;
  private int numberOfDownloads;
  private Date releaseDate;
  private String description;

  private List<String> listOfSites;

  private State state;
  private int previousID;

  public CityDTO() {
    super(DTOType.CITY);
    this.ID = 0;
    this.name = "";
    this.collectionVersion = 1.0;
    this.numberOfMaps = -1;
    this.numberOfSites = -1;
    this.numberOfDownloads = 0;
    this.releaseDate = null;
    this.description = "";

    this.state = State.NEW;
    this.previousID = 0;
  }

  public void deepCopyTo(CityDTO cityToCopyTo) {
    cityToCopyTo.setID(getID());
    cityToCopyTo.setName(getName());
    cityToCopyTo.setCollectionVersion(getCollectionVersion());
    cityToCopyTo.setNumberOfMaps(getNumberOfMaps());
    cityToCopyTo.setNumberOfSites(getNumberOfSites());
    cityToCopyTo.setNumberOfDownloads(getNumberOfDownloads());
    cityToCopyTo.setReleaseDate(getReleaseDate());
    cityToCopyTo.setDescription(getDescription());
    // Deep copy of coordinates is possible because of the way the getters & setters are implemented.
    /*cityToCopyTo.setCoordinates(getCoordinates());*/
    cityToCopyTo.setState(getState());
    cityToCopyTo.setPreviousID(getPreviousID());
  }

  public CityDTO getDeepCopy() {
    final CityDTO deepCopyOfCity = new CityDTO();
    deepCopyTo(deepCopyOfCity);
    return deepCopyOfCity;
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
   * Gets the ID
   *
   * @return the value of the ID.
   */
  public int getID() {
    return ID;
  }

  /**
   * Sets the ID
   *
   * @param ID's new value.
   */
  public void setID(int ID) {
    this.ID = ID;
  }

  /**
   * Gets the name
   *
   * @return the value of the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name
   *
   * @param name's new value.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the collectionVersion
   *
   * @return the value of the collectionVersion.
   */
  public double getCollectionVersion() {
    return collectionVersion;
  }

  /**
   * Sets the collectionVersion
   *
   * @param collectionVersion's new value.
   */
  public void setCollectionVersion(double collectionVersion) {
    this.collectionVersion = collectionVersion;
  }

  /**
   * Gets the numberOfMaps
   *
   * @return the value of the numberOfMaps.
   */
  public int getNumberOfMaps() {
    return numberOfMaps;
  }

  /**
   * Sets the numberOfMaps
   *
   * @param numberOfMaps's new value.
   */
  public void setNumberOfMaps(int numberOfMaps) {
    this.numberOfMaps = numberOfMaps;
  }

  /**
   * Gets the numberOfSites.
   *
   * @return the value of the numberOfSites.
   */
  public int getNumberOfSites() {
    return numberOfSites;
  }

  /**
   * Sets the numberOfSites.
   *
   * @param numberOfSites is the numberOfSites's new value.
   */
  public void setNumberOfSites(int numberOfSites) {
    this.numberOfSites = numberOfSites;
  }

  /**
   * Gets the numberOfDownloads
   *
   * @return the value of the numberOfDownloads.
   */
  public int getNumberOfDownloads() {
    return numberOfDownloads;
  }

  /**
   * Sets the numberOfDownloads
   *
   * @param numberOfDownloads's new value.
   */
  public void setNumberOfDownloads(int numberOfDownloads) {
    this.numberOfDownloads = numberOfDownloads;
  }

  /**
   * Gets the releaseDate
   *
   * @return the value of the releaseDate.
   */
  public Date getReleaseDate() {
    return releaseDate;
  }

  /**
   * Sets the releaseDate
   *
   * @param releaseDate's new value.
   */
  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Gets the description
   *
   * @return the value of the description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description
   *
   * @param description's new value.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the listOfSites.
   *
   * @return listOfSites's value.
   */
  public List<String> getListOfSites() {
    return listOfSites;
  }

  /**
   * Sets the listOfSites.
   *
   * @param listOfSites is the listOfSites's new value.
   */
  public void setListOfSites(List<String> listOfSites) {
    this.listOfSites = listOfSites;
  }

  /**
   * Gets the state.
   *
   * @return the value of the state.
   */
  @Override
  public State getState() {
    return state;
  }

  /**
   * Sets the state.
   *
   * @param state is the state's new value.
   */
  @Override
  public void setState(State state) {
    this.state = state;
  }

  /**
   * Gets the previous ID.
   *
   * @return the value of the previousID.
   */
  @Override
  public int getPreviousID() {
    return previousID;
  }

  /**
   * Sets the previous ID.
   *
   * @param previousID is the previousID's new value.
   */
  @Override
  public void setPreviousID(int previousID) {
    this.previousID = previousID;
  }

  @Override
  public String render() {
    return getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CityDTO)) {
      return false;
    }
    CityDTO cityDTO = (CityDTO) o;
    return getID() == cityDTO.getID();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getID());
  }

  @Override
  public String toString() {
    return "CityDTO{" + "ID=" + ID + ", name='" + name + '\'' + ", collectionVersion="
        + collectionVersion + ", numberOfMaps=" + numberOfMaps + ", numberOfDownloads="
        + numberOfDownloads + ", releaseDate=" + releaseDate + ", description='" + description
        + '\'' + "} " + ", state=" + state + super.toString();
  }

}
