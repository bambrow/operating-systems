Lab 4 Demand Paging


The goal of this lab is to simulate demand paging using the following replacement algorithms: FIFO, Random, LRU. All source code files are included in this zip file.  

To compile the program, first locate in to the current folder using cd. Then run the following command:


javac *.java


The program (all source code) will be compiled and the corresponding .class files should be generated. Among them, Paging.class is the main class that we should run.

To run the program, in the same folder, run the following command:


java Paging [M] [P] [S] [J] [N] [R]


Where [M] is the machine size in words, [P] is the page size in words, [S] is the size of each process, [J] is the job mix, [N] is the number of references for each process, and [R] is the replacement algorithm used. The output will be printed on the screen.

The program will run successfully if and only if the command line arguments are valid. The user must guarantee that there are 6 command line arguments and they are in the above order. For more information, please read the first part of the note below.

For example, the following command will run the Paging program given M = 10, P = 10, S = 20, J = 1, N = 10, R = lru:


java Paging 10 10 20 1 10 lru


You can also save the output into a file using the following command:


java Paging [M] [P] [S] [J] [N] [R] > [output_file]


For example, the following command will run the Paging program given M = 10, P = 10, S = 20, J = 1, N = 10, R = lru, and save the output to the file output.txt:


java Paging 10 10 20 1 10 lru > output.txt



Note:
1. The program assumes the input is valid. Still, there is some restrictions:
    a) there must be 6 command line arguments, and they are in the order: [M] [P] [S] [J] [N] [R]
    b) M, P, S, J, N must be integers
    c) R must be String and must be one of these: fifo, random, lru
    d) J must be one of these: 1, 2, 3, 4
    e) M, P, S, N must be positive
   Invalid input will terminate the program. 

2. The final output includes the following:
    a) first, the general information of this run: the command line arguments inputed by user
    b) then for each process, the number of page faults and the average residency time, if any
    c) finally, the total number of faults and the overall average residency time, if any

3. A short description of each file

Paging.java                the main program responsible for parsing the command line arguments and start the Pager

RandomNumberReader.java    the implementation of random number reader

Address.java               the implementation file of Address class

Page.java                  the implementation file of Page class

Process.java               the implementation file of Process class

Pager.java                 the implementation file of Pager class, which will run the demand paging using the replacement algorithm chosen by user
