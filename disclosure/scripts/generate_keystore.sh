#!/bin/bash

set -eu

SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_DIR=${SCRIPT_DIR//\/scripts/}

# Read SSL related variables from config file
source ${PROJECT_DIR}/env

KEYSTORE_DIR=${PROJECT_DIR}/src/main/resources/keystore
KEYSTORE_FILE=${KEYSTORE_DIR}/${SSL_KEY_STORE_NAME}.p12

# Create keystore directory if it does not exist
mkdir -p ${KEYSTORE_DIR}

# Delete key store file if already exists
if [[ -e ${KEYSTORE_FILE} ]]
then
  rm ${KEYSTORE_FILE}
fi

# Generate key store
keytool -genkeypair \
  -alias ${SSL_KEY_ALIAS} \
  -dname "CN=${HOST}, OU=DIS, O=SG, L=Edinburgh, S=Edinburgh, C=Scotland" \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -validity 3650 \
  -keystore ${KEYSTORE_FILE} \
  -storepass ${SSL_KEY_STORE_PASSWORD}
