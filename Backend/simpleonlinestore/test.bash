#!/bin/bash

ENV_FILE="../.././.env"

output_string=""
while IFS='=' read -r key value; do
  if [[ ! $key =~ ^# ]] && [[ -n $key ]]; then
    key=$(echo $key | xargs)
    value=$(echo $value | xargs)
    output_string+="-D$key=$value "
  fi
done < "$ENV_FILE"

mvn test $output_string -Ppostgres