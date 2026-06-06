FROM amazoncorretto:17-alpine
COPY allreva-api/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]