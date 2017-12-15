#!/bin/bash

########################################################################################################################
##   After building and starting `membership_server`, substitute ${registry} with the IP address of that server.      ##
########################################################################################################################

registry=""

java -jar GenericNode.jar ts 1234 tcp://${registry}:1234 &
java -jar GenericNode.jar ts 1235 tcp://${registry}:1234 &
java -jar GenericNode.jar ts 1236 tcp://${registry}:1234 &
java -jar GenericNode.jar ts 1237 tcp://${registry}:1234 &
java -jar GenericNode.jar ts 1238 tcp://${registry}:1234 &

wait