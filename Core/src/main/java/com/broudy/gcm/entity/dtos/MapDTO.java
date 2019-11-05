package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.broudy.gcm.entity.interfaces.IStatable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO provide a summary to MapDTO class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class MapDTO extends AbstractDTO implements IStatable {

  private int ID;
  private String name;
  private int cityID;
  private double version;

  private List<Integer> sites;
  private List<Integer> tours;

  private State state;
  private int previousID;

  public MapDTO() {
    super(DTOType.MAP);
    this.ID = -1;
    this.name = "";
    this.cityID = -1;
    this.version = 1.0;

    this.sites = new ArrayList<>();
    this.tours = new ArrayList<>();

    this.state = State.LOCKED;
    this.previousID = -1;
  }

  public MapDTO getDeepCopy() {
    final MapDTO map = new MapDTO();
    map.setID(getID());
    map.setCityID(getCityID());
    map.setVersion(getVersion());
    map.setName(getName());
    map.setSites(getSites());
    map.setTours(getTours());
    map.setState(getState());
    map.setPreviousID(getPreviousID());
    return map;
  }

  /**
   * @return the default String to be rendered.
   */
  @Override
  public String render() {
    return String.valueOf(getName());
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
   * Gets the name.
   *
   * @return the value of the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name is the String's new value.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the cityID
   *
   * @return the value of the cityID.
   */
  public int getCityID() {
    return cityID;
  }

  /**
   * Sets the cityID
   *
   * @param cityID's new value.
   */
  public void setCityID(int cityID) {
    this.cityID = cityID;
  }

  /**
   * Gets the version
   *
   * @return the value of the version.
   */
  public double getVersion() {
    return version;
  }

  /**
   * Sets the version
   *
   * @param version's new value.
   */
  public void setVersion(double version) {
    this.version = version;
  }

  /**
   * Gets the sites.
   *
   * @return the value of the sites.
   */
  public List<Integer> getSites() {
    return sites;
  }

  /**
   * Sets the sites.
   *
   * @param sites is the Integer>'s new value.
   */
  public void setSites(List<Integer> sites) {
    this.sites = sites;
  }

  /**
   * Gets the tours.
   *
   * @return the value of the tours.
   */
  public List<Integer> getTours() {
    return tours;
  }

  /**
   * Sets the tours.
   *
   * @param tours is the Integer>'s new value.
   */
  public void setTours(List<Integer> tours) {
    this.tours = tours;
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
   * Sets the state
   *
   * @param state's new value.
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MapDTO)) {
      return false;
    }
    MapDTO mapDTO = (MapDTO) o;
    return getID() == mapDTO.getID();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getID());
  }

  @Override
  public String toString() {
    return "MapDTO{" + "ID=" + ID + ", name='" + name + '\'' + ", cityID=" + cityID + ", version="
        + version + ", state=" + state + ", previousID=" + previousID + "} " + ", state=" + state
        + super.toString();
  }

}
