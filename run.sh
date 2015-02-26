#!/bin/bash

num="10000000"
echo ""
echo "Running with $((num / 1000))K"
java -Xms1024m -Xmx4096m -jar get-problem.jar $num
