package io.jenkins.plugins.conventionalcommits.process;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;

/** This class runs a given command using ProcessBuilder. */
public class DefaultProcessHelper implements ProcessHelper {

  @Override
  public String runProcessBuilder(File directory, List<String> command)
      throws IOException, InterruptedException {

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.directory(directory);
    Process process = processBuilder.start();

    String results = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
    process.waitFor();

    return results;
  }
}
