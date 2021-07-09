package io.jenkins.plugins.conventionalcommits.utils;

import io.jenkins.plugins.conventionalcommits.ConventionalCommits;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  public void log(
      Level initialLogLevel,
      Level initialConsoleLevel,
      Level requiredLogLevel,
      Level requiredConsoleLevel,
      boolean revertAfterLogging,
      String message) {
    /*
       revertAfterLogging (boolean): revert to initial log and console level after logging the current log string
    */

    beforeLogging(requiredLogLevel, requiredConsoleLevel);
    LOGGER.log(requiredLogLevel, message);
    if (revertAfterLogging) afterLogging(initialLogLevel, initialConsoleLevel);
  }
}
