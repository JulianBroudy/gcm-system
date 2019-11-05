package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.util.Objects;

/**
 * Decorator class for the Coordinate class because it is final and cannot be extended
 * TODO provide a summary to CoordinatesDTO class!!!!!
 * <p>
 * Created on the 9th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CoordinatesDTO extends AbstractDTO {

  private int ID;
  private double xCoordinate;
  private double yCoordinate;

  public CoordinatesDTO() {
    super(DTOType.COORDINATES);
    this.ID = -1;
    this.xCoordinate = -1;
    this.yCoordinate = -1;
  }


  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */

  public int getID() {
    return ID;
  }

  public void setID(int ID) {
    this.ID = ID;
  }

  public double getXCoordinate() {
    return xCoordinate;
  }

  public void setXCoordinate(double xCoordinate) {
    this.xCoordinate = xCoordinate;
  }

  public double getYCoordinate() {
    return yCoordinate;
  }

  public void setYCoordinate(double yCoordinate) {
    this.yCoordinate = yCoordinate;
  }


  @Override
  public String render() {
    return this.getXCoordinate() + this.getYCoordinate() + "";
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CoordinatesDTO)) {
      return false;
    }
    CoordinatesDTO that = (CoordinatesDTO) o;
    return getID() == that.getID() && Double.compare(that.xCoordinate, xCoordinate) == 0
        && Double.compare(that.yCoordinate, yCoordinate) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getID(), xCoordinate, yCoordinate);
  }

  @Override
  public String toString() {
    return "Coordinates [xCoordinate=" + xCoordinate + ", yCoordinate=" + yCoordinate + "]";
  }

}
