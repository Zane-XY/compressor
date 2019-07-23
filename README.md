### build instruction

- java 8 + 
- maven 3 + 
- lombok plugin for IDEs (not required for maven build)

#### build executable jar

```
mvn clean compile assembly:single
creates compressor-1.0-SNAPSHOT-jar-with-dependencies.jar inside target directory
```

#### usage

```
 java -jar ./compressor-1.0-SNAPSHOT-jar-with-dependencies.jar
 usage: Fun compressor
 -a,--action <arg>       available actions: c (for compress), d (for
                         decompress)
 -i,--input-dir <arg>    input dir
 -o,--output-dir <arg>   output dir
 -s,--split-size <arg>   split archive size (integer in MB) 
 
```

#### test result

ThinkPad X1 Core i5, Windows 10 

take 30s to compress a 300M directories with each pdf size varying from 1M to 10M.

takes 40s to decompress files back in directory structure.



