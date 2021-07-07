package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class CurrentVersionTest {

    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    @Test
    public void testMavenProjectVersion() throws IOException, InterruptedException {

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

        Version actualCurrentVersion = Version.valueOf("1.0.0");
        CurrentVersion currentVersion = new CurrentVersion();
        Version testCurrentVersion = currentVersion.getCurrentVersion(mavenDir, "");

        assertThat(testCurrentVersion, is(notNullValue()));
        assertThat(actualCurrentVersion, is(testCurrentVersion));
    }

    @Test
    public void testNpmProjectVersion() throws IOException, InterruptedException {

        File npmDir = rootFolder.newFolder("SampleNPMProject");
        File packageJson = rootFolder.newFile(npmDir.getName() + File.separator + "package.json");

        String packageJsonContent= "{\"name\": \"test-project\",\n" +
                "  \"version\": \"1.0.0\",\n" +
                "  \"description\": \"A description\"}";

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
    public void should_throw_npe_if_null_directory() {

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
