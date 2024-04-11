#!/bin/bash

# Input arguments
arg1=$1
arg2=$2

# Perform some validation on input arguments
if [ -z "$arg1" ] || [ -z "$arg2" ]; then
  echo "Missing arguments, exiting.."
  echo "Usage : $0 arg1 arg2"
  exit 1
fi

# Initialize an empty array
arg3=()

# Get the directory of the current script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
NL=$'\n'

# Directory where the jar file is located
dir="$SCRIPT_DIR/target"

# Jar file name
jar_name="cli.jar"

# Check if the jar file exists and is executable
if [ ! -f "$dir/$jar_name" ]; then
  echo "Jar file not found: $dir/$jar_name"
  exit 1
fi

if [ ! -x "$dir/$jar_name" ]; then
  chmod u+x "$dir/$jar_name" || { echo "Failed to make jar file executable"; exit 1; }
fi

# Read and process input lines
while IFS= read -r line; do
  encoded_line=$(echo "$line" | iconv -f ISO-8859-2 -t UTF-8) || { echo "Encoding conversion failed"; exit 1; }
  arg3+=("$encoded_line${NL}")
done

# Execute the Java command
mvn exec:java -Dexec.mainClass="com.noumea.digital.assessment.Main" \
  -Dexec.args="$arg1 $arg2 '${arg3[*]}'" --batch-mode -D"org.slf4j.simpleLogger.defaultLogLevel=ERROR" || {
    echo "Java execution failed"
    exit 1
}