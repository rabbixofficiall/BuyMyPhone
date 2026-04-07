#!/usr/bin/env sh

DIR="$(cd "$(dirname "$0")"; pwd -P)"
GRADLE_WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
  echo "Missing gradle-wrapper.jar"
  exit 1
fi

java -jar "$GRADLE_WRAPPER_JAR" "$@"
