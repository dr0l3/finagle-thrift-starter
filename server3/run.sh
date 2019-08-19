#!/usr/bin/env bash
docker rm -f server3
docker run -d -p 8002:8002 --name server3 dr0l3/server3
