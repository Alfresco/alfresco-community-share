#!/usr/bin/env bash
set -vex

SIZE_IN_GB="${1}"

sudo free -m -t
sudo swapoff -a
sudo fallocate -l ${SIZE_IN_GB}G /swapfile
sudo dd if=/dev/zero of=/swapfile bs=1024 count=$((${SIZE_IN_GB} * 1024 * 1024))
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo free -m -t
sudo swapon --show
free -h
