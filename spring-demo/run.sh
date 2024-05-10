#!/usr/bin/env bash

export OTEL_EXPORTER_OTLP_ENDPOINT=http://otelcollector:4318
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf

java -javaagent:opentelemetry-javaagent.jar \
      -Dotel.resource.attributes=service.name=java-service \
      -Dio.opentelemetry.javaagent.slf4j.simpleLogger.defaultLogLevel=off \
      -jar ./build/libs/demo-0.0.1-SNAPSHOT.jar

#      -Dotel.metrics.exporter=logging\

