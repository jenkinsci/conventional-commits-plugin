package io.jenkins.plugins.conventionalcommits.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

import com.github.zafarkhaja.semver.Version;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HelmProjectTypeTest {
  @Rule
  public TemporaryFolder rootFolder = new TemporaryFolder();

  @Test
  public void shouldWriteVersionBackToFile() throws Exception {
    // When : use an Helm chart
    File helmDir = rootFolder.newFolder("SampleHelmProject");
    File chartYaml = rootFolder.newFile(helmDir.getName() + File.separator + "Chart.yaml");
    String chartContent = "apiVersion: v2\n" +
        "description: Cool chart\n" +
        "home: https://github.com/xxx\n" +
        "maintainers:\n" +
        "  - name: foo\n" +
        "name: cool name\n" +
        "version: 1.0.0\n";

    FileWriter chartWriter = new FileWriter(chartYaml);
    chartWriter.write(chartContent);
    chartWriter.close();

    // When : want to update the Chart.xml with a new version
    HelmProjectType helmProjectType = new HelmProjectType();
    helmProjectType.writeVersion(helmDir, Version.valueOf("1.1.0"), null);

    // Then : the Chart.xml is updated
    assertThat(new String(
            Files.readAllBytes(Paths.get(helmDir.getPath() + File.separator + "Chart.yaml"))),
        containsString("version: 1.1.0"));
  }
}