Lab 2 Scheduling


The goal of this lab is to simulate scheduling algorithms: FCFS (First Come First Served), RR (Round Robin) with quantum 2, Uniprogrammed, and SRTN (or PSJF, Shortest Remaining Time Next). All source code files are included in this zip file.  

To compile the program, first locate in to the current folder using cd. Then run the following command:


javac *.java


The program (all source code) will be compiled and the corresponding .class files should be generated. Among them, Scheduling.class is the main class that we should run.

To run the program, in the same folder, run the following command:


java Scheduling [input_file]


Where [input_file] is the path (and name) of the input file. The output will be printed on the screen. Each scheduling algorithm will be run once.

For example, the following command will run the Scheduling program on the file test-cases/input-1.txt:


java Scheduling test-cases/input-1.txt


Also this program supports detailed output showing the states of each process before every cycle. The processes are sorted as the sorted input. To display detailed output, run with --verbose flag before the input file name:


java Scheduling --verbose [input_file]


For example, the following command will run the Scheduling program with detailed output (--verbose) on the file test-cases/input-1.txt:


java Scheduling --verbose test-cases/input-1.txt


Note that this program uses random integer, and reads random integers from a file named random-numbers. The file must be exactly named "random-numbers" and be put in the current directory together with the source code.


You can also save the output into a file using the following command:


java Scheduling [input_file] > [output_file]


For example, the following command will run the Scheduling program on the file test-cases/input-1.txt, and save the output to the file test-cases/output-1.txt:


java Scheduling test-cases/input-1.txt > test-cases/output-1.txt



Note:
1. The program assumes the input is valid. Any invalid input will result in error and termination of the program.

2. The orders of processes in detailed output and final output are based on the sorted input processes, just as the template output provided on the course website. For any non-integers, the numbers are rounded to 6 digits after the decimal point.

3. A short description of each file

Scheduling.java            the main program responsible for parsing and reading the input file and run each algorithm

Process.java               the implementation of process class

RandomNumberReader.java    the implementation of random number reader

SchedulingAlgorithm.java   the abstract parent class of all scheduling algorithms containing code that can be reused

FCFS.java                  the implementation of FCFS algorithm

RR.java                    the implementation of RR algorithm with quantum 2

Uniprogrammed.java         the implementation of Uniprogrammed algorithm

PSJF.java                  the implementation of PSJF algorithm

