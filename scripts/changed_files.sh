#!/usr/bin/env bash
set -euo pipefail

BASE_SHA="${1}"
HEAD_SHA="${2}"

git diff --name-only "$BASE_SHA" "$HEAD_SHA" \
  | grep -E '\.(java|xml|yml|yaml|md)$' \
  > changed_files.txt || true

echo "Changed files:"
cat changed_files.txt || true