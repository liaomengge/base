#!/bin/bash

if [ -z "$1" ] ;then
    printf "usage: $0 {local|remote|all} \n"
    exit 1
fi

local() {
  ../gradlew publishToMavenLocal
  echo "publish to local maven finish..."

  ../gradlew publishAllPublicationsToLocalRepository
  echo "publish to local sonatype finish..."
}

remote() {
  ../gradlew publish
  echo "publish to remote sonatype finish..."
}

case $1 in
 local)
    local
    ;;
 remote)
    remote
    ;;
 all)
    local
    remote
    ;;
esac