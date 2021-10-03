# conventional-commits-plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/conventional-commits-plugin/job/main/badge/icon)](https://ci.jenkins.io/job/Plugins/job/conventional-commits-plugin/job/main/)
[![Contributors](https://img.shields.io/github/contributors/jenkinsci/conventional-commits-plugin.svg)](https://github.com/jenkinsci/conventional-commits-plugin/graphs/contributors)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/conventional-commits.svg)](https://plugins.jenkins.io/conventional-commits)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/conventional-commits-plugin.svg?label=changelog)](https://github.com/jenkinsci/conventional-commits-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/conventional-commits.svg?color=blue)](https://plugins.jenkins.io/conventional-commits)

## Introduction

This plugin can be used to determine the next release version based on previous tags and the commit messages used.
:warning: By default only [annotated tag](https://git-scm.com/book/en/v2/Git-Basics-Tagging) are supported, to support non annotated tag you must use an option to activate this feature (see below).:warning:
It calculates the version number based on the format of the commit message.
The commit message format used is [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/).

## Getting started

This plugin can be used in a pipeline in a stage or the environment block.  Some examples of this in use are:

### In the environment block

```
pipeline {
    agent any

    environment {
        NEXT_VERSION = nextVersion()
    }

    stages {
        stage('Hello') {
            steps {
                echo "next version = ${NEXT_VERSION}"
            }
        }
    }
}
```
### In a scripted Pipeline

```groovy
def NEXT_VERSION
node {
    stage('Get next version ...') {
      NEXT_VERSION=nextVersion()
      echo "Next version : $NEXT_VERSION"
    }
    stage ('Release') {
        sh "mvn release:prepare -DreleaseVersion=$NEXT_VERSION"
        sh 'mvn release:perform'
    }
}
```

## Using Optional Parameters

The plugin provides provision to use optional parameters for support of build metadata, pre-release information, settnig the start tag, etc.

### Build Metadata

`buildMetadata` an optional parameter can be added as follows:

```
pipeline {
    agent any
    environment {
        NEXT_VERSION = nextVersion(buildMetadata: "$env.BUILD_NUMBER")
    }
    stages {
        stage('Hello') {
            steps {
                echo "next version = ${NEXT_VERSION}"
            }
        }
    }
}
```
Assuming next version is `1.1.0`.
The pipeline will output :`next version = 1.1.0+001`

### Use with prerelease information
For a `1.0.0` existing version the following code :

```
pipeline {
    agent any

    environment {
        NEXT_VERSION = nextVersion(preRelease: 'alpha')
    }

    stages {
        stage('Hello') {
            steps {
                echo "next version = ${NEXT_VERSION}"
            }
        }
    }
}
```
Will display :`next version = 1.1.0-alpha`

#### Prerelease possible combinations
There are three options to manipulate the prerelease option :
- the name of the prerelease :arrow_right: `preRelease`
- keep the existing prerelease (default **false**) :arrow_right: `preservePrelease`
- increment the existing prerelease (default **false**) :arrow_right: `incrementPreRelease`

The table below resume the combined use of these options and the result:

| current version | Breaking change commit msg | Feature commit msg | Other or empty commit msg | prerelease | preservePreRelease | incrementPreRelease	| Output              |
| :---:           | :---:                      | :---:              | :---:                     | :---:      | :---:              | :---:                | :---:               |
| 0.1.0           | X                          | -                  | -                         | - 	     | -                  | -                    |	**1.0.0**         |
| 0.1.0           | -                          | X                  | -                         | - 	     | -                  | -                    |	**0.2.0**         |
| 0.1.0           | -                          | -                  | X                         | - 	     | -                  | -                    |	**0.1.1**         |
| 0.1.0           | X                          | -                  | -                         | alpha      | -                  | -                    |	**1.0.0-alpha**   |
| 0.1.0           | -                          | X                  | -                         | alpha      | -                  | -                    |	**0.2.0-alpha**   |
| 0.1.0           | -                          | -                  | X                         | alpha      | -                  | -                    |	**0.1.1-alpha**   |
| 1.0.0-alpha     | -                          | -                  | -                         | - 	     | -                  | -                    |	**1.0.0**         |
| 0.1.0-alpha     | -                          | -                  | -                         | - 	     | -                  | -                    |	**0.1.0**         |
| 0.1.1-alpha     | -                          | -                  | -                         | - 	     | -                  | -                    |	**0.1.1**         |
| 0.1.0-alpha     | X                          | -                  | -                         | - 	     | X                  | -                    |	**1.0.0-alpha**   |
| 0.1.0-alpha     | -                          | X                  | -                         | - 	     | X                  | -                    |	**0.2.0-alpha**   |
| 0.1.0-alpha     | -                          | -                  | X                         | - 	     | X                  | -                    |	**0.1.1-alpha**   |
| 0.1.0-alpha     | -                          | -                  | X                         | - 	     | X                  | X                    |	**0.1.1-alpha.1** |
| 0.1.0-alpha     | X                          | -                  | -                         | beta       | -                  | -                    |	**1.0.0-beta**    |
| 0.1.0-alpha     | -                          | X                  | -                         | beta       | -                  | -                    |	**0.2.0-beta**    |
| 0.1.0-alpha     | -                          | -                  | X                         | beta       | -                  | -                    |	**0.1.1-beta**    |

### Write next version in the configuration file (pom.xml, package.json)
The optional parameter `writeVersion` allow writing back to the file the next calculated version.

**:warning: For some configurations files, the CLI is needed (maven fo example). :warning:**

The supported configurations files :
 - pom.xml (Maven) : need the Maven CLI in the path,
 - package.json (NPM) : need the Npm CLI in the path,
 - chart.yaml (Helm),
 - build.gradle / gradle.properties (Gradle).

Example of use :
With a project with a package.json as follows :
```json
{
  "name": "conventional-commits-plugin-example-npm",
  "version": "1.0.0",
  "description": "Npm example project"
}
```
The following pipeline with a commit with a commit message like _feat: my cool feature_:
```groovy
pipeline {
    agent any
    environment {
        NEXT_VERSION = nextVersion(writeVersion: true)
    }
    stages {
        stage('Hello') {
            steps {
                echo "next version = ${NEXT_VERSION}"
            }
        }
    }
}
```
Will update the _package.json_ as follow :
```json
{
  "name": "conventional-commits-plugin-example-npm",
  "version": "1.1.0",
  "description": "Npm example project"
}
```

## Issues

Report issues and enhancements in the [Github issue tracker](https://github.com/jenkinsci/conventional-commits-plugin/issues).

## Contributing

We encourage community contributions for this project! For more information on how to contribute to this project, see [the contribution guidelines](./.github/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)
