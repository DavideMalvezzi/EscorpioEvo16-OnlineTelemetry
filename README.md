# EscorpioEvo16 Online Telemetry
##Introduction
This collection of software is used together the [EscorpioEvo16-Dashboard](https://github.com/DavideMalvezzi/EscorpioEvo16-Dashboard) to 
provide an online telemetry system accessible [here](http://www.teamzeroc.it/telemetry).

##Android App
The android app is installed on a smartphone placed inside the prototype, near the Dashboard. When the app is started, it automatically 
tries to establish a connection with the Dashboard bluetooth module. If the connection is successful, all the data sent from the Dashboard
through the bluetooth module is redirected by the app to the WebSocket Server via internet.

##WebSocket Server
The web socket server is written with NodeJS and hosted on [modulus.io](https://modulus.io/).
When the server receive some data from the Android App it broadcasts the data to all the connected Online Telemetry clients.

##Online Telemetry client
The Online Telemetry client is a site written in HTML5, JS and PHP that provides a collections of widgets to display the incoming data.

<p align="center"><img src="https://s9.postimg.io/3v5lanuyn/a98eb67d_31c7_4793_8cf4_5d6f6c08d713.jpg"></p>
