Lab 3 Banker


The goal of this lab is to simulate resource allocation algorithms: FIFO manager (optimistic resource manager) and Banker manager (banker’s algorithm of Dijkstra). All source code files are included in this zip file.  

To compile the program, first locate in to the current folder using cd. Then run the following command:


javac *.java


The program (all source code) will be compiled and the corresponding .class files should be generated. Among them, Banker.class is the main class that we should run.

To run the program, in the same folder, run the following command:


java Banker [input_file]


Where [input_file] is the path (and name) of the input file. The output will be printed on the screen. Each resource allocation algorithm will be run once.

For example, the following command will run the Banker program on the file test-cases/input-1.txt:


java Banker test-cases/input-1.txt


You can also save the output into a file using the following command:


java Banker [input_file] > [output_file]


For example, the following command will run the Banker program on the file test-cases/input-1.txt, and save the output to the file test-cases/output-1.txt:


java Banker test-cases/input-1.txt > test-cases/output-1.txt



Note:
1. The program assumes the input is valid. Still, there is some restrictions:
    a) the number of tasks must be non-negative
    b) the number of resources must be non-negative
    c) for each activity, task-number must be positive and no greater than number of all tasks
    d) for each activity, resource-type must be positive and no greater than number of all resources
    e) for each activity, delay must be non-negative
    f) for each activity, number (claimed, requested or released) must be non-negative
    g) for each activity, the first argument must be one of the following: initiate, request, release, terminate
   Invalid input will terminate the program. 

2. The final output includes the following:
    a) for each task, print the time taken, waiting time and the percentage of time spent waiting
    b) the total time, waiting time, and overall percentage of time spent waiting for all tasks
    c) the tasks are sorted by task number
    d) the results of two resource allocation algorithms are printed in parallel

3. During the running of the program, extra messages will be printed out when a task is aborted. These situations include:
    a) in FIFOManager, when a deadlock occurs, the task with lowest task number is aborted
    b) in BankerManager, when a task’s initial claim exceeds the resource present, the task is aborted
    c) in BankerManager, when a task’s requests exceed its claims, the task is aborted

4. A short description of each file

Banker.java                the main program responsible for parsing and reading the input file and run each algorithm

Activity.java              the implementation file of Activity class

ActivityHeader.java        the implementation file of ActivityHeader class

Task.java                  the implementation file of Task class

BankerManager.java         the base class of FIFOManager and BankerManager including basic functions shared by both managers

FIFOManager.java           the implementation of FIFOManager class (optimistic resource manager)

BankerManager.java         the implementation of BankerManager class (banker’s algorithm of Dijkstra)
