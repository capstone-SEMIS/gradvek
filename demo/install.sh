curl https://raw.githubusercontent.com/capstone-SEMIS/gradvek/master/demo/docker-compose.yml > docker-compose.yml
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v "$PWD:$PWD" -w="$PWD" docker/compose pull
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v "$PWD:$PWD" -w="$PWD" docker/compose up --detach --remove-orphans
docker image prune -f
