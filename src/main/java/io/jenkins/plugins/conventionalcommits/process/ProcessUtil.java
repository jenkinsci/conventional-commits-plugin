package io.jenkins.plugins.conventionalcommits.process;

import static io.jenkins.plugins.conventionalcommits.NextVersionStep.stdout;

import java.io.File;
import java.io.IOException;

/**
 * Class to execute some CLI commands.
 */
public class ProcessUtil {

  /**
   * Execute a CLI command using ProcessBuilder.
   *
   * @param dir Directory where execute the command.
   * @param commandAndArgs Command and parameters of the command.
   *
   * @return THe output of the command.
   *
   * @throws IOException If an error occur accessing files.
   * @throws InterruptedException If the command is interrupted.
   */
  public static String execute(File dir, String... commandAndArgs)
      throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder().directory(dir).command(commandAndArgs);

    Process process = builder.start();
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      String stderr = stdout(process.getErrorStream());
      throw new IOException(
          "executing '"
              + String.join(" ", commandAndArgs)
              + "' failed in '"
              + dir
              + "' with exit code"
              + exitCode
              + " and error "
              + stderr);
    }
    return stdout(process.getInputStream());
  }
}
