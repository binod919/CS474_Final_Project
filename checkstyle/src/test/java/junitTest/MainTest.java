package junitTest;

import com.puppycrawl.tools.checkstyle.Main;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.jupiter.api.Test;
import java.io.IOException;

class MainTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    void testGetVersionString() throws IOException {
        Main.main("-V");
    }

    @Test
    void testPrintUsageInformation() throws IOException{
        Main.main("--help");
    }

    @Test
    void testWithValidArgs() throws  IOException{
        exit.expectSystemExit();
        String[] args = new String[]{"-c", "src/main/resources/sun_checks.xml", "../testfile/Demo.java"};
        Main.main(args);
    }
}