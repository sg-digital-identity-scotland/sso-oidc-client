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

if [[ "${PROTOCOL:-""}" == http ]]
then
  export SSL_ENABLED=false
elif [[ "${PROTOCOL:-""}" == https ]]
then
  export SSL_ENABLED=true
else
  echo "Invalid PROTOCOL value '${PROTOCOL:-""}'. Value must be 'http' or 'https'."
  exit 1
fi

export JWK_KEYPAIR=$(cat "${REGISTRATION_DIR}/jwk_keypair.json")

${PROJECT_DIR}/gradlew bootRun -p ${PROJECT_DIR}
