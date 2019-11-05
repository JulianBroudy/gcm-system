package com.broudy.gcm.control.services.renderings;

import com.broudy.gcm.boundary.enhanced.controls.MapLocatorIcons;
import com.broudy.gcm.control.repos.CitiesRepository;
import com.broudy.gcm.control.repos.MapsRepository;
import com.broudy.gcm.control.repos.SitesRepository;
import com.broudy.gcm.control.repos.ToursRepository;
import com.broudy.gcm.control.repos.UsersRepository;
import com.broudy.gcm.entity.State;
import com.broudy.gcm.entity.dtos.SiteDTO;
import com.broudy.gcm.entity.dtos.TourDTO;
import com.broudy.gcm.entity.interfaces.IStatable;
import com.broudy.gcm.entity.interfaces.Renderable;
import com.broudy.utils.TextColors;
import com.google.inject.Inject;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.CoordinateLine;
import com.sothawo.mapjfx.Extent;
import com.sothawo.mapjfx.MapLabel;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.Marker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is the go-to class regarding all MapView-renderings.
 * <p>
 * Created on the 1st of September, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class MapViewRenderingsService<RIS extends Renderable & IStatable> {

  private static final Logger logger = LogManager.getLogger(MapViewRenderingsService.class);

  private final UsersRepository usersRepository;
  private final CitiesRepository citiesRepository;
  private final MapsRepository mapsRepository;
  private final SitesRepository sitesRepository;
  private final ToursRepository toursRepository;
  private MapView mapView;
  private ConcurrentHashMap<RIS, SiteRendering<SiteDTO>> lastRenderedSites;
  private ConcurrentHashMap<RIS, TourRendering<TourDTO>> lastRenderedTours;

  @Inject
  public MapViewRenderingsService(UsersRepository usersRepository,
      CitiesRepository citiesRepository, MapsRepository mapsRepository,
      SitesRepository sitesRepository, ToursRepository toursRepository) {
    this.usersRepository = usersRepository;
    this.citiesRepository = citiesRepository;
    this.mapsRepository = mapsRepository;
    this.sitesRepository = sitesRepository;
    this.toursRepository = toursRepository;
  }

  /**
   * Gets the mapView.
   *
   * @return mapView's value.
   */
  public MapView getMapView() {
    return mapView;
  }

  /**
   * Sets the mapView.
   *
   * @param mapView is the mapView's new value.
   */
  public void setMapView(MapView mapView) {
    this.mapView = mapView;
  }

  public ConcurrentHashMap<RIS, SiteRendering<SiteDTO>> generateMapSitesRenderings() {
    ConcurrentHashMap<RIS, SiteRendering<SiteDTO>> sitesRenderings = new ConcurrentHashMap<>();
    List<SiteDTO> renderableSites = sitesRepository.getBeansList();

    SiteRendering<SiteDTO> siteRendering;
    // Loop over all sites to be rendered
    for (SiteDTO site : renderableSites) {

      // Create rendering from renderable and store it in HashMap
      siteRendering = generateSiteRendering(site);
      bindVisibilityToSiteExistenceInSitesOfMapList(siteRendering, site);
      sitesRenderings.put((RIS) site, siteRendering);
    }
    lastRenderedSites = sitesRenderings;
    return sitesRenderings;
  }

  private void bindVisibilityToSiteExistenceInSitesOfMapList(SiteRendering<SiteDTO> siteRendering,
      SiteDTO site) {
    siteRendering.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
      if (toursRepository.getBean().getState() != State.DEFAULT) {
        return siteRendering.partOfTour.get();
      } else if (sitesRepository.getSitesOfMap().contains(site)) {
        return true;
      } else {
        return mapsRepository.getBean().getState() == State.DEFAULT;
      }
    }, siteRendering.partOfTour, sitesRepository.sitesOfMapList()));
  }

  public SiteRendering<SiteDTO> generateSiteRendering(Renderable site) {
    if (!(site instanceof SiteDTO)) {
      throw new ClassCastException();
    }
    SiteRendering<SiteDTO> siteRendering = new SiteRendering<>((SiteDTO) site);
    return logger
        .traceExit(TextColors.GREEN.colorThis("Generated") + " rendering of: {}", siteRendering);
  }

  public ConcurrentHashMap<RIS, TourRendering<TourDTO>> generateMapToursRenderings() {
    ConcurrentHashMap<RIS, TourRendering<TourDTO>> toursRenderings = new ConcurrentHashMap<>();
    List<TourDTO> renderableTours = toursRepository.getBeansList();

    TourRendering<TourDTO> tourRendering;
    // Loop over all sites to be rendered
    for (TourDTO tour : renderableTours) {

      // Create rendering from renderable and store it in HashMap
      tourRendering = generateTourRendering(tour);
      bindVisibilityToTourExistenceInMapsOfCityList(tourRendering, tour);
      toursRenderings.put((RIS) tour, tourRendering);
    }
    lastRenderedTours = toursRenderings;
    return toursRenderings;
  }

  public TourRendering<TourDTO> generateTourRendering(TourDTO tour) {
    TourRendering<TourDTO> tourRendering = new TourRendering<TourDTO>(tour/*,*/
       /* sitesRepository.getSitesOfMap().stream()
            .filter(site -> tour.getSites().contains(((SiteDTO) site).getID()))
            .map(site -> (SiteDTO) site).collect(Collectors.toList()));*/);
    return logger
        .traceExit(TextColors.GREEN.colorThis("Generated") + " rendering of: {}", tourRendering);
  }

  private void bindVisibilityToTourExistenceInMapsOfCityList(TourRendering<TourDTO> tourRendering,
      TourDTO tour) {
    tourRendering.visibleProperty().bind(Bindings.createBooleanBinding(
        () -> toursRepository.getBean() != null && toursRepository.getBean().equals(tour),
        toursRepository.beanProperty()));
    //  tourRendering.visibleProperty().bind(Bindings
    //         .createBooleanBinding(() -> toursRepository.getBeansList().contains(tour),
    //             toursRepository.beansListProperty()));
  }

  public Extent getEnhancedExtent(Collection<? extends Coordinate> coordinates) {

    double minLatitude = 1.7976931348623157E308D;
    double maxLatitude = -1.7976931348623157E308D;
    double minLongitude = 1.7976931348623157E308D;
    double maxLongitude = -1.7976931348623157E308D;

    Coordinate coordinate;
    for (Iterator iterator = coordinates.iterator(); iterator.hasNext();
        maxLongitude = Math.max(maxLongitude, coordinate.getLongitude())) {
      coordinate = (Coordinate) iterator.next();
      minLatitude = Math.min(minLatitude, coordinate.getLatitude());
      maxLatitude = Math.max(maxLatitude, coordinate.getLatitude());
      minLongitude = Math.min(minLongitude, coordinate.getLongitude());
    }

    double correction = 0.007D;
    return Extent
        .forCoordinates(new Coordinate(minLatitude - correction, minLongitude - correction),
            new Coordinate(maxLatitude + 2 * correction, maxLongitude + correction));
  }

  /**
   * TODO provide a summary to SiteRendering class!!!!!
   * <p>
   * Created on the 03th of September, 2019.
   *
   * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
   */
  public class SiteRendering<SITE extends SiteDTO> {

    private final ObjectProperty<Marker> renderedMarker;
    private final Marker marker;
    private final SITE renderedSite;
    private final MapLabel label;
    private SiteDTO originalSite;
    private BooleanProperty visible;
    private boolean selected;
    private BooleanProperty partOfTour;


    SiteRendering(SITE renderedSite) {
      this.renderedSite = renderedSite;
      cloneSite();
      this.visible = new SimpleBooleanProperty(true);
      this.selected = false;
      this.partOfTour = new SimpleBooleanProperty(false);
      this.partOfTour.bind(Bindings
          .createBooleanBinding(() -> sitesRepository.getSitesOfTour().contains(renderedSite),
              sitesRepository.sitesOfTourList()));
      this.marker = new Marker(generateIcon().getImageURL(), -16, -48).setVisible(true);
      this.marker.visibleProperty().bind(visible);
      this.marker.setPosition(this.renderedSite.getCoordinates());
      this.marker.positionProperty().addListener((observable, oldValue, newValue) -> {
        renderedSite.setCoordinates(newValue);
      });
      this.renderedMarker = new SimpleObjectProperty<>(marker);
      this.renderedMarker.addListener((observable, oldValue, newValue) -> {
        logger.info("\n\nSITE {} changed marker from {} to {}\nisPartOfTour: {}\tselected:{}\n\n",
            renderedSite.getName(), oldValue.getId(), newValue.getId(), isPartOfTour(),
            isSelected());
        oldValue.visibleProperty().unbind();
        oldValue.setVisible(false);
        newValue.visibleProperty().bind(visible);
      });
      this.label = new MapLabel(this.renderedSite.getName())
          .setPosition(this.renderedSite.getCoordinates()).setVisible(false);
      this.label.positionProperty().bind(this.marker.positionProperty());
      this.partOfTour.addListener((observable, wasPartOfTour, isPartOfTour) -> {
        if (!wasPartOfTour && isPartOfTour) {
          // toursRepository.getBean().getSites().add(renderedSite.getID());
          Marker partOfTourMarker = new Marker(generateIcon().getImageURL(), -7, -47)
              .setPosition(marker.getPosition());
          marker.positionProperty().bindBidirectional(partOfTourMarker.positionProperty());
          mapView.removeMarker(renderedMarker.get());

          logger.info("\n\nSite {}\n!wasPartOfTour && isPartOfTour:\nOLD:{}\nNEW:{}\n\n",
              renderedSite.getName(), renderedMarker.get(), partOfTourMarker);
          renderedMarker.set(partOfTourMarker);
          mapView.addMarker(renderedMarker.get());
          label.setVisible(false);
        } else if (wasPartOfTour && !isPartOfTour) {
          // Apply rendering when not a part of tour.
          // toursRepository.getBean().getSites().remove((Integer)renderedSite.getID());
          marker.positionProperty().unbindBidirectional(renderedMarker.get().positionProperty());
          logger.info("Site {}\nwasPartOfTour && !isPartOfTour:\nOLD:{}\nNEW:{}\n\n",
              renderedSite.getName(), renderedMarker.get(), marker);
          mapView.removeMarker(renderedMarker.get());
          renderedMarker.set(marker);
          mapView.addMarker(renderedMarker.get());
        }
      });
    }

    /**
     * Creates a deep copy of renderedSite in order to compare on saving or switching selected site.
     */
    private void cloneSite() {
      originalSite = renderedSite.getDeepCopy();
    }

    /**
     * Convenience method for generating appropriate locator icon.
     *
     * @return the icon.
     */
    private MapLocatorIcons generateIcon() {
      MapLocatorIcons icon;
      switch (renderedSite.getState()) {
        case NEW:
          if (selected) {
            icon = MapLocatorIcons.YELLOW;
            break;
          }
        case LOCKED:
          if (partOfTour.get()) {
            icon = selected ? MapLocatorIcons.PIN_PINK : MapLocatorIcons.PIN_BLUE;
          } else {
            icon = selected ? MapLocatorIcons.PINK : MapLocatorIcons.BLUE;
          }
          break;
        case APPROVED:
          icon = partOfTour.get() ? MapLocatorIcons.PIN_GREEN_LIGHT : MapLocatorIcons.GREEN_LIGHT;
          break;
        case PENDING_APPROVAL:
          icon = MapLocatorIcons.BLUE_LIGHT;
          break;
        case REJECTED:
          icon = MapLocatorIcons.GREY;
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + renderedSite.getState());
      }
      return icon;
    }

    public void setSelected(boolean select, MapView mapView) {
      mapView.removeMarker(renderedMarker.get());
      setSelected(select);
      mapView.addMarker(renderedMarker.get());
      if (selected) {
        if (isPartOfTour()) {
          mapView.setExtent(Extent.forCoordinates(
              lastRenderedTours.get(toursRepository.getBean()).getCoordinatesStream()
                  .collect(Collectors.toList())));
        } else {
          mapView.setCenter(getPosition());
        }
      }
    }

    public SimpleBooleanProperty getLabelVisibility() {
      return label.visibleProperty();
    }

    /**
     * This method sets the {@link #label}'s visibility.
     *
     * @param visible is where to set it to visible or unvisible.
     */
    public void setLabelVisibility(boolean visible) {
      label.setVisible(visible);
    }

    public Marker getRenderedMarker() {
      return renderedMarker.get();
    }

    public Coordinate getPosition() {
      return renderedMarker.get().getPosition();
    }

    public void setPosition(Coordinate coordinates) {
      renderedMarker.get().setPosition(coordinates);
    }

    public ObjectProperty<Coordinate> positionProperty() {
      return marker.positionProperty();
    }

    public void attachTo(MapView mapView) {
      mapView.addMarker(renderedMarker.get());
      mapView.addLabel(label);
    }

    public void detachFrom(MapView mapView) {
      mapView.removeMarker(renderedMarker.get());
      mapView.removeLabel(label);
    }

    public BooleanProperty visibleProperty() {
      return visible;
    }

    public boolean isPartOfTour() {
      return partOfTour.get();
    }

    public BooleanProperty partOfTourProperty() {
      return partOfTour;
    }

    /**
     * This method checks whether the site was edited. i.e., {@link #renderedSite} has at least 1
     * field that is different than the {@link #originalSite}.
     *
     * @return true if it has been edited else false.
     */
    public boolean hasUnsavedChanges() {
      return renderedSite.isAccessible() != originalSite.isAccessible() || !renderedSite.getName()
          .equals(originalSite.getName()) || !renderedSite.getDescription()
          .equals(originalSite.getDescription()) || !renderedSite.getRecommendedVisitDuration()
          .equals(originalSite.getRecommendedVisitDuration()) || !renderedSite.getCategory()
          .equals(originalSite.getCategory()) || !renderedSite.getState()
          .equals(originalSite.getState()) || !renderedSite.getCoordinates()
          .equals(originalSite.getCoordinates());
    }

    public boolean isSelected() {
      return selected;
    }

    public void setSelected(boolean select) {
      if (select && !selected) {
        // Apply "selected" rendering.
        selected = true;
        final Marker selectedMarker;
        if (partOfTour.get()) {
          selectedMarker = new Marker(generateIcon().getImageURL(), -7, -47)
              .setPosition(marker.getPosition());
          marker.positionProperty().unbindBidirectional(renderedMarker.get().positionProperty());
        } else {
          selectedMarker = new Marker(generateIcon().getImageURL(), -16, -48)
              .setPosition(marker.getPosition());
        }
        marker.positionProperty().bindBidirectional(selectedMarker.positionProperty());
        renderedMarker.set(selectedMarker);
        label.setVisible(false);
      } else if (!select && selected) {
        // Apply "unselected" rendering.
        selected = false;
        if (partOfTour.get()) {
          // marker.positionProperty().unbindBidirectional(renderedMarker.get().positionProperty());
          final Marker partOfTourMarker = new Marker(generateIcon().getImageURL(), -7, -47)
              .setPosition(marker.getPosition());
          marker.positionProperty().bindBidirectional(partOfTourMarker.positionProperty());
          renderedMarker.set(partOfTourMarker);
        } else if (renderedMarker.get() != marker) {
          marker.positionProperty().unbindBidirectional(renderedMarker.get().positionProperty());
          renderedMarker.set(marker);
        }
      }
    }

    public boolean isVisible() {
      return visible.get();
    }

    public void setVisible(boolean visible) {
      this.visible.set(visible);
    }

    public void rollBack() {
      originalSite.deepCopyTo(renderedSite);
    }

    public Coordinate getOriginalPosition() {
      return originalSite.getCoordinates();
    }

    @Override
    public String toString() {
      return "SiteRendering{" + "renderedSite=" + renderedSite + '}';
    }

    int getRenderedSiteID() {
      return renderedSite.getID();
    }
  }

  /**
   * TODO provide a summary to TourRendering class!!!!!
   * <p>
   * Created on the 09th of October, 2019.
   *
   * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
   */
  public class TourRendering<TOUR extends TourDTO> {

    private final ObjectProperty<CoordinateLine> renderedCoordinatesLine;
    private final CoordinateLine coordinateLine;
    private final TOUR renderedTour;
    private final List<Integer> originalListOfSites;

    private BooleanProperty visible;
    private BooleanProperty selected;

    TourRendering(TOUR renderedTour) {
      this.renderedTour = renderedTour;
      this.selected = new SimpleBooleanProperty(true);
      this.visible = new SimpleBooleanProperty(true);
      this.coordinateLine = new CoordinateLine(lastRenderedSites.entrySet().stream().filter(
          entry -> this.renderedTour.getSites().contains(((SiteDTO) entry.getKey()).getID()))
          .map(entry -> entry.getValue().getPosition()).collect(Collectors.toList()))
          .setColor(generateColor());
      this.coordinateLine.visibleProperty().bind(visible);
      this.renderedCoordinatesLine = new SimpleObjectProperty<>(coordinateLine);
      this.renderedCoordinatesLine.addListener((observable, oldValue, newValue) -> {
        oldValue.visibleProperty().unbind();
        oldValue.setVisible(false);
        newValue.visibleProperty().bind(visible);
      });
      originalListOfSites = new ArrayList<>();
      originalListOfSites.addAll(renderedTour.getSites());
    }

    private Color generateColor() {
      if (mapView.getMapType() == MapType.BINGMAPS_AERIAL) {
        return Color
            .color(Math.random() / 2F + 0.5, Math.random() / 2F + 0.5, Math.random() / 2F + 0.5);
      } else {
        return Color.color(1 - (Math.random() / 2F + 0.5), 1 - (Math.random() / 2F + 0.5),
            1 - (Math.random() / 2F + 0.5));
      }
    }

    public BooleanProperty visibleProperty() {
      return visible;
    }


    public void detachFrom(MapView mapView) {
      mapView.removeCoordinateLine(renderedCoordinatesLine.get());
    }

    public void attachTo(MapView mapView) {
      mapView.addCoordinateLine(renderedCoordinatesLine.get());
    }

    public void addSites(SiteDTO... sites) {
      for (SiteDTO site : sites) {
        renderedTour.getSites().add(site.getID());
      }
    }


    public void addSites(ObservableList<RIS> sites) {
      for (RIS site : sites) {
        renderedTour.getSites().add(((SiteDTO) site).getID());
      }
    }

    public void removeSites(SiteDTO... sites) {
      for (SiteDTO site : sites) {
        renderedTour.getSites().remove((Integer) site.getID());
      }
    }

    public void removeSites(ObservableList<RIS> sites) {
      for (RIS site : sites) {
        renderedTour.getSites().remove((Integer) ((SiteDTO) site).getID());
      }
    }

    public void refreshCoordinates(MapView mapView) {
      detachFrom(mapView);
      renderedCoordinatesLine.set(new CoordinateLine(lastRenderedSites.entrySet().stream().filter(
          entry -> /*sitesRepository.getSitesOfTour().contains(entry.getKey())*/this.renderedTour
              .getSites().contains(((SiteDTO) entry.getKey()).getID())
          /*&& sitesRepository.getSitesOfTour().contains(entry.getKey())*/)
          .map(entry -> entry.getValue().getPosition()).collect(Collectors.toList()))
          .setColor(generateColor()));
      attachTo(mapView);
    }

    public Stream<Coordinate> getCoordinatesStream() {
      return renderedCoordinatesLine.get().getCoordinateStream();
    }

    @Override
    public String toString() {
      return "TourRendering{" + renderedTour.getID() + '}';
    }

    public boolean isSelected() {
      return selected.get();
    }

    public void setSelected(boolean selected) {
      this.selected.set(selected);
    }

    public BooleanProperty selectedProperty() {
      return selected;
    }

    public boolean containsSiteRendering(SiteRendering siteRendering) {
      return renderedTour.getSites().contains(siteRendering.getRenderedSiteID());
    }

    public Stream<Integer> getContainedSitesIDsStream() {
      return renderedTour.getSites().stream();
    }

    public List<SiteRendering<SiteDTO>> getSiteRenderingsInTour() {
      return lastRenderedSites.entrySet().stream().filter(
          entry -> /*sitesRepository.getSitesOfTour().contains(entry.getKey())*/renderedTour
              .getSites().contains(((SiteDTO) entry.getKey()).getID())).map(Entry::getValue)
          .collect(Collectors.toList());
    }

    public boolean isVisible() {
      return visible.get();
    }

    public void setVisible(boolean visible) {
      this.visible.set(visible);
    }

    public int getTourID() {
      return renderedTour.getID();
    }

  }
}
