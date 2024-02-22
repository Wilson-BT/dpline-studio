#!/bin/bash
cd /Users/wangchunshun/Documents/IdeaProjects/dpline-studio
mvn clean install -Prelease -Dmaven.test.skip=true
cd ~/Documents/IdeaProjects/dpline-studio/dpline-dist/target
tar -xzvf dpline-studio-0.0.1-bin.tar.gz
cd dpline-studio-0.0.1-bin/lib
mv dpline-flink-client-1.14-0.0.1.jar ../../../../lib
