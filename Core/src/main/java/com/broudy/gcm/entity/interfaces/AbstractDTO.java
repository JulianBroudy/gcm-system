package com.broudy.gcm.entity.interfaces;


import com.broudy.gcm.entity.DTOType;

/**
 * This is a abstract class for all Data Transfer Objects. Every object that might be transferred
 * between client and server in any given point should extend this class. Enables using
 * <code>AbstractDAO</code>.
 * <p>
 * Created on the 3rd of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public abstract class AbstractDTO<DTO_Type extends DTOType> extends Renderable {

  private final DTOType DTO_TYPE;

  public AbstractDTO(DTO_Type dtoType) {
    super();
    this.DTO_TYPE = dtoType;
  }

  /**
   * Public getter to simplify differentiation between DTOs.
   *
   * @return the DTO_TYPE
   */
  public DTOType getDTOType() {
    return DTO_TYPE;
  }

}
