package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MakeProjectType extends ProjectType {

  private static final String MAKEFILE_FILENAME = "Makefile";

  @Override
  public boolean check(File directory) {
    return new File(directory, MAKEFILE_FILENAME).exists();
  }

  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper)
      throws IOException, InterruptedException {

    String filePath =
        directory.getAbsolutePath() + System.getProperty("file.separator") + MAKEFILE_FILENAME;
    File makeFile = new File(filePath);
    Scanner scanner = new Scanner(makeFile, StandardCharsets.UTF_8.name());
    String results = "";

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (line.toLowerCase().startsWith("version ")
          || line.toLowerCase().startsWith("version")
          || line.toLowerCase().startsWith("version:")
          || line.toLowerCase().startsWith("version :")) {
        String[] words = line.split("=");
        results = words[1].trim();
        break;
      }
    }

    return Version.valueOf(results);
  }
}
