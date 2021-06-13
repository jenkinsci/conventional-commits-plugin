package io.jenkins.plugins.conventionalcommits.utils;

import com.github.zafarkhaja.semver.Version;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import hudson.tasks.Maven;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertEquals;

public class ProjectTypeTest {

    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    @Test
    public void isMavenProject() throws IOException {

        File mavenDir = rootFolder.newFolder("SampleMavenProject");
        rootFolder.newFile(mavenDir.getName() + File.separator + "pom.xml");

        ProjectType projectType = new MavenProjectType();
        assertEquals(true, projectType.check(mavenDir));
    }

    @Test
    public void isNotMavenProject() throws IOException {

        File mavenDir = rootFolder.newFolder("SampleMavenProject");
        ProjectType projectType = new MavenProjectType();
        assertEquals(false, projectType.check(mavenDir));
    }

}
