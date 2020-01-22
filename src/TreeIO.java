import java.io.File;
import java.io.IOException;

public interface TreeIO {
    Tree loadTree(File file, String prefix) throws IOException;
    boolean saveTree(File file, Tree tree);
}
