package io.jenkins.plugins.conventionalcommits;

import hudson.model.Result;
import java.net.URL;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class JenkinsTest {

    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void testPipelineWithNoTags() throws Exception {
        WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
        URL zipFile = getClass().getResource("simple-project-with-notags.zip");
        assertThat(zipFile, is(notNullValue()));

        p.setDefinition(new CpsFlowDefinition("node {\n"
                                              + "  unzip '" + zipFile.getPath() + "'\n"
                                              + "  nextVersion()\n"
                                              + "}\n", true));

        WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

        System.out.println(rule.getLog(b));

        assertThat(rule.getLog(b), containsString("Started"));
        assertThat(rule.getLog(b), containsString("nextVersion"));
        assertThat(rule.getLog(b), containsString("No tags found"));
        assertThat(rule.getLog(b), containsString("0.1.0"));
        assertThat(rule.getLog(b), containsString("Finished: SUCCESS"));
    }

    @Test
    public void testPipelineWithTags() throws Exception {
        WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
        URL zipFile = getClass().getResource("simple-project-with-tags.zip");
        assertThat(zipFile, is(notNullValue()));

        p.setDefinition(new CpsFlowDefinition("node {\n"
                                              + "  unzip '" + zipFile.getPath() + "'\n"
                                              + "  nextVersion()\n"
                                              + "}\n", true));

        WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

        System.out.println(rule.getLog(b));

        assertThat(rule.getLog(b), containsString("Started"));
        assertThat(rule.getLog(b), containsString("nextVersion"));
        assertThat(rule.getLog(b), containsString("Current Tag is: 0.1.0"));
        assertThat(rule.getLog(b), containsString("0.1.1"));
        assertThat(rule.getLog(b), containsString("Finished: SUCCESS"));
    }

    @Test
    public void testDeclarativePipelineWithTags() throws Exception {
        WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
        URL zipFile = getClass().getResource("simple-project-with-tags.zip");
        assertThat(zipFile, is(notNullValue()));

        p.setDefinition(new CpsFlowDefinition("pipeline { \n"
                                              + "  agent any\n"
                                              + "  stages {\n"
                                              + "    stage('Stage') {\n"
                                              + "      steps {\n"
                                              + "        unzip '" + zipFile.getPath() + "'\n"
                                              + "        nextVersion()\n"
                                              + "      }\n"
                                              + "    }\n"
                                              + "  }\n"
                                              + "}\n", true));

        WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

        System.out.println(rule.getLog(b));

        assertThat(rule.getLog(b), containsString("Started"));
        assertThat(rule.getLog(b), containsString("nextVersion"));
        assertThat(rule.getLog(b), containsString("Current Tag is: 0.1.0"));
        assertThat(rule.getLog(b), containsString("0.1.1"));
        assertThat(rule.getLog(b), containsString("Finished: SUCCESS"));
    }

    @Test
    public void testDeclarativePipelineWithTagsInEnvironmentBlock() throws Exception {
        WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "p");
        URL zipFile = getClass().getResource("simple-project-with-tags.zip");
        assertThat(zipFile, is(notNullValue()));

        p.setDefinition(new CpsFlowDefinition("pipeline { \n"
                                              + "  agent any\n"
                                              + "  stages {\n"
                                              + "    stage('Extract') {\n"
                                              + "      steps {\n"
                                              + "        unzip '" + zipFile.getPath() + "'\n"
                                              + "      }\n"
                                              + "    }\n"
                                              + "    stage('Version') {\n"
                                              + "      environment {\n"
                                              + "        NEXT_VERSION = nextVersion()"
                                              + "      }\n"
                                              + "      steps {\n"
                                              + "        echo \"next version = ${NEXT_VERSION}\"\n"
                                              + "      }\n"
                                              + "    }\n"
                                              + "  }\n"
                                              + "}\n", true));

        WorkflowRun b = rule.assertBuildStatus(Result.SUCCESS, p.scheduleBuild2(0).get());

        System.out.println(rule.getLog(b));

        assertThat(rule.getLog(b), containsString("Started"));
        assertThat(rule.getLog(b), containsString("nextVersion"));
        assertThat(rule.getLog(b), containsString("Current Tag is: 0.1.0"));
        assertThat(rule.getLog(b), containsString("next version = 0.1.1"));
        assertThat(rule.getLog(b), containsString("Finished: SUCCESS"));
    }
}
