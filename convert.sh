#!/bin/bash
arg1=$1
arg2=$2
arg3=()
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
NL=$'\n'

##directory where jar file is located
dir=$SCRIPT_DIR/target

##jar file name
jar_name=cli.jar

## Perform some validation on input arguments, one example below
if [ -z "$1" ] || [ -z "$2" ]; then
        echo "Missing arguments, exiting.."
        echo "Usage : $0 arg1 arg2"
        exit 1
fi

while IFS= read -r line; do
     string=$line${NL}
     trimmed_string="${string#"${string%%[![:space:]]*}"}"
     arg3+=("$trimmed_string")
done

chmod u+x $dir/$jar_name
mvn exec:java -Dexec.mainClass="com.noumea.digital.assessment.Main" \
 -Dexec.args="$arg1 $arg2 '${arg3[*]}'" --batch-mode -D"org.slf4j.simpleLogger.defaultLogLevel=ERROR"