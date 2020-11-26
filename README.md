# Qirella
Qirella is a simple quantum statevector simulator in developement
that also supports building tensor networks for the ZX-Calculus and allows interactively applying and experimenting with the rewrite rules.

It is designed as WebApp written in Kotlin and transpiled to JavaScript.

## How to run
The project uses gradle as the build tool.

### Prebuilt hosted web page
Just access the WebApp using any (modern) web browser on e.g. [https://qirella.geosearchef.de](https://qirella.geosearchef.de).  

### Prebuilt release 
Download a prebuilt release containing the JS code and required resources from the releases page of this repository, then just open `index.html` with the browser of your choice.

### Using IntelliJ IDEA
The easiest way to build and run the project is using the IntelliJ IDE. Just clone and import the project using Gradle as Kotlin/JS for browser (Frontend Application).
The IDE should automatically start downloading the specified Gradle wrapper.
Then simply run the `browserDevelopmentRun` / `build` tasks. 

### Building manually
First, clone the repository and install gradle 6.7.
Inside the project root, run:

```gradle browserDevelopementRun```

to build the application and launch the integrated web server. Using

```gradle build```

you can build the project including the distribution files which you can find in `./build/distributions` and either open using a web browser or put on a web server of your choice.