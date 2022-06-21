#!/bin/bash

cloud="cloud_data"
branch="$(git branch --show-current)"

stash="$(git stash)"
git checkout cloud_data
git pull

cp pid_data/piddatabase.db cloud_data/.
cp pid_data/config.json cloud_data/.

git add cloud_data/.
date="$(date +"%Y/%m/%d")"
git commit -m "cloud_data: ${date} data update"
git push

git checkout "${branch}"
if [ "${stash}" != "No local changes to save" ]; then
    git stash pop
fi

