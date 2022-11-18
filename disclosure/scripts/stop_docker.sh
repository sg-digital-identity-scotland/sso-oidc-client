#!/bin/bash

CONTAINER_NAME=${CONTAINER_NAME:-"oidc-private-client-springboot"}
CONTAINER_STATUS=$(docker ps -a -f name=${CONTAINER_NAME} --format "{{.Status}}")

if [[ "${CONTAINER_STATUS}" == "" ]]
then
  echo ""
  echo "Container ${CONTAINER_NAME} not found."
  exit 0
fi

if [[ ${CONTAINER_STATUS} == Up* ]]
then
  docker stop ${CONTAINER_NAME} >/dev/null 2>&1
  echo ""
  echo "Container ${CONTAINER_NAME} stopped."
  docker rm ${CONTAINER_NAME} >/dev/null 2>&1
  echo "Container ${CONTAINER_NAME} removed."
  exit 0
fi

if [[ ${CONTAINER_STATUS} == Exited* ]]
then
  docker rm ${CONTAINER_NAME} >/dev/null 2>&1
  echo ""
  echo "Container ${CONTAINER_NAME} already stopped."
  echo "Container ${CONTAINER_NAME} removed."
fi
