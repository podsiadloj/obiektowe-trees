public class TreeProcessorImpl implements TreeProcessor {
    Tree tree1;
    Tree tree2;

    @Override
    public Tree getTree1() {
        return tree1;
    }
    @Override
    public Tree getTree2() {
        return tree2;
    }

    @Override
    public void putTree1(Tree tree) {
        this.tree1 = tree;
    }
    @Override
    public void putTree2(Tree tree) {
        this.tree2 = tree;
    }

    @Override
    public void swap(String subtreeName1, String subtreeName2) {
        Tree p1 = tree1.getSubtree(subtreeName1).parent;
        Tree p2 = tree2.getSubtree(subtreeName2).parent;
        Tree s1 = p1.removeChild(subtreeName1);
        Tree s2 = p2.removeChild(subtreeName2);
        p2.addChild(s1);
        p1.addChild(s2);
    }
}
