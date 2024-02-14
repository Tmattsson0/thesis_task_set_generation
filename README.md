# Readme

## Prerequisites 
- Java 17 or later
- Maven
- Git

## How to install
To run the program, first navigate to where on your computer you want the software to run and clone the repo from with:

`git clone https://github.com/Tmattsson0/thesis_task_set_generation`

Next navigate to the folder that just appeared and run the command:

`mvn clean compile assembly:single`

A jar file called `thesis_task_set_generation-1.0-SNAPSHOT-jar-with-dependencies.jar`shoud have appeared in the `target` folder.

## How to configure

When trying to run the program, there must exist a directory named "config" in the root of the project. Or, if running the jar file, there must be a "config" folder in the same directory as that jar file.

The program takes two commandline arguments: the first one for the parameters.json file and the second for an optional config file.

In the current version of this program, the first argument has to be "parameters.json" and the corresponding parameters file has to also be named that. The optional XML config file argument and file can be named anything.

To change the number of tasks, cores, chains etc. open the "parameters.json" file and change the desired values. 

## How to run

To run the program, you can open in IntelliJ or another IDE and run like you would run other java programs.

To run from command line: Navigate to the repo folder and run the command:

`java -jar target/thesis_task_set_generation-1.0-SNAPSHOT-jar-with-dependencies.jar <parameters.json> [congig file]`

The program will run and generate a "testCases" folder with the results in it.
