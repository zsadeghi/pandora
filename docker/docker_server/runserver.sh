#!/bin/bash

#TCP Server
java -jar GenericNode.jar ts 1234 &
java -jar GenericNode.jar ts 1235 &
java -jar GenericNode.jar ts 1236 &
java -jar GenericNode.jar ts 1237 &

wait

#UDP Server
#java -jar GenericNode.jar us 1234

#RMI Server
#rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false &
#java -Djava.rmi.server.codebase=file:GenericNode.jar -jar GenericNode.jar rmis

