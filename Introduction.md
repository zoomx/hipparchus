# Introduction #

---


Nowadays amateur astronomer's community with dobsonian mounts cannot be able to upgrade their instruments into an electronically assisted systems with autotracking and goto capabilities due to the high cost of the already commercial systems. The Dobsonian owners need to have an affordable and reliable tracking platform. Especially when the observer uses high magnifications the manually corrections are very dificult and tedious. Thus the observation with a dobsonian mount is much more dificult than using an equatorial mount.

The Hipparchus is an open source hardware and software project that aims to help amateur astronomers to implement an auto tracking platform for Dobsonian telescopes in a very affordable way. This project uses the [Arduino](http://www.arduino.cc) board (Arduino Uno version) that communicates with the hardware and software parts of the system, providing an interface for these two components. The project provides all the necessary guidelines concerning the hardware parts construction.  Furthermore the software and firmware are available and free to use.

# Functionalities #

---

The Hypparchus system has two main functionalities:
  * Automatic mode
  * Manual mode

The Automatic mode provides goto and auto-tracking capabilities by using a user interface running on a laptop or PC for entering the object data (i.e RA and Dec). This mode uses the 2 star alignment method in order to be able to locate and track automatically the objects in the sky. The first version of the system will use a software running on a PC or laptop. The second version will use a smartphone for instead.

The manual mode is a secondary alternative which helps the observer to make slew movements providing an electronically assisted way for tracking the objects. Furthermore the user can adjust the speed on each axis in order to mach the tracking speed by hand for long time observations. This mode doesn't need a user interface for 2 star alignment. The movement of the scope is made by a virtual handpad running on a smartphone with buttons for position and speed.