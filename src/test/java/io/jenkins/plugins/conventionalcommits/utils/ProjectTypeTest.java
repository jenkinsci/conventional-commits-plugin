package io.jenkins.plugins.conventionalcommits.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

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

    @Test
    public void isGradleProject() throws IOException {

        File gradleDir = rootFolder.newFolder("SampleGradleProject");
        rootFolder.newFile(gradleDir.getName() + File.separator + "build.gradle");

        ProjectType projectType = new GradleProjectType();
        assertEquals(true, projectType.check(gradleDir));
    }

    @Test
    public void isNotGradleProject() throws IOException {

        File gradleDir = rootFolder.newFolder("SampleGradleProject");
        ProjectType projectType = new GradleProjectType();
        assertEquals(false, projectType.check(gradleDir));
    }

    @Test
    public void isMakeProject() throws IOException {

        File makeDir = rootFolder.newFolder("SampleMakeProject");
        rootFolder.newFile(makeDir.getName() + File.separator + "Makefile");

        ProjectType projectType = new MakeProjectType();
        assertEquals(true, projectType.check(makeDir));
    }

    @Test
    public void isNotMakeProject() throws IOException {

        File makeDir = rootFolder.newFolder("SampleMakeProject");

        ProjectType projectType = new MakeProjectType();
        assertEquals(false, projectType.check(makeDir));
    }

}
