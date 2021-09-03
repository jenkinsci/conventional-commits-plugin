package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represent a Gradle project type (i.e with a build.gradle file).
 */
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

  /**
   * Write the new calculated version in the build.gradle file.
   * If no version property is found, do nothing.
   *
   * @param directory     The directory where find the build.gradle. <b>Mandatory</b>
   * @param nextVersion   The next version to write. <b>Mandatory</b>
   * @param processHelper Not used.
   * @throws IOException          If an error occurs when accessing to the build.gradle
   * @throws InterruptedException Not used.
   */
  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {
    Objects.requireNonNull(directory);
    Objects.requireNonNull(nextVersion);

    // Line to read
    String line;
    // Absolute path to the build.temp file
    String buildTempPath =
        String.format("%s%sbuild.temp", directory.getAbsolutePath(), File.separator);
    // Absolute path to the build.gradle file
    String buildPath =
        String.format("%s%sbuild.gradle", directory.getAbsolutePath(), File.separator);

    FileWriter fw = new FileWriter(buildTempPath);
    BufferedReader reader = Files.newBufferedReader(Paths.get(buildPath));

    while ((line = reader.readLine()) != null) {
      if (line.contains("version")) {
        fw.write(String.format("version = '%s'\n", nextVersion));
      } else {
        fw.write(String.format("%s\n", line));
      }
    }
    fw.close();

    // Replace build.gradle with updated version
    Files.move(Paths.get(buildTempPath), Paths.get(buildPath), StandardCopyOption.REPLACE_EXISTING);
  }
}
