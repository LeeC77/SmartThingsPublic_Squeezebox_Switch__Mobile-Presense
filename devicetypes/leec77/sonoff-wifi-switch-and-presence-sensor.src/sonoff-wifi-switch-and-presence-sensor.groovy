/**
 *  Copyright 2019 LeeCharlton
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Sonoff Wifi Switch and Presence Sensor
 *
 *  Author: Lee Charlton (LeeC77)
 *  Date: 20-01-2019
 */
 
import groovy.json.JsonSlurper
import groovy.util.XmlSlurper

metadata {
	definition (name: "Sonoff Wifi Switch and Presence Sensor", namespace: "LeeC77", author: "Lee Charlton") {
        capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"
        capability "Presence Sensor" // Attribute: PresenceState ENUM: "not present" "present" - use for master presence
        capability "Configuration"
        capability "Health Check"
// Custom Attributes       
        attribute   "needUpdate", "string"
        attribute   "presence0", "string"
        attribute   "presence1", "string"
        attribute   "presence2", "string"
        attribute   "presence3", "string"
        attribute   "presence4", "string"
        attribute   "presence5", "string"
        attribute   "presence6", "string"
        attribute   "presence7", "string"
        attribute   "presence8", "string"
        attribute   "presence9", "string"
		attribute   "reset", "enum", ["default","check1","check2"]
        attribute   "led", "enum", ["on","off","follow","opposite","pending"]
        attribute	"multiswitch", "enum", ["down","up","on","off"]
       
        
// Custom Cammand
		command "reboot"
        command "resetDevice"
        command "timeout"
        command "dummy"
        command "ledon"
        command "ledoff"
        command "ledfollow"
        command "ledopposite"
        
	}

	simulator {
	}
    
    preferences {
    	section{
		
        input (description: "Assign a name and  host name to each presense sensor.", title: "Sensor set up", displayDuringSetup: false, type: "paragraph", element: "paragraph")
        input ( name: "name0",title: "Sensor 1 name")
        input ( name: "host0",title: "Sensor 1 host name", description: " Host name")
        input ( name: "name1",title: "Sensor 2 name")
        input ( name: "host1",title: "Sensor 2 host name", description: " Host name")
        input ( name: "name2",title: "Sensor 3 name")
        input ( name: "host2",title: "Sensor 3 host name", description: " Host name")
        input ( name: "name3",title: "Sensor 4 name")
        input ( name: "host3",title: "Sensor 4 host name", description: " Host name")
        input ( name: "name4",title: "Sensor 5 name")
        input ( name: "host4",title: "Sensor 5 host name", description: " Host name")
        input ( name: "name5",title: "Sensor 6 name")
        input ( name: "host5",title: "Sensor 6 host name", description: " Host name")
        input ( name: "name6",title: "Sensor 7 name")
        input ( name: "host6",title: "Sensor 7 host name", description: " Host name")
        input ( name: "name7",title: "Sensor 8 name")
        input ( name: "host7",title: "Sensor 8 host name", description: " Host name")
        input ( name: "name8",title: "Sensor 9 name")
        input ( name: "host8",title: "Sensor 9 host name", description: " Host name")
        input ( name: "name9",title: "Sensor 10 name")
        input ( name: "host9",title: "Sensor 10 host name", description: " Host name")
     
        
        }
        section {
        	input (description: "Set up device", title: "Device Settings", displayDuringSetup: false, type: "paragraph", element: "paragraph")
			input "logLevel", "enum", title: "Debug Logging Level", options: [0:"None", 1:"Reports", 99:"All"], displayDuringSetup: true 
            input "mode", "enum", title: "Set reporting mode: Interval or On-change ", 
            	options: [0:"Interval",1:"On-change",], displayDuringSetup: true
            input "freq", "number", title: "Set the check interval [sec]\r\n(sets reporting interval also)\r\nMinimum is 20 * number of sensors",range: "20..*", description: "seconds >20", displayDuringSetup: true
            input "hyst", "number",title: "Set the hysteresis\r\n(number of checks before decaring absent)\r\ninteger 0 - off to 99",range: "0..99", displayDuringSetup: true
			input "btn", "enum", title: "Set the physical switch connection to the relay ", 
            	options: [0:"disconnected",1:"On-change",], displayDuringSetup: true
}
       
	}
/////////////////////////////////////////////////////////// Tiles /////////////////////////////////////////////////////

	tiles (scale: 2){      
		standardTile("switch", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat"){
			//tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				state "on", label:'relay ${name}', action:"switch.off", backgroundColor:"#00a0dc", icon: "https://static.thenounproject.com/png/2207828-200.png", nextState:"turningOff"
				state "off", label:'relay ${name}', action:"switch.on", backgroundColor:"#ffffff", icon: "https://static.thenounproject.com/png/2207824-200.png", nextState:"turningOn"
				state "turningOn", label:'relay ${name}', action:"switch.off", backgroundColor:"#00a0dc", icon: "https://static.thenounproject.com/png/2207824-200.png", nextState:"turningOff"
				state "turningOff", label:'relay ${name}', action:"switch.on", backgroundColor:"#ffffff", icon: "https://static.thenounproject.com/png/2207828-200.png", nextState:"turningOn"
			//}
        }
        standardTile("led", "device.led", width: 2, height: 2, inactiveLabel: false, decoration: "flat"){
        		state "off", label:'led ${name}', action:"ledon", backgroundColor:"#ffffff", icon: "https://static.thenounproject.com/png/318148-200.png", nextState:"pending"
        		state "on", label:'led ${name}', action:"ledfollow", backgroundColor:"#00a0dc", icon: "https://static.thenounproject.com/png/318148-200.png", nextState:"pending"
				state "follow", label:'led ${name}', action:"ledopposite", backgroundColor:"#00a0dc", icon: "https://static.thenounproject.com/png/318148-200.png", nextState:"pending"
				state "opposite", label:'led ${name}', action:"ledoff", backgroundColor:"#00a0dc", icon: "https://static.thenounproject.com/png/318148-200.png", nextState:"pending"
                state "pending", label:'led ${name}'//, nextState:"on"
        }

		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", label:"", action:"refresh.refresh", icon: "https://static.thenounproject.com/png/15460-200.png"
		}
        standardTile("configure", "device.needUpdate", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "NO" , label:'', action:"configuration.configure", icon:"https://raw.githubusercontent.com/LeeC77/SmartThingsPublic/master/Sonoff/configokay.png"
            state "YES", label:'', action:"configuration.configure", icon:"https://raw.githubusercontent.com/LeeC77/SmartThingsPublic/master/Sonoff/configneeded.png"
        }

        standardTile("reset", "device.reset", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
            state "default", label:"Factory Reset", action:"timeout", backgroundColor:"#ffffff", nextState: "check1",  icon:""
            state "check1", label:"Are you sure?", action:"dummy",  backgroundColor:"#ffffff", nextState: "check2",  icon:""
            state "check2", label:"Really sure?", action:"resetDevice",  backgroundColor:"#ffffff" , nextState: "default",  icon:""
        }
      	valueTile("multiswitch", "device.multiswitch", width: 2, height: 2) {
    		state "multiswitch", label:'button ${currentValue}', icon: "https://static.thenounproject.com/png/384143-200.png"
		}
        valueTile("ip", "ip", width: 2, height: 1) {
    		state "ip", label:'IP Address\r\n${currentValue}'
		}
        valueTile("msgtime", "device.msgtime", width: 2, height: 1) {
    		state "msgtime", label:'${currentValue}'
		}
        valueTile("pres0", "device.presence0", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres1", "device.presence1", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres2", "device.presence2", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres3", "device.presence3", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres4", "device.presence4", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres5", "device.presence5", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres6", "device.presence6", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres7", "device.presence7", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres8", "device.presence8", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        valueTile("pres9", "device.presence9", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'${currentValue}', icon: "https://static.thenounproject.com/png/2305461-200.png"
        }
        
        // Used as the summary/main icon
        valueTile("dummy", "device.presence9", decoration: "flat", height: 2, width: 2, inactiveLabel: false){
        	state "val" ,  label:'multi presence', icon: "https://static.thenounproject.com/png/2305461-200.png"
        } 
    }

	main(["dummy"])
	details(["switch", "led","multiswitch","refresh","configure","ip","msgtime",
             "pres0","pres1","pres2","pres3","pres4","pres5","pres6","pres7","pres8","pres9","reset"])
}
/////////////////////////////////// Install & Update ///////////////////////////////////////////////////
def installed() {
	log.debug "installed()"
	configure()
}

