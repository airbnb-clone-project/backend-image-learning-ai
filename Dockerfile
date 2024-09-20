# ================================
# 빌드 스테이지
# ================================
FROM gradle:8.8-jdk11 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시 최적화를 위해 의존성 파일 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드 (캐시 활용)
RUN gradle dependencies --no-daemon

# 프로젝트 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드 (테스트 제외)
RUN gradle build -x test --no-daemon

# ================================
# 실행 스테이지
# ================================
FROM openjdk:11-jre-slim

# 애플리케이션 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 애플리케이션이 사용하는 포트 노출 (예: 8080)
EXPOSE 8080
