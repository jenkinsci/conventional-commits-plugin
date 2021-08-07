package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;

abstract class ProjectType {

  public abstract boolean check(File directory);

  public abstract Version getCurrentVersion(File directory, ProcessHelper processHelper)
      throws IOException, InterruptedException;

  public abstract void writeVersion(
      File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException;
}
