#!/usr/bin/env bash

if head -n 1 project.clj | grep SNAPSHOT > /dev/null; then
  echo "Deploying snapshot to Clojars";
  lein deploy clojars;
else
  echo "Not deploying release version: do this manually";
fi
