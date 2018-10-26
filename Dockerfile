FROM openjdk:11-jre

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/myservice/myservice.jar"]

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/myservice/myservice.jar