def configure() {
    logging("configure()", 1)
    logging("DH settings ${settings}", 1)
    logging("Last known device settings ${state.currentProperties}",1)
    def cmds = []
    cmds = update_needed_settings()
    if (cmds != []) cmds
}

def updated()
{
    logging("updated()", 1)
    logging("DH settings ${settings}", 1)
    logging("Last known device settings ${state.currentProperties}",1)

    def cmds = [] 
    cmds = update_needed_settings()
    
    sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "lan", hubHardwareId: device.hub.hardwareID])// Attribute belongs to Heath Check Capability
    sendEvent(name:"needUpdate", value: device.currentValue("needUpdate"), displayed:false, isStateChange: true) // forces the isStateChange to true and prevents display in the mob App activity feed.
    if (cmds != []) response(cmds)
 	for (int i =0; i< 10; i++){ 
    	sendEvent(name:"presence${i}", value: null) // sets senors to null
    }
 
}
///////////////////////////////////////////////////////////////////////////////////////////////////////
private def logging(message, level) {
    if (logLevel != "0"){
    switch (logLevel) {
       case "1":
          if (level > 1)
             log.debug "$message"
       break
       case "99":
          log.debug "$message"
       break
    }
    }
}
//////////////////////////////////////// Parse /////////////////////////////////////////////////////
def parse(description) {
	//log.debug "Parsing: ${description}"
    def events = []
    def descMap = parseDescriptionAsMap(description)
    logging ("descMap: ${descMap}", 1) // LC
	def body
    //body = descMap["body"]
	//logging ("body: ${body}", 1) // LC
    
    
    if (!state.mac || state.mac != descMap["mac"]) {
		log.debug "Mac address of device found ${descMap["mac"]}"
        updateDataValue("mac", descMap["mac"])
	}
    
    if (state.mac != null && state.dni != state.mac) state.dni = setDeviceNetworkId(state.mac)
    if (descMap["body"]) body = new String(descMap["body"].decodeBase64())  //LC
    //logging ("body: ${body}", 1) // LC

	if (body && body != "") {
    	def timeZone = location.getTimeZone()
		def time = new Date(now())//.toString()
    	def timeString = (time.format("HH:mm:ss", timeZone)).toString()
    	
    	if(body.startsWith("{") || body.startsWith("[")) {
    		def slurper = new JsonSlurper()
    		def result = slurper.parseText(body)
    		log.debug "result: ${result}"
            def keyset = result.keySet()
            //log.debug "keyset: ${keyset}"
            events << createEvent(name: "msgtime", value: "${timeString}\n\r${keyset}", displayed: false)
			//  LC confirms settings status
    		if (result.containsKey("config")) {
            	def temp = result.config
                def keys = temp.keySet()
                def cmd =[]
                def key
                def value
                if (temp.relay =="0"){ // relay turned off in the case of a button press at the sonoff
                	if (device.currentValue("switch") != "off"){
                        events << createEvent(name:"switch", value: "off")
                        def eventString=syncState("off") // synchronise led and relay ststus
                        events << getAction (eventString)
                    }
                } 
                else if (temp.relay =="1"){// relay turned on in the case of a button press at the sonoff
                    if (device.currentValue("switch") != "on"){
                        events << createEvent(name:"switch", value: "on")
                        def eventString=syncState("on") // synchronise led and relay ststus
                        events << getAction (eventString)
                    }
                }
                for  (int i =0; i< keys.size(); i++){ // send the status of each element to config properties 
                	key = keys[i]
                    value= temp."${key}".toInteger()
                	cmd = [name:"${key}",value:value]
                	//logging ("${key} is ${value}", 1)
            		events << update_current_properties(cmd)
                }
    		}
            // LC confirm command response    
            if (result.containsKey("OK")){
            	if (result.OK == "1"){
                	if (result.containsKey("cmd")){
                    	if ((result.cmd == "on") | (result.cmd =="off")){ // switch successful
                    		events << createEvent(name:"switch", value: result.cmd)
                            def eventString=syncState(result.cmd) // synchronise led and relay ststus
                            events << getAction (eventString)
                        }
                        if ((result.cmd == "led on") | (result.cmd =="led off")){
                        	//log.debug ("state.led = ${state.led}")
                        	if (state.led == "off"){
                        		def splitstring = result.cmd.split(' ')[1]
                            	//log.debug splitstring
                        		events << createEvent(name:"led", value: splitstring)
                            }
                        }                      
                        if (result.cmd == "set addr"){
                        	logging ("Device configured with SmartThings Hub IP", 1)
                        }
                        // do other command responses here
                    }
                } else {
                	logging ("Response from device was NOK", 1)
                }
            }
            // LC button state
            if (result.containsKey("button")) {
            	if (result.button == "dwn"){
                	events << createEvent(name:"multiswitch", value: "down")
                }
                if (result.button == "up"){
                	//events << createEvent(name:"multiswitch", value: "up")
                	def value = toggleButton()
                    events << createEvent(name:"multiswitch", value: value)
                    events << getAction("/report")
                }
            }
            // LC confirm sensor status
            if (result.containsKey("sensors")){
            	def temp=result.sensors
            	def keys=temp.keySet()
                def key
                def name
                def value
                def cmd = []
                //logging ("temp ==> ${temp} and keys ==> ${keys}",1)
                for  (int i =0; i< keys.size(); i++){
                    key = keys[i]
                    //logging ("temp ==> ${temp} and keys ==> ${keys} key${i }==> ${key}" ,1)
                    value = temp."${key}".state
                    name = temp."${key}".name
                    logging ("${name} is ${value}",1)
                    // used to compare current setting with DH settings
                    cmd = [name:"name${i}",value:name]
            		events << update_current_properties(cmd)
                    cmd = [name:"host${i}",value:temp."${key}".hostName]
            		events << update_current_properties(cmd)                    
                    // Check if in or out and change tiles accordingly
                    if (temp.containsKey("sensor${i}")){
                        if (value == "in"){
                        	//sendEvent (name:"presence${i}", value: "${name}: present")
                            events << createEvent(name:"presence${i}", value: "${name}: present")
                        } else{
                            events << createEvent(name:"presence${i}", value: "${name}: absent")
                        }
                    } else { events << createEvent(name:"presence${i}", value: null) }
                }    
             }
    	} else {
        	log.debug "Response is not JSON: $body"
    	}
    }
    if (!device.currentValue("ip") || (device.currentValue("ip") != getDataValue("ip"))) events << createEvent(name: 'ip', value: getDataValue("ip"))
    return events
}


