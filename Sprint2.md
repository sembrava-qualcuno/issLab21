To run this sprint, run the following commands each on their own shell:

1. Run the **WEnv** and the **BasicRobot**
   
   ```shell
   docker-compose -f basicrobotVirtual.yaml
   ```

2. Run the **weight sensor** mock
   
   ```shell
   gradlew runWeightSensor
   ```

3. Run the sonar mock
   
   ```shell
   gradlew runSonar   
   ```

4. Run the application
   
   ```shell
   gradlew run
   ```

## Weight Sensor Mock

To update the weight you have to insert an `integer`:

```shell
> 10
Weight updated to 10
```

## Sonar Mock

To update the sonar state you have to insert `true` or `false`:

```shell
> true
Sonar engaged: true
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
