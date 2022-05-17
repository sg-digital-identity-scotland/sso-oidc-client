#!/bin/bash

set -eu

SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_DIR=${SCRIPT_DIR//\/scripts/}

while [ $# -gt 0 ]; do
  if [[ $1 == *"--"* ]]; then
    v="${1/--/}"
    declare $v="$2"
    export $v
  fi
  shift
done

${PROJECT_DIR}/gradlew jibDockerBuild \
  -p ${PROJECT_DIR} \
  -P IMAGE_NAME=${IMAGE_NAME:-""} \
  -P IMAGE_TAG=${IMAGE_TAG:-""}
