# gradvek
GRaph of ADVerse Event Knowledge

## Build process

GitHub Actions builds the artifacts needed for deployment.
There is one workflow divided into three jobs:
* test
  * The first job uses the Maven `test` goal to run regression tests.
* build
  * The second job uses the Maven `install` goal to create a production build.  The build artifacts are saved for use in the next job.
* deploy
  * The build artifacts from the previous job are restored.  Then the main project Dockerfile is used to generate an image, which is pushed to Docker Hub.

Prior to building and deploying in production, the [GitHub context](https://docs.github.com/en/actions/learn-github-actions/contexts#github-context) is checked to ensure that the `master` branch is being used.
Otherwise, these jobs are skipped.

Separating the build and deploy jobs has two main advantages.
First, it's clear at a glance if a failure occurred during Maven or during Docker Hub operations.
Second, the build artifacts are available for later inspection if desired for troubleshooting.
