package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.interfaces.IStatable;
import java.util.Objects;

/**
 * TODO provide a summary to SiteDTO class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SiteDTO extends CoordinatizedDTO implements IStatable {

  private static final long serialVersionUID = -6034279098009927603L;

  private int ID;
  private String name;
  private int cityID;
  private String description;
  private String recommendedVisitDuration;
  private SiteCategory category;
  private boolean accessible;

  private State state;
  private int previousID;

  public SiteDTO() {
    super(DTOType.SITE);
    this.ID = 0;
    this.name = "";
    this.description = "";
    this.recommendedVisitDuration = "";
    this.category = new SiteCategory();
    this.accessible = false;

    this.state = State.NEW;
    this.previousID = 0;
  }

  public void deepCopyTo(SiteDTO siteToCopyTo) {
    siteToCopyTo.setID(getID());
    siteToCopyTo.setName(getName());
    siteToCopyTo.setCityID(getCityID());
    siteToCopyTo.setDescription(getDescription());
    siteToCopyTo.setRecommendedVisitDuration(getRecommendedVisitDuration());
    siteToCopyTo.setCategory(getCategory());
    siteToCopyTo.setAccessible(isAccessible());
    // Deep copy of coordinates is possible because of the way the getters & setters are implemented.
    siteToCopyTo.setCoordinates(getCoordinates());
    siteToCopyTo.setState(getState());
    siteToCopyTo.setPreviousID(getPreviousID());
    // siteToCopyTo.setRequest(getRequest());
  }

  public SiteDTO getDeepCopy() {
    final SiteDTO deepCopyOfSite = new SiteDTO();
    deepCopyTo(deepCopyOfSite);
    return deepCopyOfSite;
  }

  @Override
  public String render() {
    return getName();
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
   * Gets the cityID.
   *
   * @return the value of the cityID.
   */
  public int getCityID() {
    return cityID;
  }

  /**
   * Sets the cityID.
   *
   * @param cityID is the $field.typeName's new value.
   */
  public void setCityID(int cityID) {
    this.cityID = cityID;
  }

  /**
   * Gets the description.
   *
   * @return the value of the description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description is the String's new value.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the recommendedVisitDuration.
   *
   * @return the value of the recommendedVisitDuration.
   */
  public String getRecommendedVisitDuration() {
    return recommendedVisitDuration;
  }

  /**
   * Sets the recommendedVisitDuration.
   *
   * @param recommendedVisitDuration is the String's new value.
   */
  public void setRecommendedVisitDuration(String recommendedVisitDuration) {
    this.recommendedVisitDuration = recommendedVisitDuration;
  }

  /**
   * Gets the category.
   *
   * @return the value of the category.
   */
  public SiteCategory getCategory() {
    return category;
  }

  /**
   * Sets the category.
   *
   * @param category is the SiteCategory's new value.
   */
  public void setCategory(SiteCategory category) {
    this.category = category;
  }

  /**
   * Gets the accessible.
   *
   * @return the value of the accessible.
   */
  public boolean isAccessible() {
    return accessible;
  }

  /**
   * Sets the accessible.
   *
   * @param accessible is the $field.typeName's new value.
   */
  public void setAccessible(boolean accessible) {
    this.accessible = accessible;
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
   * @param state is the State's new value.
   */
  @Override
  public void setState(State state) {
    this.state = state;
  }

  /**
   * Gets the previousID.
   *
   * @return the value of the previousID.
   */
  @Override
  public int getPreviousID() {
    return previousID;
  }

  /**
   * Sets the previousID.
   *
   * @param previousID is the $field.typeName's new value.
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
    if (!(o instanceof SiteDTO)) {
      return false;
    }
    SiteDTO siteDTO = (SiteDTO) o;
    return getID() == siteDTO.getID() && getCoordinatesDTO().equals(siteDTO.getCoordinatesDTO());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getID());
  }

  @Override
  public String toString() {
    return "SiteDTO{" + "ID=" + ID + ", name='" + name + '\'' + ", cityID=" + cityID
        + ", description='" + description + '\'' + ", recommendedVisitDuration='"
        + recommendedVisitDuration + '\'' + ", category=" + category + ", accessible=" + accessible
        + ", state=" + state + ", previousID=" + previousID + "} " + ", state=" + state + super
        .toString();
  }

}
