# WebEngDUS UG Talk - Nov. 16th, 2017

Hey folks, this is the demo project from the talk at the WebEngDUS on
Nov. 16th, 2017.

## How to start everything up

We use a lot of different stuff here, so getting it started can be
overwhelming at first. Just follow the instructions and "trust me" for
a moment.

All services assume, that you execute them in a separate terminal session,
either with tabs, tmux or screen, or you use something like
iTerm2's split feature.

**Important**

* The base directory for all commands is `kafka-dist`.
* The `kafka-dist/bin` directory contains scripts prefixed with `demo-`.
  These scripts are custom additions from us and intend to ease the demo process.
* All services we start here are `CTRL-C`-able without doing harm.

### MySQL

We assume, that a MySQL server is running on localhost, port 3306.
We further assume, that there is a root user with no password.

Please execute this in the MySQL CLI:

    CREATE DATABASE IF NOT EXISTS `shop`;

### Zookeeper

We run ZK in foreground. Alternatively you can add `-daemon` and move it
into the background. There is a corresponding stop command to remove the
process later on.

    bin/zookeeper-server-start.sh config/zookeeper.properties

### Kafka

We run Kafka in foreground. Alternatively you can add `-daemon` and move it
into the background. There is a corresponding stop command to remove the
process later on.

    bin/kafka-server-start.sh config/server.properties

### Kafka Topics

Before we start more of the components, we need to add a few topics to the broker.

    bin/demo-create_topics.sh

### Kafka Connect

Kafka Connect is, without using systemd or daemon tools, a foreground-only
tool. We've tried to commit working version of each plugin into the
`kafka-dist/plugins` directory, so you can start Kafka Connect right away.

We've assembled already the correct command in the demo scripts for you.

    bin/demo-start_connect.sh

### Landoop Kafka Connect UI (Optional)

**This one needs Docker** and gives you a nice little UI for the Kafka Connect
installation.

The start script is "made for macOS", because it assumes that your primary
network interface is named `en0`. Depending on your OS, this might need some adjustment.

Change the following line in `bin/demo-docker_connect_ui.sh` according to your needs:

    MY_IP=$(ifconfig en0 | grep inet | grep -v inet6 | awk '{print $2}')

Then run:

    bin/demo-docker_connect_ui.sh


### Shop Service
We don't provide the shop service as jar, because it would make the repository
even bigger.

Instead, we give you the command to build and run it from your shell. Gradle will download the
dependencies on demand and then start the Spring Boot application.

**Important:** Don't run this for the first time on a mobile connection! Gradle will leave your
with the impression of downloading half of the internet.

**Change the directory to the root of the repository!**

    cd ..
    ./gradlew shop-service:bootRun 

### Additional scripts

#### Delete topics

This script removes the topics from the broker. The broker is already
configured to enable topic deletion.

    bin/demo-delete_topics.sh

#### Reset sink offsets

This script resets the offset of the redis sink to force it to read the
topic from the earliest offset.

    bin/demo-reset_sink_offsets.sh