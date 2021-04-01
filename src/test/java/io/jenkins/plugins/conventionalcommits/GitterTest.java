package io.jenkins.plugins.conventionalcommits;

import io.jenkins.plugins.conventionalcommits.GitterImpl;
import java.util.Arrays;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GitterTest {

    @Test
    public void canDetermineLatestCommitFromList() {
        GitterImpl gitter = new GitterImpl();
        String latestTag = gitter.latestTag(Arrays.asList("0.0.1", "0.0.2", "0.1.0", "1.0.0"));
        assertThat(latestTag, is(notNullValue()));
        assertThat(latestTag, is("1.0.0"));

        latestTag = gitter.latestTag(Arrays.asList("1.0.0", "1.0.0-alpha"));
        assertThat(latestTag, is(notNullValue()));
        assertThat(latestTag, is("1.0.0"));

        latestTag = gitter.latestTag(Arrays.asList());
        assertThat(latestTag, is(nullValue()));
    }
}
