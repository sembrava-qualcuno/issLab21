FROM openjdk:12.0.2
EXPOSE 8080
ADD ./build/distributions/*.tar /

WORKDIR /parkmanagerservice-boot-1.0/bin
COPY ./src/main/resources/*.pl ./
RUN ls

ENTRYPOINT ["bash", "parkmanagerservice"]