def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
        
        if (nameAndValue.length == 2) map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
        else map += [(nameAndValue[0].trim()):""]
	}
}

def syncState(status){
	log.debug ("syncState()")
    def eventString =""
    log.debug status
            if (status == "on"){ // turn on  / off led
            if (device.currentValue("led") =="follow"){
                eventString =("/led?state=1")
            } else if (device.currentValue("led") =="opposite"){
                eventString =("/led?state=0")
            }
        } else {
            if (device.currentValue("led") =="follow"){
                eventString =("/led?state=0")
            } else if (device.currentValue("led") =="opposite"){
                eventString =("/led?state=1")
            }
        }
	return eventString
}

//////////////////////////////////////////// Attributes //////////////////////////////////////////
def toggleButton(){
	if (state.button == "off") {
    	state.button = "on"
        return "on"
    } else {
    	state.button = "off"
    }
    return "off"
}
def getstatus(){
	log.debug "getstatus()"
    def cmds = []
    cmds << getAction("/report")
    return cmds
}

//////////////////////////////////////////// Commands ///////////////////////////////////////////
def on() {
	log.debug "on()"
    def cmds = []
    cmds << getAction("/on")
    //cmds << getAction("/led?state=1")
    //cmds << getAction("/addr?ip=192.168.1.119&port=39500")
    return cmds
}

