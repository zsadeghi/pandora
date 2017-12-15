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

### Considerations

The TCP communication protocl expects all messages to be sandwiched between `^<<` and `^>>&`. For instance, if you are
using telnet to communicate, to list all data you will have to send `^<<store^>>&`, and the answer will also be contained
in the same way.

This is to allow for multiline communication, or communication not punctuated by the newline feed character.

## Replication

To replicate your data with this application, set the data store to `dds`:

    /path/to/launcher --data-store=dds

which will internally use the in-memory data store by default, and use beacon transmission for automatic replica discovery
over the local network.

The underlying data store must support locking and reverting.

### Replica Discovery

Replica discovery can be achieved in one of the following three ways:

1. Reading the list of potential replicas from a static file (which can optionally be refreshed periodically). This
method will treat the list of replicas as potential nodes, and syncs to them when they go online. The format of the
file dictates one replica per line, and each replica will have a URI: `schema://host:port/?options=...`; for instance
a REST replica located at `192.168.1.70` listening to port `9091` and with a context path `store` will be addressed
as `rest://192.168.1.70:9091/?base=store`. After a node goes online, it will automatically sync to the latest data
from other nodes.

2. Using a third-party node (running on any of the supported protocols) as a registry for nodes. Each node will register
itself as it goes online, and unregisters itself as it goes offline. Auto-sync is also supported.

3. Using a beacon, every node starts transmitting its specification every 5 seconds, and maintains a list of other nodes
on the network internally. This is done via UDP. Your router must allow broadcasts on UDP, and all nodes must agree on
the beacon port. This can also be used to maintain different sets of replicated data nodes which are unaware of each
other's pool, since their beacon port is different. Nodes will also let each other know if they are being gracefully
shut down.

### Locking Methods

The in-memory data store supports two locking mechanisms:

1. Optimistic locking: you can place multiple locks on each key, and conclude them. If the items have been modified by other transactions
in the meanwhile, your transactions cannot conclude.
2. Pessimistic locking: you can place a single, exclusive lock on the key, and only the holder of the key can modify the data. This is the
default locking method.

## Interactive Client

This application comes with an interactive client to ease communication with the server. You can simply run the application as
`/path/to/launcher interactive [options]` to start an interactive session.

## Known Issues

- REST client currently cannot send out generic commands to the server, so any such interaction is limited
to other protocols. These commands include test commands and commands pertaining to the RAFT server.

- If a Raft follower node crashes when using UDP beacon for node discovery, during restart, it might race for
leadership and override the other nodes' logs. As such, it is not safe to use UDP node discovery at the moment.

- If the file-discovery mode is chosen for the Raft data-store, the I/O operation might race with the election.
As such, the data-store may override your timeout to avoid such a scenario.

## Raft Data Store

The Raft data store starts up like the regular distributed data store, accepting all the same set of command-line
configuration options.

This means you can start it with either beacon discovery, registery discovery, or file-based discovery.

Once started, the swarm will inter-communicate via the known address of each of the other members, recovering
from crashes as discussed in the Raft paper.

### Features

- Heartbeats are sent in parallel. This is achieved via the `sendAppend` method of the `RaftDataStore` class.
- The leader sends out append requests after each command, as well as during heartbeats. This means that logs
are rapidly replicated. The halflife of the timeout is initially 150ms, which means that the timeout period might
be anywhere from 150ms to 300ms. Also, heartbeats are sent out every tenth of a timeout, to avoid unnecessary
election attempts when the network is slow.