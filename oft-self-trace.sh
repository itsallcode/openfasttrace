#!/bin/sh

script_path=$(dirname "$(readlink -f "$0")")
base_dir="$script_path"
oft_script="$base_dir/oft"

$oft_script trace "$base_dir/doc/spec" \
    "$base_dir/importer/markdown/src" \
    "$base_dir/importer/specobject/src" \
    "$base_dir/importer/zip/src" \
    "$base_dir/importer/tag/src" \
    "$base_dir/core/src" \
    "$base_dir/reporter/plaintext/src" \
    "$base_dir/reporter/html/src" \
    "$base_dir/reporter/aspec/src" \
    "$base_dir/product/target/test-classes/example/src" \
    "$base_dir/product/src" \
    "$base_dir/product/src/test/resources/example/src" \
    "$base_dir/api/src" \
    "$base_dir/exporter/specobject/src" \
    "$base_dir/exporter/common/src" \
    "$base_dir/testutil/src"
