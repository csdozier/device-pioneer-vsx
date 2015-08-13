/**
 *  Pioneer VSX-1130 Integration via REST API Callback
 *
 *  Make sure and publish smartapp after pasting in code.
 *  Author: Scott Dozier
 */
definition(
    name: "Pioneer VSX-1130 Integration",
    namespace: "scdozier",
    author: "Scott Dozier",
    description: "Pioneer VSX-1130 Integration",
    category: "My Apps",
    iconUrl: "http://i.imgur.com/xUCNTXB.jpg?1",
    iconX2Url: "http://i.imgur.com/NI16VUZ.jpg?1",
    oauth: true
)


preferences {
	section("Allow App to Control These Receivers...") {
		input "switches", "capability.switch", title: "Which Receivers?", multiple: true
	}
    
}

mappings {

	path("/vsxreceivers") {
		action: [
			GET: "listReceivers"
		]
	}
	path("/switches/:id") {
		action: [
			GET: "showSwitch"
		]
	}
	path("/vsxreceiver/:id/:command/:state") {
		action: [
			GET: "updateReceiver"
		]
	}
    
}

def installed() {}

def updated() {}


//switches
def listReceivers() {
	switches.collect{device(it,"switch")}
}

def showSwitch() {
	show(switches, "switch")
}
void updateReceiver() {
	update(switches)
}



def deviceHandler(evt) {}

private void update(devices) {
	log.debug "update, request: params: ${params}, devices: $devices.id"
	//def command = request.JSON?.command
    def command = params.command
    def state = params.state
    //let's create a toggle option here
	if (command) 
    {
		def device = devices.find { it.id == params.id }
		if (!device) {
			httpError(404, "Device not found")
		} else {
        	if(command == "power")
       		{
            	device.update("switch",state)
       		}
            if(command == "mute")
       		{
            	if (state == "on")
                {
            		device.update("mute","muted")
                }
                if (state == "off")
                {
              		device.update("mute","unmuted")
				}
            }
            if(command == "volumeset")
       		{
            	if (state.isNumber())
                {
              		device.update("level",state.toInteger())
				}
       		}
            if(command == "input")
       		{
              		device.update("status",state)
                    device.update("input",state)
                    device.update("trackDescription",state)
       		}
            if(command == "track")
       		{
                    device.update("trackDescription",state)
       		}
		}
	}
}

private show(devices, type) {
	def device = devices.find { it.id == params.id }
	if (!device) {
		httpError(404, "Device not found")
	}
	else {
		def attributeName = type == "motionSensor" ? "motion" : type
		def s = device.currentState(attributeName)
		[id: device.id, label: device.displayName, value: s?.value, unitTime: s?.date?.time, type: type]
	}
}


private device(it, type) {
	it ? [id: it.id, label: it.label, type: type] : null
}
