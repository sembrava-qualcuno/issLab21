FROM openjdk:12.0.2
EXPOSE 8080
ADD ./build/distributions/*1.1.tar /

WORKDIR /parkmanagerservice-boot-1.1/bin
COPY ./src/main/resources/*.pl ./
COPY ./src/main/resources/parkingMap.bin ./
RUN ls

ENTRYPOINT ["bash", "parkmanagerservice"]
