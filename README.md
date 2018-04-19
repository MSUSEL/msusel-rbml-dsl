# MSUSEL RBML DSL

## Introduction

## Building
There are two options:

1. You can use your own version of Maven and run the following commands at the command line, from the project root directory:
   * Compiling:
      ```bash
        $ mvn clean compile
      ```
   * Packaging into a Jar with dependencies
      ```bash
        $ mvn clean package
      ```
   * Packaging into a Jar and deploying to the [maven repo](https://github.com/MSUSEL/msusel-maven-repo):
      ```bash
        $ mvn clean deploy
      ```

2. You can use the Maven wrapper which comes with the project:
   * On Mac and Linux:
      - Compiling:
      ```bash
        $ ./mvnw clean compile
      ```
      - Packaging into a Jar with Dependencies:
      ```bash
        $ ./mvnw clean package
      ```
      - Packaging into a Jar and deploying to the [maven repo](https://github.com/MSUSEL/msusel-maven-repo):
      ```bash
        $ ./mvnw clean deploy
      ```
   * Windows:
      - Compiling:
      ```bash
        $ .\mvnw.cmd clean compile
      ```
      - Packaging into a Jar with Dependencies:
      ```bash
        $ .\mvnw.cmd clean package
      ```
      - Packaging into a Jar and deploying to the [maven repo](https://github.com/MSUSEL/msusel-maven-repo):
      ```bash
        $ .\mvnw.cmd clean deploy
      ```

## License
As will all projects from MSUSEL this project is licensed under the MIT open source license. All source files associated with this project should have a copy of the license at the top of the file.

If a build fails due to license header issues, this can be remedied using the following command sequence at the command line:

- With an independently installed Maven system:
    * Linux, Mac, Windows:
    ```bash
     $ mvn license:format
    ```
- Using the Maven Wrapper:
    * Linux and Mac:
    ```bash
     $ ./mvnw license:format
    ```
    * Windows:
    ```bash
     $ .\mvnw.cmd license:format