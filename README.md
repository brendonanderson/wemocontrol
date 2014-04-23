wemocontrol
===========

WeMo API written in Groovy for controlling switches.

How to:

1.  Clone the repository (`git clone https://github.com/brendonanderson/wemocontrol.git`)
1.  `cd wemocontrol`
1.  Build the code (`gradle build`)
1.  Build the jar (`gradle uberjar`)
1.  `cd build/libs`
1.  Search for WeMo devices (`java -jar wemocontrol.jar -d -i en1 -t 20000` where en1 is your network interface name)
1.  Turn on a device that was discovered (`java -jar wemocontrol.jar -i en1 -e http://192.168.1.120:49153/setup.xml -n`)
1.  Turn off a device that was discovered (`java -jar wemocontrol.jar -i en1 -e http://192.168.1.120:49153/setup.xml -f`)
