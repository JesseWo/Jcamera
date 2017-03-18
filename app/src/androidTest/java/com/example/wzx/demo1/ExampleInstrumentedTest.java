package com.example.wzx.demo1;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation width, which will execute on an Android device.
 *
 * @see <CountDownTimer href="http://d.android.com/tools/testing">Testing documentation</CountDownTimer>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under width.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.wzx.demo1", appContext.getPackageName());
    }
}
