FROM openjdk:14-alpine
COPY target/transactions-stats-*.jar transactions-stats.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "transactions-stats.jar"]