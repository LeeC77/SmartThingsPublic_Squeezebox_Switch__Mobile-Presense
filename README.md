# SmartThingsPublic

## Contents of repository by project  
Readme Contents List   
- Introduction 
- Squeezebox Switch Project  
- Mobile Presence Project  
	- Files required  
	- Installation and setup Summary  

## Introduction
All code contained here has been developed and tested (to some degree) on Samsung SmartThings using the Hub V2 and the classic App.   
Often additional developments not in Groovy are reference, linked or retained in the repository to build devices or implement functions elsewhere in my smart home environment.

## Squeezebox Switch Project
The files in this repository are the latest versions of the SmartThings Apps and device handlers. 
A more complete project with all the insatallation and  more detailed instruction is available here: https://github.com/LeeC77/SmartThings-and-Squeezebox-Controller. The intent is to keep this project up to date with significant changes.

## Mobile Presence Project
This project uses a repurposed Sonoff switch to track the connection status of up to 10 Wi-Fi enabled devices or mobile phones. The Sonoff switch communicates over WiFi to a device handler that make the connection status of each mobile device available in SmartThings. Finally a Smart App subscribes to the status of the device handler and sets the status of forcible presence devices accordingly.  
### Files required:  
Forcible Mobile Presence device handler by krlaframboise (device handler):  
	https://github.com/krlaframboise/SmartThings/tree/master/devicetypes/krlaframboise/forcible-mobile-presence.src  
Sonoff Wifi Switch and Presence Sensor (device handler): https://github.com/LeeC77/SmartThingsPublic/tree/master/devicetypes/leec77/sonoff-wifi-switch-and-presence-sensor.src  
	This requires the Sonoff switch to be repurposed and the firmware updated:  
	https://github.com/bench745/Sonoff-SmartThings-Compatible-Firmware  
Sonoff-connect a Service Manager for Sonoff switches (smart app) originally  produced by erom123 and updated to support the ‘Sonoff Wifi Switch and Presence Sensor’ device handler above:  
	https://github.com/LeeC77/SmartThingsPublic/blob/master/smartapps/leec77/sonoff-connect.src/sonoff-connect.groovy  
	Installation set up etc see this thread: https://community.smartthings.com/t/release-sonoff-sonoff-th-s20-dual-4ch-pow-touch-device-handler-smartapp-5-10-smart-switches/45957  
Presence Coordinator Sonoff (smart app):  
https://github.com/LeeC77/SmartThingsPublic/blob/master/smartapps/leec77/presense-coordinator-sonoff.src/presense-coordinator-sonoff.groovy
### Installation and setup Summary  
Once you have made your Sonoff device install the smart Apps and device handlers through the SmartThings IDE.  
Follow erom123’s thread to install the child Sonoff Wi-Fi Switch and Presence Sensor device.  
Set up the Presence Coordinator Sonoff with the static Wif addresses of the mobile devices you want to monitor.   
Set up Virtual Forcible Mobile Presence, one for each person who has a Wi-Fi enable device.  
Use the Presence Coordinator Sonoff to link the Virtual Forcible Mobile Presence to the Presence Coordinator Sonoff.   
More detail is given for the Presence Coordinator Sonoff in the file here:   https://github.com/LeeC77/SmartThingsPublic/blob/master/Sonoff%20wifi%20and%20mobile%20presence%20sensor.pdf

