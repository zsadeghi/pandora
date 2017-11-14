# Pandora

Pandora is a generic client/server platform that supports a command line interface for a very basic
backend data store.

## Installing

An easy install script for Ubuntu machines has been provided.

All you need to do is:

    git checkout https://github.com/zsadeghi/pandora.git
    cd pandora
    chmod +x install.sh
    sudo ./install.sh

This will grab all dependencies (JDK, Maven), build the project using Maven, and set up the
Docker files under `docker` for you.

## Provided CLIs

Alternatively, you can build the project's main reactor under `pandora-build` and use one of the
two provided CLIs:

* `pandora-cli/target/pandora.jar`
* `pandora-kisscli/target/GenericNode.jar`

The KISS (Keep It Stupid Simple) CLI simply translates all interface commands to those understood
by the regular CLI (which resembles most common POSIX-compliant applications more closely).

Both clients come with usage instructions which will be provided to you if you run them without any
arguments.

The install script will link the KISS CLI to the provided Docker images.

## Supported Protocols

This application supports any protocol, so long as a client and a server for that specific protocol are provided
and have been statically bound to the launch script. You can find an example of this under `pandora-tcp`.

Currently, the following protocols have been implemented:

* TCP
* UDP
* RMI
* REST

## Support Data Store

The data store attached to the server instances can be changed, so long as an implementation is provided. Currently,
only an in-memory data store is implemented, but this can be easily extended.

All data store implementations are wrapped in a monitor, so that regardless of implementation, data store access
is always synchronized.