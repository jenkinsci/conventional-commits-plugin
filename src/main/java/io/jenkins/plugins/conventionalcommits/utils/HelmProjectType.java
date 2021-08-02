package io.jenkins.plugins.conventionalcommits.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.dto.HelmChart;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/** Represent an Helm project type (i.e with a Chart.yaml file). */
public class HelmProjectType extends ProjectType {
  // Helm chart's file name
  private static final String CHART_YAML_NAME = "Chart.yaml";

  /**
   * To know if the project is an Helm project type.
   *
   * @param directory The directory to check. <b>Mandatory</b>
   * @return true if a Chart.yaml file is found.
   */
  @Override
  public boolean check(File directory) {
    Objects.requireNonNull(directory);
    return new File(directory.getAbsoluteFile() + File.separator + CHART_YAML_NAME).exists();
  }

  /**
   * Return the next version of the version attribute.
   *
   * @param directory The project's directory.
   * @param processHelper Not used.
   * @return The next calculated version (based on Semver).
   * @throws IOException If an error occur reading files.
   */
  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper) throws IOException {
    Objects.requireNonNull(directory);

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Convert Chart.yaml to a DTO
    HelmChart chart =
        mapper.readValue(
            new File(directory.getAbsoluteFile() + File.separator + CHART_YAML_NAME),
            HelmChart.class);
    return Version.valueOf(chart.getVersion());
  }

  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {}
}
