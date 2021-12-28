package io.jenkins.plugins.conventionalcommits.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ProjectTypeTest {

  @Rule public TemporaryFolder rootFolder = new TemporaryFolder();

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

  @Test
  public void isNpmProject() throws IOException {
    File npmDir = rootFolder.newFolder("SampleNpmFolder");
    rootFolder.newFile(npmDir.getName() + File.separator + "package.json");

    ProjectType projectType = new NpmProjectType();

    assertEquals(true, projectType.check(npmDir));
  }

  @Test
  public void isNotNpmProject() throws IOException {
    File npmDir = rootFolder.newFolder("SampleNpmFolder");
    ProjectType projectType = new NpmProjectType();
    assertEquals(false, projectType.check(npmDir));
  }

  @Test
  public void isPythonProject() throws IOException {
    File pyDir = rootFolder.newFolder("SamplePythonProject");
    rootFolder.newFile(pyDir.getName() + File.separator + "setup.py");
    ProjectType projectType = new PythonProjectType();
    assertEquals(true, projectType.check(pyDir));
  }

  @Test
  public void isNotPythonProject() throws IOException {
    File pyDir = rootFolder.newFolder("SamplePythonProject");
    ProjectType projectType = new PythonProjectType();
    assertEquals(false, projectType.check(pyDir));
  }

  @Test
  public void isHelmProject() throws IOException {
    File helmDir = rootFolder.newFolder("SampleHelmFolder");
    rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");
    ProjectType projectType = new HelmProjectType();
    assertTrue(projectType.check(helmDir));
  }

  @Test
  public void isHelmNotProject() throws IOException {
    File helmDir = rootFolder.newFolder("SampleHelmFolder");
    ProjectType projectType = new HelmProjectType();
    assertFalse(projectType.check(helmDir));
  }

  @Test
  public void isPythonProjectWithTOMLFile() throws IOException {
    File pyDir = rootFolder.newFolder("SamplePythonProject");
    rootFolder.newFile(pyDir.getName() + File.separator + "pyproject.toml");
    ProjectType projectType = new PythonProjectType();
    assertTrue(projectType.check(pyDir));
  }

  @Test
  public void isGoProject() throws IOException {
    File goDir = rootFolder.newFolder("SampleGoProject");
    rootFolder.newFile(goDir.getName() + File.separator + "go.mod");
    ProjectType projectType = new GoProjectType();
    assertEquals(true, projectType.check(goDir));
  }

  @Test
  public void isNotGoProject() throws IOException {
    File goDir = rootFolder.newFolder("SampleGoProject");
    ProjectType projectType = new GoProjectType();
    assertFalse(projectType.check(goDir));
  }
}
