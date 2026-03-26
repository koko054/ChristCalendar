# 1단계: 실행 환경 구성 (AWS 환경에 최적화된 Amazon Corretto 21 사용)
FROM amazoncorretto:21-al2023-jdk

# 빌드된 JAR 파일의 위치 (Gradle 기준)
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Spring Boot 기본 포트 개방
EXPOSE 8080

# 컨테이너 실행 명령어
ENTRYPOINT ["java", "-Dserver.address=0.0.0.0", "-Dserver.port=8080", "-jar", "/app.jar"]
