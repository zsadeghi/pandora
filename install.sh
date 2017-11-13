#!/bin/bash
# Assume Ubuntu
# Make sure this is the root user
if [ $(id -u) -ne 0 ];
then
	echo "Insufficient privileges"
	exit 1
fi

echo "Updating repositories"
apt-get update
echo "Installing the default JRE"
apt-get install -y default-jre
echo "Installing network tools"
apt-get install -y net-tools
echo "Installing Apache Maven (latest)"
apt-get install -y maven
