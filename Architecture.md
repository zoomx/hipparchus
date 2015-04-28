# Architecture of the system #

---


<a href='https://picasaweb.google.com/lh/photo/p6jqeIQVAvF_1h6odtFwQtMTjNZETYmyPJy0liipFm0?feat=embedwebsite'><img src='https://lh4.googleusercontent.com/-UEpeA4UedBw/TwhIhEkPIAI/AAAAAAAAAJ4/MHIrU5hRqr0/s800/Hypparchos_Architecture.jpg' height='400' width='560' /></a>

### Telescope ###

---

The telescope component is the part of the system that provides the hardware parts. The hardware parts corresponds to 2 DC motors and 2 PS/2 mouse. DC motors performs the movement of the scope and has to be applied to the scope axis. The 2 mouse provides position feedback to the system. These PS/2 mouse interface's with the Arduino board and provides the Altitude and Azimuth of the scope.

### Electronics ###

---

The electronics is the second part of the system. This part provides an interface layer between the telescope parts and the software. It is a middleware that helps the system to translate low level commands (e.g motor movement) into the high level layer. In this layer sits the Arduino board and all the relative electronics like motor driver, bluetooth module and certain hardware parts.

### Software ###

---

This is the last layer of the system that provides all the sophisticated business logic. It provides a mechanism for calculating alt-az to ra-dec  using the matrix method transformation of a 3 known stars. These stars are being selected by a list that corresponds to the visible stars in our location. The software sends and gets the position of the searching object to be calculated. Finally the software provides a user interface for inserting and monitor all the required aspects for the goto.