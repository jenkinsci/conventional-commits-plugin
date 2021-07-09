package io.jenkins.plugins.conventionalcommits.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ProjectTypeFactory {

    static Map<String, ProjectType> projectTypeMap = new HashMap<>();
    static {
        projectTypeMap.put("maven", new MavenProjectType());
        projectTypeMap.put("gradle", new GradleProjectType());
        projectTypeMap.put("make", new MakeProjectType());
        projectTypeMap.put("npm", new NpmProjectType());
    }

    public static ProjectType getProjectType(File directory) {

        ProjectType projectType = null;

        for (Map.Entry<String,ProjectType> entryProjectType : projectTypeMap.entrySet()){
            ProjectType candidate = entryProjectType.getValue();
            if (candidate.check(directory)){
                projectType = candidate;
                break;
            }
        }

        return projectType;
    }

}
