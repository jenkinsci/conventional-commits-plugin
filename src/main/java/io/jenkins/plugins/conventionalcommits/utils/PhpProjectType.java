package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import io.jenkins.plugins.conventionalcommits.process.ProcessUtil;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import org.json.JSONObject;

/**
 * Represents a PHP project type.
 * Projects managed by Composer with the composer.json file are supported.
 */
public class PhpProjectType extends ProjectType {

  private boolean checkComposerJson(File directory) {
    return new File(directory, "composer.json").exists();
  }

  /**
   * Checks if the composer.json config file exists at the root directory.
   *
   * @param directory The directory in which the composer.json file is searched for.
   * @return A boolean true / false is returned.
   */
  @Override
  public boolean check(File directory) {
    return checkComposerJson(directory);
  }

  /**
   * Gets the current version of the composer.json file if explicitly defined.
   * If version is not stored in composer.json then resort to using the latest Git tag.
   *
   * @param directory The directory in which the composer.json file is ideally found.
   * @param processHelper The helper to scan the json file for version property.
   * @return The current version of the PHP package concerned.
   * @throws IOException If an error is thrown while file is read.
   * @throws InterruptedException If an error is thrown due to interruption.
   */
  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper)
      throws IOException, InterruptedException {

    String result = "";
    String composerJson;

    // Check if a version property has been set in composer.json
    if (checkComposerJson(directory)) {
      String filePath = directory.getAbsolutePath() + File.separator + "composer.json";
      try (Scanner scanner = new Scanner(new File(filePath), "UTF-8")) {
        composerJson = scanner.useDelimiter("\\A").next();
      }
      JSONObject composerJsonObject = new JSONObject(composerJson);
      if (composerJsonObject.has("version")) {
        result = (String) composerJsonObject.get("version");
      } else {
        try {
          result = ProcessUtil.execute(directory, "git", "describe", "--abbrev=0", "--tags").trim();
        } catch (IOException exp) {
          String message = "No Git tags found";
          LogUtils logger = new LogUtils();
          logger.log(Level.INFO, Level.INFO, Level.FINE, Level.FINE, true, message);
        }
      }
    }
    return Version.valueOf(result.trim());
  }

  /**
   * Checks if there is a composer.json file in the directory.
   * If that's the case then the file will remain the same.
   * If no composer.json file is found no error is thrown.
   *
   * @param directory The directory to which the file is written.
   * @param nexVersion The next version to use.
   * @param processHelper The helper to run the command (not used here).
   * @throws IOException If an error is thrown while file is read.
   * @throws InterruptedException If an error is thrown due to interruption.
   */
  @Override
  public void writeVersion(File directory, Version nexVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {
    if (checkComposerJson(directory)) {
      String message = "The composer.json file already exists";
      LogUtils logger = new LogUtils();
      logger.log(Level.INFO, Level.INFO, Level.FINE, Level.FINE, true, message);
    }
  }
}
