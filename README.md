# How to run MapReduce under single node mode
> System: Linux Ubuntu 18.04

### Steps
1. Create folders for input files and class files
2. set $HADOOP_CLASSPATH environment variable
```bash
export HADOOP_CLASSPATH=$(hadoop classpath)
```
and test it using
```bash
echo $HADOOP_CLASSPATH
```
3. Create directories on HDFS
```bash
hadoop fs -mkdir <DIRECTORY_NAME>
hadoop fs -mkdir <HDFS_INPUT_DIRECTORY_NAME>
```
4. Go to `localhost:9870`, `Utilities -> Browse the file system` check if directories are created
5. Upload the input file to HDFS and check it on web UI
```bash
hadoop fs -put <INPUT_FILE> <HDFS_INPUT_DIRECTORY>
```
6. cd into the directory that contains your source code (Assumming you only have one file to compile)
7. Compile that java code
```bash
javac -classpath ${HADOOP_CLASSPATH} -d <CLASSES_FOLDER> <JAVA_FILE_TO_COMPILE>
```
8. cd into the classes directory, create a jar using class files you just compiled
```bash
jar cf wc.jar <JAVA_FILE_NAME>*.class
```
9. Run the jar file on Hadoop
```bash
hadoop jar <JAR_FILE> <CLASS_NAME> <HDFS_INPUT_DIRECTORY> <HDFS_OUTPUT_DIRECTORY>
```
10. You should be able to see `INFO mapreduce.Job: Job ... completed successfully`
11. View the output
```bash
hadoop dfs -cat <HDFS_OUTPUT_DIRECTORY>/*
```
