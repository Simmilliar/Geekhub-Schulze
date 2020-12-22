FROM openjdk
WORKDIR /usr/src/app
COPY ./build/libs/Geekhub-Schulze-1.0.jar ./
EXPOSE 8080
CMD [ "java", "-jar", "Geekhub-Schulze-1.0.jar" ]
