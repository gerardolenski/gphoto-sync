#!/bin/bash

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=gphoto-sync \
  -Dsonar.host.url=http://localhost:9000