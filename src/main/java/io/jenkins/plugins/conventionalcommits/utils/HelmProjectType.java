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
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;

/**
 * Represent an Helm project type (i.e with a Chart.yaml file).
 */
public class HelmProjectType extends ProjectType {
  // Helm chart's file name
  private static final String CHART_YAML_NAME = "Chart.yaml";
  // Name of the key that store version value in a Chart.yaml
  private static final String VERSION_KEY_IN_CHART = "version";
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
    // Remove --- as start marker
    yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    // Indent array with 2 spaces
    yamlFactory.enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR);
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

    String version = (String) readChartFile(directory).get(VERSION_KEY_IN_CHART);
    if (StringUtils.isNotEmpty(version)) {
      return Version.valueOf((String) readChartFile(directory).get(VERSION_KEY_IN_CHART));
    }
    throw new IOException("Unable to get the version field in chart file.");
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

    Map<String, Object> helmChart = readChartFile(directory);
    if (StringUtils.isNotEmpty((String) helmChart.get(VERSION_KEY_IN_CHART))) {
      helmChart.put(VERSION_KEY_IN_CHART, nextVersion.toString());
      yamlMapper.writeValue(
          new File(directory.getAbsoluteFile() + File.separator + CHART_YAML_NAME),
          helmChart);
    } else {
      throw new IOException("Unable to get the version field in chart file.");
    }
  }

  // Private methods

  /**
   * Utility method to read a Chart.yaml file in a directory.
   *
   * @param directory The directory where is the Chart.yaml. <b>Mandatory</b>
   * @return helm DTO {@link HelmChart}
   * @throws IOException If an error occurred when accessing files or directory.
   */
  private Map<String, Object> readChartFile(File directory) throws IOException {
    Objects.requireNonNull(directory);

    // Convert Chart.yaml to a DTO
    return yamlMapper.readValue(
        new File(directory.getAbsoluteFile() + File.separator + CHART_YAML_NAME), Map.class);
  }
}
