#!/bin/bash

# Check for Maven 3
if type mvn; then
    echo found mvn executable in PATH
    _mvn=mvn
else
    echo "error: no Maven found"
    exit 1
fi
if [ "$_mvn" ]; then
    version=$("$_mvn" -version 2>&1 | awk '/Maven/ {print $3}')
    echo version "$version"
    declare -i major
    major=$(echo "$version" 2>&1 | awk -F \. '{print $1}')
    if [ $major -ge 3 ]; then
        echo "Maven 3 found, OK"
    else         
        echo "error: Maven 3 is required"
        exit 1
    fi
fi

# No need to check for Java: Maven will check it

# Build manifest builder & notebook
$_mvn clean package

# Create manifest file
./create-manifest-for-maven.sh

rm -f gwt_gaechannel/src/main/webapp/gwtgaechanneltodo/gwtgaechanneltodo.nocache.js

# Change current work directory & execute from there
cd gwt_gaechannel/target/gwtgaechanneltodo-0.0.1-SNAPSHOT
mvn -f ../../pom.xml gwt:run
