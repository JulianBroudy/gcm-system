package com.broudy.gcm.entity.interfaces;

import java.io.Serializable;

/**
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public abstract class Renderable implements Serializable {

  protected String textToBeRendered;

  protected Renderable() {

  }

  /**
   * Default constructor.
   */
  public Renderable(String textToBeRendered) {
    super();
    this.textToBeRendered = textToBeRendered;
  }


  /**
   * @return the default String to be rendered.
   */
  public String render() {
    return this.textToBeRendered;
  }
}
