/**
 *  Pioneer VSX-1130 Network Receiver
 *     Works on VSX1130,VSX1124
 *    SmartThings SmartDevice to connect your Pioneer VSX Network Receiver to SmartThings via a REST to socket gateway.
 *
 * 
 */


 

metadata {
	definition (name: "Pioneer VSX Network Receiver", namespace: "scdozier", 
    	author: "Scott Dozier") {
        capability "Actuator"
		capability "Switch" 
        capability "Polling"
        capability "Music Player"
        
        attribute "input", "string"
        
        command "inputSelect"
        command "inputNext"
        command "inputPrev"
        command "update"
      	}

    preferences {
        input("gatewayIp", "text", title: "IP", description: "Gateway IP Address",defaultValue: "8.8.8.8")
        input("gatewayPort", "number", title: "Port", description: "Gateway Port Number (usually 80 or 443)",defaultValue: 80)
        input("zone", "text", title: "Zone (main or hdz)", description: "Zone (main or hdz)",defaultValue: "main")
    }
	simulator {
		// TODO-: define status and reply messages here
	}

	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, canChangeBackground: true) {
            state "on", label: '${name}', action:"switch.off", backgroundColor: "#79b821", icon:"st.Electronics.electronics16"
            state "off", label: '${name}', action:"switch.on", backgroundColor: "#ffffff", icon:"st.Electronics.electronics16"
        }
		standardTile("poll", "device.poll", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
			state "poll", label: "", action: "polling.poll", icon: "st.secondary.refresh", backgroundColor: "#FFFFFF"
		}
        valueTile("input", "device.input", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false,decoration: "flat") {
			state "input", label: '${currentValue}', action: "inputNext", icon: "", backgroundColor: "#FFFFFF"
  			state "wait", label: '${currentValue}', action: "inputNext", icon: "", backgroundColor: "#79b821"

		}
        standardTile("mute", "device.mute", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state "muted", label: '${name}', action:"unmute", backgroundColor: "#79b821", icon:"st.Electronics.electronics13"
            state "unmuted", label: '${name}', action:"mute", backgroundColor: "#ffffff", icon:"st.Electronics.electronics13"
		}
        controlTile("level", "device.level", "slider", height: 1, width: 2, inactiveLabel: false, range: "(0..100)") {
			state "level", label: '${name}', action:"setLevel"
		}
        
		main "switch"
        details(["switch","input","mute","level","poll"])
	}
}


private void update(attribute,state) {
    log.debug "update state, request: attribute: ${attribute}  state: ${state}"
    sendEvent(name: attribute, value: state) 
    }

def parse(String description) {
	log.debug "Parsing '${description}'"
    
 	def map = stringToMap(description)
    if(!map.body) { return }
	def body = new String(map.body.decodeBase64())

	def statusrsp = new XmlSlurper().parseText(body)
	def power = statusrsp.Main_Zone.Basic_Status.Power_Control.Power.text()
    if(power == "On") { 
    	sendEvent(name: "switch", value: 'on')
    }
    if(power != "" && power != "On") { 
    	sendEvent(name: "switch", value: 'off')
    }
    
    def inputChan = statusrsp.Main_Zone.Basic_Status.Input.Input_Sel.text()
    if(inputChan != "") { 
    	sendEvent(name: "input", value: inputChan)
	}

    def muteLevel = statusrsp.Main_Zone.Basic_Status.Volume.Mute.text()
    if(muteLevel == "On") { 
    	sendEvent(name: "mute", value: 'muted')
	}
    if(muteLevel != "" && muteLevel != "On") {
	    sendEvent(name: "mute", value: 'unmuted')
    }
    
    
    if(statusrsp.Main_Zone.Basic_Status.Volume.Lvl.Val.text()) { 
    	def volLevel = statusrsp.Main_Zone.Basic_Status.Volume.Lvl.Val.toBigInteger()
    	if(volLevel != device.currentValue("level") as Integer) {
    		sendEvent(name: "level", value: volLevel)
        }
    }

    //log.debug "MATCH: '${volLevel}'"
}

def setLevel(val) {
	sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "level", value: val)    
    return request("volumeset/${val}")
}

def on() {
	log.debug('turning on')
	return request('power/on')
}

def off() { 
	return request('power/off')
    }

def mute() { 
	sendEvent(name: "mute", value: "muted")
    return request('mute/on')
}

def unmute() { 
	sendEvent(name: "mute", value: "unmuted")
    return request('mute/off')
}

def inputNext() { 
	sendEvent(name: "input", value: "wait")
	return request('input/next')
}


def inputPrev() { 
	sendEvent(name: "input", value: "wait")
	return request('input/previous')
}

def inputSelect(code) {
 	sendEvent(name: "input", value: channel	)
	return request("input/set/${code}")

}

def poll() { 
	refresh()
}

def refresh() {
	return request("refresh")
}

def play()
{
	sendEvent(name: 'state', value: 'playing')
	return on()
}

def pause()
{
	sendEvent(name: 'state', value: 'paused')
	return off()
}

def stop()
{
	return off()
}

def nextTrack()
{
	return inputNext()
}

def previousTrack()
{
	return inputPrev()
}

def request(request) { 
	log.debug("Request:'${request}'")
    def hosthex = convertIPtoHex(gatewayIp)
    def porthex = convertPortToHex(gatewayPort)
    log.debug("${device.deviceNetworkId}")
    def hubAction = new physicalgraph.device.HubAction(
   	 		'method': 'GET',
    		'path': "/pioneervsxcontrol/${zone}/${request}"+"&apiserverurl="+java.net.URLEncoder.encode(apiServerUrl("/api/smartapps/installations"), "UTF-8"),
        	'body': '',
        	'headers': [ HOST: "${hosthex}:${porthex}" ]
		) 
        
    log.debug hubAction
    return hubAction
}


private String convertIPtoHex(ipAddress) { 
	log.debug('convertIPtoHex:'+"${ipAddress}")
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	log.debug('convertIPtoHex:'+"${port}")
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}
