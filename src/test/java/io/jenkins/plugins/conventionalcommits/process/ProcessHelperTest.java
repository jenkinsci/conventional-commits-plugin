package io.jenkins.plugins.conventionalcommits.process;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessHelperTest {

    @Mock
    private ProcessHelper helper;

    @Test
    public void testCanMockOutProcessBuilder() {
        assertThat(helper, is(notNullValue()));
        when(helper.runCommand("run", "this", "command")).thenReturn("bingbong");
        String stdOut = helper.runCommand("run", "this", "command");
        assertThat(stdOut, is(notNullValue()));
        assertThat(stdOut, is("bingbong"));
    }

    @Test
    public void testCanMockOutProcessBuilderShouldFail() {
        assertThat(helper, is(notNullValue()));
        when(helper.runCommand("run", "this", "command")).thenThrow(new RuntimeException("dfgdfgdfgd"));
        try {
            helper.runCommand("run", "this", "command");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeException e) {
            // do nothing
        }
    }
}
