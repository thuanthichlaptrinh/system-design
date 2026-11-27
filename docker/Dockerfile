# syntax=docker/dockerfile:1.7
ARG BASE_REGISTRY=docker.io/library

##############################################
# Stage 1: deps - nền chung cho build và dev
##############################################
FROM ${BASE_REGISTRY}/maven:3.9-eclipse-temurin-21 AS deps
WORKDIR /workspace
COPY pom.xml ./

##############################################
# Stage 2: build - đóng gói jar (test chạy riêng bằng `make test`)
##############################################
FROM deps AS build
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package -Dmaven.test.skip=true

##############################################
# Stage 3: runtime - image chạy thật
##############################################
FROM ${BASE_REGISTRY}/eclipse-temurin:21-jre AS runtime
WORKDIR /app
RUN useradd --system --uid 10001 app
COPY --from=build /workspace/target/*.jar app.jar
USER app
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -fsS http://127.0.0.1:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]

##############################################
# Stage 4: dev - chạy từ source được mount vào
##############################################
FROM deps AS dev
ARG UID=1000
# Chạy không phải root. Tạo sẵn thư mục để named volume nhận đúng quyền.
RUN (id -u ${UID} >/dev/null 2>&1 || useradd --uid ${UID} --create-home dev) \
  && mkdir -p /var/maven/.m2 /workspace/target \
  && chown -R ${UID} /var/maven /workspace
ENV MAVEN_CONFIG=/var/maven/.m2
USER ${UID}
EXPOSE 8081
CMD ["mvn", "-B", "-Duser.home=/var/maven", "spring-boot:run"]
