package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CurrentVersionTest {

  @Rule public TemporaryFolder rootFolder = new TemporaryFolder();

  @Mock private ProcessHelper processHelper;

  @Test
  public void testMavenProjectVersion() throws IOException, InterruptedException {

    String os = System.getProperty("os.name");
    String commandName = "mvn";

    if (os.contains("Windows")) {
      commandName += ".cmd";
    }

    List<String> command =
        Arrays.asList(
            commandName, "help:evaluate", "-Dexpression=project.version", "-q", "-DforceStdout");

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
    when(processHelper.runProcessBuilder(mavenDir, command)).thenReturn("1.0.0");

    Version actualCurrentVersion = Version.valueOf("1.0.0");
    CurrentVersion currentVersion = new CurrentVersion();
    currentVersion.setProcessHelper(processHelper);

    Version testCurrentVersion = currentVersion.getCurrentVersion(mavenDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void testGradleProjectVersion() throws IOException, InterruptedException {

    String os = System.getProperty("os.name");
    String commandName = "gradle";

    if (os.contains("Windows")) {
      commandName += ".bat";
    }
    List<String> command = Arrays.asList(commandName, "-q", "properties");

    File gradleDir = rootFolder.newFolder("SampleGradleProject");
    File buildGradle = rootFolder.newFile(gradleDir.getName() + File.separator + "build.gradle");

    String buildGradleContent =
        "group 'com.sample.gradle'\n"
            + "version = '1.0.0'\n"
            + "\n"
            + "apply plugin: 'java'\n"
            + "\n"
            + "sourceCompatibility = 1.8\n"
            + "\n"
            + "sourceCompatibility = 1.8\n"
            + "targetCompatibility = 1.8";

    FileWriter buildGradleWriter = new FileWriter(buildGradle);
    buildGradleWriter.write(buildGradleContent);
    buildGradleWriter.close();

    assertThat(processHelper, is(notNullValue()));
    when(processHelper.runProcessBuilder(gradleDir, command)).thenReturn("version: 1.0.0");

    Version actualCurrentVersion = Version.valueOf("1.0.0");
    CurrentVersion currentVersion = new CurrentVersion();
    currentVersion.setProcessHelper(processHelper);

    Version testCurrentVersion = currentVersion.getCurrentVersion(gradleDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void testMakeProjectVersion() throws IOException, InterruptedException {

    File makeDir = rootFolder.newFolder("SampleMakeProject");
    File makeFile = rootFolder.newFile(makeDir.getName() + File.separator + "Makefile");

    String makeFileContent =
        "name=sample\n" + "version=1.10.0\n" + "\n" + "build:\n" + "\techo \"hello world\" \n";

    FileWriter makeFileWriter = new FileWriter(makeFile);
    makeFileWriter.write(makeFileContent);
    makeFileWriter.close();

    Version actualCurrentVersion = Version.valueOf("1.10.0");
    CurrentVersion currentVersion = new CurrentVersion();
    Version testCurrentVersion = currentVersion.getCurrentVersion(makeDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void testNpmProjectVersion() throws IOException, InterruptedException {

    File npmDir = rootFolder.newFolder("SampleNPMProject");
    File packageJson = rootFolder.newFile(npmDir.getName() + File.separator + "package.json");

    String packageJsonContent =
        "{\"name\": \"test-project\",\n"
            + "  \"version\": \"1.0.0\",\n"
            + "  \"description\": \"A description\"}";

    FileWriter packageJsonWriter = new FileWriter(packageJson);
    packageJsonWriter.write(packageJsonContent);
    packageJsonWriter.close();

    Version actualCurrentVersion = Version.valueOf("1.0.0");
    CurrentVersion currentVersion = new CurrentVersion();
    Version testCurrentVersion = currentVersion.getCurrentVersion(npmDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void testPythonProjectVersionSetupPyExists() throws IOException, InterruptedException {

    String os = System.getProperty("os.name");
    String commandName = "python";

    if (!os.contains("Windows")) {
      commandName += "3";
    }

    List<String> command = Arrays.asList(commandName, "setup.py", "--version");

    File pyDir = rootFolder.newFolder("SamplePythonProject");
    File setupFile = rootFolder.newFile(pyDir.getName() + File.separator + "setup.py");
    File setupCfg = rootFolder.newFile(pyDir.getName() + File.separator + "setup.cfg");

    String setupFileContent = "from setuptools import setup\n" + "setup()";

    String setupCfgContent =
        "[metadata]\n" + "name = sample\n" + "version = 0.1.0\n" + "author = Sample Author";

    FileWriter setupCfgWriter = new FileWriter(setupCfg);
    setupCfgWriter.write(setupCfgContent);
    setupCfgWriter.close();

    FileWriter setupFileWriter = new FileWriter(setupFile);
    setupFileWriter.write(setupFileContent);
    setupFileWriter.close();

    assertThat(processHelper, is(notNullValue()));
    when(processHelper.runProcessBuilder(pyDir, command)).thenReturn("0.1.0");

    Version actualCurrentVersion = Version.valueOf("0.1.0");
    CurrentVersion currentVersion = new CurrentVersion();
    currentVersion.setProcessHelper(processHelper);

    Version testCurrentVersion = currentVersion.getCurrentVersion(pyDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void testPythonProjectVersionSetupCfgExists() throws IOException, InterruptedException {

    File pyDir = rootFolder.newFolder("SamplePythonProject");
    File setupCfg = rootFolder.newFile(pyDir.getName() + File.separator + "setup.cfg");

    String setupCfgContent =
        "[metadata]\n" + "name = sample\n" + "version = 0.1.0\n" + "author = Sample Author";

    FileWriter setupCfgWriter = new FileWriter(setupCfg);
    setupCfgWriter.write(setupCfgContent);
    setupCfgWriter.close();

    Version actualCurrentVersion = Version.valueOf("0.1.0");
    CurrentVersion currentVersion = new CurrentVersion();
    Version testCurrentVersion = currentVersion.getCurrentVersion(pyDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void shouldReadHelmChartCurrentVersion() throws IOException, InterruptedException {

    File helmDir = rootFolder.newFolder("SampleHelmProject");
    File chartYaml = rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");

    String packageJsonContent =
            "apiVersion: v2\n" +
                    "description: Chart's description\n" +
                    "home: https://github.com/xxx\n" +
                    "name: xxx\n" +
                    "version: 1.0.0\n" +
                    "appVersion: 0.0.1\n" +
                    "engine: gotpl\n" +
                    "sources:\n" +
                    "  - https://github.com/xxxx";

    FileWriter chartYamlWriter = new FileWriter(chartYaml);
    chartYamlWriter.write(packageJsonContent);
    chartYamlWriter.close();

    Version actualCurrentVersion = Version.valueOf("1.0.0");
    CurrentVersion currentVersion = new CurrentVersion();
    Version testCurrentVersion = currentVersion.getCurrentVersion(helmDir, "");
    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void testPythonProjectVersionPyProjectTOMLExists()
      throws IOException, InterruptedException {

    File pyDir = rootFolder.newFolder("SamplePythonProject");
    File tomlFile = rootFolder.newFile(pyDir.getName() + File.separator + "pyproject.toml");

    String tomlContent =
        "[project]\n"
            + "name = \"spam\"\n"
            + "version = \"1.0.0\"\n"
            + "description = \"Lovely Spam! Wonderful Spam!\"\n"
            + "readme = \"README.rst\"";

    FileWriter tomlWriter = new FileWriter(tomlFile);
    tomlWriter.write(tomlContent);
    tomlWriter.close();

    Version actualCurrentVersion = Version.valueOf("1.0.0");
    CurrentVersion currentVersion = new CurrentVersion();
    Version testCurrentVersion = currentVersion.getCurrentVersion(pyDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void CurrentVersion_NoProjectWithTag() throws IOException, InterruptedException {

    File testDir = rootFolder.newFolder("SampleProject");
    Version actualCurrentVersion = Version.valueOf("0.1.0");

    CurrentVersion currentVersion = new CurrentVersion();
    Version testCurrentVersion = currentVersion.getCurrentVersion(testDir, "0.1.0");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }

  @Test
  public void CurrentVersion_NoProjectNoTag() throws IOException, InterruptedException {

    File testDir = rootFolder.newFolder("SampleProject");
    Version actualCurrentVersion = Version.valueOf("0.0.0");

    CurrentVersion currentVersion = new CurrentVersion();
    Version testCurrentVersion = currentVersion.getCurrentVersion(testDir, "");

    assertThat(testCurrentVersion, is(notNullValue()));
    assertThat(actualCurrentVersion, is(testCurrentVersion));
  }
}
