package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WriteVersionTest {

  @Rule public TemporaryFolder rootFolder = new TemporaryFolder();

  @Mock private ProcessHelper processHelper;

  @Captor private ArgumentCaptor<ArrayList<String>> captor;

  @Test
  public void testWriteMavenProjectVersion() throws IOException, InterruptedException {

    String os = System.getProperty("os.name");
    String commandName = "mvn";

    if (os.contains("Windows")) {
      commandName += ".cmd";
    }

    List<String> command = Arrays.asList(commandName, "versions:set", "-DnewVersion=2.0.0");

    File mavenDir = rootFolder.newFolder("SampleMavenProject");
    File pom = rootFolder.newFile(mavenDir.getName() + File.separator + "pom.xml");

    String pomContent =
        "<project>\n"
            + " <modelVersion>4.0.0</modelVersion>\n"
            + " <groupId>com.test.app</groupId>\n"
            + " <artifactId>test-app</artifactId>\n"
            + " <version>1.0.0</version>\n"
            + "</project>\n";

    FileWriter pomWriter = new FileWriter(pom);
    pomWriter.write(pomContent);
    pomWriter.close();

    assertThat(processHelper, is(notNullValue()));
    when(processHelper.runProcessBuilder(mavenDir, command)).thenReturn("2.0.0");

    WriteVersion writer = new WriteVersion();
    writer.setProcessHelper(processHelper);
    writer.write(Version.valueOf("2.0.0"), mavenDir);

    verify(processHelper).runProcessBuilder(any(), captor.capture());
    List<String> capturedCommand = captor.getValue();
    assertThat(capturedCommand, is(command));
  }

  @Test
  public void testWriteVersionFailed() throws IOException, InterruptedException {

    File dir = rootFolder.newFolder("SampleProject");

    WriteVersion writer = new WriteVersion();
    writer.write(Version.valueOf("1.0.0"), dir);
    // FIXME: Check if logger was called
  }
}
