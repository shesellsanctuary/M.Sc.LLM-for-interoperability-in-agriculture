FROM openjdk:11-jre-slim
VOLUME /tmp
COPY target/*.jar app.jar
# TODO: Consider using non-root user to run the application
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 8080
