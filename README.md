# gradvek

GRaph of ADVerse Event Knowledge

## Build process

GitHub Actions builds the artifacts needed for deployment.
There is one workflow divided into four jobs:
* test
  * The first job uses the Maven `test` goal to run regression tests.
* build
  * The second job uses the Maven `install` goal to create a production build.  The build artifacts are saved for use in the next job.
* publish
  * The build artifacts from the previous job are restored.  Then the main project Dockerfile is used to generate an image, which is pushed to Docker Hub.
* deploy
  * A Compute Engine instance on the Google Cloud Platform runs the script in `demo/install.sh`, which in turn retrieves a fresh copy of the updated image published in the previous step.  A container based on this image is then run alongside a Neo4j container.  With the application and database running, the Compute Engine instance hosts the website.  NOTE: This instance is intended as a demo controlled by the dev team, not as a production instance controlled by the customer.

Separating the build and publish jobs has two main advantages.  First, it's clear at a glance if a failure occurred during Maven or during Docker Hub operations.  Second, the build artifacts are available for later inspection if desired for troubleshooting.

In addition, before continuing past the test phase, the [GitHub context](https://docs.github.com/en/actions/learn-github-actions/contexts#github-context) is checked to ensure that the `master` branch is being used.  If not, the remainder of the jobs are skipped by default.  The default may be overridden by setting the workflow input "Deploy even if this is not on the master branch" to TRUE on manual invocation.  This allows testing some CI/CD changes before merging to `master`, using the demo instance like a staging environment.

## Running locally

To run the `gradvek` application locally, run `docker-compose up` from the project's root directory. This will spin up two docker containers:

1. A Neo4j container, which will run the database used by this application.
2. The application container, [gradvek/app](https://hub.docker.com/r/gradvek/app), the latest version of this application that has been published to DockerHub.

Several ports provide access to the running software.

| Port | Purpose                                                                                |
|------|----------------------------------------------------------------------------------------|
| 3000 | The frontend of the application.  Browse to http://localhost:3000 to see the web site. |
| 8080 | The backend of the application.  Serves requests from the frontend.                    |
| 7474 | The Neo4j web interface.  Allows inspection of the database through Cypher queries.    |
| 7687 | The Neo4j bolt interface.  Serves requests from the backend.                           |

## Running against Neo4j Desktop

Instead of running the Neo4j database in a Docker container, it's also possible to use a Neo4j database running as part of [Neo4j Desktop](https://neo4j.com/download/).  Neo4j Desktop provides a Cypher development environment similar to the browser interface of the Docker container.  However, Neo4j Desktop also provides [Bloom](https://neo4j.com/product/bloom/), a more powerful visualization tool that also allows interacting with the database without using Cypher.

To run the application using a Neo4j Desktop database, follow the steps below.  It assumes you have already installed Neo4j Desktop.

* Start Neo4j Desktop.
* Create a new project if you haven't set one up before.  If you have, skip ahead to start the DBMS.
  * Add a local DBMS to the project.
  * Set the password to `gradvek`.
  * In the DBMS settings (click the ... while hovering over the DBMS):
    * Search for the string "non-local connections".
    * Uncomment the next line that reads `dbms.default_listen_address=0.0.0.0`.
    * Apply the change and close.
* Start the DBMS.
* Get the latest Docker image using `docker pull gradvek/app`.
* Run the container using `docker run -p 3000:3000 -p 8080:8080 gradvek/app -d`.
* Open your browser to localhost:3000.
* Inspect the state of the DB with Browser or Bloom.
* When you're done:
  * Find the running container id with `docker ps --filter status=running -q`.
  * Stop the container with `docker stop <container_id>`.
  * Or if you're using bash, simply run `docker stop $(docker ps --filter status=running -q)`.

If you want to make local changes to the application, you will need to create your own local development environment instead of using the image hosted on DockerHub. See  the instructions in `springdb/README.md`.
