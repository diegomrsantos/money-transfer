# Base Alpine Linux based image with OpenJDK only
FROM openjdk:8-jre-alpine

# remote debugging port
EXPOSE 50505

ENTRYPOINT ["/usr/bin/java", \
"-jar", \
#"-agentlib:jdwp=transport=dt_socket,address=50505,suspend=n,server=y", \
#"-Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1024m -Xmx1024m", /
"/usr/share/money-transfer/money-transfer.jar"]


# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD target/lib           /usr/share/money-transfer/lib

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/money-transfer/money-transfer.jar