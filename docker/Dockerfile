FROM wurstmeister/kafka:2.13-2.7.0

LABEL maintainer="leonardo bonacci"
LABEL version="4.2"

COPY confluentinc-kafka-connect-elasticsearch-11.0.6 /opt/kafka/libs
COPY ecs-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/kafka/libs

# we replace the default connect-standalone.properties so we can properly resolve to our local kafka docker development
COPY connect-standalone.properties /opt/kafka/config/

COPY connect-es-sink.properties /opt/kafka/config/

# we replace the start command creating a connector instead.
COPY start-kafka.sh /usr/bin/

# permissions
RUN chmod a+x /usr/bin/start-kafka.sh
