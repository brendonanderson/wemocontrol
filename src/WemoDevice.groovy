

import groovy.transform.ToString
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

@ToString(includeNames = true)
class WemoDevice {
    String name
    String modelName
    String modelNumber
    String serialNumber
    String macAddress
    String ipAddress
    String port
    String endpoint
    String firmware

    WemoDevice(String endpoint) {
        HTTPBuilder http = new HTTPBuilder(endpoint)
        http.request(Method.GET, ContentType.XML) {
            response.success = { resp, xml ->
                this.name = xml.device.friendlyName.text()
                this.modelName = xml.device.modelName.text()
                this.modelNumber = xml.device.modelNumber.text()
                this.serialNumber = xml.device.serialNumber.text()
                this.macAddress = xml.device.macAddress.text()
                this.ipAddress = new URL(endpoint).host
                this.port = new URL(endpoint).port
                this.endpoint = endpoint
                this.firmware = xml.device.firmwareVersion.text()
//                this. = xml.device..text()
            }
        }

    }

    public Boolean isOn() {
        Boolean isOn = false
        HTTPBuilder http = new HTTPBuilder("http://${ipAddress}:${port}/upnp/control/basicevent1")
        http.request(Method.POST, ContentType.XML) {
            body = '<?xml version="1.0" encoding="utf-8"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:GetBinaryState xmlns:u="urn:Belkin:service:basicevent:1"><BinaryState>1</BinaryState></u:GetBinaryState></s:Body></s:Envelope>'
            headers.'SOAPACTION' = "\"urn:Belkin:service:basicevent:1#GetBinaryState\""
            headers.'Content-Type' = "text/xml; charset=\"utf-8\""
            headers.'Accept' = ""
            response.success = { resp, xml ->
                isOn = xml.Body.GetBinaryStateResponse.BinaryState.text() as Integer
            }
        }
        isOn
    }

    public void turnOff() {
        HTTPBuilder http = new HTTPBuilder("http://${ipAddress}:${port}/upnp/control/basicevent1")
        http.request(Method.POST, ContentType.XML) {
            body = '<?xml version="1.0" encoding="utf-8"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:SetBinaryState xmlns:u="urn:Belkin:service:basicevent:1"><BinaryState>0</BinaryState></u:SetBinaryState></s:Body></s:Envelope>'
            headers.'SOAPACTION' = "\"urn:Belkin:service:basicevent:1#SetBinaryState\""
            headers.'Content-Type' = "text/xml; charset=\"utf-8\""
            headers.'Accept' = ""
            response.success = { resp, xml ->
            }
        }
    }

    public void turnOn() {
        HTTPBuilder http = new HTTPBuilder("http://${ipAddress}:${port}/upnp/control/basicevent1")
        http.request(Method.POST, ContentType.XML) {
            body = '<?xml version="1.0" encoding="utf-8"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:SetBinaryState xmlns:u="urn:Belkin:service:basicevent:1"><BinaryState>1</BinaryState></u:SetBinaryState></s:Body></s:Envelope>'
            headers.'SOAPACTION' = "\"urn:Belkin:service:basicevent:1#SetBinaryState\""
            headers.'Content-Type' = "text/xml; charset=\"utf-8\""
            headers.'Accept' = ""
            response.success = { resp, xml ->
            }
        }
    }

}
