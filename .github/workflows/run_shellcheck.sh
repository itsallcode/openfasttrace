#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

base_dir="$( cd "$(dirname "$0")/../.." >/dev/null 2>&1 ; pwd -P )"

shellcheck --enable=all --severity=warning --check-sourced --color=auto \
            "$base_dir/.github/workflows/run_shellcheck.sh" \
            "$base_dir/.github/workflows/github_release.sh" \
            "$base_dir/oft" \
            "$base_dir/oft-self-trace.sh"

