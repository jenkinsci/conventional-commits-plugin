package io.jenkins.plugins.conventionalcommits.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GradleProjectTypeTest {
  @Mock
  private ProcessHelper mockProcessHelper;

  @Rule
  final public TemporaryFolder rootFolder = new TemporaryFolder();

  final private String buildGradleWithVersionContent =
      "foo = foo value\n" +
          "version = 1.0.0\n" +
          "bar = bar value";
  final private String buildGradleWithoutVersionContent =
      "foo = foo value\n" +
          "bar = bar value";

  private void createBuildGradleFiles(File gradleDir, String content) throws Exception {
    rootFolder.newFile(gradleDir.getName() + File.separator + "build.gradle");
    File buildGradle = rootFolder.newFile(gradleDir.getName() + File.separator + "gradle.properties");
    FileWriter gradleWriter = new FileWriter(buildGradle);
    gradleWriter.write(content);
    gradleWriter.close();

  }

  @Test
  public void shouldCheckGradleProjectOk() throws Exception {
    // Given a directory with a build.gradle
    File gradleDir = rootFolder.newFolder("SampleGradleProject");
    createBuildGradleFiles(gradleDir, buildGradleWithVersionContent);

    // When asking if it's a gradle project
    GradleProjectType gradleProjectType = new GradleProjectType();
    boolean isGradleProject = gradleProjectType.check(gradleDir);

    // Then answer true
    assertThat(isGradleProject, equalTo(true));
  }

  @Test
  public void shouldCheckGradleProjectKo() throws Exception {
    // Given a directory with a build.gradle
    File gradleDir = rootFolder.newFolder("SampleFooProject");

    // When asking if it's a gradle project
    GradleProjectType gradleProjectType = new GradleProjectType();
    boolean isGradleProject = gradleProjectType.check(gradleDir);

    // Then answer true
    assertThat(isGradleProject, equalTo(false));
  }

  @Test
  public void shouldGetCurrentVersion() throws Exception {
    // Given a gradle project in 1.0.0 version
    File gradleDir = rootFolder.newFolder("SampleGradleProject");
    createBuildGradleFiles(gradleDir, buildGradleWithVersionContent);
    // Set mock for npm version command
    when(mockProcessHelper.runProcessBuilder(any(), any())).thenReturn("foo: foo\nversion: 1.0.0\nbar: bar");

    // When asking to get the current version
    GradleProjectType gradleProjectType = new GradleProjectType();
    Version version = gradleProjectType.getCurrentVersion(gradleDir, mockProcessHelper);

    // Then answer 1.0.0
    assertThat(version, equalTo(Version.valueOf("1.0.0")));
  }

  @Test
  public void shouldWriteNextVersionToFile() throws Exception {
    // Given a directory with a gradle.build file
    File gradleDir = rootFolder.newFolder("SampleGradleProject");
    createBuildGradleFiles(gradleDir, buildGradleWithVersionContent);

    // When : write next version to the file
    GradleProjectType gradleProjectType = new GradleProjectType();
    gradleProjectType.writeVersion(gradleDir, Version.valueOf("1.1.0"), null);

    // Then : the file is updated
    assertThat(new String(
            Files.readAllBytes(Paths.get(gradleDir.getPath() + File.separator + "gradle.properties"))),
        containsString("version = 1.1.0"));
  }

  @Test(expected = IOException.class)
  public void shouldThrowIOExceptionIfNoBuildFile() throws Exception {
    // Given : a project without a build.file
    File gradleDir = rootFolder.newFolder("SampleGradleProject");

    // When : ask to write next version in file
    GradleProjectType gradleProjectType = new GradleProjectType();
    gradleProjectType.writeVersion(gradleDir, Version.valueOf("1.1.0"), null);

    // Then : IOException is thrown
  }

  @Test(expected = IOException.class)
  public void shouldThrowIOExceptionIfNoVersionTag() throws Exception {
    // Given a directory with a gradle.build file
    File gradleDir = rootFolder.newFolder("SampleGradleProject");
    createBuildGradleFiles(gradleDir, buildGradleWithoutVersionContent);

    // When : write next version tp the file
    GradleProjectType gradleProjectType = new GradleProjectType();
    gradleProjectType.writeVersion(gradleDir, Version.valueOf("1.1.0"), null);

    // Then : IOException is thrown
  }
}
