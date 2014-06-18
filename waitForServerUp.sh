#!/bin/sh

while true;
do
  if [ -z "$(netstat -ntl | grep 8888 | grep LISTEN)" ]; then
    echo "Server is down"
    sleep 1
  else
    echo "Server is up"
    break
  fi
done

