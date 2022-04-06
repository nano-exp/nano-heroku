#!/usr/bin/env bash
cd "$(dirname "$0")" || exit 1

cd ..

exec java $JAVA_OPTS -jar worker/build/libs/worker.jar "$@"
