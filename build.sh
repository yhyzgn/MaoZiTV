#!/usr/bin/env bash

echo building ...

./gradlew :app:clean :app:assembleRelease

echo Build Successful
