package com.joaocenoura.hankerrank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author João Rodrigues <jlrodrigues.dev@gmail.com>
 */
@RunWith(JUnitParamsRunner.class)
public abstract class BaseTest {

    private final Class testClass;
    private boolean debug;

    public BaseTest(Class testClass) {
        this.testClass = testClass;
        this.debug = false;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Object[] fetchTestcases() throws IOException {
        debug("--------------------------------------------------------------------------------");
        debug("Parameterizing " + testClass.getSimpleName() + "...");
        String testcasesPath = "/" + testClass.toString()
                .replace("class", "")
                .replace(".", "/")
                .replace(" ", "");

        debug("> scan testcase folder " + testcasesPath);

        List<String> files = IOUtils.readLines(
                getResourceAsStream(testcasesPath),
                StandardCharsets.UTF_8.name());

        debug("> scan found " + files.size() + " files");

        List<Testcase> testcases = new ArrayList<Testcase>();
        int index = 0;
        Testcase testcase = new Testcase();
        for (String filename : files) {
            String file = testcasesPath + "/" + filename;

            if (index == 0) {
                testcase = new Testcase();
                testcase.setTestcaseInput(file);
                testcases.add(testcase);

                index = 1;
            } else if (index == 1) {
                testcase.setTestcaseOutput(file);
                debug("> created new testcase");
                debug(">> [input]  " + testcase.getTestcaseInput());
                debug(">> [output] " + testcase.getTestcaseOutput());
                index = 0;
            }
        }

        debug("Parameterizing completed");
        return testcases.toArray();
    }

    @Test
    @Parameters(method = "fetchTestcases")
    public void performTest(Testcase testcase) throws IOException {
        info("--------------------------------------------------------------------------------");
        debug("Performing test...");
        info("> target class   " + testClass.getName());
        info("> tescase input  " + testcase.getTestcaseInput());
        if (debug) {
            System.out.println(convertInputStreamToString(getResourceAsStream(testcase.getTestcaseInput())));
        }
        info("> tescase output " + testcase.getTestcaseOutput());
        String expectation = convertInputStreamToString(getResourceAsStream(testcase.getTestcaseOutput()));
        if (debug) {
            System.out.println(expectation);
        }

        // backup original stdin & stdout
        InputStream originalIn = System.in;
        final PrintStream originalOut = System.out;

        // change stdin & stdout
        System.setIn(getResourceAsStream(testcase.getTestcaseInput())
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream newOut = new PrintStream(baos);
        System.setOut(newOut);

        try {
            // execute test
            Method method = testClass.getMethod("main", String[].class);
            String[] params = null;
            method.invoke(null, (Object) params);

            System.out.flush();
            // restore stdout
            System.setOut(originalOut);

            // assert effective result with expected result
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            String testResult = convertInputStreamToString(bais);
            if (testResult != null) {
                testResult = testResult.trim();
            }

            debug("> test result\n" + testResult);

            Assert.assertEquals(expectation, testResult);
            info("> test result    PASSED");
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException ex) {
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            String testResult = convertInputStreamToString(bais);
            System.setOut(originalOut);
            System.out.println(testResult);
            Logger.getLogger(BaseTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

//  ############################################################################
//    HELPERS
//  ############################################################################
    private InputStream getResourceAsStream(String resourcePath) {
        return BaseTest.class.getResourceAsStream(resourcePath);
    }

    private String convertInputStreamToString(InputStream is) throws IOException {
        StringWriter w = new StringWriter();
        IOUtils.copy(is, w, StandardCharsets.UTF_8.name());
        return w.toString();
    }

    private String prefix() {
        return "[" + testClass.getSimpleName() + "] ";
    }

    private void info(String log) {
        System.out.println(prefix() + log);
    }

    private void debug(String log) {
        if (debug) {
            info(log);
        }
    }
}
