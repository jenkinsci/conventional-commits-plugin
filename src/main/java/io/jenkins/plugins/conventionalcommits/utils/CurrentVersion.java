package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.DefaultProcessHelper;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/** This class focus on getting the current version (latest release version) of a project. */
public class CurrentVersion {

  private ProcessHelper processHelper;

  public void setProcessHelper(ProcessHelper processHelper) {
    this.processHelper = processHelper;
  }

  private Version getCurrentVersionTag(String latestTag) {
    return Version.valueOf(latestTag.isEmpty() ? "0.0.0" : latestTag);
  }

  /**
   * Compares read version with .
   *
   * @param currentVersion Project's current version read from the configuration file.
   * @param latestTag The last tagged version.
   * @param logger Jenkins logger.
   * @return The latest version as the new current version (based on Semver).
   */
  private Version checkCurrentVersion(
      Version currentVersion, String latestTag, PrintStream logger) {

    Version tagVersion = Version.valueOf(latestTag);

    if (tagVersion.greaterThan(currentVersion)) {
      String message =
          "[WARNING]: Version mismatch found between the configuration file and the latest tag. "
              + "Using the later version: "
              + "%1$s as the base version to calculate the next version.%n";
      logger.format(message, latestTag);
      logger.flush();
      currentVersion = tagVersion;
    }

    return currentVersion;
  }

  /**
   * Return the next version of the version attribute.
   *
   * @param directory The project's directory.
   * @param latestTag The last tagged version.
   * @return The current version (based on Semver).
   * @throws IOException If an error occur reading files.
   * @throws InterruptedException If an error occurs while executing command using processHelper.
   */
  public Version getCurrentVersion(File directory, String latestTag, PrintStream logger)
      throws IOException, InterruptedException {

    Version currentVersion;
    ProjectType projectType = ProjectTypeFactory.getProjectType(directory);

    if (projectType != null) {
      if (processHelper == null) {
        processHelper = new DefaultProcessHelper();
      }
      currentVersion = projectType.getCurrentVersion(directory, processHelper);
    } else {
      currentVersion = getCurrentVersionTag(latestTag);
    }

    if (!latestTag.isEmpty()) {
      currentVersion = checkCurrentVersion(currentVersion, latestTag, logger);
    }

    return currentVersion;
  }
}
