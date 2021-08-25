package io.jenkins.plugins.conventionalcommits.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.zafarkhaja.semver.Version;
import io.jenkins.plugins.conventionalcommits.dto.HelmChart;
import io.jenkins.plugins.conventionalcommits.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Represent an Helm project type (i.e with a Chart.yaml file).
 */
public class HelmProjectType extends ProjectType {
  // Helm chart's file name
  private static final String CHART_YAML_NAME = "Chart.yaml";
  // Jackson mapper to handle YAML file
  private final ObjectMapper yamlMapper;

  /**
   * Default constructor.
   * Init the YAML mapper.
   */
  public HelmProjectType() {
    YAMLFactory yamlFactory = new YAMLFactory();
    // Remove quotes on strings
    yamlFactory.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
    yamlMapper = new ObjectMapper(yamlFactory);
    // Ignore properties that are not in the DTO
    yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

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
   * @param directory     The project's directory.
   * @param processHelper Not used.
   * @return The next calculated version (based on Semver).
   * @throws IOException If an error occur reading files.
   */
  @Override
  public Version getCurrentVersion(File directory, ProcessHelper processHelper) throws IOException {
    // readChartFile(directory) can't be null, an Exception is thrown if a problem occur
    // (file note found, file malformed, ..)
    return Version.valueOf(readChartFile(directory).getVersion());
  }

  /**
   * Write back to the Chart.yaml the new Version.
   *
   * @param directory     The directory where is the Chart.yaml. <b>Mandatory</b>
   * @param nextVersion   The next version to write. <b>Mandatory</b>
   * @param processHelper Can be null, not used.
   * @throws IOException          If an error occurred when accessing files or directory.
   * @throws InterruptedException Not used.
   */
  @Override
  public void writeVersion(File directory, Version nextVersion, ProcessHelper processHelper)
      throws IOException, InterruptedException {
    Objects.requireNonNull(nextVersion);

    HelmChart helmChart = readChartFile(directory);
    helmChart.setVersion(nextVersion.toString());

    yamlMapper.writeValue(new File(directory.getAbsoluteFile() + File.separator + CHART_YAML_NAME),
        helmChart);
  }

  // Private methods

  /**
   * Utility method to read a Chart.yaml file in a directory.
   *
   * @param directory The directory where is the Chart.yaml. <b>Mandatory</b>
   * @return helm DTO {@link HelmChart}
   * @throws IOException If an error occurred when accessing files or directory.
   */
  private HelmChart readChartFile(File directory) throws IOException {
    Objects.requireNonNull(directory);

    // Convert Chart.yaml to a DTO
    return yamlMapper.readValue(
        new File(directory.getAbsoluteFile() + File.separator + CHART_YAML_NAME), HelmChart.class);
  }
}
