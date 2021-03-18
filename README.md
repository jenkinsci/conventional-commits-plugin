# conventional-commits-plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/conventional-commits-plugin-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/conventional-commits-plugin-plugin/job/master/)
[![Contributors](https://img.shields.io/github/contributors/jenkinsci/conventional-commits-plugin-plugin.svg)](https://github.com/jenkinsci/conventional-commits-plugin-plugin/graphs/contributors)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/conventional-commits-plugin.svg)](https://plugins.jenkins.io/conventional-commits-plugin)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/conventional-commits-plugin-plugin.svg?label=changelog)](https://github.com/jenkinsci/conventional-commits-plugin-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/conventional-commits-plugin.svg?color=blue)](https://plugins.jenkins.io/conventional-commits-plugin)

## Introduction

This plugin can be used to determine the next release version based on previous tags and the commit messages used.  It calculates the version number based on the
format of the commit message.  The commit message format used is [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/).

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

## Issues

Report issues and enhancements in the [Github issue tracker](https://github.com/jenkinsci/conventional-commits/issues).

## Contributing

TODO review the default [CONTRIBUTING](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md) file and make sure it is appropriate for your plugin, if not then add your own one adapted from the base file

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

