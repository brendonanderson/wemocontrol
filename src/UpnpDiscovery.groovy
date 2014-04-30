import groovy.util.logging.Commons

@Commons
class UpnpDiscovery {

    Long msToListen
    Boolean inService = true
    Set<String> endpoints = [] as Set<String>
    String iface

    public UpnpDiscovery(String iface, Long msToListen) {
        this.msToListen = msToListen
        this.iface = iface
    }

    public Set<String> getEndpoints() {
        def listenerThread = Thread.start {
            this.startListener()
        }
        discover()
        def controlThread = Thread.start {
            sleep(msToListen)
            inService = false
        }

        controlThread.join()
        listenerThread.join()
        return endpoints
    }

    private void startListener() {
        List<Thread> processThreads = []
        MulticastSocket recSocket = new MulticastSocket(null)
        recSocket.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 1901))
        recSocket.setTimeToLive(10)
        recSocket.setSoTimeout(1000)
        recSocket.joinGroup(InetAddress.getByName("239.255.255.250"))
        log.info("waiting to receive")
        while (inService) {
            byte[] buf = new byte[2048]
            DatagramPacket input = new DatagramPacket(buf, buf.length)
            try {
                recSocket.receive(input)
                def processThread = Thread.start {
                    String originaldata = new String(input.data)
    //                log.info(originaldata)
                    if (originaldata.toLowerCase().indexOf("location:") > -1) {
                        String location = originaldata.substring(originaldata.toLowerCase().indexOf("location:"))
                        location = location.substring(0, location.indexOf("\n"))
                        location = location.substring(location.indexOf(":") + 1, location.length())
                        endpoints.add(location.trim())
                    }
                }
                processThreads.add(processThread)

            } catch (SocketTimeoutException e) {
//                log.info("socket timeout")
            }
        }
        processThreads.each { it.join() }
        recSocket.leaveGroup(InetAddress.getByName("239.255.255.250"))
        recSocket.disconnect()
        recSocket.close()
    }

    private void discover() {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900)
        MulticastSocket socket = new MulticastSocket(null)
        try {
            socket.bind(new InetSocketAddress(getIpOfInterface(iface), 1901))
            StringBuilder packet = new StringBuilder()
            packet.append( "M-SEARCH * HTTP/1.1\r\n" )
            packet.append( "HOST: 239.255.255.250:1900\r\n" )
            packet.append( "MAN: \"ssdp:discover\"\r\n" )
            packet.append( "MX: ").append( "5" ).append( "\r\n" )
//            packet.append( "ST: " ).append( "ssdp:all" ).append( "\r\n" ).append( "\r\n" )
            packet.append( "ST: " ).append( "urn:Belkin:device:controllee:1" ).append( "\r\n" ).append( "\r\n" )
            byte[] data = packet.toString().bytes
            log.info("sending discovery packet")
            socket.send(new DatagramPacket(data, data.length, socketAddress))
        } catch (IOException e) {
            inService = false
            throw e
        } finally {
            socket.disconnect()
            socket.close()
        }
    }
    private String getIpOfInterface(String iface) {
        NetworkInterface ni = NetworkInterface.getByName(iface)
        InetAddress address = ni.getInetAddresses().find { it.siteLocalAddress }
        address.hostAddress
    }
}
