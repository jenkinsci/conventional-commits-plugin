package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/** Represent a Maven project type (i.e with a pom.xml file). */
public class MavenProjectType extends ProjectType {

  public boolean check(File directory) {
    return new File(directory, "pom.xml").exists();
  }

  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper)
      throws IOException, InterruptedException {

    String os = System.getProperty("os.name");
    String commandName = "mvn";

    if (os.contains("Windows")) {
      commandName += ".cmd";
    }

    List<String> command =
        Arrays.asList(
            commandName, "help:evaluate", "-Dexpression=project.version", "-q", "-DforceStdout");
    String results = processHelper.runProcessBuilder(directory, command);

    return Version.valueOf(results);
  }

  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {

    String os = System.getProperty("os.name");
    String commandName = "mvn";

    if (os.contains("Windows")) {
      commandName += ".cmd";
    }

    List<String> command =
        Arrays.asList(
            commandName, "versions:set", "-DnewVersion=" + nextVersion.toString());
    processHelper.runProcessBuilder(directory, command);
  }
}
