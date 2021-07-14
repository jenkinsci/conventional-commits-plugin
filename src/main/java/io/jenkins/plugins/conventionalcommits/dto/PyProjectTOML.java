package io.jenkins.plugins.conventionalcommits.dto;

import com.moandjiezana.toml.Toml;

import java.io.File;

/** DTO to represent a pyproject.toml file. */
public class PyProjectTOML {

  public String getVersion(String TOMLFilePath) {
    Toml tomlFile = new Toml().read(new File(TOMLFilePath));

    Toml project = tomlFile.getTable("project");
    return project.getString("version");
  }
}
