package org.cowboyprogrammer.org.util;

import org.junit.Test;

import static org.cowboyprogrammer.org.util.StringUtils.reverse;
import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testReverseNull() throws Exception {
        assertNull(reverse(null));
    }

    @Test
    public void testReverseString() throws Exception {
        assertEquals("cba", reverse("abc"));
    }
}