package com.broudy.gcm.control.repos;

import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.dtos.PurchaseDTO;
import com.google.inject.Inject;


/**
 * Manages all user related matters.
 * Accepts <code>PurchaseDTO</code> and all its subclasses.
 * TODO provide a more in-depth summary to PurchasesRepository class!!!!!
 * <p>
 * Created on the 8th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 * @see PurchaseDTO
 */
public class PurchasesRepository extends BeanRepository<PurchaseDTO> {

  private final UsersRepository usersRepository;
  private final CitiesRepository citiesRepository;

  private PurchaseDTO DEFAULT_PURCHASE;

  @Inject
  public PurchasesRepository(UsersRepository usersRepository, CitiesRepository citiesRepository) {
    super(new PurchaseDTO());
    this.usersRepository = usersRepository;
    this.citiesRepository = citiesRepository;
  }

  /**
   * This method is called from within the {@link #BeanRepository} constructor.
   * It is used to initialize subclasses' private specific DEFAULT_DTO with the <code>theDTO</code>
   * passed to the constructor.
   *
   * @param emptyPurchaseDTO is the passed DTO that would be set as the specific DEFAULT_DTO in
   *     subclass.
   */
  @Override
  protected <C extends PurchaseDTO> void setupInitialBean(C emptyPurchaseDTO) {
    this.DEFAULT_PURCHASE = emptyPurchaseDTO;
    resetDefaultCity();
  }

  private void resetDefaultCity() {
    DEFAULT_PURCHASE.setCityID(0);
    DEFAULT_PURCHASE.setCustomerEmail("Should not be seen!");
    DEFAULT_PURCHASE.setExtended(false);
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


  public void requestCustomerActivePurchases() {
    PurchaseDTO purchase = getBean().getDeepCopy();
    purchase.setCustomerEmail(usersRepository.getBean().getEmail());
    sendInquiry(purchase, Inquiry.PURCHASE_FETCH_ALL_ACTIVE);
  }

  public void requestSubscriptionExtension() {
    sendInquiry(getBean(), Inquiry.PURCHASE_EXTEND_SUBSCRIPTION);
  }

  public void requestNewSubscription() {
    PurchaseDTO newPurchase = getBean().getDeepCopy();
    newPurchase.setCustomerEmail(usersRepository.getBean().getEmail());
    newPurchase.setCityID(citiesRepository.getBean().getID());
    sendInquiry(newPurchase, Inquiry.PURCHASE_NEW_SUBSCRIPTION);
  }

  @Override
  protected void sendInquiry(PurchaseDTO purchaseToAttach, Inquiry inquiry) {
    final ClientsInquiry<PurchaseDTO> purchaseInquiry = ClientsInquiry.of(PurchaseDTO.class);
    purchaseInquiry.setTheDTO(purchaseToAttach);
    purchaseInquiry.setInquiry(inquiry);
    GCMClient.getInstance().send(purchaseInquiry);
  }

}
