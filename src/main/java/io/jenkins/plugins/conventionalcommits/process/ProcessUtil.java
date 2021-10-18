package io.jenkins.plugins.conventionalcommits.process;

import com.google.common.io.LineReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Class to execute some CLI commands.
 */
public class ProcessUtil {

  /**
   * Reads data from stdout.
   *
   * @param in InputStream object.
   * @return read data.
   * @throws IOException If an error occur reading files.
   */
  private static String stdout(InputStream in) throws IOException {
    StringBuilder builder = new StringBuilder();
    LineReader reader = new LineReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      builder.append(line);
      builder.append(System.getProperty("line.separator"));
    }
    return builder.toString();
  }

  /**
   * Execute a CLI command using ProcessBuilder.
   *
   * @param dir            Directory where execute the command.
   * @param commandAndArgs Command and parameters of the command.
   * @return THe output of the command.
   * @throws IOException          If an error occur accessing files.
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
