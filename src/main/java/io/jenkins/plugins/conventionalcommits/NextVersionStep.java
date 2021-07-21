package io.jenkins.plugins.conventionalcommits;

import com.github.zafarkhaja.semver.Version;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.LineReader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.conventionalcommits.utils.CurrentVersion;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Base class of the plugin.
 */
public class NextVersionStep extends Step {

  private String outputFormat;
  private String startTag;
  // Pre release information (optional)
  private String preRelease;

  @DataBoundConstructor
  public NextVersionStep() {
    // empty constructor, for now...
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
   * Reads data from stdout.
   *
   * @param in InputStream object.
   * @return read data.
   * @throws IOException If an error occur reading files.
   */
  public static String stdout(InputStream in) throws IOException {
    StringBuilder builder = new StringBuilder();
    LineReader reader = new LineReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      builder.append(line);
      builder.append(System.getProperty("line.separator"));
    }
    return builder.toString();
  }

  @DataBoundSetter
  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  @DataBoundSetter
  public void setStartTag(String startTag) {
    this.startTag = startTag;
  }

  @DataBoundSetter
  public void setPreRelease(String preRelease) {
    this.preRelease = preRelease;
  }

  @Override
  public StepExecution start(StepContext stepContext) throws Exception {
    return new Execution(outputFormat, startTag, preRelease, stepContext);
  }

  /**
   * This class extends Step Execution class, contains the run method.
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

    @SuppressFBWarnings(
        value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
        justification = "Only used when starting.")
    // Pre release information to add to the next version
    private final transient String preRelease;

    /**
     * Constructor with fields initialisation.
     * @param outputFormat Output format for the next version
     * @param startTag Git tag
     * @param preRelease Pre release information to add
     * @param context Jenkins context
     */
    protected Execution(String outputFormat, String startTag, String preRelease,
                        @Nonnull StepContext context) {
      super(context);
      this.outputFormat = outputFormat;
      this.startTag = startTag;
      this.preRelease = preRelease;
    }

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
        // git describe --abbrev=0 --tags
        String latestTag = "";
        try {
          latestTag = execute(dir, "git", "describe", "--abbrev=0", "--tags").trim();
          getContext().get(TaskListener.class).getLogger().println("Current Tag is: " + latestTag);
        } catch (IOException exp) {
          if (exp.getMessage().contains("No names found, cannot describe anything.")) {
            getContext().get(TaskListener.class).getLogger().println("No tags found");
          }
        }

        Version currentVersion = new CurrentVersion().getCurrentVersion(dir, latestTag);

        String commitMessagesString = null;
        if (latestTag.isEmpty()) {
          commitMessagesString = execute(dir, "git", "log", "--pretty=format:%s").trim();
        } else {
          // FIXME get a list of commits between 'this' and the tag
          // git log --pretty=format:%s tag..HEAD
          commitMessagesString =
              execute(dir, "git", "log", "--pretty=format:%s", latestTag + "..HEAD").trim();
        }

        List<String> commitHistory = Arrays.asList(commitMessagesString.split("\n"));

        // based on the commit list, determine how to bump the version
        Version nextVersion = new ConventionalCommits().nextVersion(currentVersion, commitHistory);

        // If pre-release information, add it
        if (StringUtils.isNotBlank(preRelease)) {
          nextVersion = nextVersion.setPreReleaseVersion(preRelease);
        }

        // TODO write the version using the output template
        getContext().get(TaskListener.class).getLogger().println(nextVersion);

        return nextVersion.toString();
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
      return "Next Version: determine the next version from the conventional commit history";
    }

    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return ImmutableSet.of(TaskListener.class, FilePath.class);
    }

    @Override
    public String getFunctionName() {
      return "nextVersion";
    }
  }
}
