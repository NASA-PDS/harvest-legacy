#!/usr/bin/env bash

# Copyright 2019, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
# * Redistributions must reproduce the above copyright notice, this list of
# conditions and the following disclaimer in the documentation and/or other
# materials provided with the distribution.
# * Neither the name of Caltech nor its operating division, the Jet Propulsion
# Laboratory, nor the names of its contributors may be used to endorse or
# promote products derived from this software without specific prior written
# permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

COLLECTION="pds"
PROPS=("-Dauto=yes")
RECURSIVE=""
FILES=()

SCRIPT_DIR=$(cd "$( dirname $0 )" && pwd)
PARENT_DIR=$(cd ${SCRIPT_DIR}/.. && pwd)
LIB_DIR=${PARENT_DIR}/lib
TOOL_JAR=(${LIB_DIR}/solr-core-*.jar)

function print_usage() {
  echo ""
  echo "Usage: search_post.sh [OPTIONS] <file|directory>"
  echo ""
  echo "OPTIONS"
  echo "======="
  echo "  -host <host> (default: localhost)"
  echo "  -port <port> (default: 8983)"
  echo ""
}

# Check number of command line parameters
if [[ $# -eq 0 ]]; then
  print_usage
  exit
fi

# Parse command line parameters
while [ $# -gt 0 ]; do
  if [[ -d "$1" ]]; then
    # Directory
    RECURSIVE=yes
    FILES+=("$1")
  elif [[ -f "$1" ]]; then
    # File
    FILES+=("$1")
  else
    if [[ "$1" == -* ]]; then
      if [[ "$1" == "-port" ]]; then
        shift
        PROPS+=("-Dport=$1")
      elif [[ "$1" == "-host" ]]; then
        shift
        PROPS+=("-Dhost=$1")
      else
        echo -e "\nUnrecognized argument: $1\n"
        exit 1
      fi
    else
      echo -e "\nUnrecognized argument: $1\n"
      exit 1
    fi
  fi
  shift
done

# Setup Java
if [ -n "$JAVA_HOME" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

# Test that Java exists and is executable
"$JAVA" -version >/dev/null 2>&1 || { echo >&2 "Java is required to run this tool! Please install Java 8 or greater before running this script."; exit 1; }

# Parameters for Solr post tool
PROPS+=("-Dc=$COLLECTION" "-Ddata=files")
if [[ -n "$RECURSIVE" ]]; then
  PROPS+=("-Drecursive=yes")
fi

# Call Solr post tool
echo "$JAVA" -classpath "${TOOL_JAR[0]}" "${PROPS[@]}" org.apache.solr.util.SimplePostTool "${FILES[@]}"
"$JAVA" -classpath "${TOOL_JAR[0]}" "${PROPS[@]}" org.apache.solr.util.SimplePostTool "${FILES[@]}"
