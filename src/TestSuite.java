import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestSuite {

    private static TreeManager tm;

    @BeforeAll
    static void setup() throws IOException
    {
        File t1 = new File("testTree1.txt");
        File t2 = new File("testTree2.txt");
        if(t1.exists()){t1.delete();}
        if(t2.exists()){t2.delete();}
        PrintWriter writer1 = new PrintWriter("testTree1.txt", StandardCharsets.UTF_8);
        writer1.println("@tasks 7");
        writer1.println("");
        writer1.println("1 2 2 3");
        writer1.println("2 3 4 5 6");
        writer1.println("3 1 7");
        writer1.println("4 0");
        writer1.println("5 0");
        writer1.println("6 0");
        writer1.println("7 0");
        writer1.println("");
        writer1.println("@proc 5");
        writer1.println("lorem ipsum dolor sit amet...");
        writer1.close();

        PrintWriter writer2 = new PrintWriter("testTree2.txt", StandardCharsets.UTF_8);
        writer2.println("@tasks 15");
        writer2.println("1 3 2, 3, 4");
        writer2.println("2 2 5, 6");
        writer2.println("3 0");
        writer2.println("4 3 7, 8, 9");
        writer2.println("5 0");
        writer2.println("6 0");
        writer2.println("7 1 10");
        writer2.println("8 1 11");
        writer2.println("9 0");
        writer2.println("10 3 12, 13, 14");
        writer2.println("11 0");
        writer2.println("12 0");
        writer2.println("13 1 15");
        writer2.println("14 0");
        writer2.println("15 0");
        writer2.println("@proc 5");
        writer2.println("lorem ipsum dolor sit amet...");
        writer2.close();
        tm = new TreeManagerImpl(new TreeProcessorImpl(), new TreeIOImpl());
    }

    @Order(1)
    @Test
    void loadTrees() {
        try{
            tm.loadTrees(new File("testTree1.txt"), new File("testTree2.txt"));
        } catch (Exception e) {
            fail("Loading trees failed:" + e.getMessage());
        }
    }

    @Order(2)
    @Test
    void getTree1() {
        Tree tree = tm.getTree1();
        assertNotNull(tree);
        assertEquals(tree.children.size(), 2);
        assertEquals(tree.name, "1_1");
        assertNotNull(tree.getSubtree("1_2"));
        assertEquals(tree.getSubtree("1_2").children.size(), 3);
        assertNotNull(tree.getSubtree("1_3"));
        assertEquals(tree.getSubtree("1_3").children.size(), 1);
        assertEquals(tree.getSubtree("1_3").children.get(0).name, "1_7");
        assertNotNull(tree.getSubtree("1_6"));
        assertEquals(tree.getSubtree("1_6").children.size(), 0);
        assertEquals(tree.getSubtree("1_6").parent.name, "1_2");
        assertNull(tree.getSubtree("1_8"));
        assertNull(tree.getSubtree("2_10"));
        assertNull(tree.getSubtree("1"));
    }

    @Order(3)
    @Test
    void getTree2() {
        Tree tree = tm.getTree2();
        assertNotNull(tree);
        assertEquals(tree.children.size(), 3);
        assertEquals(tree.name, "2_1");
        assertNotNull(tree.getSubtree("2_10"));
        assertEquals(tree.getSubtree("2_10").children.size(), 3);
        assertEquals(tree.getSubtree("2_10").parent.name, "2_7");
        assertNotNull(tree.getSubtree("2_13"));
        assertEquals(tree.getSubtree("2_13").children.get(0).name, "2_15");
        assertEquals(tree.getSubtree("2_13").parent.name, "2_10");
        assertNull(tree.getSubtree("1_2"));
        assertNull(tree.getSubtree("1"));
        assertNull(tree.getSubtree("2_16"));
    }

    @Order(4)
    @Test
    void swap() {
        tm.swap("1_2", "2_10");
        Tree tree1 = tm.getTree1();
        Tree tree2 = tm.getTree2();
        assertNotNull(tree1.getSubtree("2_10"));
        assertEquals(tree1.getSubtree("2_10").parent.name, "1_1");
        assertTrue(tree1.getSubtree("2_10").children.stream().anyMatch(t->t.name.equals("2_13")));
        assertTrue(tree1.children.stream().anyMatch(t -> t.name.equals("2_10")));
        assertNull(tree1.getSubtree("1_2"));
        assertNotNull(tree2.getSubtree("1_2"));
        assertEquals(tree2.getSubtree("1_2").parent.name, "2_7");
        assertTrue(tree2.getSubtree("1_2").children.stream().anyMatch(t->t.name.equals("1_5")));
        assertTrue(tree2.getSubtree("2_7").children.stream().anyMatch(t -> t.name.equals("1_2")));
        assertNull(tree2.getSubtree("2_10"));
    }

    @Order(5)
    @Test
    void saveTrees() {
        tm.saveTrees(new File("testTree1.txt"), new File("testTree2.txt"));
        try{
            tm.loadTrees(new File("testTree1.txt"), new File("testTree2.txt"));
        } catch (Exception e) {
            fail("Loading trees failed:" + e.getMessage());
        }
        Tree tree1 = tm.getTree1();
        Tree tree2 = tm.getTree2();
        assertNotNull(tree1.getSubtree("1_2_10"));
        assertEquals(tree1.getSubtree("1_2_10").parent.name, "1_1_1");
        assertTrue(tree1.getSubtree("1_2_10").children.stream().anyMatch(t->t.name.equals("1_2_13")));
        assertTrue(tree1.children.stream().anyMatch(t -> t.name.equals("1_2_10")));
        assertNull(tree1.getSubtree("1_1_2"));
        assertNotNull(tree2.getSubtree("2_1_2"));
        assertEquals(tree2.getSubtree("2_1_2").parent.name, "2_2_7");
        assertTrue(tree2.getSubtree("2_1_2").children.stream().anyMatch(t->t.name.equals("2_1_5")));
        assertTrue(tree2.getSubtree("2_2_7").children.stream().anyMatch(t -> t.name.equals("2_1_2")));
        assertNull(tree2.getSubtree("2_2_10"));
    }
}