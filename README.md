# Data format translation program

## The task
Given are two files - both contain the same content - one is a CSV file the other is a PRN file, 
we want you write a command line utility which will read these CSV files and PRN files from stdin and, 
based on a command line option, print either JSON or HTML to stdout, so that it would work as part of a 
command pipeline.

Input with differing formats (e.g. dates, currency) should produce identical output.
This means that irrespective of whether the input data format was CSV or PRN, the output should
be the same. There will be a check for differences in the evaluation.
 
Non ASCII characters should be handled and rendered correctly. 

No content should be lost in translation and all output should be readable when encoded to UTF-8.

The solution will be tested like this
```
cat ./Workbook2.csv | your-solution csv html > csv.html.txt
cat ./Workbook2.prn | your-solution prn html > prn.html.txt
diff csv.html.txt prn.html.txt
cat ./Workbook2.csv | your-solution csv json > csv.json.txt
cat ./Workbook2.prn | your-solution prn json > prn.json.txt
diff csv.json.txt prn.json.txt
``` 

## How to proceed
Solutions in Kotlin are preferred, but if you're not familiar with Kotlin then use your main (work) language - 
Java, Python, Go, Ruby, JS, TS, etc. Any open source libraries which make life easier for you are of course allowed.

## How to deliver
Please include only the source code, any test code, and the build files. - no IDE files or build products. 
Please include a README.md with instructions on how to build and run the solution.

Please return the solution as a git repository. Make regular commits and pushes, so that we can see the evolution of the solution. Tar.gz or zip files are fine for delivery

## Deadline
You have 48 hours to complete the task. We reckon a couple of evenings should be enough.

# Submitted solution
## Prerequisites:
Java 8 or higher, Maven, IntelliJ or other ide

## How to test
1. cd PROJECT_DIR, mvn clean install
2. Use the following commands to test

```
cat ./Workbook2.csv | ./convert.sh csv html > csv.html.txt
cat ./Workbook2.prn | ./convert.sh prn html > prn.html.txt
diff csv.html.txt prn.html.txt
cat ./Workbook2.csv | ./convert.sh csv json > csv.json.txt
cat ./Workbook2.prn | ./convert.sh prn json > prn.json.txt
diff csv.json.txt prn.json.txt
``` 