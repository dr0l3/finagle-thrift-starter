#!/usr/bin/env bash
docker run -d --net="host" -p 1235:1235 --name server2 server2