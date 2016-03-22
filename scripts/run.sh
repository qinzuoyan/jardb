#!/bin/bash

java -classpath ${project.build.finalName}.jar:library/* com.xiaomi.infra.pegasus.client.$*

