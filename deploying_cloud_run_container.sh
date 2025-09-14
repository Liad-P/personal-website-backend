#!/bin/bash

# This causes the script to exit on the first failure, which is useful to avoid silent errors:
set -e

# Sets variables like the $SERVICE_ACCOUNT_EMAIL
# Load service account email and other variables
source secrets/.envOther

if [[ -z "$SERVICE_ACCOUNT_EMAIL" ]]; then
  echo "Error: SERVICE_ACCOUNT_EMAIL is not set."
  exit 1
fi


ENV_FILE=".env"
PROJECT_ID=$(gcloud config get-value project)

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

if [[ -n "${CORS_ORIGINS}" ]]; then
  echo "CORS_ORIGINS is set to a non-empty value: '$CORS_ORIGINS'."
  gcloud run deploy backend-website-service \
    --image "$DOCKER_CONTAINER_REF" \
    --set-env-vars "QUARKUS_HTTP_CORS_ORIGINS=$CORS_ORIGINS,CORS_ORIGINS=$CORS_ORIGINS" \
    --set-secrets="$ENV_VARS" \
    --region=us-central1
else
  echo "CORS_ORIGINS is unset or empty."
  gcloud run deploy backend-website-service \
    --image "$DOCKER_CONTAINER_REF" \
    --set-secrets="$ENV_VARS" \
    --region=us-central1
fi


