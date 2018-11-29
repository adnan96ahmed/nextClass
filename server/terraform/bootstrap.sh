#!/bin/bash
apt update
apt install docker.io docker-compose -y
docker swarm init
ssh-keygen -f "$HOME/.ssh/id_ed25519" -t ed25519 -N ''
