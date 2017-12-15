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
apt-get install -y default-jdk
echo "Installing Apache Maven (latest)"
apt-get install -y maven
echo "Building all modules"
cd pandora-build
mvn clean install
cd ..
echo "Linking the JAR file"
rm -rf docker/docker_client/GenericNode.jar
rm -rf docker/docker_server/GenericNode.jar
cp pandora-kisscli/target/GenericNode.jar docker/GenericNode.jar