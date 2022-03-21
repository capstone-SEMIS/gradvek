#!/bin/bash

trap "kill -2 0" EXIT

# Start the back end

# https://stackoverflow.com/a/2013589
NEO4JHOST="${NEO4JHOST:-$(for i in $(echo "$(cat /proc/net/route | head -2 | tail -1 | awk '{print $3}')" | sed -E 's/(..)(..)(..)(..)/\4 \3 \2 \1/' ) ; do printf "%d." $((16#$i)); done | sed 's/.$//')}"

# https://unix.stackexchange.com/a/561524
export NEO4JURL=bolt://$NEO4JHOST:7687

java -jar /app.jar &

# Start the front end
export PATH="/node:$PATH"
cd frontend
npm run start &

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?
