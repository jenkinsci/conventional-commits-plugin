package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.NotImplementedException;

/**
 * Represents a Go project type.
 * Projects with the go.mod file are supported.
 */
public class GoProjectType extends ProjectType {

  private boolean checkGoMod(File directory) {
    return new File(directory, "go.mod").exists();
  }

  /**
   * Checks if the go.mod config file exists at the root directory.
   *
   * @param directory The directory in which the go.mod file is searched for.
   * @return A boolean true / false is returned.
   */
  @Override
  public boolean check(File directory) {
    return checkGoMod(directory);
  }

  /**
   * Gets the current version of the Go module as indicated by release tag on GitHub.
   *
   * @param directory The directory in which the go.mod file is ideally found.
   * @param processHelper The helper to run the "go list" command.
   * @return The current version of the Go module concerned.
   * @throws IOException If an error is thrown while file is read.
   * @throws InterruptedException If an error is thrown due to interruption.
   */
  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper)
      throws IOException, InterruptedException {

    String commandName = "go";

    String result = "";

    // Compiles the regex to create desired version pattern for finding the current version
    String versionRegex = "^v[0-9]+.[0-9]+.[0-9]+(-((\\balpha\\b)|(\\bbeta\\b)).[0-9])?$";
    Pattern pattern = Pattern.compile(versionRegex);

    if (checkGoMod(directory)) {
      List<String> command = Arrays.asList(commandName, "list", "-m", "-versions");
      String longResult = processHelper.runProcessBuilder(directory, command);
      Matcher match = pattern.matcher(longResult);
      // The last version should be the most current version
      // The result variable is overwritten by the last version in the while loop
      while (match.find()) {
        result = match.group();
      }
    }
    result = result.substring(1);
    return Version.valueOf(result.trim());
  }

  /**
   * Check if there is a Go config file (go.mod) file in the directory.
   * If that's the case change the file will remain the same.
   * This is because for Go modules the release version is from GitHub repository tags.
   *
   * @param directory The directory to which the file is written.
   * @param nextVersion The next version to use.
   * @param processHelper The helper to run the command (not used here).
   * @throws NotImplementedException If not implemented means the Go project is not supported.
   * @throws IOException If an error is thrown while file is read.
   * @throws InterruptedException If an error is thrown due to interruption.
   */
  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException, NotImplementedException {
    if (checkGoMod(directory)) {
      System.out.println("The go.mod file already exists");
    } else {
      throw new NotImplementedException("Project not supported");
    }
  }
}
