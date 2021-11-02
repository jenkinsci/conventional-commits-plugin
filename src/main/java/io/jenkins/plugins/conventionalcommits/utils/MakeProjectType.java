package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

/** Represent a Make project type (i.e with a Makefile). */
public class MakeProjectType extends ProjectType {

  private static final String MAKEFILE_FILENAME = "Makefile";

  @Override
  public boolean check(File directory) {
    return new File(directory, MAKEFILE_FILENAME).exists();
  }

  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper) 
      throws IOException, InterruptedException {

    String filePath = directory.getAbsolutePath() + System.getProperty("file.separator")
        + MAKEFILE_FILENAME;
    File makeFile = new File(filePath);
    Scanner scanner = new Scanner(makeFile, StandardCharsets.UTF_8.name());
    String results = "";

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (line.toLowerCase().contains("version")) {
        String[] words = line.split("=");
        results = words[1].trim();
        break;
      }
    }
    scanner.close();
    return Version.valueOf(results);
  }

  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {
    Objects.requireNonNull(directory);
    Objects.requireNonNull(nextVersion);

    // Absolute path to the Makefile
    String buildTempPath = String.format("%s%sMakefile.temp", directory.getAbsolutePath(),
        File.separator);
    // Absolute path to the Makefile
    String buildPath = String.format("%s%sMakefile", directory.getAbsolutePath(), File.separator);

    //Version type to match
    String[] versionToMatch = new String[] {"version", "version:", "version :"};

    // Flag to know if a version tag is in the Makefile
    boolean isVersionFound =
            createNewUpdateFile(buildPath, buildTempPath, nextVersion, false, versionToMatch);

    if (!isVersionFound) {
      throw new IOException("Unable to get version in Makefile");
    }
  }
}
