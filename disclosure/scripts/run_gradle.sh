#!/bin/bash

set -eu

SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_DIR=${SCRIPT_DIR//\/scripts/}

for line in $(cat ${PROJECT_DIR}/env)
do
  export "${line//$'\r'}"
done

REGISTRATION_DIR=${PROJECT_DIR}/registration
for line in $(cat ${REGISTRATION_DIR}/client)
do
  export "${line//$'\r'}"
done

export JWK_KEYPAIR=$(cat "${REGISTRATION_DIR}/jwk_keypair.json")

${PROJECT_DIR}/gradlew bootRun -p ${PROJECT_DIR}
