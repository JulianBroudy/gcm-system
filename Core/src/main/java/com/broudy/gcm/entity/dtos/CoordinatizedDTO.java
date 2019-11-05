package com.broudy.gcm.entity.dtos;

import com.broudy.gcm.entity.DTOType;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.sothawo.mapjfx.Coordinate;
import java.util.Objects;

/**
 * Convenience class.
 * TODO provide a summary to CoordinatizedDTO class!!!!!
 * <p>
 * Created on the 17th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public abstract class CoordinatizedDTO extends AbstractDTO {

  private static final long serialVersionUID = -1964067080906943978L;

  private CoordinatesDTO coordinates = new CoordinatesDTO();
  private double zoom;

  public CoordinatizedDTO(DTOType DTO_TYPE) {
    super(DTO_TYPE);
  }


  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */


  public Coordinate getCoordinates() {
    return new Coordinate(coordinates.getXCoordinate(), coordinates.getYCoordinate());
  }

  public void setCoordinates(Coordinate coordinates) {
    this.coordinates.setXCoordinate(coordinates.getLatitude());
    this.coordinates.setYCoordinate(coordinates.getLongitude());
  }

  public CoordinatesDTO getCoordinatesDTO() {
    return coordinates;
  }

  public void setCoordinatesDTO(CoordinatesDTO coordinates) {
    this.coordinates = coordinates;
  }


  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CoordinatizedDTO)) {
      return false;
    }
    CoordinatizedDTO that = (CoordinatizedDTO) o;
    return getCoordinates().equals(that.getCoordinates());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCoordinates());
  }
}
