# Telescope #

---

<a href='https://picasaweb.google.com/lh/photo/uMcrSJmvrh4EQrpVdJ4j-9MTjNZETYmyPJy0liipFm0?feat=embedwebsite'><img src='https://lh3.googleusercontent.com/-4FifTiNSQA0/TxR0UmxA99I/AAAAAAAAAP4/zHPMyDBudV4/s640/telescope.jpg' height='300' width='420' /></a>

### Motors ###

---

The Motor characteristics are:
  * DC motors with gearbox.
  * 2RPM output.
  * Torque power 40Ncm
  * Power supply 12VDc.

The motors are coneccted with a dual h-bridge driver which accepts the pwm signal from Arduino. The motors can move in both directions very slowly providing slew and goto movement. These motors can be purchased from ebay at very low cost (i.e. 43.2Euro).

### Mouses ###

---

Two mouses have the role of the feedback sensors (i.e. encoders). This in conjunction with the DC motors we can implement a closed loop servo system. The mouses are placed in the circumference of the Alt and Az bearings. Then we can recieve the current position of the scope. This is an advantage because we can increase the resolution of the mouse feedback by increasing the diameter of the alt/az bearings. In order to determine the steps to go we need to determine the steps per revolution of each axis (i.e. SPR).