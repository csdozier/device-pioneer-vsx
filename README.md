# device-pioneer-vsx
Smartthings Device Type for Pioneer VSX 1130/1124 Receivers


This allows you to connect up a VSX-1130 or VSX-1124 (likely others) receiver to smartthings.  The device utilizes a proxy which connects to the receiver via a socket connection.  


##Installation
1.  Create a new Smartthings device type using device-type-pioneervsx.groovy
2.  Add your receiver to SmartThings using the above device type. Set the gateway IP address to the computer which will be running the python script.
3.  Create a new smartApp using smartapp-pioneervsx-integration.groovy
4.  Gather the app id, device id, and [access token](http://docs.smartthings.com/en/latest/smartapp-web-services-developers-guide/tutorial-part2.html#get-an-access-token) from smartthings.
5.  Copy vsxproxysrvr.py and vsxproxysrvr.cfg on to a computer (or raspberry pi) with Python 2.7
6.  Edit vsxproxysrvr.cfg
7.  Set the appid,deviceid,and access_token.
8.  Set the receivers ip address.
9.  Start proxy (python vsxproxysrvr.py)


##API
The proxy accepts the following REST requests to control the receiver.

*/pioneervsxcontrol/main/power/[on|off]
*/pioneervsxcontrol/hdz/power/[on|off]
*/pioneervsxcontrol/main/volumeset/[0-100]
*/pioneervsxcontrol/hdz/volumeset/[0-100]
*/pioneervsxcontrol/main/mute/[on|off]
*/pioneervsxcontrol/hdz/mute/[on|off]
*/pioneervsxcontrol/main/input/set/[code]
*/pioneervsxcontrol/hdz/input/set/[code]
*/pioneervsxcontrol/main/input/[next|previous]
*/pioneervsxcontrol/hdz/input/[next|previous]
*/pioneervsxcontrol/main/refresh
*/pioneervsxcontrol/hdz/refresh
*/pioneervsxcontrol/main/tuner/[next|previous]
