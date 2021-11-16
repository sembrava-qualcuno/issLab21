# Usage

To run this sprint you can use **Gradle** or **Docker**

## Gradle

Run the following commands each on their own shell:

1. Run the **WEnv** and the **BasicRobot**
   
   ```shell
   docker-compose -f basicrobotVirtual.yaml up
   ```

2. Run the **indoorarea**
   
   ```shell
   gradlew indoorarea:run
   ```

3. Run the **outdoorarea**
   
   ```shell
   gradlew outdoorarea:run
   ```

4. Run the **parkingarea**
   
   ```shell
   gradlew parkingarea:run
   ```

5. Run the **parkmanagerservice**
   
   ```shell
   gradlew parkmanagerservice:run
   ```

## Docker

You can use the images hosted on [Docker Hub](https://hub.docker.com/u/sembravaqualcuno) running the docker compose file:

```shell
docker-compose -f automatedcarparking.yaml up
```

Otherwise, you can create a docker image for each subproject:

```shell
cd indoorarea
docker build --rm -t indoorarea .

cd outdoorarea
docker build --rm -t outdoorarea .

cd parkingarea
docker build --rm -t parkingarea .

cd parkmanagerservice
docker build --rm -t parkmanagerservice .
```

and then you can just run the same docker compose file changing the images.

Once started the containers, if you want to change the sensors' values you have to attach to the standard in/out of each container throught the command `docker attach <container>`, here there is an example:

```shell
> docker ps
CONTAINER ID   IMAGE                                                         COMMAND                  CREATED          STATUS          PORTS                                            NAMES
322358afbda9   sembravaqualcuno/automatedcarparking-parkmanagerservice:1.1   "bash parkmanagerser…"   40 minutes ago   Up 21 minutes   0.0.0.0:8080->8080/tcp, 0.0.0.0:8080->8080/udp   itunibosembrava_qualcunosprint4_parkmanagerservice_1
54a55e0f95fc   sembravaqualcuno/automatedcarparking-outdoorarea:1.0          "bash /outdoorarea-1…"   40 minutes ago   Up 21 minutes   8026/tcp, 0.0.0.0:8026->8026/udp                 itunibosembrava_qualcunosprint4_outdoorarea_1
808028c3ba08   sembravaqualcuno/automatedcarparking-parkingarea:1.0          "bash /parkingarea-1…"   40 minutes ago   Up 21 minutes   8027/tcp, 0.0.0.0:8027->8027/udp                 itunibosembrava_qualcunosprint4_parkingarea_1
65a633c4da93   sembravaqualcuno/automatedcarparking-indoorarea:1.0           "bash /indoorarea-1.…"   40 minutes ago   Up 21 minutes   8025/tcp, 0.0.0.0:8025->8025/udp                 itunibosembrava_qualcunosprint4_indoorarea_1
a1819aac00d5   natbodocker/basicrobot21virtual:1.0                           "bash it.unibo.qak21…"   3 months ago     Up 21 minutes   0.0.0.0:8020->8020/tcp, 0.0.0.0:8020->8020/udp   itunibosembrava_qualcunosprint4_basicrobot_1
63fff39a051a   natbodocker/virtualrobotdisi:2.0                              "docker-entrypoint.s…"   3 months ago     Up 21 minutes   0.0.0.0:8090-8091->8090-8091/tcp                 itunibosembrava_qualcunosprint4_wenv_1
> docker attach 65a633c4da93
10
Weight updated to 10
```

## Indoorarea

To update the weight you have to insert an `integer`:

```shell
> 10
Weight updated to 10
```

## Outdoorarea

To update the sonar state you have to insert `true` or `false`:

```shell
> true
Sonar engaged: true
```

## Parkingarea

To update the temperature you have to insert an `integer`:

```shell
> 30
Temperature updated to 30
```

## Manager credentials

The default manager credentials are:

    username: **manager**

    password: **admin**

They can be changed throught the `variables.env` file:

```properties
# Manager credentials
MANAGER_USERNAME=manager
MANAGER_PASSWORD=admin
```

## REST-API

To send commands throught the REST-API you can use:

- A simple web browser

- An API platform like [Postman](https://www.postman.com/) (you can import the [postman collection](../it.unibo.sembrava_qualcuno.sprint1/userDocs/Automated%20Car-Parking%20postman_collection.json))

- A command line tool like [curl](https://curl.se/), for example:
  
  ```shell
  curl -X GET http://localhost:8080/client/reqenter
  {"code":1,"message":"The indoor area or trolley are engaged"}
  ```

To learn about the REST-API commands available in this sprint check out the documentation
