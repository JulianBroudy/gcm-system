package com.broudy.gcm.control.repos;

import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.dtos.CreditCardDTO;
import com.google.inject.Inject;


/**
 * Manages all credit card related matters.
 * Accepts <code>CreditCardDTO</code> and all its subclasses.
 * TODO provide a more in-depth summary to CreditCardsRepository class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see CreditCardDTO
 */
public class CreditCardsRepository extends BeanRepository<CreditCardDTO> {

  private final UsersRepository usersRepository;
  private CreditCardDTO DEFAULT_CC;

  @Inject
  public CreditCardsRepository(UsersRepository usersRepository) {
    super(new CreditCardDTO());
    this.usersRepository = usersRepository;
  }

  /**
   * This method is called from within the {@link #BeanRepository} constructor.
   * It is used to initialize subclasses' private specific DEFAULT_DTO with the <code>theDTO</code>
   * passed to the constructor.
   *
   * @param emptyCCDTO is the passed DTO that would be set as the specific DEFAULT_DTO in
   *     subclass.
   */
  @Override
  protected <C extends CreditCardDTO> void setupInitialBean(C emptyCCDTO) {
    this.DEFAULT_CC = emptyCCDTO;
    resetDefaultCreditCard();
  }

  ///**
  //    * This method is called from within the {@link #BeanRepository} constructor.
  //    * It is used to initialize subclasses' private specific DEFAULT_DTO with the <code>theDTO</code>
  //    * passed to the constructor.
  //    *
  //    * @param emptyCCDTO is the passed DTO that would be set as the specific DEFAULT_DTO in subclass.
  //    */
  //   @Override
  //   protected void setupInitialBean(CreditCardDTO emptyCCDTO) {
  //     this.DEFAULT_CC = emptyCCDTO;
  //     resetDefaultCreditCard();
  //   }
  private void resetDefaultCreditCard() {
    DEFAULT_CC.setAccountsEmail("");
    DEFAULT_CC.setCardholdersFullName("");
    DEFAULT_CC.setCardholdersID("");
    DEFAULT_CC.setCcNumber("");
    DEFAULT_CC.setCvv("");
    DEFAULT_CC.setExpirationDate(null);
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
        getSuperBean().setBean(bean.get());
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

  public void createCC() {
    bean.get().setAccountsEmail(usersRepository.getBean().getEmail());
  }

  @Override
  protected void sendInquiry(CreditCardDTO dtoToAttach, Inquiry inquiry) {

  }
}