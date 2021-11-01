# Usage

To run this sprint you can use **Gradle** or **Docker**

## Gradle

Run the following commands each on their own shell:

1. Run the **WEnv** and the **BasicRobot**
   
   ```shell
   docker-compose -f basicrobotVirtual.yaml
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
docker-compose -f automatedcarparking.yaml
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
