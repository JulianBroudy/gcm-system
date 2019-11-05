package com.broudy.gcm.boundary.enhanced.controls;

import java.net.URL;

/**
 * This class contains all needed icons for the map.
 * <p>
 * Created on the 1st of August, 2019.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public enum MapLocatorIcons {

  REGULAR_SITE_LOCATOR {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/site-locator.png");
    }
  }, NICE_SITE_LOCATOR_BLACK {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/site-locator-black.png");
    }
  }, NICE_SITE_LOCATOR_RED {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/site-locator-red.png");
    }
  }, BLACK {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-black.png");
    }
  }, GREY {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-grey.png");
    }
  }, BLUE {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-blue.png");
    }
  }, BLUE_LIGHT {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-blue-light.png");
    }
  }, YELLOW {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-yellow.png");
    }
  }, GREEN_LIGHT {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-green-light.png");
    }
  }, PINK {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-pink.png");
    }
  }, PURPLE {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/cyclic-site-locator-purple.png");
    }
  }, SPHERICAL_GREEN_LIGHT {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/spherical-site-locator-green-light.png");
    }
  }, SPHERICAL_BLUE {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/spherical-site-locator-blue.png");
    }
  }, SPHERICAL_PINK {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/spherical-site-locator-pink.png");
    }
  }, SPHERICAL_YELLOW {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/spherical-site-locator-yellow.png");
    }
  }, PIN_GREEN_LIGHT {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/pin-site-locator-green-light.png");
    }
  }, PIN_BLUE {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/pin-site-locator-blue.png");
    }
  }, PIN_PINK {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/pin-site-locator-pink.png");
    }
  }, JUST_GREEN_LIGHT {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/site-locator-green-light.png");
    }
  }, JUST_BLUE {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/site-locator-blue.png");
    }
  }, JUST_PINK {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/site-locator-pink.png");
    }
  }, ATTENTION_BLUE {
    @Override
    public URL getImageURL() {
      return getClass().getResource("/images/mapJFX/attention-site-locator-blue.png");
    }
  };

  /**
   * @return the icon's path.
   */
  public abstract URL getImageURL();
}
