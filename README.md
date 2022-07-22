# Maintain protectors frontend

This service is responsible for updating the information held about protectors in a trust registration.
A trust does not need to have a protector.

### running the service ###

To run locally using the micro-service provided by the service manager:

***sm --start TRUSTS_ALL -r***

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 9796 but is defaulted to that in build.sbt).

`sbt clean run`

### testing the service ###

To test the service locally run use the following command, this will run both the unit and integration tests and check the covergae of the tests.

`sbt clean coverage test it:test coverageReport`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
