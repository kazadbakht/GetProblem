#!/bin/bash

num=5000000
echo ""
echo "Running with $num"
java -Xms1024m -Xmx4096m -jar get-problem.jar $num
