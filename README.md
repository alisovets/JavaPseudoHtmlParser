## The pseudo html parser
It's test job for one potential employer 

Implement Java-program that performs a predetermined paging rendering html-document based on partition
on the page and stores the results in rendering image files.

## requirements

1. Java SE 5.0
2. Using third-party libraries is prohibited.
3. Mmemory usage restriction – 16 Мб.
4. Output images to be stored in the directory where the source file is located, the directory name to be generated accordingly: <input.html>.render
5. A test application must run the following command line:
java –Xmx16m –Xms16m –Xss16m -jar <filename.jar> -i <input.html> -w <width> -h <height>
when:
key i – html-source location in the file system (the file size is not more than 1 MB),
keys w and h –  are accordingly the width and height in pixels of the output image (100 <= w,h <= 1600)

Example:
java –Xmx16m –Xms16m –Xss16m -jar myhtml.jar -i input.html -w 800 -h 1280

6. Supported tags:

* html, 
* head, 
* title, 
* body (atrs: bgcolor, text, leftmargin, topmargin, rightmargin, bottommargin, fontmargin, font-family, fontsize), 
* h1-h6 (atrs: color), 
* br, 
* b (atrs: color) - bold style with a shadow, 
* i (atrs: color), 
* u (atrs: color) - underlined by a wavy line,
* p (atrs: align), 
* img (atrs: src, width, height),
* <!-- --> - comment
* unsupported tags are ignored

## How to build

You require the following to build:
* Latest stable [Oracle JDK 5](http://www.oracle.com/technetwork/java/)
* Latest stable [Apache Maven](http://maven.apache.org/)

Go to the root project directory and input:

mvn install


## How start

Go to the target directory and for example, input:

java -Xmx16m -Xms16m -Xss16m -jar pseudohtmlparser-0.0.1-SNAPSHOT.jar  -i ../testData/test1.htm  -w 500 -h 600

or 
java -jar pseudohtmlparser-0.0.1-SNAPSHOT.jar  -i ../testData/test1.htm  -w 800 -h 1200

(expected that there is a file test1.htm in the directory testData)


