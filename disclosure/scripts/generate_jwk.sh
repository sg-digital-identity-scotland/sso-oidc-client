#!/bin/bash


set -eu

SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_DIR=${SCRIPT_DIR//\/scripts/}
REGISTRATION_DIR=${PROJECT_DIR}/registration

mkdir -p ${REGISTRATION_DIR}

java -jar ${PROJECT_DIR}/lib/json-web-key-generator.jar \
 -t RSA -a RS256 -s 2048 -u sig \
 -P ${REGISTRATION_DIR}/jwk_public_key.json \
 -o ${REGISTRATION_DIR}/jwk_keypair.json

echo ""
echo "Generated JWK public key and key pair under ${REGISTRATION_DIR}"
