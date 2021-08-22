package io.jenkins.plugins.conventionalcommits.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Class to interact with a NPM project type. */
public class NpmProjectType extends ProjectType {
  // Name of the package.json file
  private static final String PACKAGE_JSON_NAME = "package.json";

  /**
   * Check if the project is a NPM project (i.e a package.json is found).
   *
   * @param directory The directory of the project.
   * @return True if a package.json is found.
   */
  @Override
  public boolean check(File directory) {
    return new File(directory, PACKAGE_JSON_NAME).exists();
  }

  /**
   * Return the next version of the version attribute.
   *
   * @param directory The project's directory.
   * @param processHelper Not used.
   * @return The next calculated version (based on Semver).
   * @throws IOException If an error occur reading files.
   */
  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper) throws IOException {
    Objects.requireNonNull(directory);

    ObjectMapper mapper = new ObjectMapper();

    // Convert package.json to a Map
    Map<?, ?> map =
        mapper.readValue(
            Paths.get(directory.getPath() + File.separator + PACKAGE_JSON_NAME).toFile(),
            Map.class);

    return Version.valueOf((String) map.get("version"));
  }

  /**
   * Write back to the <code>package.json</code> file the next version.<br>
   * Use the npm command <code>version</code>, see : https://docs.npmjs.com/cli/v6/commands/npm-version.
   *
   * @param directory The directory where write the file.
   * @param nextVersion The next version to use.
   * @param processHelper The helper to run the command.
   * @throws IOException If errors occurs when write the file
   * @throws InterruptedException It errors occurs with the npm command.
   */
  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {
    List<String> command =
        Arrays.asList("npm", "version", nextVersion.toString());
    processHelper.runProcessBuilder(directory, command);
  }
}
