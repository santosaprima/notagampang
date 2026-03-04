#!/bin/bash

# Get staged Kotlin files
STAGED_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep -E '\.(kt|kts)$')

if [ -z "$STAGED_FILES" ]; then
    echo "No staged Kotlin files to lint."
    exit 0
fi

echo "Running ktlintFormat on staged files..."

# Run ktlintFormat
./gradlew ktlintFormat

# Re-stage files that might have been modified by ktlintFormat
# We only want to re-stage the files that were already staged
for FILE in $STAGED_FILES; do
    if [ -f "$FILE" ]; then
        git add "$FILE"
    fi
done

echo "Running ktlintCheck..."

# Run ktlintCheck to ensure everything is correct
./gradlew ktlintCheck

# Capture the exit code of ktlintCheck
RESULT=$?

if [ $RESULT -ne 0 ]; then
    echo "ktlintCheck failed. Please fix the issues before committing."
    exit $RESULT
fi

echo "ktlint check passed."
exit 0
