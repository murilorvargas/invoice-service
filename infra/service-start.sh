#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "==> Tearing down previous environment..."
./service-stop.sh

echo "==> Starting infrastructure..."
docker compose up -d invoice-service-mysql invoice-service-localstack

echo "==> Waiting for LocalStack to be ready..."
until docker compose exec invoice-service-localstack curl -sf http://localhost:4566/_localstack/health > /dev/null 2>&1; do
  echo "  LocalStack not ready yet, retrying..."
  sleep 2
done

echo "==> Configuring LocalStack..."
docker compose exec invoice-service-localstack awslocal sns create-topic --name card_entry
docker compose exec invoice-service-localstack awslocal sqs create-queue --queue-name invoice-card_entry-consumer
docker compose exec invoice-service-localstack awslocal sns subscribe \
  --topic-arn arn:aws:sns:us-east-1:000000000000:card_entry \
  --protocol sqs \
  --notification-endpoint arn:aws:sqs:us-east-1:000000000000:invoice-card_entry-consumer

echo "==> Starting application..."
docker compose up -d --build --wait invoice-service-app invoice-service-consumer

echo "==> All done! Application available at http://localhost:8080"
