package io.jenkins.plugins.conventionalcommits.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/** Factory class to support multiple project types. */
public class ProjectTypeFactory {

  static Map<String, ProjectType> projectTypeMap = new HashMap<>();

  static {
    projectTypeMap.put("maven", new MavenProjectType());
    projectTypeMap.put("gradle", new GradleProjectType());
    projectTypeMap.put("make", new MakeProjectType());
    projectTypeMap.put("npm", new NpmProjectType());
    projectTypeMap.put("python", new PythonProjectType());
    projectTypeMap.put("helm", new HelmProjectType());
    projectTypeMap.put("go", new GoProjectType());
    projectTypeMap.put("php", new PhpProjectType());
  }

  /**
   * Detects &amp; returns the type of the project.
   *
   * @param directory The project's directory.
   * @return Detected project type.
   */
  public static ProjectType getProjectType(File directory) {

    ProjectType projectType = null;

    for (Map.Entry<String, ProjectType> entryProjectType : projectTypeMap.entrySet()) {
      ProjectType candidate = entryProjectType.getValue();
      if (candidate.check(directory)) {
        projectType = candidate;
        break;
      }
    }

    return projectType;
  }
}
