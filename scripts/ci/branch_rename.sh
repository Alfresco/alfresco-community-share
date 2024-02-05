#!/usr/bin/env bash

echo "====="

destination=$(echo "$2" | sed "s/remotes\/origin\/"$4"/$3/")
source=$(echo "$2" | sed "s/remotes\///")
source_reduced=$(echo "$2" | sed "s/remotes\/origin\///")

if [ $# -ne 5 ]; then
    echo "Rationale : Rename a branch on the server without checking it out."
    echo "Usage     : $(basename $0) <remote> <old name> <new name>"
    echo "Params   : $(basename $0) $1 $source ($source_reduced) $destination"
    echo "COMMAND   : git push $1 $source:refs/heads/$destination :$source_reduced"

fi

git push $1 "$source":refs/heads/$destination :$source_reduced