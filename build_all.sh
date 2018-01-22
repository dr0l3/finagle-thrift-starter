#!/usr/bin/env bash
sbt server/assembly
docker build -t dr0l3/server ./server

sbt server2/assembly
docker build -t dr0l3/server2 ./server2

sbt server3/assembly
docker build -t dr0l3/server3 ./server3
