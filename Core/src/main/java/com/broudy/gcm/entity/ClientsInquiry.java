package com.broudy.gcm.entity;

import com.broudy.gcm.entity.interfaces.AbstractDTO;
import java.io.Serializable;
import javax.naming.OperationNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A "messenger" class providing needed functionality to send requests to server.
 * <p>
 * Created on the 19th of August, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ClientsInquiry<DTO extends AbstractDTO> extends
    ClientServerCommunicator<DTO> implements Serializable {

  private final static Logger logger = LogManager.getLogger(ClientsInquiry.class);
  private Inquiry inquiry;

  /**
   * Private constructor; use the factory method instead
   */
  private ClientsInquiry(Class<DTO> dtoClass) {
    super(dtoClass);
    this.inquiry = Inquiry.UNASSIGNED;
  }

  /**
   * Factory method
   */
  public static <DTO extends AbstractDTO> ClientsInquiry<DTO> of(Class<DTO> dtoClass) {
    return new ClientsInquiry<DTO>(dtoClass);
  }

//
//  /**
//   * Gets the theDTO.
//   *
//   * @return theDTO's value.
//   */
//  @Override
//  public DTO getTheDTO() {
//    if (theDTO == null) {
//      throw new NullPointerException("The " + getInquirysDTOType() + " is null!");
//    }
//    return theDTO;
//  }
//
//  /**
//   * Gets the listOfDTOs.
//   *
//   * @return listOfDTOs's value.
//   */
//  @Override
//  public List<DTO> getListOfDTOs() {
//    if (listOfDTOs == null) {
//      throw new NullPointerException("The listOf" + getInquirysDTOType() + "s is null!");
//    }
//    return listOfDTOs;
//  }


  /**
   * Gets the inquiry.
   *
   * @return inquiry's value.
   */
  public Inquiry getInquiry() {
    return inquiry;
  }

  /**
   * Sets the inquiry.
   *
   * @param inquiry is the inquiry's new value.
   */
  public void setInquiry(Inquiry inquiry) {
    this.inquiry = inquiry;
  }


  /**
   * Gets the inquiry's {@link DTOType}.
   *
   * @return inquiry's DTOType value.
   */
  public DTOType getInquirysDTOType() {
    return DTOType.valueOf(
        getDtoClass().getSimpleName().substring(0, getDtoClass().getSimpleName().length() - 3)
            .toUpperCase());
  }


  public enum Inquiry implements TransferableMessage, Serializable {

    // TODO: Needs revision and rethinking queries
    // TODO: Override "getQueryByCity" - ByCollection? - method where it might be relevant
    // TODO: Add method to retrieve some columns --> override where relevant (ONE / ALL)
    // TODO: Add "SOME_***" enums?

    UNASSIGNED {
      @Override
      public String getQuery() {
        try {
          throw new OperationNotSupportedException("Unimplemented");
        } catch (OperationNotSupportedException e) {
          e.printStackTrace();
        }
        return null;
      }
    }, USER_ATTEMPT_SIGN_IN {
      @Override
      public String getQuery() {
        return "SELECT * FROM users WHERE Email=? AND Password=?";
      }
    }, USER_ALTER_ONLINE_STATUS {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`users` SET `Online` = ? WHERE (`Email` = ?)";
      }
    }, USER_EXIT_SYSTEM {
      @Override
      public String getQuery() {
        return USER_ALTER_ONLINE_STATUS.getQuery();
      }
    }, USER_DISCONNECT_OTHER_CONNECTIONS {
      @Override
      public String getQuery() {
        throw new UnsupportedOperationException();
      }
    }, USER_EMPLOYEE_ID {
      @Override
      public String getQuery() {
        return "SELECT * FROM employees WHERE EmployeesEmail = ?";
      }
    }, USER_SIGN_UP {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`users` (`Email`, `Password`, `FirstName`, `LastName`, `Username`, `PhoneNumber`) VALUES (?,?,?,?,?,?)";
      }
    }, CITY_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM cities WHERE CityID != 0 AND State = 'APPROVED' ORDER BY CityName ASC;";
      }
    }, CITY_FETCH_ONE {
      @Override
      public String getQuery() {
        return "SELECT * FROM cities WHERE CityID = ?;";
      }
    }, EMPLOYEE_CITY_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.cities AS c1 WHERE (c1.CityID != 0 "
            + "AND NOT EXISTS (SELECT * FROM `gcm-db`.cities AS c2 WHERE c1.CityID = c2.PreviousCityID)) ORDER BY CityName ASC;";
      }
    }, EMPLOYEE_WORKSPACE_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT CityID FROM `gcm-db`.`employees_workspaces` WHERE (EmployeeID = ?);";
      }

    }, EMPLOYEE_CITY_APPROVAL_REQUEST_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.cities AS c, `gcm-db`.employees_workspaces AS ews WHERE ews.CityID = c.CityID AND c.State = 'PENDING_APPROVAL';";
      }
    }, CITY_SAVE {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`cities` SET `CityName` = ?, "
            + "`Description` = ? WHERE (`CityID` = ?);";
        //  `CoordinatesID` = ?, `MapViewZoom` = ?
      }
    }, CITY_SAVE_AND_LOCK {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`cities` "
            + "(`CityName`, `CollectionVersion`, `PreviousCityID`, `ReleaseDate`, `NumberOfMaps`, `Description`) "
            + "VALUES (?, ?, ?, CURDATE(), ?, ?);";
      /*return "UPDATE `gcm-db`.`cities` "
          + "SET `Name` = ?, `CollectionVersion` = ?, `NumberOfMaps` = ?, "
          + "`Description` = ?, `PreviousCityID` = ?, `CoordinatesID` = ? "
          + "WHERE (`CityID` = '77');";*/
      }
    }, CITY_LOCK_TO_EMPLOYEE {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`employees_workspaces` (`EmployeeID`, `CityID`) "
            + "VALUES(?, ?);";
      }
    }, CITY_UNLOCK_FROM_EMPLOYEE {
      @Override
      public String getQuery() {
        return "DELETE from `gcm-db`.`employees_workspaces` WHERE (`CityID` = ?);";
      }
    }, CITY_REQUEST_APPROVAL {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`cities` SET `State` = 'PENDING_APPROVAL' WHERE (`CityID` = ?);";
      }
    }, CITY_CANCEL_APPROVAL_REQUEST {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`cities` SET `State` = 'LOCKED' WHERE (`CityID` = ?);";
      }
    }, CITY_REJECT_APPROVAL_REQUEST {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`cities` SET `State` = 'REJECTED' WHERE (`CityID` = ?);";
      }
    }, CITY_APPROVE_APPROVAL_REQUEST {
      @Override
      public String getQuery() {
        return "CALL APPROVE_CITY(?,?)";
      }
    }, CITY_REJECTED_TO_LOCKED {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`cities` SET `State` = 'LOCKED' WHERE (`CityID` = ?);";
      }
    }, CITY_GET_MAPS_COUNT {
      @Override
      public String getQuery() {
        return "SELECT COUNT(MapID) FROM `gcm-db`.maps WHERE CityID = ?;";
      }
    }, CITY_GET_SITES_COUNT {
      @Override
      public String getQuery() {
        return "SELECT COUNT(SiteID) FROM `gcm-db`.sites WHERE CityID = ?;";
      }
    }, CITY_GET_SITES_NAMES {
      @Override
      public String getQuery() {
        return "SELECT SiteName FROM `gcm-db`.sites WHERE CityID = ?;";/* AND State = 'APPROVED';*/
      }
    }, CITY_DELETE_ONE {
      @Override
      public String getQuery() {
        return "DELETE FROM `gcm-db`.`cities` WHERE `CityID` = ?;";
      }
    }, CITY_UPDATE_SITES_COUNT {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`cities` SET NumberOfSites = (SELECT COUNT(SiteID) FROM `gcm-db`.sites WHERE sites.CityID = ?) WHERE `CityID` = ?;";
      }
    }, MAP_FETCH_ONE {
      @Override
      public String getQuery() {
        return "SELECT * FROM maps WHERE (MapID = ?);";
      }
    }, MAP_OF_CITY_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.maps WHERE (CityID = ?) AND State = 'APPROVED' ORDER BY MapName ASC;";
      }
    }, MAP_DETACHED_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.maps WHERE (CityID = 0) ORDER BY MapName ASC;";
      }
    }, EMPLOYEE_MAP_OF_CITY_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.maps WHERE (CityID = ?) ORDER BY MapName ASC;";
        // return "SELECT * FROM `gcm-db`.maps AS m1 WHERE"
        //     + "(EXISTS(SELECT * FROM `gcm-db`.maps AS m2 WHERE m1.PreviousMapID = m2.MapID)) "
        //     + "OR (m1.CityID = 0 AND NOT EXISTS(SELECT * FROM `gcm-db`.maps AS m2 WHERE m1.MapID = m2.PreviousMapID)) "
        //     + "OR (m1.CityID = ?);";
      }
    }, MAP_SAVE {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`maps` "
            + "(`MapName`, `CityID`, `MapVersion`, `State`, `PreviousMapID`) "
            + "VALUES (?, ?, ?, ?, ?);";
      }
    }, MAP_SAVE_AND_LOCK {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`maps` "
            + "(`MapName`, `CityID`, `MapVersion`, `State`, `PreviousMapID`) "
            + "VALUES (?, ?, ?, ?, ?);";
      }
    }, MAP_ATTACH_MAPS_TO_CITY {
      @Override
      public boolean isCallable() {
        return true;
      }

      @Override
      public String getQuery() {
        return "CALL ATTACH_MAP(?, ?);";
      }
    }, MAP_DETACH_MAPS_FROM_CITY {
      @Override
      public boolean isCallable() {
        return true;
      }

      @Override
      public String getQuery() {
        return "CALL DETACH_MAP(?);";
      }

    }, MAP_FETCH_SITES_LIST {
      @Override
      public String getQuery() {// AND MapVersion = ?, employee query?
        return "SELECT SiteID FROM `gcm-db`.city_map_site WHERE MapID = ?;";
      }
    }, MAP_ADD_SITE {
      @Override
      public String getQuery() {
        return "INSERT INTO`gcm-db`.city_map_site (`MapID`, `CityID`, `SiteID`) "
            + "VALUES (?, ?, ?);";
      }
    }, MAP_REMOVE_SITES {
      @Override
      public String getQuery() {
        return "DELETE FROM `gcm-db`.`city_map_site` WHERE (`MapID` = ? AND `SiteID` = ?);";
      }
    }, SITE_OF_CITY_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM sites WHERE CityID = ?;";
      }
    }, SITE_SAVE {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`sites` SET `SiteName` = ?, `CityID` = ?, `Description` = ?, "
            + "`RecommendedVisitDuration` = ?, `Category` = ?, `Accessible` = ? WHERE (`SiteID` = ?);";
      }
    }, SITE_SAVE_AND_LOCK {
      @Override
      public String getQuery() {
        return "CALL CREATE_NEW_SITE(?,?,?,?,?,?,?,?)";
      }
    }, SITE_OF_CITY_DELETE_ALL {
      @Override
      public String getQuery() {
        return "DELETE FROM `gcm-db`.`sites` WHERE (`CityID` = ?);";
      }
    }, CC_NEW_CUSTOMER {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`creditcards` (`AccountsEmail`, `CardholdersName`, `CardholdersID`, `CreditCardNumber`, `CreditCardExpirationDate`, `CreditCardCSV`) VALUES (?, ?, ?, ?, ?,?)";
      }
    }, COORDINATES_FETCH_ONE {
      @Override
      public String getQuery() {
        return "SELECT * FROM coordinates WHERE CoordinatesID = ?";
      }
    }, COORDINATES_SAVE {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`coordinates` SET `Latitude` = ?, `Longitude` = ? WHERE (`CoordinatesID` = ?);";
      }
    }, COORDINATES_SAVE_AND_LOCK {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`coordinates` (`Latitude`, `Longitude`) VALUES (?, ?);";
      }
    }, SITE_CATEGORY_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT DISTINCT(Category) FROM sites;";
      }
    }, SITE_DELETE {
      @Override
      public String getQuery() {
        return "DELETE FROM sites WHERE SiteID = ?;";
      }
    }, EMPLOYEE_TOUR_OF_MAP_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.`tours` WHERE MapID = ?";
      }
    }, TOUR_OF_MAP_FETCH_ALL {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.tours AS t WHERE t.MapID = ? AND 1 < (SELECT COUNT(*) FROM `gcm-db`.tours_sites AS ts WHERE ts.TourID = t.TourID);";
      }
    }, TOUR_FETCH_SITES_LIST {
      @Override
      public String getQuery() {
        return "SELECT SiteID FROM `gcm-db`.tours_sites WHERE TourID = ?;";
      }
    }, TOUR_SAVE_AND_LOCK {
      @Override
      public String getQuery() {
        return "CALL CREATE_NEW_TOUR(?,?,?,?,?,?,?,?)";
      }
    }, TOUR_SAVE {
      @Override
      public String getQuery() {
        return null;
      }
    }, TOUR_DELETE {
      @Override
      public String getQuery() {
        return "DELETE FROM `gcm-db`.tours_sites WHERE TourID = ?;";
      }
    }, PURCHASE_FETCH_ALL_ACTIVE {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.purchases WHERE CustomerEmail = ? AND ExpirationDate > CURRENT_DATE();";
      }
    }, PURCHASE_EXTEND_SUBSCRIPTION {
      @Override
      public String getQuery() {
        return "UPDATE `gcm-db`.`purchases` SET `WasExtended` = TRUE, `ExpirationDate` = ADDDATE(`purchases`.`ExpirationDate`, INTERVAL 6 MONTH) WHERE (`PurchaseID` = ?);";
      }
    }, PURCHASE_FETCH_ONE {
      @Override
      public String getQuery() {
        return "SELECT * FROM `gcm-db`.`purchases` WHERE (`PurchaseID` = ?);";
      }
    }, PURCHASE_NEW_SUBSCRIPTION {
      @Override
      public String getQuery() {
        return "INSERT INTO `gcm-db`.`purchases` (`CustomerEmail`, `PurchaseDate`, `CityID`, `ExpirationDate`) VALUES (?, CURDATE(), ?, ADDDATE(CURDATE(), INTERVAL 6 MONTH));";
      }
    };

    /**
     * Method to get the actual query behind the request.
     *
     * @return the query.
     */
    public abstract String getQuery();

    public boolean isCallable() {
      return false;
    }

  }

}
