#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

base_dir="$( cd "$(dirname "$0")/../.." >/dev/null 2>&1 ; pwd -P )"
readonly base_dir
readonly pom_file="$base_dir/parent/pom.xml"

# Read project version from pom file
project_version=$(grep "<revision>" "$pom_file" | sed --regexp-extended 's/\s*<revision>(.*)<\/revision>\s*/\1/g')
readonly project_version
echo "Read project version '$project_version' from $pom_file"

readonly artifact_path="$base_dir/product/target/openfasttrace-${project_version}.jar"
readonly sbom_path="$base_dir/product/target/site/org.itsallcode.openfasttrace_openfasttrace-${project_version}.spdx3.json"

calculate_checksum() {
    local file_path="$1"
    local checksum_file_path="${file_path}.sha256"
    readonly file_path checksum_file_path
    echo "Calculating SHA-256 checksum for '$file_path'"
    (cd "$(dirname "$file_path")" && sha256sum "$(basename "$file_path")") > "$checksum_file_path"
}

calculate_checksum "$artifact_path"
readonly artifact_checksum_path="${artifact_path}.sha256"
calculate_checksum "$sbom_path"
readonly sbom_checksum_path="${sbom_path}.sha256"

shopt -s nullglob
native_release_assets=()
package_native_binary() {
    local native_binary="$1"
    local temporary_directory
    local package_path
    temporary_directory=$(mktemp -d)
    readonly temporary_directory
    cp "$native_binary" "$temporary_directory/oft"

    case "${native_binary##*/}" in
        openfasttrace-windows*)
            package_path="${native_binary%.exe}.zip"
            (cd "$temporary_directory" && zip --quiet "$package_path" oft)
            ;;
        openfasttrace-linux* | openfasttrace-macos*)
            package_path="${native_binary}.tar.gz"
            tar --create --gzip --file "$package_path" --directory "$temporary_directory" oft
            ;;
        *)
            rm -rf "$temporary_directory"
            return 1
            ;;
    esac

    rm -rf "$temporary_directory"
    calculate_checksum "$package_path"
    native_release_assets+=("$package_path" "${package_path}.sha256")
}

for native_binary in "$base_dir"/native-image/target/openfasttrace-*; do
    [[ ${native_binary##*/} =~ ^openfasttrace-(linux|windows|macos)([-._].*)?$ ]] || continue
    package_native_binary "$native_binary"
done

# Create GitHub release
readonly changes_file="$base_dir/doc/changes/changes_${project_version}.md"
notes=$(cat "$changes_file")
readonly notes

readonly title="Release $project_version"
readonly tag="$project_version"
echo
echo "Release Information"
echo "-------------------"
echo "Git tag          : $tag"
echo "Title            : $title"
echo "Changes file     : $changes_file"
echo "Artifact file    : $artifact_path"
echo "Artifact checksum: $artifact_checksum_path"
echo "SBOM file        : $sbom_path"
echo "SBOM checksum    : $sbom_checksum_path"
printf 'Platform binary packages: %s\n' "${native_release_assets[@]}"

release_url=$(gh release create --latest --title "$title" --notes "$notes" --target main "$tag" \
    "$artifact_path" "$artifact_checksum_path" "$sbom_path" "$sbom_checksum_path" \
    "${native_release_assets[@]}")
readonly release_url
echo "Release URL: $release_url"
