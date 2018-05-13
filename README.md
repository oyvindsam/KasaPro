# KasaPro
Control you Kasa smart-bulb with ease

Goals of this project:
1. Connect to Kasa-lightbulb and save the token for later use.
2. Easy control options for connected light (100%, 50%, OFF etc.)
3. SystemUI tile for easy switching

Possible further features: widget, support for multiple devices, ON/OFF-timer

##  How to get token:

1. Generate a UUID (v4)
2. Send a HTTP POST to https://eu-wap.tplinkcloud.com (content type appliaction/json):

		{
		  "method": "login",
		  "params": {
		    "appType": "Kasa_Android",
		    "cloudUserName": "EMAIL",
		    "cloudPassword": "PASSWORD",
		    "terminalUUID": "UUID"
		  }
		}

UUID, EMAIL, PASSWORD must be changed.

Kasa token is in the "token" json field in the payload:

	{
	  "error_code": 0,
	  "result": {
	    "accountId": "170****",
	    "regTime": "2017-08-28 18:13:55",
	    "email": "EMAIL",
	    "token": "TOKEN"
	  }
	}

##  How to get device id:
Send a HTTP POST to https://eu-wap.tplinkcloud.com/?token=TOKEN (content-type application/json)

	{
	  "method": "getDeviceList"
	}
	
  Change TOKEN. Device id in "deviceId" field in payload:

	{
	  "error_code": 0,
	  "result": {
	    "deviceList": [
	      {
		"fwVer": "1.7.1 Build 171109 Rel.163935",
		"deviceName": "Smart Wi-Fi LED Bulb with Dimmable Light",
		"status": 1,
		"alias": "Livets Lys",
		"deviceType": "IOT.SMARTBULB",
		"appServerUrl": "https://eu-wap.tplinkcloud.com",
		"deviceModel": "LB100(EU)",
		"deviceMac": "50C7********",
		"role": 0,
		"isSameRegion": true,
		"hwId": "111E359******************************",
		"fwId": "00000000000000000000000000000000",
		"oemId": "ED2934C******************************",
		"deviceId": "8012D5ADBED98D4*************************",
		"deviceHwVer": "1.0"
	      }
	    ]
	  }
	}


##  How to control light:
Set light: Send a HTTP POST to https://eu-wap.tplinkcloud.com/?token=TOKEN (content-type application/json)

	{
	  "method": "passthrough",
	  "params": {
	    "deviceId": "DEVICE_ID",
	    "requestData": {
	      "smartlife.iot.smartbulb.lightingservice": {
		"transition_light_state": {
		  "on_off": 1,
		  "brightness": 100
		}
	      }
	    }
	  }
	}

Where requestData is json string like so: 

	"{\"smartlife.iot.smartbulb.lightingservice\":{\"transition_light_state\":{\"on_off\":1,\"brightness\":100} } } "

To get Device state just pass in empty transition_light_state: {}

TOKEN, DEVICE-ID must be changed accordingly. Light bulb states are selvf explanatory. "error_code" in payload should be 0 for successful change:

	{
	  "error_code": 0,
	  "result": {
	    "responseData": {
	      "smartlife.iot.smartbulb.lightingservice": {
		"transition_light_state": {
		  "on_off": 1,
		  "mode": "normal",
		  "hue": 0,
		  "saturation": 0,
		  "color_temp": 2700,
		  "brightness": 100,
		  "err_code": 0
		}
	      }
	    }
	  }
	}