def off() {
    log.debug "off()"
	def cmds = []
    cmds << getAction("/off")
    //cmds << getAction("/led?state=0")
    return cmds
}

def ledon() {
	log.debug "ledon()"
    state.led="off"
    def cmds = []
    cmds << getAction("/led?state=1")
    //cmds << createEvent ( name:"led", value: "follow")
    //sendEvent (name:"led", value: "on")
    return cmds
}
def ledfollow() {
	log.debug "ledfollow()"
    state.led="follow"
	//cmds << createEvent ( name:"led", value: "opposite")
    sendEvent (name:"led", value: "follow")
	
}

def ledopposite() {
	log.debug "ledopposite()"
    state.led="opposite"
    //cmds << createEvent ( name:"led", value: "off")
    sendEvent ( name:"led", value: "opposite")
}

def ledoff() {
    log.debug "ledoff()"
    state.led="off"
	def cmds = []
    cmds << getAction("/led?state=0")
    //cmds << createEvent ( name:"led", value: "on")
    //sendEvent ( name:"led", value: "off")
    return cmds
}

def refresh() {
	log.debug "refresh()"
    def cmds = []
    cmds << getAction("/report")
    cancelReset()
    return cmds
}

def ping() { // Is a command belonging to the Health Check capability.
    log.debug "ping()"
    refresh()
}

