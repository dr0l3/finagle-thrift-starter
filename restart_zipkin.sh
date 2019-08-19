#!/usr/bin/env bash
docker rm -f zipkin
docker run -d -p 9411:9411 -p 9410:9410 --name zipkin -e "SCRIBE_ENABLED=true" -e "JAVA_OPTS=-Dlogging.level.zipkin=DEBUG -Dlogging.level.zipkin2=DEBUG" openzipkin/zipkin