#!/bin/bash

# 1. 에러 발생 시 즉시 중단 설정
set -e

# 2. 변수 설정 (나중에 리전이나 계정 ID가 바뀌면 여기만 수정하면 됩니다)
AWS_ACCOUNT_ID="YOUR_ACCOUNT_ID"
AWS_REGION="ap-northeast-1" # 도쿄 리전
REPO_NAME="christ-calendar"
IMAGE_NAME="christ-calendar"
ECR_URL="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

echo "----------------------------------------------------"
echo "🚀 [1/5] 프로젝트 빌드 시작 (Gradle)..."
echo "----------------------------------------------------"
./gradlew clean build

echo "----------------------------------------------------"
echo "🔑 [2/5] AWS ECR 로그인..."
echo "----------------------------------------------------"
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_URL}

echo "----------------------------------------------------"
echo "🐳 [3/5] 도커 이미지 빌드..."
echo "----------------------------------------------------"
docker build --platform linux/amd64 -t ${IMAGE_NAME} .

echo "----------------------------------------------------"
echo "🏷️ [4/5] 이미지 태깅..."
echo "----------------------------------------------------"
docker tag ${IMAGE_NAME}:latest ${ECR_URL}/${REPO_NAME}:latest

echo "----------------------------------------------------"
echo "⬆️ [5/5] ECR로 이미지 푸시..."
echo "----------------------------------------------------"
docker push ${ECR_URL}/${REPO_NAME}:latest

echo "----------------------------------------------------"
echo "✅ 배포 이미지 업로드 완료!"
echo "----------------------------------------------------"
