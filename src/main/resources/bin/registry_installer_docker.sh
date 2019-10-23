#!/usr/bin/env bash

SOLR_HEAP=2048m

maxShardsPerNode=3
numShards=3
replicationFactor=1

DOCKER_IMAGE=registry
DOCKER_VOLUMES="solr_data"

PROMPT=true
COMMAND=""

SCRIPT_DIR=$(cd "$( dirname $0 )" && pwd)
PARENT_DIR=$(cd ${SCRIPT_DIR}/.. && pwd)
LOG=${PARENT_DIR}/registry_installer_docker-log-$(date '+%Y%m%d_%H%M%S').txt

VERSION_FILE=${PARENT_DIR}/VERSION.txt
VERSION="latest"


function print_usage() {
  echo ""
  echo "Usage: $0 [OPTIONS] COMMAND"
  echo ""
  echo "Options:"
  echo "  -n   No prompt"
  echo ""
  echo "Commands:"
  echo "  install     Install registry in docker"
  echo "  uninstall   Uninstall all registry components from docker"
  echo ""
}

# Check number of command line parameters
if [[ $# -eq 0 ]]; then
  print_usage
  exit
fi

# Parse command line parameters
while [ $# -gt 0 ]; do
  if [[ "$1" == "-n" ]]; then
    PROMPT=false
  elif [[ ("$1" == "install" || "$1" == "uninstall") ]]; then
    COMMAND="$1"
  else
    echo -e "\nUnrecognized argument: $1\n"
    exit 1
  fi
  shift
done

# Version
if [ -f "$VERSION_FILE" ]; then
  VERSION=$(head -n 1 "$VERSION_FILE")
  if [[ $VERSION == '${project.version}' ]]; then
    echo "WARNING: Invalid version '\${project.version}'. Will use default version 'latest'"
    VERSION="latest"
  fi
else
  echo "WARNING: $VERSION_FILE doesn't exist. Will use default version 'latest'"
fi

print_status() {
  if [ $1 -eq 0 ]; then
    echo "SUCCESS"
  else
    echo "FAILED"
    echo "See $LOG"
    echo
    exit 1
  fi
}

print_solr_status() {
    status=$(echo $1 | tr -d '\n' | awk -F"status" '{print $2}' | awk -F, '{print $1}' | awk -F: '{print $2}')
    print_status $((status))
}

build_docker_image() {
    echo -ne "Building Registry Docker Image.                               " | tee -a $LOG
    cd ${PARENT_DIR}/build
    docker build -t $DOCKER_IMAGE:$VERSION -f Dockerfile ../ >>$LOG 2>&1
    print_status $?
}

create_docker_volumes() {
    for vol in $DOCKER_VOLUMES; do
        docker volume create $vol >>$LOG 2>&1
    done
}

remove_registry_container() {
    containerId=$(docker ps -a | grep "registry" | awk '{print $1}')
    if [ ! -z "$containerId" ]; then
        docker stop $containerId >> $LOG 2>&1
        docker rm $containerId >> $LOG 2>&1
    fi
}

wait_for_solr() {
    attempt_counter=0
    max_attempts=12

    echo "Waiting for Solr server to start." | tee -a $LOG

    until $(curl --output /dev/null --max-time 5 --silent --head --fail http://localhost:8983/solr); do
        if [ ${attempt_counter} -eq ${max_attempts} ];then
            echo "Could not start Solr."
            exit 1
        fi
        attempt_counter=$(($attempt_counter+1))
        sleep 5
    done
}

start_registry_container() {
    echo -ne "Starting Registry Docker Container                            " | tee -a $LOG
    docker run --name ${DOCKER_IMAGE} -u solr\
      -v solr_data:/opt/solr/server/solr/ \
      -d -p 8983:8983 \
      -e SOLR_HEAP=$SOLR_HEAP \
      $DOCKER_IMAGE:$VERSION >>$LOG 2>&1

    print_status $?
}

create_solr_collections() {
    # Create the Registry collections
    echo -ne "Creating a Registry Service Blob collection (registry)        " | tee -a $LOG
    docker exec --user=solr ${DOCKER_IMAGE} solr create -c registry -d registry -s ${numShards} -rf ${replicationFactor} >>$LOG 2>&1
    print_status $?

    # Create XPath collection
    echo -ne "Creating a Registry Service XPath collection (xpath)          " | tee -a $LOG
    check=$(curl "http://localhost:8983/solr/admin/collections?action=CREATE&name=xpath&maxShardsPerNode=${maxShardsPerNode}&numShards=${numShards}&replicationFactor=${replicationFactor}" 2>>$LOG | tee -a $LOG)
    print_solr_status "$check"

    # Create the Search collection 
    echo -ne "Creating a Search collection (pds)                            " | tee -a $LOG
    docker exec --user=solr ${DOCKER_IMAGE} solr create -c pds -d pds -s ${numShards} -rf ${replicationFactor} >>$LOG 2>&1
    print_status $?
}

confirm_uninstall() {
    if [ "$PROMPT" = true ]; then
        while true; do
            echo ""
            read -p "Are you sure you want to uninstall the Registry and Search and all associated indices? (y/n) " yn
            case $yn in
                [Yy]* ) break;;
                [Nn]* ) exit 0;;
                * ) echo "Please answer y[es] or n[o].";;
            esac
        done
    fi
}

remove_solr_collections() {
    # Remove 'registry' collection
    echo "Removing the Registry Blob collection from the SOLR.              " | tee -a $LOG
    docker exec -it --user=solr ${DOCKER_IMAGE} solr delete -c registry  >>$LOG 2>&1

    # Remove 'xpath' collection
    echo "Removing the Registry XPath collection.                           " | tee -a $LOG
    docker exec -it --user=solr ${DOCKER_IMAGE} solr delete -c xpath  >>$LOG 2>&1

    # Remove 'pds' collection
    echo "Removing the Search collection.                                   " | tee -a $LOG
    docker exec -it --user=solr ${DOCKER_IMAGE} solr delete -c pds >>$LOG 2>&1

    echo "Stopping the SOLR instance.                                       " | tee -a $LOG
    docker exec -it ${DOCKER_IMAGE} solr stop >>$LOG 2>&1
}

remove_registry_image() {
    echo "Removing Registry Docker Images.                                  " | tee -a $LOG
    docker rmi -f "$DOCKER_IMAGE:$VERSION" >>$LOG 2>&1
}

remove_docker_volumes() {
    for vol in $DOCKER_VOLUMES; do
	echo "Removing '"$vol"' volume                   " | tee -a $LOG
	docker volume rm $vol  >>$LOG 2>&1
    done
}


# Execute commands
if [[ $COMMAND == "install" ]]; then
  echo "Installing..."
  build_docker_image
  create_docker_volumes
  start_registry_container
  wait_for_solr
  create_solr_collections
  # Print solr status
  docker exec ${DOCKER_IMAGE} solr status >>$LOG 2>&1

elif [[ $COMMAND == "uninstall" ]]; then
  echo "Uninstalling..."
  confirm_uninstall
  remove_solr_collections
  remove_registry_container
  remove_registry_image
  remove_docker_volumes
fi
