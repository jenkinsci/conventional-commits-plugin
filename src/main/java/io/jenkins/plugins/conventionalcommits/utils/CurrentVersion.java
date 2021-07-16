package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.DefaultProcessHelper;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;

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
   * Return the next version of the version attribute.
   *
   * @param directory The project's directory.
   * @param latestTag The last tagged version.
   * @return The current version (based on Semver).
   * @throws IOException If an error occur reading files.
   * @throws InterruptedException If an error occurs while executing command using processHelper.
   */
  public Version getCurrentVersion(File directory, String latestTag)
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

    return currentVersion;
  }
}
