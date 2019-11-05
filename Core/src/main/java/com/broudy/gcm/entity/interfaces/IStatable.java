package com.broudy.gcm.entity.interfaces;

import com.broudy.gcm.entity.State;

/**
 * This interface provides an abstraction level for controllers to get this information without
 * knowing Rendered DTO's specific type.
 * <p>
 * Created on the 22nd of July, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public interface IStatable {

  /**
   * Gets the state.
   *
   * @return the value of the state.
   */
  State getState();

  /**
   * Sets the state.
   *
   * @param state is the state's new value.
   */
  void setState(State state);

  /**
   * Gets the previous ID.
   *
   * @return the value of the previousID.
   */
  int getPreviousID();

  /**
   * Sets the previous ID.
   *
   * @param previousID is the previousID's new value.
   */
  void setPreviousID(int previousID);
}
