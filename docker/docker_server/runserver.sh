#!/bin/bash

#TCP Server
java -jar GenericNode.jar ts 1234 &
java -jar GenericNode.jar ts 1235 &
java -jar GenericNode.jar ts 1236 &
java -jar GenericNode.jar ts 1237 &

wait

