package io.jenkins.plugins.conventionalcommits.dto;

import com.moandjiezana.toml.Toml;
import java.io.File;

/** DTO to represent a pyproject.toml file. */
public class PyProjectToml {

  /**
   * Return the next version of the version attribute.
   *
   * @param tomlFilePath The path to the pyproject.toml file.
   * @return Current Version of the project mentioned in the pyproject.toml file.
   */
  public String getVersion(String tomlFilePath) {
    Toml tomlFile = new Toml().read(new File(tomlFilePath));

    Toml project = tomlFile.getTable("project");
    return project.getString("version");
  }
}
