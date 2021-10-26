package io.jenkins.plugins.conventionalcommits.process;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProcessUtilTest {
  @Rule
  public TemporaryFolder rootFolder = new TemporaryFolder();

  @Test
  public void shouldExecuteACommand() throws Exception {
    // Given : A command

    // When : Call the execute command
    String result = ProcessUtil.execute(rootFolder.newFolder("foo"), "git", "--version");

    // Then : The command was executed
    assertThat(result, CoreMatchers.containsString("git version"));
  }

}