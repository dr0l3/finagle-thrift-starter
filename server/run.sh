#!/usr/bin/env bash
docker run -d --net="host" -p 1234:1234 --name server server
