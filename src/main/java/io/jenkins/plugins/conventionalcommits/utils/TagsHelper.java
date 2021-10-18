package io.jenkins.plugins.conventionalcommits.utils;

import static io.jenkins.plugins.conventionalcommits.process.ProcessUtil.execute;

import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.jenkinsci.plugins.workflow.steps.StepContext;

/**
 * Helper to handle tags.
 */
public class TagsHelper {
  /**
   * Return the last tag.
   *
   * @param context                 Jenkins context.
   * @param dir                     The project's directory.
   * @param includeNonAnnotatedTags If true include the non annotated tag.
   * @return The last tag of the project.
   */
  public static String getLatestTag(StepContext context, File dir, boolean includeNonAnnotatedTags)
      throws InterruptedException, IOException {
    Objects.requireNonNull(dir, "Directory is mandatory");
    Objects.requireNonNull(context, "Context is mandatory");

    String latestTag = "";
    try {
      if (includeNonAnnotatedTags) {
        latestTag = execute(dir, "git", "tag", "-l").trim();
        latestTag = latestTag.substring(latestTag.lastIndexOf("\n") + 1);
      } else {
        latestTag = execute(dir, "git", "describe", "--abbrev=0", "--tags").trim();
      }
    } catch (IOException exp) {
      if (exp.getMessage().contains("No names found, cannot describe anything.")) {
        context.get(TaskListener.class).getLogger().println("No tags found");
      }
    }

    context.get(TaskListener.class).getLogger().println("Current Tag is: " + latestTag);
    return latestTag;
  }

}
