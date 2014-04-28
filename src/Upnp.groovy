import WemoDevice

class Upnp {


    public static void main(String[] args) {
        def cli = new CliBuilder(usage:
"""
Example usages:

Discover devices:
  -d -i en1 -t 10000
Turn on a device:
  -n -i en1 -e "http://192.168.1.1:49153/setup.xml
Turn off a device:
  -f -i en1 -e "http://192.168.1.1:49153/setup.xml
Device status:
  -s -i en1 -e "http://192.168.1.1:49153/setup.xml
"""

        )
        cli.h("Help", required: false)
        cli.i("Network interface to use", required: false, args: 1)
        cli.t("Time to wait for response (in ms)", required: false, args: 1)
        cli.d("Discover devices", required: false)
        cli.e("Device endpoint", required: false, args: 1)
        cli.s("Check status of device", required: false)
        cli.n("Turn device on", required: false)
        cli.f("Turn defice off", required: false)


        def options = cli.parse(args)
        if (!options) {
            println cli.usage
            return
        }

        if (options.h) {
            println cli.usage
        } else if (options.d && options.i && options.t) {
            UpnpDiscovery listener = new UpnpDiscovery(options.i, options.t as Long)
            Set endpoints = listener.getEndpoints()
            println("found ${endpoints.size()} endpoints")
            endpoints.each { String it ->
                WemoDevice dev = new WemoDevice(it)
                println("${dev.name},${it}")
            }
        } else if (options.s && options.i && options.e) {
            WemoDevice wemoDevice = new WemoDevice(options.e)
            println wemoDevice.isOn()
        } else if (options.n && options.i && options.e) {
            WemoDevice wemoDevice = new WemoDevice(options.e)
            println "Turning on device at ${options.e}"
            wemoDevice.turnOn()
        } else if (options.f && options.i && options.e) {
            WemoDevice wemoDevice = new WemoDevice(options.e)
            println "Turning off device at ${options.e}"
            wemoDevice.turnOff()
        } else {
            println "The combination of arguments given does not mean anything to me..."
        }
    }



}
