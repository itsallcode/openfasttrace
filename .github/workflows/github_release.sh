#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

base_dir="$( cd "$(dirname "$0")/../.." >/dev/null 2>&1 ; pwd -P )"
pom_file="$base_dir/parent/pom.xml"

# Read project version from pom file
project_version=$(grep "<revision>" "$pom_file" | sed --regexp-extended 's/<.{0,1}revision>//g' | xargs)
echo "Read project version '$project_version' from $pom_file"

# Calculate checksum
artifact="$base_dir/product/target/openfasttrace-${project_version}.jar"
echo "Calculate sha256sum for file '$artifact'"
file_dir="$(dirname "$artifact")"
file_name=$(basename "$artifact")
pushd "$file_dir"
checksum_file_name="${file_name}.sha256"
sha256sum "$file_name" > "$checksum_file_name"
checksum_file_path="$file_dir/$checksum_file_name"
popd

# Create GitHub release
changes_file="$base_dir/doc/changes/changes_${project_version}.md"
echo "Changes file: $changes_file"
title="Release $project_version"
notes=$(cat "$changes_file")
tag="$project_version"
release_url=$(gh release create --latest --draft --title "$title" --notes "$notes" --target main "$tag" "$artifact" "$checksum_file_path")
echo "Release URL: $release_url"
