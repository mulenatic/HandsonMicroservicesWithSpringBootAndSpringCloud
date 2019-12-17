#!/bin/bash

if [ $# -eq 0 ]
then
    echo 'No parameters given'
    exit
fi

for i in api/build.gradle util/build.gradle
do
    echo "Adding version in $i";
    sed -i 's/id '\"'io.spring.dependency-management'\"' \/\/version/id '\"'io.spring.dependency-management'\"' version/g' $i;
done

./gradlew $@;

for i in api/build.gradle util/build.gradle
do
    echo "Removing version in $i";
    sed -i 's/id '\"'io.spring.dependency-management'\"' version/id '\"'io.spring.dependency-management'\"' \/\/version/g' $i;
done
