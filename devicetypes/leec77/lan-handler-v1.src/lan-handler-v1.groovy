import groovy.json.JsonSlurper

// Handles LMS(SBS),Lan wifi Pinger (wifidetect),Squeezebox sensors, and DLNA

metadata {
	definition (name: "LAN Handler V1", namespace: "LeeC77", author: "Lee Charlton") {
		capability "Sensor"
    	attribute "hubInfo", "string"
    	attribute "sbsresponse", "string"
    	attribute "wifidetect", "string"
    	// added to support SB sensor reports //
    	attribute "luminancetouch", "number"
    	attribute "luminancebed", "number"
    	attribute "luminancekitr", "number"
    	attribute "luminancekitl", "number"
    	attribute "proximitytouch", "string"
    	// added to handle SB DLNA responses //
    	attribute "dlnaresponse", "string"
	}
	preferences {
		section("Debug") {
    		input "level","enum", title: "What level of debug? ", options: ["0","1","2"], required: true

    	}
	}
    
    // define tiles use attribute name as device.
    // Used as the summary/main icon
	tiles (scale: 2){
    	valueTile("hubInfo", "device.hubInfo", decoration: "flat", height: 1, width: 1, inactiveLabel: false, canChangeBackground: true) {
            	state "hubInfo", label:'${currentValue}', icon: "https://static.thenounproject.com/png/1962847-200.png"
    	}
    // Used ato dispaly server response
    	valueTile("info", "device.hubInfo", decoration: "flat", height: 6, width:6, inactiveLabel: false, canChangeBackground: true){
        	state "hubInfo" ,  label:'${currentValue}'//, icon: "https://static.thenounproject.com/png/1962847-200.pn"
    	} 
	}
	// Tile Layouts:
	main(["hubInfo"])
    details(["info"])
}


def parse(description) {
	if ((level as Integer) > 0){log.trace "LAN Handler ${description}"}
    def descMap = parseDescriptionAsMap(description)
    def body = new String(descMap["body"].decodeBase64())
    def slurper = new JsonSlurper()
    def processed = false
    def result
    try {  //json
    	result = slurper.parseText(body)
    	if ((level as Integer) > 1){log.debug result}
        /***********************************************/
		/* section added to catch messages from bridge */
		if (result.containsKey("SBSResponse")) {
        	def message = "${result.SBSResponse}"
            message=message.replaceAll("[\n\r]", "") // strips off the CRLF
            notify (message) // 19 April 2020
       		processed = true
            sendEvent (name: "sbsresponse", value:result.SBSResponse)
            if ((level as Integer) > 1){log.trace "LAN Handler SB response"}
    	}
		/***********************************************/
		/* section catches messages from wifi detect   */
    	if (result.containsKey("message")) {
    		processed = true //LC
            notify (result.message) // 19 April 2020
        	sendEvent(name:"wifidetect",value:result.message)
    	}
		/***********************************************/
		/* section catches messages from Squeezebox sensors   */
    	if (result.containsKey("sensor")) {
       		processed = true //LC
            def message = "${result.sensor}"
            message=message.replaceAll("[\n\r]", "")
            notify (message) // 19 April 2020
       		if ((level as Integer) > 1){log.debug "Sensor response ${result.sensor}"}
       		def workstring = result.sensor
       		// expected format of result.sensor = "Sensor:lounge,Paremeter:Ambient,Value:xxxx"
       		def splitstring = workstring.split(",")
       		def Sensortype=splitstring[0].split(":")
       		def parametertype=splitstring[1].split(":")
       		def resultval=splitstring[2].split(":")
       		if ((level as Integer) > 1){log.debug "${Sensortype[1]} : ${parametertype[1]} : ${resultval[1]}"}
       		if (Sensortype[1] == "lounge"){
       			if(parametertype[1] == "Ambient"){
            		sendEvent(name:"luminancetouch",value:resultval[1])
            	}
            if(parametertype[1]=="Proximity"){
            	sendEvent(name:"proximitytouch",value:resultval[1])
            	}
       		}
       	else if (Sensortype[1] == "bedroom"){
       		if(parametertype[1] == "Ambient"){
            	sendEvent(name:"luminancebed",value:resultval[1])
            	}
       		}
       		else if (Sensortype[1] == "kitchen R"){
       			if(parametertype[1] == "Ambient"){
            		sendEvent(name:"luminancekitr",value:resultval[1])
            	}
       		}
       		else if (Sensortype[1] == "kitchen L"){
       			if(parametertype[1] == "Ambient"){
            		sendEvent(name:"luminancekitl",value:resultval[1])
            	}
       		} 
    	}
	}
    catch (Throwable t) {
    	try {
			result = parseLanMessage(description)
        	if ((result =~ /Logitech/) || (result =~ /xml/)) {
        		processed = true
    			if ((level as Integer) > 1){log.trace"Response form Logitech Server"}
                notify ("DLNA Response") // 19 April 2020
                }
                sendEvent (name: "dlnaresponse", value:description) // LC Original need this              
			}
        catch (Throwable tt) {
        	sendEvent(name: "parseError", value: "$tt", description: description)
			throw tt
            }
        }
    if(processed == false){ //LC
    	if ((level as Integer) > 0){log.trace "LAN slurper unexpected message ===>${description} \n\r ===> ${result}"} //LC
    	} //LC
    return 
}

def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
        if (nameAndValue.length == 2) map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
        else map += [(nameAndValue[0].trim()):""]
	}
}

def notify(message){
    if ((level as Integer) > 0){log.debug "In notify () > ${message}"}
	def timeZone = location.getTimeZone()
	def time = new Date(now())//.toString()
    def timeString = (time.format("HH:mm", timeZone)).toString()
    message = "${timeString}: ${message}"
    def notifiyString = device.currentValue("hubInfo")
    def splitString=notifiyString.split('\r\n') // split into individual messages
    def sizeOf = splitString.size()
    if (sizeOf > 12){sizeOf=12} // how many old messages todisplay
    	for (int i =0; i< sizeOf; i++){
    		message = ("${message}\r\n${splitString[i]}")// add new notification to next most recent
    }
    sendEvent(name:"hubInfo", value: message/*, displayed:false*/)
    if ((level as Integer) > 1){log.debug " Done one"}
}