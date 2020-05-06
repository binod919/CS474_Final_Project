package junitTest;

import static com.puppycrawl.tools.checkstyle.checks.naming.AbstractNameCheck.MSG_INVALID_PATTERN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.internal.util.Checks;
import org.powermock.reflect.Whitebox;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;
import com.puppycrawl.tools.checkstyle.XpathFileGeneratorAstFilter;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.Context;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.blocks.LeftCurlyCheck;
import com.puppycrawl.tools.checkstyle.checks.coding.EmptyStatementCheck;
import com.puppycrawl.tools.checkstyle.checks.coding.HiddenFieldCheck;
import com.puppycrawl.tools.checkstyle.checks.indentation.CommentsIndentationCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocPackageCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocParagraphCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.ConstantNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.MemberNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.TypeNameCheck;
import com.puppycrawl.tools.checkstyle.filters.SuppressWithNearbyCommentFilter;
import com.puppycrawl.tools.checkstyle.filters.SuppressionCommentFilter;
import com.puppycrawl.tools.checkstyle.filters.SuppressionXpathFilter;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class TestTreeWalker extends AbstractModuleTestSupport {

    @TempDir
    public File temporaryFolder;

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/treewalker";
    }



    @Test
    public void testSetupChildExceptions() {
        final TreeWalker treeWalker = new TreeWalker();
        final PackageObjectFactory factory = new PackageObjectFactory(
                new HashSet<>(), Thread.currentThread().getContextClassLoader());
        treeWalker.setModuleFactory(factory);

        final Configuration config = new DefaultConfiguration("java.lang.String");
        try {
            treeWalker.setupChild(config);
            fail("Exception is expected");
        }
        catch (CheckstyleException ex) {
            assertEquals("TreeWalker is not allowed as a parent of java.lang.String Please review "
                    + "'Parent Module' section for this Check in web documentation if "
                    + "Check is standard.", ex.getMessage(), "Error message is not expected");
        }
    }


    @Test
    public void testSetupChild() throws Exception {
        final TreeWalker treeWalker = new TreeWalker();
        final PackageObjectFactory factory = new PackageObjectFactory(
                new HashSet<>(), Thread.currentThread().getContextClassLoader());
        treeWalker.setModuleFactory(factory);
        treeWalker.setTabWidth(50);
        treeWalker.finishLocalSetup();

        final Configuration config = new DefaultConfiguration(
                XpathFileGeneratorAstFilter.class.getName());

        treeWalker.setupChild(config);

        final Set<TreeWalkerFilter> filters = Whitebox.getInternalState(treeWalker, "filters");
        final int tabWidth = Whitebox.getInternalState(filters.iterator().next(), "tabWidth");

        assertEquals(50, tabWidth, "expected tab width");
    }
    
    @Test
    public void testSetupChild2() throws Exception {
        final TreeWalker treeWalker = new TreeWalker();
        final PackageObjectFactory factory = new PackageObjectFactory(
                new HashSet<>(), Thread.currentThread().getContextClassLoader());
        treeWalker.setModuleFactory(factory);
        treeWalker.setTabWidth(0);
        treeWalker.finishLocalSetup();

        final Configuration config = new DefaultConfiguration(
                XpathFileGeneratorAstFilter.class.getName());

        treeWalker.setupChild(config);

        final Set<TreeWalkerFilter> filters = Whitebox.getInternalState(treeWalker, "filters");
        final int tabWidth = Whitebox.getInternalState(filters.iterator().next(), "tabWidth");

        assertEquals(0, tabWidth, "expected tab width");
    }
    @Test
    public void testSetupChild3() throws Exception {
        final TreeWalker treeWalker = new TreeWalker();
        final PackageObjectFactory factory = new PackageObjectFactory(
                new HashSet<>(), Thread.currentThread().getContextClassLoader());
        treeWalker.setModuleFactory(factory);
        treeWalker.setTabWidth(-25);
        treeWalker.finishLocalSetup();

        final Configuration config = new DefaultConfiguration(
                XpathFileGeneratorAstFilter.class.getName());

        treeWalker.setupChild(config);

        final Set<TreeWalkerFilter> filters = Whitebox.getInternalState(treeWalker, "filters");
        final int tabWidth = Whitebox.getInternalState(filters.iterator().next(), "tabWidth");

        assertEquals(-25, tabWidth, "expected tab width");
    }


    @Test
    public void testFinishLocalSetupFullyInitialized() {
        final TreeWalker treeWalker = new TreeWalker();
        treeWalker.setSeverity("error");
        treeWalker.setTabWidth(75);
        treeWalker.finishLocalSetup();

        final Context context = Whitebox.getInternalState(treeWalker, "childContext");
        assertEquals("error", context.get("severity"), "Severity differs from expected");
        assertEquals(String.valueOf(75), context.get("tabWidth"),
                "Tab width differs from expected");
    }
    

    @Test
    public void testFinishLocalSetupFullyInitialized2() {
        final TreeWalker treeWalker = new TreeWalker();
        treeWalker.setSeverity("error");
        treeWalker.setTabWidth(0);
        treeWalker.finishLocalSetup();

        final Context context = Whitebox.getInternalState(treeWalker, "childContext");
        assertEquals("error", context.get("severity"), "Severity differs from expected");
        assertEquals(String.valueOf(0), context.get("tabWidth"),
                "Tab width differs from expected");
    }

    @Test
    public void testFinishLocalSetupFullyInitialized3() {
        final TreeWalker treeWalker = new TreeWalker();
        treeWalker.setSeverity("error");
        treeWalker.setTabWidth(-100);
        treeWalker.finishLocalSetup();

        final Context context = Whitebox.getInternalState(treeWalker, "childContext");
        assertEquals("error", context.get("severity"), "Severity differs from expected");
        assertEquals(String.valueOf(-100), context.get("tabWidth"),
                "Tab width differs from expected");
    }

    public static class BadJavaDocCheck extends AbstractCheck {

        @Override
        public int[] getDefaultTokens() {
            return getAcceptableTokens();
        }

        @Override
        public int[] getAcceptableTokens() {
            return new int[] {TokenTypes.SINGLE_LINE_COMMENT};
        }

        @Override
        public int[] getRequiredTokens() {
            return getAcceptableTokens();
        }

    }

    public static class VerifyInitCheck extends AbstractCheck {

        private static boolean initWasCalled;

        @Override
        public int[] getDefaultTokens() {
            return CommonUtil.EMPTY_INT_ARRAY;
        }

        @Override
        public int[] getAcceptableTokens() {
            return getDefaultTokens();
        }

        @Override
        public int[] getRequiredTokens() {
            return getDefaultTokens();
        }

        @Override
        public void init() {
            super.init();
            initWasCalled = true;
        }

        public static boolean isInitWasCalled() {
            return initWasCalled;
        }

    }

    public static class VerifyDestroyCheck extends AbstractCheck {

        private static boolean destroyWasCalled;

        @Override
        public int[] getDefaultTokens() {
            return CommonUtil.EMPTY_INT_ARRAY;
        }

        @Override
        public int[] getAcceptableTokens() {
            return getDefaultTokens();
        }

        @Override
        public int[] getRequiredTokens() {
            return getDefaultTokens();
        }

        @Override
        public void destroy() {
            super.destroy();
            destroyWasCalled = true;
        }

        public static void resetDestroyWasCalled() {
            destroyWasCalled = false;
        }

        public static boolean isDestroyWasCalled() {
            return destroyWasCalled;
        }

    }

    public static class VerifyDestroyCommentCheck extends VerifyDestroyCheck {

        @Override
        public boolean isCommentNodesRequired() {
            return true;
        }

    }

    public static class RequiredTokenIsEmptyIntArray extends AbstractCheck {

        @Override
        public int[] getRequiredTokens() {
            return CommonUtil.EMPTY_INT_ARRAY;
        }

        @Override
        public int[] getDefaultTokens() {
            return new int[] {TokenTypes.ANNOTATION};
        }

        @Override
        public int[] getAcceptableTokens() {
            return CommonUtil.EMPTY_INT_ARRAY;
        }

    }

}
