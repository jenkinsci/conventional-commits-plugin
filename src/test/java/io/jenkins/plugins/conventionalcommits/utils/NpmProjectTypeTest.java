package io.jenkins.plugins.conventionalcommits.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NpmProjectTypeTest {
  @Rule
  public TemporaryFolder rootFolder = new TemporaryFolder();

  @Mock
  private ProcessHelper mockProcessHelper;

  /**
   * Helper to create a package.json file with a version.
   *
   * @param npmDir  The directory where create the file
   * @param version The version to set
   * @throws Exception If errors occurs when creation the file
   */
  private void createPackageJson(File npmDir, String version) throws Exception {
    Files.deleteIfExists(Paths.get(npmDir.getPath() + File.separator + "package.json"));
    File packageJson = rootFolder.newFile(npmDir.getName() + File.separator + "package.json");
    String packageJsonContent =
        "{\n" +
            "\"name\": \"conventional-commits-plugin-example-npm\",\n" +
            "\"version\": " + version + ",\n" +
            "\"description\": \"Npm example project\"\n" +
            "}";
    FileWriter packageWriter = new FileWriter(packageJson);
    packageWriter.write(packageJsonContent);
    packageWriter.close();

  }

  @Test
  public void shouldWriteVersionBack() throws Exception {
    // Set npm project
    File npmDir = rootFolder.newFolder("SampleNpmProject");
    createPackageJson(npmDir, "1.0.0");

    // Set mock for npm version command
    when(mockProcessHelper.runProcessBuilder(any(), any()))
        .then(invocationOnMock -> {
              createPackageJson(npmDir, "1.1.0");
              return "1.1.0";
            }
        );
    NpmProjectType npmProjectType = new NpmProjectType();

    npmProjectType.writeVersion(npmDir, Version.valueOf("1.1.0"), mockProcessHelper);

    verify(mockProcessHelper).runProcessBuilder(any(File.class),
        eq(Arrays.asList("npm", "version", "1.1.0")));

    assertThat(new String(
            Files.readAllBytes(Paths.get(npmDir.getPath() + File.separator + "package.json"))),
        containsString("1.1.0"));
  }

  @Test(expected = IOException.class)
  public void shouldThrowIOException() throws Exception {
    // Set foo project
    File fooDir = rootFolder.newFolder("SampleFooProject");

    // Set mock for throw IOException (npm not installed)
    when(mockProcessHelper.runProcessBuilder(any(), any())).thenThrow(
        new IOException("Cannot run program \"npm\""));
    NpmProjectType npmProjectType = new NpmProjectType();

    npmProjectType.writeVersion(fooDir, Version.valueOf("1.1.0"), mockProcessHelper);
  }
}
