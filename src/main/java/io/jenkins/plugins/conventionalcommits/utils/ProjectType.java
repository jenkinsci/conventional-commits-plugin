package io.jenkins.plugins.conventionalcommits.utils;

import java.io.File;
import java.io.IOException;

import com.github.zafarkhaja.semver.Version;

abstract class ProjectType {

    public abstract boolean check(File directory);
    public abstract Version getCurrentVersion(File directory) throws IOException, InterruptedException;

}
