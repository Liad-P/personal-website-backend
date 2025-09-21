#!/bin/bash

# This causes the script to exit on the first failure, which is useful to avoid silent errors:
set -e

# Sets variables like the $SERVICE_ACCOUNT_EMAIL
# Load service account email and other variables
if [[ ! -f "secrets/.envOther" ]]; then
  echo "Error: secrets/.envOther file not found."
  exit 1
fi
source secrets/.envOther

if [[ -z "$SERVICE_ACCOUNT_EMAIL" ]]; then
  echo "Error: SERVICE_ACCOUNT_EMAIL is not set."
  exit 1
fi


ENV_FILE=".env"
PROJECT_ID=$(gcloud config get-value project)
if [[ -z "$PROJECT_ID" ]]; then
  echo "Error: PROJECT_ID is not set."
  exit 1
fi

DOCKER_CONTAINER_REF="gcr.io/$PROJECT_ID/website-backend:latest"

docker build -t "$DOCKER_CONTAINER_REF" .
docker push "$DOCKER_CONTAINER_REF"

UPDATE_KEYS_FLAG="false"
# Uncomment if you have new keys
if [ "$UPDATE_KEYS_FLAG" = "true" ]; then
  while IFS='=' read -r key value
  do
    if [[ ! -z "$key" && "$key" != \#* ]]; then
      echo "$key being put in secrets"
      if ! gcloud secrets describe "$key" --project="$PROJECT_ID" >/dev/null 2>&1; then
        echo -n "$value" | gcloud secrets create "$key" --data-file=- --project="$PROJECT_ID"
      else
        echo -n "$value" | gcloud secrets versions add "$key" --data-file=- --project="$PROJECT_ID"
      fi

      gcloud secrets add-iam-policy-binding "$key" \
        --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
        --role="roles/secretmanager.secretAccessor" \
        --project="$PROJECT_ID"
    fi
  done < "$ENV_FILE"
fi

# Produces some thing that looks like: DB_HOST=DB_HOST:latest,DB_PORT=DB_PORT:latest,API_KEY=API_KEY:latest
ENV_VARS=$(grep -v '^#' "$ENV_FILE" | grep -v '^$' | awk -F= '{print $1"="$1":latest"}' | paste -sd, -)

CONFIG_ENV="secrets/.envConfig"
ENV_CONFIG_VARS=$(grep -v '^#' "$CONFIG_ENV" | grep -v '^$' | awk -F= '{print $1"="$2}' | paste -sd, -)

if [[ -n "$CORS_ORIGINS" ]]; then
  echo "CORS_ORIGINS is set to a non-empty value: '$CORS_ORIGINS'."
  gcloud run deploy backend-website-service \
    --image "$DOCKER_CONTAINER_REF" \
    --set-env-vars "QUARKUS_HTTP_CORS_ORIGINS=$CORS_ORIGINS,$ENV_CONFIG_VARS" \
    --set-secrets="$ENV_VARS" \
    --region=us-central1 \
    --allow-unauthenticated
else
  echo "CORS_ORIGINS is unset or empty."
  gcloud run deploy backend-website-service \
    --image "$DOCKER_CONTAINER_REF" \
    --set-env-vars "$ENV_CONFIG_VARS" \
    --set-secrets="$ENV_VARS" \
    --region=us-central1 \
    --allow-unauthenticated
fi



