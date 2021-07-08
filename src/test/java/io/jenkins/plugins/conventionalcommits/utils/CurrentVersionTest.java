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

    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    @Mock
    private ProcessHelper processHelper;

    @Test
    public void testMavenProjectVersion() throws IOException, InterruptedException {

        String os = System.getProperty("os.name");
        String commandName = "mvn";

        if (os.contains("Windows")) {
            commandName += ".cmd";
        }

        List<String> command = Arrays.asList(commandName, "help:evaluate",
                "-Dexpression=project.version", "-q", "-DforceStdout");

        File mavenDir = rootFolder.newFolder("SampleMavenProject");
        File pom = rootFolder.newFile(mavenDir.getName() + File.separator + "pom.xml");

        String pomContent = "<project>\n" +
                " <modelVersion>4.0.0</modelVersion>\n" +
                " <groupId>com.test.app</groupId>\n" +
                " <artifactId>test-app</artifactId>\n" +
                " <version>1.0.0</version>\n" +
                "</project>\n";

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

        String buildGradleContent = "group 'com.sample.gradle'\n" +
                "version = '1.0.0'\n" +
                "\n" +
                "apply plugin: 'java'\n" +
                "\n" +
                "sourceCompatibility = 1.8\n" +
                "\n" +
                "sourceCompatibility = 1.8\n" +
                "targetCompatibility = 1.8";

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
