public interface TreeManager {
    void loadTrees(String def1, String def2) throws InvalidInputException;
    boolean saveTrees(String filename1, String filename2);

    Tree getTree1();
    Tree getTree2();

    // swaps subtrees, making sure that they are on different trees
    void swap(String subtreeName1, String subtreeName2);

    class InvalidInputException extends Exception {}
}
