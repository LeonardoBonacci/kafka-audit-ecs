#!/bin/bash

sed -e "s|{CONNECTION}|$ENV_CONNECTION|g" -e "s|{USERNAME}|$ENV_USERNAME|g" -e "s|{PASSWORD}|$ENV_PASSWORD|g" /opt/kafka/config/connect-es-sink.properties > /opt/kafka/config/connect-es-sink-runtime.properties

if [ ! -z "$ENV_TOPIC" ]; then
    echo $ENV_TOPIC
    sed -i -e "s/confluent-audit-log-events/$ENV_TOPIC/g" /opt/kafka/config/connect-es-sink-runtime.properties
fi

echo "show me the connector config!"
echo "--------------------------------------------------"
cat /opt/kafka/config/connect-es-sink-runtime.properties
echo "--------------------------------------------------"

sed -e "s|{BROKER}|$ENV_BROKER|g" -e "s|{KEY}|$ENV_KEY|g" -e "s|{SECRET}|$ENV_SECRET|g" /opt/kafka/config/connect-standalone.properties > /opt/kafka/config/connect-standalone-runtime.properties
echo "--------------------------------------------------"
echo "show me the connect cluster config!"
cat /opt/kafka/config/connect-standalone-runtime.properties
echo "--------------------------------------------------"

# connector start command here.
exec "/opt/kafka/bin/connect-standalone.sh" "/opt/kafka/config/connect-standalone-runtime.properties" "/opt/kafka/config/connect-es-sink-runtime.properties"
