import java.security.InvalidKeyException;

public interface TreeProcessor {

    Tree getTree1();
    Tree getTree2();

    void putTree1(Tree tree);
    void putTree2(Tree tree);

    void swap(String subtreeName1, String subtreeName2) throws InvalidKeyException;
}
