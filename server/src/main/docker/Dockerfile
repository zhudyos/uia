FROM openjdk:8-jre-alpine
MAINTAINER kevinz@weghst.com

ENV JAVA_OPTS=""

ADD build/libs/app.jar app.jar

EXPOSE 8080
CMD [
    "sh",
    "-c",
    "java $JAVA_OPTS -jar /app.jar"
]