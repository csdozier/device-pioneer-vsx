# device-pioneer-vsx
Smartthings Device Type for Pioneer VSX Receivers


This allows you to connect up a VSX-1130/VSX-1124/etc receiver to smartthings.  The device utilizes a proxy which connects to the receiver via a socket connection.  


##Prerequisites

 - Python 2.7
 - requests (pip install requests)

##Installation

 1. Download all files from this repository
 2. Login to https://graph.api.smartthings.com/
 3. Click **My Device Handlers** 
 4. Click **Create New Device Handler**
 5. Click **From Code** and paste in the contents of device-type-pioneervsx.groovy 
 6. Create **Create**
 7.  Click **Publish**
 8. Click **My Devices** from the top menu bar and then click **New Device**
 9. Give the device an appropriate name (ex. Downstairs Receiver) .  Set the type to **Pioneer VSX Network Receiver** .  Put in a number for the network id (ex. 999)
 10. Click Create
 11. Make device id shown in the url in the browser, ex. https://graph.api.smartthings.com/device/show/xxx-xxx-xxx-xxx-xxx . The device id is in the url after /show/. You will need this id for the config file in a later step.
 12. Click **My SmartApps**
 13.  Click **New Smartapp**
 14. Click **From Code** and paste in the contents of smartapp-pioneervsx-integration.groovy
 15. Click **Publish**
 16.  Click **App Settings** then Oauth, the **Enable Oauth in Smart App**
 17.  Write down the **Client ID** and **Client Secret**
 18.  Open a web browser window in private mode (incognito).  Navigate to this URL into your browser, substituting in the Client Id: https://graph.api.smartthings.com/oauth/authorize?response_type=code&client_id=<Client Id>&scope=app&redirect_uri=http://localhost

    If you are prompted to login to SmartThings, go ahead.
    Select you location from the drop down list and the receiver you want to have access to through the REST API
    Click the Authorize button.
    You'll be redirected to a URL that looks like this: http://localhost/?code=<Code>
    
    Copy the Code from the URL for later use.
    

 19. Go back to https://graph.api.smartthings.com/ide/apps and click on your VSX Integration SmartApp
 20. Click **Simulator**.  Set the Location, select your receivers, then click Install.
 21. Copy the **API Token** shown in the bottom right corner. This is the callbackurl_access_token
 22. Copy the URL from **API Endpoint** https://graph.api.smartthings.com/api/smartapps/installations/xxx-xxx-xxx-xxx . The last part of the url will be the callbackurl_app_id
 23.  Open the Smarthings app on your mobile device.  
	 1. Go to My Home->Things and find your receiver you created in Step 10.  Click the receiver then the gear box to go to settings.   Change the IP to the IP address of the computer that will be running the python proxy server (NOT the IP address of the receiver). Click Done
	 2. Go to Automation and Click **Pioneer VSX Integration**.  Verify that your receiver is set under **Which Receivers?**
 24. Install python and requests (pip install requests)
 25. Copy the .cfg and .py files to location where you will run the server
 26. Edit the vsxproxysrvr.cfg file editing the following items (at minimum)
	 1.  If you are using a Pioneer reciever from 2016+ (ie VSX-1131) set use_eiscp=true
	 2. Set callbackurl_app_id and callbackurl_access_token to values from steps 21 and 22
	 3. Set callbackurl_main_zone_device_id to the value from step 11
	 4. Under the receiver section, set host to the IP address of your Pioneer VSX receiver

 27.  Start the program using **python vsxproxysrvr.py**


##API
The proxy accepts the following REST requests to control the receiver.

* /pioneervsxcontrol/main/power/[on|off]
* /pioneervsxcontrol/hdz/power/[on|off]
* /pioneervsxcontrol/main/volumeset/[0-100]
* /pioneervsxcontrol/hdz/volumeset/[0-100]
* /pioneervsxcontrol/main/mute/[on|off]
* /pioneervsxcontrol/hdz/mute/[on|off]
* /pioneervsxcontrol/main/input/set/[code]
* /pioneervsxcontrol/hdz/input/set/[code]
* /pioneervsxcontrol/main/input/[next|previous]
* /pioneervsxcontrol/hdz/input/[next|previous]
* /pioneervsxcontrol/main/refresh
* /pioneervsxcontrol/hdz/refresh
* /pioneervsxcontrol/main/tuner/[next|previous]

