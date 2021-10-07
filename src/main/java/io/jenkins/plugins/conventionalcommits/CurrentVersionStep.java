package io.jenkins.plugins.conventionalcommits;

import static io.jenkins.plugins.conventionalcommits.NextVersionStep.stdout;

import com.github.zafarkhaja.semver.Version;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.conventionalcommits.utils.CurrentVersion;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Step to get the current version of the project.
 * Example :
 * <code>def CURRENT_VERSION = currentVersion()</code>
 */
public class CurrentVersionStep extends Step {

  private String outputFormat;
  private String startTag;

  @DataBoundConstructor
  public CurrentVersionStep() {
    // empty constructor, for now...
  }

  @DataBoundSetter
  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  @DataBoundSetter
  public void setStartTag(String startTag) {
    this.startTag = startTag;
  }

  @Override
  public StepExecution start(StepContext stepContext) throws Exception {
    return new Execution(
        outputFormat,
        startTag,
        stepContext);
  }

  private static String execute(File dir, String... commandAndArgs)
      throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder().directory(dir).command(commandAndArgs);

    Process process = builder.start();
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      String stderr = stdout(process.getErrorStream());
      throw new IOException(
          "executing '"
              + String.join(" ", commandAndArgs)
              + "' failed in '"
              + dir
              + "' with exit code"
              + exitCode
              + " and error "
              + stderr);
    }
    return stdout(process.getInputStream());
  }

  /**
   * This class extends Step Execution class, contains the run method.
   * This is the main entry point of the step.
   */
  public static class Execution extends SynchronousStepExecution<String> {

    private static final long serialVersionUID = 1L;

    @SuppressFBWarnings(
        value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    private final transient String outputFormat;

    @SuppressFBWarnings(
        value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    private final transient String startTag;

    /**
     * Constructor with fields initialisation.
     *
     * @param outputFormat Output format for the next version
     * @param startTag     Git tag
     * @param context      Jenkins context
     */
    protected Execution(String outputFormat, String startTag, @Nonnull StepContext context) {
      super(context);
      this.outputFormat = outputFormat;
      this.startTag = startTag;
    }

    /**
     * Return the last tag.
     *
     * @param dir                     The project's directory.
     * @param includeNonAnnotatedTags If true include the non annotated tag.
     * @return The last tag of the project.
     */
    private String getLatestTag(File dir, boolean includeNonAnnotatedTags)
        throws InterruptedException, IOException {
      Objects.requireNonNull(dir, "Directory is mandatory");
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
          getContext().get(TaskListener.class).getLogger().println("No tags found");
        }
      }

      getContext().get(TaskListener.class).getLogger().println("Current Tag is: " + latestTag);
      return latestTag;
    }

    /**
     * Entry point of the step.
     *
     * @return The current version of the project.
     * @throws Exception If errors occurs ;).
     */
    @Override
    protected String run() throws Exception {
      FilePath workspace = getContext().get(FilePath.class);
      if (workspace == null) {
        throw new IOException("no workspace");
      }

      // if the workspace is remote then lets make a local copy
      if (workspace.isRemote()) {
        throw new IOException("workspace.isRemote(), not entirely sure what to do here...");
      } else {
        File dir = new File(workspace.getRemote());
        String latestTag = getLatestTag(dir, false);

        Version currentVersion =
            new CurrentVersion()
                .getCurrentVersion(
                    dir, latestTag, getContext().get(TaskListener.class).getLogger());


        return currentVersion.toString();
      }
    }
  }

  /**
   * This Class implements the abstract class StepDescriptor.
   */
  @Extension
  public static class DescriptorImpl extends StepDescriptor {

    @Override
    public String getDisplayName() {
      return "Current Version: determine the next version from the conventional commit history";
    }

    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(TaskListener.class, FilePath.class);
    }

    @Override
    public String getFunctionName() {
      return "currentVersion";
    }
  }
}
