#!/usr/bin/env bash

set -ex

mvn docker:start flyway:migrate -Pit