///////////////////////////////////////// Build the command mesages /////////////////////////////


private getAction(uri){ // builds the HTTP GET and sends it 
  updateDNI()
  def userpass
  //log.debug uri
  if(password != null && password != "") 
    userpass = encodeCredentials("admin", password)
    
  def headers = getHeader(userpass)

  def hubAction = new physicalgraph.device.HubAction(
    method: "GET",
    path: uri,
    headers: headers
  )
  return hubAction    
}

private postAction(uri, data){ 
  updateDNI()
  
  def userpass
  
  if(password != null && password != "") 
    userpass = encodeCredentials("admin", password)
  
  def headers = getHeader(userpass)
  
  def hubAction = new physicalgraph.device.HubAction(
    method: "POST",
    path: uri,
    headers: headers,
    body: data
  )
  return hubAction    
}

private setDeviceNetworkId(ip, port = null){
    def myDNI
    if (port == null) {
        myDNI = ip
    } else {
  	    def iphex = convertIPtoHex(ip)
  	    def porthex = convertPortToHex(port)
        myDNI = "$iphex:$porthex"
    }
    log.debug "Device Network Id set to ${myDNI}"
    return myDNI
}

private updateDNI() { 
    if (state.dni != null && state.dni != "" && device.deviceNetworkId != state.dni) {
       device.deviceNetworkId = state.dni
    }
}

private getHostAddress() {
    if (override == "true" && ip != null && ip != ""){
        return "${ip}:80"
    }
    else if(getDeviceDataByName("ip") && getDeviceDataByName("port")){
        return "${getDeviceDataByName("ip")}:${getDeviceDataByName("port")}"
    }else{
	    return "${ip}:80"
    }
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}

private encodeCredentials(username, password){
	def userpassascii = "${username}:${password}"
    def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    return userpass
}

private getHeader(userpass = null){
    def headers = [:]
    headers.put("Host", getHostAddress())
    headers.put("Content-Type", "application/x-www-form-urlencoded")
    if (userpass != null)
       headers.put("Authorization", userpass)
    return headers
}

