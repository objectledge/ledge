#!/bin/bash

read -t 5
if [ "good input" = "$REPLY" ]; then
  exit 0
else
  exit 1
fi
