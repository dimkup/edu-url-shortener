FROM openjdk:8-jre-alpine

EXPOSE 7000
ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/javalin/javalin-app.jar"]

ARG JAR_FILE='edu-url-shortner-1.0-SNAPSHOT-shaded.jar'
ADD target/${JAR_FILE} /usr/share/javalin/javalin-app.jar