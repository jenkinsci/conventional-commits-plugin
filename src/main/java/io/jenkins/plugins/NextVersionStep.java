package io.jenkins.plugins;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
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
import com.github.zafarkhaja.semver.Version;

public class NextVersionStep extends Step {

    private String outputFormat;
    private String startTag;

    @DataBoundConstructor
    public NextVersionStep() {
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
        return new Execution(outputFormat,
                startTag,
                stepContext);
    }

    public static class Execution extends SynchronousStepExecution<String> {

        private static final long serialVersionUID = 1L;

        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
        private transient final String outputFormat;

        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
        private transient final String startTag;

        protected Execution(String outputFormat, String startTag, @Nonnull StepContext context) {
            super(context);
            this.outputFormat = outputFormat;
            this.startTag = startTag;
        }

        @Override
        protected String run() throws Exception {
            Gitter git = new GitterImpl();

            String latestTag = null;
            if (StringUtils.isNotEmpty(startTag)) {
                latestTag = startTag;
            } else {
                latestTag = git.latestTag();
            }

            getContext().get(TaskListener.class).getLogger().println("Current Tag is: " + latestTag);

            // TODO get a list of commits between 'this' and the tag
            List<String> commits = git.commits(latestTag);
            for (String commit: commits) {
                getContext().get(TaskListener.class).getLogger().println("Commit: " + commit);
            }

            if (latestTag == null) {
                getContext().get(TaskListener.class).getLogger().println("Setting current version to 0.0.0 as this appears to be the first release");
            }
            Version currentVersion = Version.valueOf(latestTag == null ? "0.0.0" : latestTag);

            // based on the commit list, determine how to bump the version
            Version nextVersion = new ConventionalCommits().nextVersion(currentVersion, commits);

            // TODO write the version using the output template

            getContext().get(TaskListener.class).getLogger().println(nextVersion);

            return nextVersion.toString();
        }

    }

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
