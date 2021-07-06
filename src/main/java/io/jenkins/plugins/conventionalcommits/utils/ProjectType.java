package io.jenkins.plugins.conventionalcommits.utils;

import java.io.File;
import java.io.IOException;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;

abstract class ProjectType {

    public abstract boolean check(File directory);
    public abstract Version getCurrentVersion(File directory, ProcessHelper processHelper) throws IOException, InterruptedException;

}
