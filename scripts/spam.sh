#!/usr/bin/env bash

INPUT_STRING=hello
while [ "$INPUT_STRING" != "bye" ]
do
  curl curl 192.168.39.90:32244/hello/rune
  sleep 0.05
done