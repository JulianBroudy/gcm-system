package com.broudy.gcm.control.repos;

import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.dtos.UserDTO;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;

/**
 * Manages all user related matters.
 * Accepts <code>UserDTO</code> and all its subclasses.
 * TODO provide a more in-depth summary to UsersRepository class!!!!!
 * <p>
 * Created on the 7th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see UserDTO
 */
public class UsersRepository extends BeanRepository<UserDTO> {

  private UserDTO DEFAULT_USER;

  @Inject
  public UsersRepository() {
    super(new UserDTO());
  }

  /**
   * This method is called from within the {@link #BeanRepository} constructor.
   * It is used to initialize subclasses' private specific DEFAULT_DTO with the <code>theDTO</code>
   * passed to the constructor.
   *
   * @param emptyUserDTO is the passed DTO that would be set as the specific DEFAULT_DTO in
   *     subclass.
   */
  @Override
  protected <C extends UserDTO> void setupInitialBean(C emptyUserDTO) {
    this.DEFAULT_USER = emptyUserDTO;
    resetDefaultUser();
  }

  private void resetDefaultUser() {
    DEFAULT_USER.setEmail("");
    DEFAULT_USER.setPassword("");
    DEFAULT_USER.setPhoneNumber("");
    DEFAULT_USER.setLastName("");
    DEFAULT_USER.setFirstName("");
    DEFAULT_USER.setUsername("");
    setBean(DEFAULT_USER);
  }

  /**
   * This is the last method called from within the {@link #BeanRepository} constructor.
   * <p>
   * Should implement any and all initialization needs.
   */
  @Override
  protected void startInternalInitialization() {
    // Bind bean to superBean
    bean.addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        logger.debug(newValue);
        getSuperBean().setBean(bean.get());
        // PurchasesManager.getInstance().requestAllSubscriptionsOfUser();
      }
    });
  }

  /*
   *     ____ _ _            _         ___                   _      _
   *    / ___| (_) ___ _ __ | |_ ___  |_ _|_ __   __ _ _   _(_)_ __(_) ___  ___
   *   | |   | | |/ _ \ '_ \| __/ __|  | || '_ \ / _` | | | | | '__| |/ _ \/ __|
   *   | |___| | |  __/ | | | |_\__ \  | || | | | (_| | |_| | | |  | |  __/\__ \
   *    \____|_|_|\___|_| |_|\__|___/ |___|_| |_|\__, |\__,_|_|_|  |_|\___||___/
   *                                                |_|
   */

  public void requestLoginAuthentication() {
    logger.trace(TextColors.PURPLE.colorThis("requestLoginAuthentication") + " was just invoked!");
    sendInquiry(Inquiry.USER_ATTEMPT_SIGN_IN);
  }

  public void requestUserSignUp() {
    logger.trace(TextColors.PURPLE.colorThis("requestUserSignUp") + " was just invoked!");
    sendInquiry(Inquiry.USER_SIGN_UP);
  }

  public void requestUserSignOut() {
    logger.trace(TextColors.PURPLE.colorThis("requestUserSignOut") + " was just invoked!");
    sendInquiry(Inquiry.USER_ALTER_ONLINE_STATUS);
  }


  public void requestSystemExit() {
    logger.trace(TextColors.PURPLE.colorThis("requestSystemExit") + " was just invoked!");
    sendInquiry(Inquiry.USER_EXIT_SYSTEM);
  }

  public void requestLogOtherSessionsOutAndLoginHere() {
    logger.trace(TextColors.PURPLE.colorThis("requestLogOtherSessionsOutAndLoginHere")
        + " was just invoked!");
    sendInquiry(Inquiry.USER_DISCONNECT_OTHER_CONNECTIONS);
  }

  /*
   *   ____                           _                       __  __      _   _               _
   *  / ___|___  _ ____   _____ _ __ (_) ___ _ __   ___ ___  |  \/  | ___| |_| |__   ___   __| |___
   * | |   / _ \| '_ \ \ / / _ \ '_ \| |/ _ \ '_ \ / __/ _ \ | |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
   * | |__| (_) | | | \ V /  __/ | | | |  __/ | | | (_|  __/ | |  | |  __/ |_| | | | (_) | (_| \__ \
   *  \____\___/|_| |_|\_/ \___|_| |_|_|\___|_| |_|\___\___| |_|  |_|\___|\__|_| |_|\___/ \__,_|___/
   *
   */

  protected void sendInquiry(Inquiry inquiry) {
    final ClientsInquiry<UserDTO> userInquiry = ClientsInquiry.of(UserDTO.class);
    userInquiry.setTheDTO(bean.get());
    userInquiry.setInquiry(inquiry);
    GCMClient.getInstance().send(userInquiry);
  }

  public void requestBeanReset() {
    resetDefaultUser();
  }

  @Override
  protected void sendInquiry(UserDTO dtoToAttach, Inquiry inquiry) {

  }

  public void requestUserInformationSaving() {

  }
}
