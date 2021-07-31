# conventional-commits-plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/conventional-commits-plugin/job/main/badge/icon)](https://ci.jenkins.io/job/Plugins/job/conventional-commits-plugin/job/main/)
[![Contributors](https://img.shields.io/github/contributors/jenkinsci/conventional-commits-plugin.svg)](https://github.com/jenkinsci/conventional-commits-plugin/graphs/contributors)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/conventional-commits.svg)](https://plugins.jenkins.io/conventional-commits)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/conventional-commits-plugin.svg?label=changelog)](https://github.com/jenkinsci/conventional-commits-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/conventional-commits.svg?color=blue)](https://plugins.jenkins.io/conventional-commits)

## Introduction

This plugin can be used to determine the next release version based on previous tags and the commit messages used.  
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
### Use with prerelease information
For a `1.0.0` existing version the following code :

```
pipeline {
    agent any

    environment {
        NEXT_VERSION = nextVersion(prerelease: 'alpha')
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

## Issues

Report issues and enhancements in the [Github issue tracker](https://github.com/jenkinsci/conventional-commits/issues).

## Contributing

TODO review the default [CONTRIBUTING](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md) file and make sure it is appropriate for your plugin, if not then add your own one adapted from the base file

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)
