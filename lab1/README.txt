Lab 1 Linker


The goal of this lab is to implement a two-pass linker in Java. The only class included in the source code is Linker.java, which provides all the functions.

To compile the program, first locate in to the current folder using cd. Then run the following command:


javac Linker.java


The program will be compiled and a Linker.class file will be generated.

To run the program, in the same folder, run the following command:


java Linker [input_file]


Where [input_file] is the path (and name) of the input file. The output will be printed on the screen.

For example, the following command will run the Linker program on the file test-cases/input-1.txt:


java Linker test-cases/input-1.txt


You can also save the output into a file using the following command:


java Linker [input_file] > [output_file]


For example, the following command will run the Linker program on the file test-cases/input-1.txt, and save the output to the file test-cases/output-1.txt:


java Linker test-cases/input-1.txt > test-cases/output-1.txt



