package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/** Represent a Gradle project type (i.e with a build.gradle file). */
public class GradleProjectType extends ProjectType {

  public boolean check(File directory) {
    return new File(directory, "build.gradle").exists();
  }

  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper)
      throws IOException, InterruptedException {

    String os = System.getProperty("os.name");
    String commandName = "gradle";

    if (os.contains("Windows")) {
      commandName += ".bat";
    }

    List<String> command = Arrays.asList(commandName, "-q", "properties");
    String results = processHelper.runProcessBuilder(directory, command);

    String version = "undefined";

    String[] resultLines = results.split("[\\r\\n]+");
    for (String line : resultLines) {
      if (line.startsWith("version:")) {
        String[] words = line.split(" ");
        version = words[1];
        break;
      }
    }
    return Version.valueOf(version);
  }

  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {}
}
