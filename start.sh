#!/bin/bash

# Start the back end
java -jar /app.jar &

# Start the front end
export PATH="/node:$PATH"
cd frontend
npm run start &

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?
