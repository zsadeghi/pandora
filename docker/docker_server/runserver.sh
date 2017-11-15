#!/bin/bash

#TCP Server
java -jar GenericNode.jar ts 1234

#UDP Server
#java -jar GenericNode.jar us 1234

#RMI Server
#rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false &
#java -Djava.rmi.server.codebase=file:GenericNode.jar -jar GenericNode.jar rmis

