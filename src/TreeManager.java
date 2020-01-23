import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;

public interface TreeManager {

    void loadTrees(File file1, File file2) throws InvalidInputException, IOException;
    boolean saveTrees(File file1, File file2);

    Tree getTree1();
    Tree getTree2();

    // swaps subtrees
    void swap(String subtreeName1, String subtreeName2) throws InvalidKeyException;

    class InvalidInputException extends Exception {}
}
