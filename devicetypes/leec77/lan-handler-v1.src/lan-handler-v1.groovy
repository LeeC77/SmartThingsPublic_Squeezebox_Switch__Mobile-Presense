import groovy.json.JsonSlurper

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
    
    // define tiles use attribute name as device.
    // Used as the summary/main icon
	tiles (scale: 2){
    valueTile("hubInfo", "device.hubInfo", decoration: "flat", height: 6, width: 6, inactiveLabel: false, canChangeBackground: true) {
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
	//log.trace "LAN Handler ${description}"
    def descMap = parseDescriptionAsMap(description)
    def body = new String(descMap["body"].decodeBase64())
    def slurper = new JsonSlurper()
    def processed = false
    def result
    try {  //json
    	result = slurper.parseText(body)
    	
    	//log.debug result

		/* section added to catch messages from bridge */
		if (result.containsKey("SBSResponse")) {
       		processed = true
        	sendEvent(name:"hubInfo", value:result.SBSResponse)
        	//log.debug "SBServer response ${value:result.SBSResponse}"
        	sendEvent (name: "sbsresponse", value:result.SBSResponse)
            //log.trace "LAN Handler SB response"
    	}
		/***********************************************/

		/* section catches messages from wifi detect   */
    	if (result.containsKey("message")) {
    		processed = true //LC
        	sendEvent(name:"hubInfo", value:result.message)
        	sendEvent(name:"wifidetect",value:result.message)
    	}
		/***********************************************/
		/* section catches messages from Squeezebox sensors   */
    	if (result.containsKey("sensor")) {
       		processed = true //LC
       		sendEvent(name:"hubInfo", value:result.sensor)
       		//log.debug "Sensor response ${result.sensor}"
       		def workstring = result.sensor
       		// expected format of result.sensor = "Sensor:lounge,Paremeter:Ambient,Value:xxxx"
       		def splitstring = workstring.split(",")
       		//log.debug "${splitstring[1]}"
       		def Sensortype=splitstring[0].split(":")
       		def parametertype=splitstring[1].split(":")
       		def resultval=splitstring[2].split(":")
       
       		//log.debug "${Sensortype[1]} : ${parametertype[1]} : ${resultval[1]}"
    
       		if (Sensortype[1] == "lounge"){
       			if(parametertype[1] == "Ambient"){
            		sendEvent(name:"luminancetouch",value:resultval[1])
                	//log.debug "event sent"
                	//return
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
    			//log.trace"Response form Logitech Server"
                sendEvent(name:"hubInfo", value:"DLNA Response")
                }
                sendEvent (name: "dlnaresponse", value:description) // LC Original need this
                //sendEvent (name: "dlnaresponse", value:result)// LC
                log.trace "DLNA Response= ${description}"
                //log.trace "DLNA Response headers = ${result.header}"
                //log.trace "DLNA Parsed= ${result}"
                //log.trace "DLNA sid= ${result.headers["sid"]}"
                //log.trace "DLNA xml= ${result.xml}"
                
                //temp.each {key, val ->
    			//log.debug "result key: $key, value: $val"}
                
			}
        catch (Throwable tt) {
        	sendEvent(name: "parseError", value: "$tt", description: description)
			throw tt
            }
        }
    if(processed == false){ //LC
    	log.trace "LAN slurper unexpected message ===>${description} \n\r ===> ${result}" //LC
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