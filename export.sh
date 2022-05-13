#!/bin/bash

# script for exporting docker images to tar files and compressing them
for img in $(docker-compose config | awk '{if ($1 == "image:") print $2;}'); do
  images="$images $img"
done

docker save  $images | pigz > services.tar.gz

