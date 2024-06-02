#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

base_dir="$( cd "$(dirname "$0")/../.." >/dev/null 2>&1 ; pwd -P )"
readonly base_dir
readonly pom_file="$base_dir/parent/pom.xml"

# Read project version from pom file
project_version=$(grep "<revision>" "$pom_file" | sed --regexp-extended 's/<.{0,1}revision>//g' | xargs)
readonly project_version
echo "Read project version '$project_version' from $pom_file"

# Calculate checksum
readonly artifact="$base_dir/product/target/openfasttrace-${project_version}.jar"
echo "Calculate sha256sum for file '$artifact'"
file_dir="$(dirname "$artifact")"
readonly file_dir
file_name=$(basename "$artifact")
readonly file_name
pushd "$file_dir"
readonly checksum_file_name="${file_name}.sha256"
sha256sum "$file_name" > "$checksum_file_name"
readonly checksum_file_path="$file_dir/$checksum_file_name"
popd

# Create GitHub release
readonly changes_file="$base_dir/doc/changes/changes_${project_version}.md"
echo "Changes file: $changes_file"
readonly title="Release $project_version"
notes=$(cat "$changes_file")
readonly notes
readonly tag="$project_version"
release_url=$(gh release create --latest --title "$title" --notes "$notes" --target main "$tag" "$artifact" "$checksum_file_path")
readonly release_url
echo "Release URL: $release_url"
