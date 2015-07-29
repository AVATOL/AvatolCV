#!/bin/bash

THIS_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo ${THIS_DIR}/hello_via_runtime.sh
${THIS_DIR}/hello_via_runtime.sh Jed
