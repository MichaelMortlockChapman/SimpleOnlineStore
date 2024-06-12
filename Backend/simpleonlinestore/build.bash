#!/bin/bash

ENV_FILE=$1

output_string=""
while IFS='=' read -r key value; do
  if [[ ! $key =~ ^# ]] && [[ -n $key ]]; then
    key=$(echo $key | xargs)
    value=$(echo $value | xargs)
    output_string+="-D$key=$value "
  fi
done < "$ENV_FILE"

set -x
mvn install $output_string -DskipTests=true
set +x