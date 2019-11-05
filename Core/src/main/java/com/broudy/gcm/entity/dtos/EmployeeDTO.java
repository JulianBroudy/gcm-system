package com.broudy.gcm.entity.dtos;

/**
 * This class is created in order to make future different roles' hierarchies easy. Any changes to
 * attributes of ALL employees should be made here.
 * <p>
 * TODO provide a summary to EmployeeDTO class!!!!!
 * <p>
 * Created on the 7th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class EmployeeDTO extends UserDTO {

  private int employeeID;

  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */

  /**
   * Gets the employeeID
   *
   * @return the value of the employeeID.
   */
  public int getEmployeeID() {
    return employeeID;
  }

  /**
   * Sets the employeeID
   *
   * @param employeeID's new value.
   */
  public void setEmployeeID(int employeeID) {
    this.employeeID = employeeID;
  }

}
