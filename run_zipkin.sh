#!/usr/bin/env bash
docker run -d --name zipkin --net="host" -p 9411:9411 -p 9410:9410 -e "SCRIBE_ENABLED=true" -e "JAVA_OPTS=-Dlogging.level.zipkin=DEBUG -Dlogging.level.zipkin2=DEBUG" openzipkin/zipkin
