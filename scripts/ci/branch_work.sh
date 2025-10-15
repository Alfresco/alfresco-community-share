#!/usr/bin/env bash

#Script aimed to extract all the support/HF and support/SP branches and convert them following repo convention of release/X.n.a
#The script extracts the list, send it to branch_rename (remotely) , names manipulation, branching the new one and deleting the original.

for b in $( git branch -a | grep "support\/HF" ) ; do
   source branch_rename.sh "origin" "$b" "release" "support\/HF"
done


done#for b in $( git branch -a | grep "support\/SP" ) ; do
   source branch_rename.sh "origin" "$b" "release" "support\/SP"
done