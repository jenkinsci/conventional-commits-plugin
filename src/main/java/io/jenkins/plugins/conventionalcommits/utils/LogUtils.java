package io.jenkins.plugins.conventionalcommits.utils;

import io.jenkins.plugins.conventionalcommits.ConventionalCommits;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Handles Logging. */
public class LogUtils {

  private static final Logger LOGGER = Logger.getLogger(ConventionalCommits.class.getName());
  private static final Handler consoleHandler = new ConsoleHandler();

  private void beforeLogging(Level loggerLevel, Level consoleLevel) {
    LOGGER.setLevel(loggerLevel);
    consoleHandler.setLevel(consoleLevel);
    LOGGER.addHandler(consoleHandler);
  }

  private void afterLogging(Level loggerLevel, Level consoleLevel) {
    LOGGER.removeHandler(consoleHandler);
    LOGGER.setLevel(loggerLevel);
    consoleHandler.setLevel(consoleLevel);
  }

  /**
   * Logs to console.
   *
   * @param initialLogLevel Log Level before the current log.
   * @param initialConsoleLevel ConsoleHandler's level before the current log.
   * @param requiredLogLevel Log Level at which current log is to be written.
   * @param requiredConsoleLevel ConsoleHandler's level for the current log string.
   * @param revertAfterLogging If True, revert to initial log and console level after logging the
   *     current log string.
   * @param message Log message.
   */
  public void log(
      Level initialLogLevel,
      Level initialConsoleLevel,
      Level requiredLogLevel,
      Level requiredConsoleLevel,
      boolean revertAfterLogging,
      String message) {

    beforeLogging(requiredLogLevel, requiredConsoleLevel);
    LOGGER.log(requiredLogLevel, message);
    if (revertAfterLogging) {
      afterLogging(initialLogLevel, initialConsoleLevel);
    }
  }
}
