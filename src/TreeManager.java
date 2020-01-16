public interface TreeManager {
    void loadTrees(String def1, String def2);
    void saveTrees(String filename1, String filename2);

    Tree getTree1();
    Tree getTree2();

    // swaps subtrees, making sure that they are on different trees
    void swap(String subtreeName1, String subtreeName2);
}
