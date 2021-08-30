package io.jenkins.plugins.conventionalcommits.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

import com.github.zafarkhaja.semver.Version;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HelmProjectTypeTest {
  @Rule
  public TemporaryFolder rootFolder = new TemporaryFolder();

  final private String goodChartContent = "apiVersion: v2\n" +
      "description: Cool chart\n" +
      "home: https://github.com/xxx\n" +
      "maintainers:\n" +
      "  - name: foo\n" +
      "name: cool name\n" +
      "version: 1.0.0\n";
  final private String chartContentWithoutVersion = "apiVersion: v2\n" +
      "description: Cool chart\n" +
      "home: https://github.com/xxx\n" +
      "maintainers:\n" +
      "  - name: foo\n" +
      "name: cool name\n";

  @Test
  public void shouldCheckIfItSHelmProject() throws Exception {
    // Given : use an Helm Chart
    File helmDir = rootFolder.newFolder("SampleHelmProject");
    File chartYaml = rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");

    FileWriter chartWriter = new FileWriter(chartYaml);
    chartWriter.write(goodChartContent);
    chartWriter.close();

    // When : ask to if it's a Helm project
    HelmProjectType helmProjectType = new HelmProjectType();
    Boolean isHelmProject = helmProjectType.check(helmDir);

    // Then : get true for a Helm project
    assertThat(isHelmProject, equalTo(true));
  }

  @Test
  public void shouldCheckIfItSNotHelmProject() throws Exception {
    // Given : use an Helm Chart
    File helmDir = rootFolder.newFolder("SampleFooProject");

    // When : ask to if it's a Helm project
    HelmProjectType helmProjectType = new HelmProjectType();
    Boolean isHelmProject = helmProjectType.check(helmDir);

    // Then : get false for a Helm project
    assertThat(isHelmProject, equalTo(false));
  }

  @Test
  public void shouldGetCurrentVersionForHelmChart() throws Exception {
    // Given : use an Helm Chart
    File helmDir = rootFolder.newFolder("SampleHelmProject");
    File chartYaml = rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");

    FileWriter chartWriter = new FileWriter(chartYaml);
    chartWriter.write(goodChartContent);
    chartWriter.close();

    // When : ask to get the next version
    HelmProjectType helmProjectType = new HelmProjectType();
    Version version = helmProjectType.getCurrentVersion(helmDir, null);

    // Then : get the current version (1.0.0)
    assertThat(version, equalTo(Version.valueOf("1.0.0")));
  }

  @Test(expected = IOException.class)
  public void shouldNotGetCurrentVersionIfNoVersionField() throws Exception {
    // Given : use an Helm Chart
    File helmDir = rootFolder.newFolder("SampleHelmProject");
    File chartYaml = rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");


    FileWriter chartWriter = new FileWriter(chartYaml);
    chartWriter.write(chartContentWithoutVersion);
    chartWriter.close();

    // When : ask to get the next version
    HelmProjectType helmProjectType = new HelmProjectType();
    Version version = helmProjectType.getCurrentVersion(helmDir, null);

    // Then : throw IOException
  }

  @Test(expected = FileNotFoundException.class)
  public void shouldNotGetCurrentVersionIfNoCHartFile() throws Exception {
    // Given : use an Helm Chart
    File helmDir = rootFolder.newFolder("SampleHelmProject");

    // When : ask to get the next version
    HelmProjectType helmProjectType = new HelmProjectType();
    Version version = helmProjectType.getCurrentVersion(helmDir, null);

    // Then : throw FileNotFoundException
  }

  @Test
  public void shouldWriteVersionBackToFile() throws Exception {
    // Given : use an Helm chart
    File helmDir = rootFolder.newFolder("SampleHelmProject");
    File chartYaml = rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");

    FileWriter chartWriter = new FileWriter(chartYaml);
    chartWriter.write(goodChartContent);
    chartWriter.close();

    // When : want to update the Chart.xml with a new version
    HelmProjectType helmProjectType = new HelmProjectType();
    helmProjectType.writeVersion(helmDir, Version.valueOf("1.1.0"), null);

    // Then : the Chart.xml is updated
    String chartExpected = "apiVersion: v2\n" +
        "description: Cool chart\n" +
        "home: https://github.com/xxx\n" +
        "maintainers:\n" +
        "  - name: foo\n" +
        "name: cool name\n" +
        "version: 1.1.0\n";
    assertThat(new String(
            Files.readAllBytes(Paths.get(helmDir.getPath() + File.separator + "Chart.yaml"))),
        containsString(chartExpected));
  }

  @Test(expected = FileNotFoundException.class)
  public void shouldNotWriteVersionBackToFileIfNoChartFile() throws Exception {
    // Given : use an Helm chart
    File helmDir = rootFolder.newFolder("SampleFooProject");

    // When : want to update the Chart.xml with a new version
    HelmProjectType helmProjectType = new HelmProjectType();
    helmProjectType.writeVersion(helmDir, Version.valueOf("1.1.0"), null);

    // Then : throw FileNotFoundException
  }

  @Test(expected = IOException.class)
  public void shouldNotWriteVersionBackToFileIfNoVersionField() throws Exception {
    // Given : use an Helm chart
    File helmDir = rootFolder.newFolder("SampleHelmProject");
    File chartYaml = rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");

    FileWriter chartWriter = new FileWriter(chartYaml);
    chartWriter.write(chartContentWithoutVersion);
    chartWriter.close();

    // When : want to update the Chart.xml with a new version
    HelmProjectType helmProjectType = new HelmProjectType();
    helmProjectType.writeVersion(helmDir, Version.valueOf("1.1.0"), null);

    // Then : throw IOException
  }
}

