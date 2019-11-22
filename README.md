# GCM System - A Maps Editor & Store 

**A multi-threaded full-stack scalabale-ish JavaFX UX focused TCP/IP Client-Server communication adhering to the 3-tier client-server architicture as well as the Entity-Control-Business architictural pattern implementing OCSF.**

[![JavaDoc](https://img.shields.io/badge/info-JavaDoc-informational)](https://julianbroudy.github.io/gcm-system/index.html?overview-summary.html)


<br>

## Current Features 
[![Samples](https://img.shields.io/badge/%20%20OR%20%20-Click%20to%20Scroll%20Down%20to%20Samples!-brightgreen?style=flat)](https://github.com/JulianBroudy/gcm-system#samples)
#### GCM System provides services not only to the company's employees, but to its customers as well.

**Employees - *with various ranks* - can:**
- Add a city to their workspace. <br>Which renders the city unaccessible to the rest of the employees until it is released or discarded.
- Create new cities.
- Attach and/or remove cities' maps.
- Edit maps:
  - Create, modify and delete sites.
  - Create, modify and delete tours.
- Change cities' prices *(Rank: Manager & up)*.
- Request modified city release approval or cancel an existing request.
- Request city's new prices approval or cancel an existing request *(Rank: Manager)*.
- View modified city and approve/reject release request *(Rank: Manager)*.
- Approve/reject city's new prices approval *(Rank: Company Manager & up)*.
- Edit their information.
- Exit or log out.
<br>

**Unregistered customers can:**
- Sign up.

**Registered as well as unregistered customers can:**
- Browse the cities catalog - *LOCKED MODE* if unregistered.
- Filter catalog by:
  - City name.
  - Site name.
  - City's description.
- Exit

**Registered customers can:**
- Login.
- Edit their credentials: name, password, etc...
- Change their billing information.
- Purchase a city:
  - Subscribe - customer gains a 6-months online access (including future updates) with unlimited number of downloads and the ability to    hide/show maps' components before downloading it.
  - One Time Purchase - customer gets a one time download for each map of purchased city.
- Extend subscription.
- View active subscriptions.
- View purchase history.
- Donate.
- Log out.

<br><br>

## Samples!
[![WAIT](https://img.shields.io/badge/!!!-Please%20give%20the%20Gifs%20a%20moment%20to%20properly%20load.-grey?style=flat&labelColor=bf0f1b)](https://github.com/JulianBroudy/gcm-system/blob/master/README.md#samples)



### Maps Editor - Tours Editing
![Sample 3](https://github.com/JulianBroudy/gcm-system/blob/master/Especially%20For%20You/Map%20Editor%20-%20tours.gif)

<br>

### Maps Editor - Sites Editing
![Sample 2](https://github.com/JulianBroudy/gcm-system/blob/master/Especially%20For%20You/Map%20Editor%20-%20sites.gif)

<br>

### City Release Process
![Sample 5](https://github.com/JulianBroudy/gcm-system/blob/master/Especially%20For%20You/City%20Release%20Requests.gif)

<br>

### Cities Editor
![Sample 1](https://github.com/JulianBroudy/gcm-system/blob/master/Especially%20For%20You/Cities%20Editor.gif)

<br>

### Search Engine, Subscription & Download Processes
![Sample 7](https://github.com/JulianBroudy/gcm-system/blob/master/Especially%20For%20You/SubscribeAndDownload%20Sample.gif)

<br>

### Search Engine of an Unregistered Customer
![Sample 7](https://github.com/JulianBroudy/gcm-system/blob/master/Especially%20For%20You/Just%20Browse%20Sample.gif)

<br>

### Sign Up Process
![Sample 6](https://github.com/JulianBroudy/gcm-system/blob/master/Especially%20For%20You/Sign%20Up%20Sample.gif)

<br>

## Built With
* [OCSF](http://www.site.uottawa.ca/school/research/lloseng/supportMaterial/ocsf/ocsf.html) - The client-server framework used
* [MapJFX](https://github.com/sothawo/mapjfx/) - The map library that this app wouldn't be possible without!
* [JFoenix](http://www.jfoenix.com/) - GUI library
* [ControlsFX](https://github.com/controlsfx/controlsfx/) - GUI library
* [FontAwesomeFX](https://bintray.com/jerady/maven/FontAwesomeFX/) - Icons library
* [Maven](https://maven.apache.org/) - Dependency Management
* [Guice](https://github.com/google/guice/) - Used to improve overall performance and introduce a centralized class responsible for scene switching

<br>

## Authors

* **[Julian Broudy](https://github.com/JulianBroudy)**

<br>

## Acknowledgments

* **[MVP Java](https://github.com/mvpjava)**



