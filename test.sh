#!/bin/bash

mvn clean install
cd target
unzip -d tmp get-problem-0.1-standalone.zip
cd tmp
./run.sh
