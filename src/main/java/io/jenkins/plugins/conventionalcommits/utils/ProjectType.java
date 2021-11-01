package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

abstract class ProjectType {

  public abstract boolean check(File directory);

  public abstract Version getCurrentVersion(File directory, ProcessHelper processHelper)
      throws IOException, InterruptedException;

  public abstract void writeVersion(
      File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException;

  /**
   * Write an updated temporary file with next version
   * then replace the project config file with it.
   *
   * @param buildPath Path of the file updated
   * @param buildTempPath Path of a temporary file to replace buildPath
   * @param nextVersion Version to update config file
   * @param isIndented Can the config file have indentation
   * @param matchingWords Tab of word to match the version
   * @return if a version tag was found
   * @throws IOException If errors occurs when write the file
   */
  public boolean createNewUpdateFile(String buildPath, String buildTempPath, Version nextVersion,
                                  boolean isIndented, String[] matchingWords)
          throws IOException {
    // Line to read
    String line;
    // Flag to know if a version tag is in the config file
    boolean isVersionTag = false;

    String currentVersion;

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(buildPath))) {
      try (BufferedWriter fw = Files.newBufferedWriter(Paths.get(buildTempPath),
              StandardCharsets.UTF_8)) {
        while ((line = reader.readLine()) != null) {
          if (!isVersionTag && Arrays.stream(matchingWords)
                  .anyMatch(isIndented ? line.toLowerCase()::contains :
                          line.toLowerCase()::startsWith)) {
            String[] words = line.split("=");
            currentVersion = words[1].trim();
            if (currentVersion.contains("\"")) {
              fw.write(String.format("%s%n", line.replace(currentVersion, "\""
                      + nextVersion.toString() + "\"")));
            } else {
              fw.write(String.format("%s%n", line.replace(currentVersion, nextVersion.toString())));
            }
            isVersionTag = true;
          } else {
            fw.write(String.format("%s%n", line));
          }
        }
      }

      if (isVersionTag) {
        // Replace config with updated version
        Files.move(Paths.get(buildTempPath), Paths.get(buildPath),
                StandardCopyOption.REPLACE_EXISTING);
      } else {
        Files.deleteIfExists(Paths.get(buildPath));
      }
    }
    return isVersionTag;
  }
}
