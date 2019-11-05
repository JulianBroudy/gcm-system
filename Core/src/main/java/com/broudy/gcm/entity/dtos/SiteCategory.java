package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.util.Objects;

/**
 * TODO provide a summary to SiteCategory class!!!!!
 * <p>
 * Created on the 9th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SiteCategory extends AbstractDTO {

  private static final long serialVersionUID = 5823914198625113447L;

  private int ID;
  private String name;

  public SiteCategory() {
    super(DTOType.SITE_CATEGORY);
    this.ID = 0;
    this.name = "";
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

  @Override
  public String render() {
    return getName();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SiteCategory)) {
      return false;
    }
    SiteCategory that = (SiteCategory) o;
    return getID() == that.getID() && getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getID(), getName());
  }

  @Override
  public String toString() {
    return "SiteCategory{" + "ID=" + ID + ", name='" + name + '\'' + "} " + super.toString();
  }
}