def reboot() {  // not supported
	log.debug "reboot()"
    //unschedule(cancelReset)
    def uri = "/reboot"
    getAction(uri)
}

def resetDevice() {
	log.debug "reset()"
    //unschedule(cancelReset)
    def uri = "/reset"
    getAction(uri)
    //cancelReset()
}

def timeout(){
	logging ("set timeout",1)
    sendEvent(name: "reset" , value: "check1")
	runIn(3, cancelReset)  //3 seconds
}
def dummy(){
	logging ("dummy()",1)
    sendEvent(name: "reset" , value: "check2")
}

def cancelReset(){
	logging ("timeout",1)
	sendEvent(name: "reset" , value: "default") // reset tile
}

def sync(ip, port) {
    def existingIp = getDataValue("ip")
    def existingPort = getDataValue("port")
    if (ip && ip != existingIp) {
        updateDataValue("ip", ip)
        sendEvent(name: 'ip', value: ip)
    }
    if (port && port != existingPort) {
        updateDataValue("port", port)
    }
}


 /*  Code has elements from other community source @CyrilPeponnet (Z-Wave Parameter Sync). */

def update_current_properties(cmd)
{
	//logging ("update_current_properties ${cmd}",1)
    def currentProperties = state.currentProperties ?: [:]
    currentProperties."${cmd.name}" = cmd.value
    
    def tempCMD= settings."${cmd.name}"
    //logging ("Settings = ${tempCMD}",1)
    //logging ("Current = ${currentProperties}  remebered = ${state.currentProperties}",1)

    if (settings."${cmd.name}" != null)
    {

        if (settings."${cmd.name}".toString() == cmd.value.toString())
        {
           // sendEvent(name:"needUpdate", value:"NO", displayed:false, isStateChange: true)
        }
        else
        {
            sendEvent(name:"needUpdate", value:"YES", displayed:false, isStateChange: true)
            logging ("${cmd.name} needs to update from ${cmd.value} to ${tempCMD}  ${device.currentValue("needUpdate")}",1)
        }
    }
    state.currentProperties = currentProperties
}


def update_needed_settings()
{
    logging ("configuring",1)
    def cmds = []
   	def isUpdateNeeded = "NO"
    def ip= device.hub.getDataValue("localIP")
    def port= device.hub.getDataValue("localSrvPortTCP")
    cmds << getAction("/addr?ip=${ip}&port=${port}")
    cmds << getAction("/clear") // clear the sensor table
    def name
    def hostname
    def listLength ="${settings.keySet()}"
    listLength = listLength.split('name')
    listLength=listLength.size()
    if (listLength >0){
    	logging ("found at least one presence setting",1)
        for  (int i =0; i< 10; i++){
        	name =settings."name${i}" as String
            //logging ("${name}",1)
        	if (name){
            hostname = settings."host${i}" as String
            //logging ("${domain}",1)
            	if (hostname){
            		logging ("${name} --> ${hostname}",1)
        			cmds << getAction("/new?name=${name}&hostname=${hostname}")
                   // cmds << new physicalgraph.device.HubAction("delay 1000")
                }
        	}
        }
    }
    def mode
    def freq
    def hyst
    def btn
    if (settings.mode){
    	mode = settings.mode.toInteger()
    } else {
    	mode = 0
    }
    if (settings.freq){
    	freq = settings.freq.toInteger()
    } else {
    	freq	 = 180
    }
    if (settings.hyst){
    	hyst = settings.hyst.toInteger()
    } else {
    	hyst = 0
    }
     if (settings.btn){
    	btn = settings.btn.toInteger()
    } else {
    	btn = 1
    }   
    cmds << getAction("/mode?mode=${mode}&freq=${freq}&hyst=${hyst}&btn=${btn}")
   	logging ( "Configuration message ${cmds}",1)
    // LC

    ledoff()
    sendEvent(name:"needUpdate", value: isUpdateNeeded, displayed:false, isStateChange: true)
    return cmds
}

