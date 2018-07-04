
# Release

* This project primarily uses bnd to generate and publish releases.
* bnd [is configured](cnf/build.bnd) to [release to a Maven repository](https://bnd.bndtools.org/plugins/maven.html), which is [hosted on Bintray](https://bintray.com/coastalhacking/emf-osgi).
* TODO: Bintray is configured to push releases to JCentral and snapshots to [OJO](https://www.jfrog.com/confluence/display/RTF/Deploying+Snapshots+to+oss.jfrog.org).
* Version numbers are based on [semantic versioning](https://semver.org/), using the OSGi Alliance [flavor](https://enroute.osgi.org/FAQ/210-semantic_versioning.html) of semantic versioning for released bundles.
* Releases are generated and deployed via [Travis CI](https://docs.travis-ci.com/user/deployment/script/) when git tags are pushed, where the tag equals the version number minus the OSGi qualifier.
* TODO: Snapshots are generated and deployed via pushes to the `develop` branch, also using Travis CI.

## bnd and Maven

* bnd uses a [connection settings](https://bnd.bndtools.org/instructions/connection-settings) [file](cnf/connection-settings.xml) to store the credentials used to authenticate to Bintray. This file is configured in the [workspace-global build configuration](cnf/build.bnd).
* The connection settings file refers to an [environmental variable](https://bnd.bndtools.org/macros/env.html) for the [Bintray API key](https://www.jfrog.com/confluence/display/RTF/Updating+Your+Profile#UpdatingYourProfile-APIKey), which is [defined and encrypted](https://docs.travis-ci.com/user/environment-variables/#Defining-encrypted-variables-in-.travis.yml) by Travis CI.
* The workspace-global build configuration also include a `-snapshot` [directive](https://bnd.bndtools.org/instructions/snapshot.html). When uncommented for a release, this uses an OSGi-preferred timestamp qualifier.
* Bundles to be released include a `-pom` [directive](https://bnd.bndtools.org/instructions/pom), which creates the Maven POM for the bundle. Also, these bundles include `SNAPSHOT` in their version numbers at all times. The aforementioned `-snapshot` directive controls this value.

## Releasing

This project uses [this git branching model](https://nvie.com/posts/a-successful-git-branching-model/?).

* Develop on `develop`.
* Create a release branch. Modify [cnf/build.bnd](cnf/build.bnd) to uncomment the `-snapshot: ${tstamp}` line.
* Merge into `master`.
* Tag `master` commit with the appropriate semantic version.
* Delete the release branch.
  
