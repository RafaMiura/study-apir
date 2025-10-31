#!/usr/bin/env bash

set -e

sudo apt update 
sudo apt install -y jq

HTTP_STATUS=$(
    curl -X 'POST' \
    'http://localhost:9000/api/v2/produtos' \
    -H 'accept: */*' \
    -H 'Content-Type: application/json' \
    -w "%{http_code}" \
    -o product_create.json \
    -d '{
    "nome": "Uva"
    }'
)

if [ "$HTTP_STATUS" -ne 201 ]; then
    echo "Erro ao criar produto"
    exit 1  
fi

PRODUTO_ID=$(jq -r '.id' product_create.json)
echo "Produto criado com ID: $PRODUTO_ID"

echo "Status HTTP: $HTTP_STATUS"

HTTP_STATUS=$(curl -X GET 'http://localhost:9000/api/v2/produtos' -o product_list.json -w "%{http_code}" -H 'accept: */*')
echo "Status HTTP: $HTTP_STATUS"
if [ "$HTTP_STATUS" -ne 200 ]; then
    echo "Erro ao acessar a API de produtos"
    exit 1  
fi