package hello.servlet;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class TestCode {
    private String string = "abcdefg";

    @Test
    public void copyToStringTest() throws Exception {
        Charset charset = Charset.defaultCharset();
        InputStream inputStream = spy(new ByteArrayInputStream(string.getBytes(charset)));
        String actual = StreamUtils.copyToString(inputStream, charset);
        System.out.println("actual = " + actual);
        MatcherAssert.assertThat(actual, equalTo(string));
        verify(inputStream, never()).close();
    }
}
