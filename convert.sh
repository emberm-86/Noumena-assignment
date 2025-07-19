#!/bin/bash

# Input arguments
arg1=$1
arg2=$2

export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

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

# Lock file
LOCKFILE="$SCRIPT_DIR/lockfile.lock"
TMPLOCKFILE=$(mktemp)

# Function to remove the lock file on exit
cleanup() {
  rm -f "$TMPLOCKFILE" "$LOCKFILE"
}
trap cleanup EXIT

# Try to acquire the lock
if ! ln "$TMPLOCKFILE" "$LOCKFILE" 2>/dev/null; then
  echo "Another instance is running, exiting.."
  cleanup
  exit 1
fi

# Check if the jar file exists and is executable
if [ ! -f "$dir/$jar_name" ]; then
  echo "Jar file not found: $dir/$jar_name"
  cleanup
  exit 1
fi

if [ ! -x "$dir/$jar_name" ]; then
  chmod u+x "$dir/$jar_name" || { echo "Failed to make jar file executable"; cleanup; exit 1; }
fi

# Read and process input lines
while IFS= read -r line; do
  encoded_line=$(echo "$line" | iconv -f ISO-8859-1 -t UTF-8) || { echo "Encoding conversion failed"; cleanup; exit 1; }
  arg3+=("$encoded_line")
done

joined_arg3=$(printf "%s\n" "${arg3[@]}")
encoded_arg3=$(printf "%s" "$joined_arg3" | base64 | tr -d '\n')

# Execute the Java command
mvn exec:java -Dexec.args="$arg1 $arg2 \"$encoded_arg3\"" \
	-Dexec.jvmArgs="-Dfile.encoding=UTF-8" \
	--batch-mode -D"org.slf4j.simpleLogger.defaultLogLevel=ERROR" || {
    echo "Java execution failed"
    cleanup
    exit 1
}

cleanup
