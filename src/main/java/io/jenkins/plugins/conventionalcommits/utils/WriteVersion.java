package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.DefaultProcessHelper;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/** Class to write back the calculated next semantic version into the config file of a project. */
public class WriteVersion {
  private ProcessHelper processHelper;

  public void setProcessHelper(ProcessHelper processHelper) {
    this.processHelper = processHelper;
  }

  /**
   * Writes next semantic version in a file.
   *
   * @param nextVersion The project's calculated next semantic version.
   * @param directory Directory of the project
   * @throws IOException If an error occurs while reading/writing files.
   * @throws InterruptedException If an error occurs while executing command using processHelper.
   */
  public String write(Version nextVersion, File directory)
      throws IOException, InterruptedException {

    ProjectType projectType = ProjectTypeFactory.getProjectType(directory);
    String message = "The next version was written to the configuration file.";

    if (projectType != null) {
      if (processHelper == null) {
        processHelper = new DefaultProcessHelper();
      }
      projectType.writeVersion(directory, nextVersion, processHelper);
    } else {
      message = "Could not write the next version to the configuration file.";
      LogUtils logger = new LogUtils();
      logger.log(Level.INFO, Level.INFO, Level.FINE, Level.FINE, true, message);
    }
    return message;
  }
}
