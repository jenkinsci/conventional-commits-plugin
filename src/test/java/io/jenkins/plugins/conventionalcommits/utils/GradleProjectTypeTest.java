package io.jenkins.plugins.conventionalcommits.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

import com.github.zafarkhaja.semver.Version;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GradleProjectTypeTest {
  @Rule
  final public TemporaryFolder rootFolder = new TemporaryFolder();

  final private String buildGradleWithVersionContent =
      "foo = foo value\n" +
          "version = 1.0.0\n" +
          "bar = bar value";
  final private String buildGradleWithoutVersionContent =
      "foo = foo value\n" +
          "bar = bar value";

  private void createBuildGradleFile(File gradleDir, String content) throws Exception {
    File buildGradle = rootFolder.newFile(gradleDir.getName() + File.separator + "gradle.properties");
    FileWriter gradleWriter = new FileWriter(buildGradle);
    gradleWriter.write(content);
    gradleWriter.close();

  }

  @Test
  public void shouldWriteNextVersionToFile() throws Exception {
    // Given a directory with a gradle.build file
    File gradleDir = rootFolder.newFolder("SampleGradleProject");
    createBuildGradleFile(gradleDir, buildGradleWithVersionContent);

    // When : write next version tp the file
    GradleProjectType gradleProjectType = new GradleProjectType();
    gradleProjectType.writeVersion(gradleDir, Version.valueOf("1.1.0"), null);

    // Then : the file is updated
    String buildGradleExpected =
        "foo = foo value\n" +
            "version = 1.1.0\n" +
            "bar = bar value";
    assertThat(new String(
            Files.readAllBytes(Paths.get(gradleDir.getPath() + File.separator + "gradle.properties"))),
        containsString(buildGradleExpected));
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
    createBuildGradleFile(gradleDir, buildGradleWithoutVersionContent);

    // When : write next version tp the file
    GradleProjectType gradleProjectType = new GradleProjectType();
    gradleProjectType.writeVersion(gradleDir, Version.valueOf("1.1.0"), null);

    // Then : IOException is thrown
  }
}