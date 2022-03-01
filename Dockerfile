FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY /build/resources/main/key-pair.der key-pair.der
ENTRYPOINT ["java","-jar","/app.jar"]["java", "-jar", "/hello-codedeploy.jar"]