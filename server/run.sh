#!/usr/bin/env bash
docker rm -f server
docker run -d -e "SERVICE2_SERVICE_HOST=localhost" -e "SERVICE2_SERVICE_PORT=1235" -p 1234:1234 --name server dr0l3/server
