#!/bin/bash

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

IMAGE_NAME=${IMAGE_NAME:-"oidc-private-client-springboot"}
IMAGE_TAG=${IMAGE_TAG:-"1.0.0"}
CONTAINER_NAME=${IMAGE_NAME}

CONTAINER_STATUS=$(docker ps -a -f name=${CONTAINER_NAME} --format "{{.Status}}")
if [[ ${CONTAINER_STATUS} == Exited* ]]
then
  docker rm ${CONTAINER_NAME} >/dev/null 2>&1
  echo ""
  echo "Container ${CONTAINER_NAME} removed."
fi
if [[ ${CONTAINER_STATUS} == Up* ]]
then
  echo ""
  echo "Container ${CONTAINER_NAME} is already running."
  exit 0
fi

for line in $(cat ${PROJECT_DIR}/env)
do
  export "${line//$'\r'}"
done

if [[ "${PROTOCOL:-""}" == http ]]
then
  SSL_ENABLED=false
elif [[ "${PROTOCOL:-""}" == https ]]
then
  SSL_ENABLED=true
else
  echo "Invalid PROTOCOL value '${PROTOCOL:-""}'. Value must be 'http' or 'https'."
  exit 1
fi

for line in $(cat ${PROJECT_DIR}/registration/client)
do
  export "${line//$'\r'}"
done
REGISTRATION_DIR=${PROJECT_DIR}/registration
export JWK_KEYPAIR=$(cat "${REGISTRATION_DIR}/jwk_keypair.json")

docker run \
  --name ${CONTAINER_NAME} \
  -p ${PORT}:${PORT} \
  --env-file ${PROJECT_DIR}/registration/client \
  --env-file ${PROJECT_DIR}/env \
  -e JWK_KEYPAIR="${JWK_KEYPAIR}" \
  -e SSL_ENABLED="${SSL_ENABLED}" \
  -d \
  ${IMAGE_NAME}:${IMAGE_TAG}

echo ""
echo "----------------------------------------------------------------"
echo "  Running ${CONTAINER_NAME} in the background"
echo "----------------------------------------------------------------"
echo ""
echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
echo "Container:"
echo "  Name: ${CONTAINER_NAME}"
echo "  Network: ${NETWORK}"
echo "Endpoints:"
echo "  From host:             ${PROTOCOL}://${HOST}:${PORT}"
echo "  From Docker container: ${CONTAINER_NAME}:${PORT}"
