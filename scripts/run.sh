#!/usr/bin/env sh
set -eu

./mvnw -q -DskipTests package
java -jar target/uno-cli.jar "$@"
