FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
COPY target/shareit-gateway-0.1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
