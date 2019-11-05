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
public class TourDTO extends AbstractDTO implements IStatable {

  private int ID;
  private int mapID;

  private List<Integer> sites;

  private State state;
  private int previousID;

  public TourDTO() {
    super(DTOType.MAP);
    this.ID = -1;
    this.mapID = -1;

    this.sites = new ArrayList<>();

    this.state = State.LOCKED;
    this.previousID = -1;
  }

  public TourDTO getDeepCopy() {
    final TourDTO tour = new TourDTO();
    tour.setID(getID());
    tour.setMapID(getMapID());
    tour.setSites(getSites());
    tour.setState(getState());
    tour.setPreviousID(getPreviousID());
    return tour;
  }

  /**
   * @return the default String to be rendered.
   */
  @Override
  public String render() {
    return "Tour Number " + getID();
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
   * Gets the city ID.
   *
   * @return cityID's value.
   */
  public int getMapID() {
    return mapID;
  }

  /**
   * Sets the city ID.
   *
   * @param mapID is the cityID's new value.
   */
  public void setMapID(int mapID) {
    this.mapID = mapID;
  }

  /**
   * Gets the sites.
   *
   * @return sites's value.
   */
  public List<Integer> getSites() {
    return sites;
  }

  /**
   * Sets the sites.
   *
   * @param sites is the sites's new value.
   */
  public void setSites(List<Integer> sites) {
    this.sites = sites;
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
    if (!(o instanceof TourDTO)) {
      return false;
    }
    TourDTO tourDTO = (TourDTO) o;
    return getID() == tourDTO.getID() && getMapID() == tourDTO.getMapID()
        && getPreviousID() == tourDTO.getPreviousID() /*&& Objects
        .equals(getSites(), tourDTO.getSites())*/ && getState() == tourDTO.getState();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getID(), getMapID(), getState(), getPreviousID());
  }

  @Override
  public String toString() {
    return "TourDTO{" + "ID=" + ID + ", cityID=" + mapID + ", sites=" + sites + ", state=" + state
        + ", previousID=" + previousID + "} " + super.toString();
  }

}
