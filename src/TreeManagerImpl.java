import java.io.*;
import java.security.InvalidKeyException;

public class TreeManagerImpl implements TreeManager {
    private TreeProcessor treeProcessor;
    private TreeIO treeIO;

    @Override
    public void loadTrees(File file1, File file2) throws InvalidInputException, IOException {
        try {
            this.treeProcessor.putTree1(this.treeIO.loadTree(file1, "1_"));
            this.treeProcessor.putTree2(this.treeIO.loadTree(file2, "2_"));
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new InvalidInputException();
        }
    }

    @Override
    public boolean saveTrees(File file1, File file2) {
        return (treeIO.saveTree(file1, this.treeProcessor.getTree1()) && treeIO.saveTree(file2, this.treeProcessor.getTree2()));
    }

    @Override
    public Tree getTree1() {
        return this.treeProcessor.getTree1();
    }

    @Override
    public Tree getTree2() {
        return this.treeProcessor.getTree2();
    }

    @Override
    public void swap(String subtreeName1, String subtreeName2) throws InvalidKeyException {
        this.treeProcessor.swap(subtreeName1, subtreeName2);
    }

    public TreeManagerImpl(TreeProcessor treeProcessor, TreeIO treeIO){
        this.treeProcessor = treeProcessor;
        this.treeIO = treeIO;
    }
}
