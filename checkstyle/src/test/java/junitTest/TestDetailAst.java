package junitTest;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

class TestDetailAst {
	
	@Test
	void testInitializeAst() {
        final DetailAstImpl ast1 = new DetailAstImpl();
        ast1.setText("Hello");
        ast1.setType(1);
        ast1.setLineNo(5);
        ast1.setColumnNo(10);

        final DetailAstImpl ast2 = new DetailAstImpl();
        ast2.initialize(ast1);

        assertEquals("Hello", ast2.getText(), "text not equal");
        assertEquals(1, ast2.getType(), "type not equal");
        assertEquals(5, ast2.getLineNo(), "line number not equal");
        assertEquals(10, ast2.getColumnNo(), "column number not equal");
	}
	
	@Test
	 void testSetFirstChild() throws Exception {
        final DetailAstImpl parent = new DetailAstImpl();
        final DetailAstImpl child = new DetailAstImpl();

        parent.setFirstChild(child);


        assertEquals(child, parent.getFirstChild(), "child not set to first child");
        assertEquals(parent, child.getParent(), "parent not set for child");

    }
	
    @Test
     void testSetSiblingNull() throws Exception {
        final DetailAstImpl parent = new DetailAstImpl();
        final DetailAstImpl child = new DetailAstImpl();
        
        parent.setFirstChild(child);
        assertEquals(1, parent.getChildCount(), "Invalid child count");

        child.addPreviousSibling(null);
        child.addNextSibling(null);

        assertEquals(1, parent.getChildCount(), "Invalid child count");
        }
    
    @Test
     void testSetSibling() throws Exception {
         DetailAstImpl parent = new DetailAstImpl();
        final DetailAstImpl child1 = new DetailAstImpl();
        final DetailAstImpl child2 = new DetailAstImpl();
        
        parent.setFirstChild(child1);
        
        assertEquals(1, parent.getChildCount(), "Invalid child count");
        parent.addChild(child2);
        child1.setNextSibling(child2);

        assertEquals(2, parent.getChildCount(), "Invalid child count");
        assertEquals(child2,child1.getNextSibling(), "child2 not set as next sibling");
        }
    
    
    @Test
     void testAddPreviousSiblingNullParent() {
        final DetailAstImpl child = new DetailAstImpl();
        final DetailAST newSibling = new DetailAstImpl();

        child.addPreviousSibling(newSibling);

        assertEquals(child, newSibling.getNextSibling(), "Invalid child token");
        assertEquals(newSibling, child.getPreviousSibling(), "Invalid child token");
    }
    
    @Test
     void testAddPreviousSibling() {
        
        final DetailAstImpl child = new DetailAstImpl();
        final DetailAstImpl parent = new DetailAstImpl();
        final DetailAST previousSibling = new DetailAstImpl();

        parent.setFirstChild(child);

        child.addPreviousSibling(previousSibling);

        assertEquals(previousSibling, child.getPreviousSibling(), "previousSibling did not set");
        assertEquals(previousSibling, parent.getFirstChild(), "previousSibling not set as first child");

        final DetailAST newPreviousSibling = new DetailAstImpl();

        child.addPreviousSibling(newPreviousSibling);

        assertEquals(newPreviousSibling, child.getPreviousSibling(), "new previousSibling did not set");
        assertEquals(previousSibling, newPreviousSibling.getPreviousSibling(), "old previousSibling did not set as previous sibling of new previous sibling");
        assertEquals(newPreviousSibling, previousSibling.getNextSibling(), "old previuos sibling did not set as next sibling of newPrevious sibling");
        assertEquals(previousSibling, parent.getFirstChild(), "previous sibling is not first child any more");
    }
    
    @Test
     void testAddNextSibling() {
        final DetailAstImpl parent = new DetailAstImpl();
        final DetailAstImpl child = new DetailAstImpl();
        final DetailAstImpl sibling = new DetailAstImpl();
        final DetailAST newSibling = new DetailAstImpl();
        parent.setFirstChild(child);
        child.setNextSibling(sibling);
        child.addNextSibling(newSibling);

        assertEquals(parent, newSibling.getParent(), "parent did not set for newSibling");
        assertEquals(sibling, newSibling.getNextSibling(), "old Sibling did not set as nextSibling of new Sibling");
        assertEquals(newSibling, child.getNextSibling(), "new Sibling did not set as nextSibling of child");
    }

    @Test
     void testAddNextSiblingNullParent() {
        final DetailAstImpl child = new DetailAstImpl();
        final DetailAstImpl newSibling = new DetailAstImpl();
        final DetailAstImpl oldParent = new DetailAstImpl();
        oldParent.addChild(newSibling);
        child.addNextSibling(newSibling);

        assertEquals(oldParent, newSibling.getParent(), "parent did not set");
        assertNull(newSibling.getNextSibling(), "next sibling does have next Sibling");
        assertEquals(newSibling, child.getNextSibling(), "child doesn't have next Sibling");
    }
    
    @Test
    public void testInsertSiblingBetween() throws Exception {
        final DetailAstImpl root = new DetailAstImpl();
        final DetailAstImpl child1 = new DetailAstImpl();
        final DetailAST child2 = new DetailAstImpl();
        final DetailAST child3 = new DetailAstImpl();

        assertEquals(0, root.getChildCount(), "root is a parent");
        root.setFirstChild(child1);
        assertEquals(1, root.getChildCount(), "root did not set as parent");
        child1.addNextSibling(child2);
        assertEquals(child2, child1.getNextSibling(), "child2 did not set as nextSibling of child1");
        child1.addNextSibling(child3);
        assertEquals(child3, child1.getNextSibling(), "child3 did not set as nextSibling of child1");
        assertEquals(child2,child3.getNextSibling(), "child2 did not set as nextSibling of child2");
    }
    
    @Test
     void testAddChildNull() {
    	final DetailAstImpl root = new DetailAstImpl();
    	root.addChild(null);
    	assertEquals(0, root.getChildCount(), "root is a parent");    	
    } 
    
    @Test
    void testAddChild() {
    	final DetailAstImpl parent = new DetailAstImpl();
    	 final DetailAstImpl child1 = new DetailAstImpl();
         final DetailAstImpl child2 = new DetailAstImpl();       
         parent.addChild(child1);
         parent.addChild(child2);
         assertEquals(2, parent.getChildCount(), "parent doesn't have child");
         
    }
    
    @Test
	void testSetParent() throws Exception{
    	final DetailAstImpl parent = new DetailAstImpl();
    	final DetailAstImpl child = new DetailAstImpl();
    	final Method setParentMethod = getSetParentMethod();
        setParentMethod.invoke(child, parent);
        assertEquals(parent, child.getParent(), "parent did not set");
    	
	}

    // This method helps to call private method from DetailAstImpl class
    private static Method getSetParentMethod() throws Exception {
        final Class<DetailAstImpl> detailAstClass = DetailAstImpl.class;
        final Method setParentMethod =
            detailAstClass.getDeclaredMethod("setParent", DetailAstImpl.class);
        setParentMethod.setAccessible(true);
        return setParentMethod;
    }
}
														