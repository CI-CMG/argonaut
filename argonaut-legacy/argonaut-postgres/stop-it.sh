#!/usr/bin/env bash

set -ex

docker container stop postgis-1
docker container rm postgis-1