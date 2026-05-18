#!/bin/sh

set -o errexit
set -o nounset

readonly version="4.4.0"
readonly default_local_repo="${HOME}/.m2/repository"

if [ -d "$default_local_repo" ]; then
    local_repo="$default_local_repo"
else
    echo "Default local Maven repository not found at $default_local_repo. Attempting to determine it from Maven settings..."
    local_repo=$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
fi
readonly local_repo
readonly jar_file="$local_repo/org/itsallcode/openfasttrace/openfasttrace/$version/openfasttrace-$version.jar"

if [ ! -f "$jar_file" ]; then
    echo "JAR file $jar_file not found in local Maven repository. Downloading it..."
    mvn --batch-mode org.apache.maven.plugins:maven-dependency-plugin:3.10.0:get \
        -Dartifact=org.itsallcode.openfasttrace:openfasttrace:"$version" \
        -Dtransitive=false
fi

exec java -jar "$jar_file" "$@"
