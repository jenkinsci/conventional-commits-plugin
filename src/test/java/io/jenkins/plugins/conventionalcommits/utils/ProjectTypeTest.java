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
    public void should_is_npm_project() throws IOException{
        File npmDir = rootFolder.newFolder("SampleNpmFolder");
        rootFolder.newFile(npmDir.getName() + File.separator + "package.json");

        ProjectType projectType = new NpmProjectType();

        assertEquals(true, projectType.check(npmDir));
    }

    @Test
    public void should_is_not_npm_project() throws IOException{
        File npmDir = rootFolder.newFolder("SampleNpmFolder");

        ProjectType projectType = new NpmProjectType();

        assertEquals(false, projectType.check(npmDir));
    }
}
