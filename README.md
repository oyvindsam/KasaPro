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
<code>
{  "method": "login",  "params": {  "appType": "Kasa_Android",  "cloudUserName": "EMAIL",  "cloudPassword": "PASSWORD",  "terminalUUID": "UUID"  } }
</code> 

UUID, EMAIL, PASSWORD must be changed.

Kasa token is in the "token" json field in the payload:
<code>
  {
	"error_code": 0,
	"result": {
		"accountId": "170****",
		"regTime": "2017-08-28 18:13:55",
		"email": "EMAIL",
		"token": "TOKEN"
	}
}
 </code>

##  How to get device id:
Send a HTTP POST to https://eu-wap.tplinkcloud.com/?token=TOKEN (content-type application/json)
<code>
{"method": "getDeviceList" }
  </code>
  Change TOKEN. Device id in "deviceId" field in payload.

##  How to control light:
Set light: Send a HTTP POST to https://eu-wap.tplinkcloud.com/?token=TOKEN (content-type application/json)
<code>
{"method":"passthrough", "params": {"deviceId": "DEVICE-ID", "requestData": "{\"smartlife.iot.smartbulb.lightingservice\":{\"transition_light_state\":{\"on_off\":1,\"brightness\":100} } } " } }
</code>

TOKEN, DEVICE-ID must be changed accordingly. Light bulb states are selvf explanatory. 