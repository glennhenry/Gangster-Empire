# Goodgame Gangster private server

It barely works. Still in tutorial mission.

## How to play?

(Windows only for now)

1. Install [Java 24](https://www.oracle.com/java/technologies/downloads/). We recommend installing it in the default directory. (e.g., for Windows 64-bit users, download the x64 MSI installer).
2. Add JAVA_HOME to your environment variable.
3. Install [MongoDB community edition](https://www.mongodb.com/try/download/community). (You don't have to install MongoDB compass and installing it as a service is optional)
4. Install [Adobe AIR runtime](https://airsdk.harman.com/runtime).
5. Download this repo as ZIP (or clone with Git). Then, extract it anywhere you like.
6. Run the MongoDB server, this can be done by running the `runmongo.bat/sh` scripts (for more information see MongoDB's tutorial, [this is for Windows](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-windows/)).
7. Run the game server, this can be done by running the `runserver.bat/sh` script. You can also run the server by executing `./gradlew run` or `.\gradlew run` in CMD.
8. Run the game by going to `static/game` folder then double clicking on `Goodgame Gangster.exe`.

For development, you can instead run the game from `rungame.bat`; this runs the game in debug mode (using `adl`), in which you will need [Adobe AIR SDK](https://airsdk.harman.com/download).
