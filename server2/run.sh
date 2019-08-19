#!/usr/bin/env bash
docker rm -f server2
docker run -d -e "SERVICE2_SERVICE_HOST=localhost" -e "SERVICE2_SERVICE_PORT=1236" -p 8001:8001 --name server2 dr0l3/server2
