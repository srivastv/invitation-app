# invitation-app

Given a file with list of users with their GPS coordinates and a base location (GPS coordinate), this app prints the users within a given radius.

Sample file is available at src/main/resources/customer.txt

**Compile :** mvn clean compile assembly:single

**Run: ** java -jar target/invitation-app-1.0-SNAPSHOT-jar-with-dependencies.jar 
**Run test :** mvn test

Test coverage report will be availabe at : **target/site/jacoco/index.html**

