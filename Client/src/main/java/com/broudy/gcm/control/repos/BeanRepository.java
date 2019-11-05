package com.broudy.gcm.control.repos;

import com.broudy.gcm.control.GCMClient;
import com.broudy.gcm.entity.ClientsInquiry;
import com.broudy.gcm.entity.ClientsInquiry.Inquiry;
import com.broudy.gcm.entity.interfaces.AbstractDTO;
import com.broudy.gcm.entity.interfaces.Renderable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfxtras.labs.scene.control.BeanPathAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * An abstract repository-like class for convenience and re-usability considerations.
 * Each repository will have a bean, a list of beans, and a super bean.
 * <p>
 * Created on the 5th of June, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public abstract class BeanRepository<DTO extends AbstractDTO> {

  protected final Logger logger = LogManager.getLogger(this.getClass());

  protected ObjectProperty<DTO> bean;
  protected BeanPathAdapter<DTO> superBean;
  protected ListProperty<DTO> beansList;

  /**
   * Constructs the basis of any DTORepository.
   * Initializes the {@link #bean}'s property and the {@link #superBean} with the passed dto as well
   * as initializes {@link #beansList}.
   * Constructor calls {@link #setupInitialBean} and {@link #startInternalInitialization()}.
   *
   * @param theDTO the initial {@link Renderable} to initialize this repository with.
   */
  BeanRepository(DTO theDTO) {

    // Initialize & instantiate the bean.
    bean = new SimpleObjectProperty<>(theDTO);
    logger.info("Initialized {}'s bean", theDTO.getClass().getSimpleName());

    // Initialize bean's super bean.
    superBean = new BeanPathAdapter<>(theDTO);
    logger.info("Initialized {}'s superBean", theDTO.getClass().getSimpleName());

    // Initialize & initialize beans list.
    beansList = new SimpleListProperty<>(FXCollections.observableArrayList());
    logger.info("Initialized {}'s beansList", theDTO.getClass().getSimpleName());

    setupInitialBean(theDTO);
    startInternalInitialization();
  }

  /**
   * This is the last method called from within the {@link #BeanRepository} constructor.
   * <p>
   * Should implement any and all initialization needs.
   */
  protected abstract void startInternalInitialization();

  /**
   * This method is called from within the {@link #BeanRepository} constructor.
   * It is used to initialize subclasses' private specific DEFAULT_DTO with the <code>theDTO</code>
   * passed to the constructor.
   *
   * @param theDTO is the passed DTO that would be set as the specific DEFAULT_DTO in subclass.
   */
  protected abstract <C extends DTO> void setupInitialBean(C theDTO);

  /*
   *     ____      _   _                   ___     ____       _   _
   *    / ___| ___| |_| |_ ___ _ __ ___   ( _ )   / ___|  ___| |_| |_ ___ _ __ ___
   *   | |  _ / _ \ __| __/ _ \ '__/ __|  / _ \/\ \___ \ / _ \ __| __/ _ \ '__/ __|
   *   | |_| |  __/ |_| ||  __/ |  \__ \ | (_>  <  ___) |  __/ |_| ||  __/ |  \__ \
   *    \____|\___|\__|\__\___|_|  |___/  \___/\/ |____/ \___|\__|\__\___|_|  |___/
   *
   */

  /**
   * Gets the bean's value.
   *
   * @return the bean's value.
   */
  public DTO getBean() {
    logger.info("{}'s bean has been retrieved: {}",
        bean.get() != null ? bean.get().getClass().getSimpleName() : "NULL", bean.get());
    return bean.get();
  }

  /**
   * Sets bean's property value.
   *
   * @param bean is the bean's property's new value.
   */
  public void setBean(DTO bean) {
    this.bean.set(bean);
    logger.info("{}'s bean has been set to: {}",
        this.bean.get() != null ? this.bean.get().getClass().getSimpleName() : "NULL", bean);
  }

  /**
   * Gets the bean's ObjectProperty.
   *
   * @return the bean's ObjectProperty.
   */
  public <RenderableDTO extends Renderable> ObjectProperty<RenderableDTO> beanProperty() {
    logger.info("{}'s bean property has been retrieved.",
        bean.get() != null ? bean.get().getClass().getSimpleName() : "NULL");
    return (ObjectProperty<RenderableDTO>) bean;
  }

  /**
   * Gets the superBean.
   *
   * @return the superBean.
   */
  public BeanPathAdapter<DTO> getSuperBean() {
    logger.info("{}'s superBean has been retrieved.",
        bean.get() != null ? bean.get().getClass().getSimpleName() : "NULL");
    return superBean;
  }

  /**
   * Gets the beansList ObservableList.
   *
   * @return the value of the beansList.
   */
  public <RenderableDTO extends Renderable> ObservableList<RenderableDTO> getBeansList() {
    logger.info("{}'s beansList has been retrieved.",
        bean.get() != null ? bean.get().getClass().getSimpleName() : "NULL");
    return (ObservableList<RenderableDTO>) beansList.get();
  }

  /**
   * Sets the beansList ObservableList.
   *
   * @param beansList is the beansList's new value.
   */
  public void setBeansList(ObservableList<? super DTO> beansList) {
    this.beansList.set((ObservableList<DTO>) beansList);
    logger.info("BeansRepository: {}", this.getClass().hashCode());
    logger.info("{}'s beansList has been set to: {}",
        bean.get() != null ? bean.get().getClass().getSimpleName() : "NULL", beansList);
  }

  /**
   * Gets the beansList's ListProperty.
   *
   * @return the beansList ListProperty.
   */
  public <RenderableDTO extends Renderable> ListProperty<RenderableDTO> beansListProperty() {
    return (ListProperty<RenderableDTO>) beansList;
  }

  protected void sendInquiry(Inquiry inquiry) {
    sendInquiry(bean.get(), inquiry);
  }

  protected abstract void sendInquiry(DTO dtoToAttach, Inquiry inquiry);

  protected void sendInquiry(ClientsInquiry<DTO> clientsInquiry) {
    GCMClient.getInstance().send(clientsInquiry);
  }

}
