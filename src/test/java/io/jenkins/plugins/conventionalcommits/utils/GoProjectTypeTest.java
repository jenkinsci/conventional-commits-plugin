package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GoProjectTypeTest {
  @Rule
  public TemporaryFolder rootFolder = new TemporaryFolder();

  @Mock
  private ProcessHelper mockProcessHelper;

  private void createGoMod(File goDir) throws Exception {
    Files.deleteIfExists(Paths.get(goDir.getPath() + File.separator + "go.mod"));
    File goMod = rootFolder.newFile(goDir.getName() + File.separator + "go.mod");
    String configContent =
      "module github.com/slim-patchy/hey\n" +
        "\n" +
        "require (\n" +
        "\tgolang.org/x/net v0.0.0-20191009170851-d66e71096ffb\n" +
        "\tgolang.org/x/text v0.3.2 // indirect\n" +
        ")\n" +
        "\n" +
        "go 1.13\n";
    FileWriter goWriter = new FileWriter(goMod);
    goWriter.write(configContent);
    goWriter.close();
  }

  @Test
  public void shouldGetCurrentVersionForAGoMod() throws Exception {
    // Given a Go project with a go.mod
    File goDir = rootFolder.newFolder("SampleGoProject");
    createGoMod(goDir);
    when(mockProcessHelper.runProcessBuilder(any(), any())).thenReturn("v0.1.4");

    // Asking to have the current version of the project
    GoProjectType goProjectType = new GoProjectType();
    Version readVersion = goProjectType.getCurrentVersion(goDir, mockProcessHelper);

    // The current version is returned
    assertThat(readVersion, IsEqual.equalTo(Version.valueOf("0.1.4")));
  }
}
