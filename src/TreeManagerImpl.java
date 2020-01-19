import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeManagerImpl implements TreeManager {
    Tree tree1;

    Tree tree2;

    @Override
    public void loadTrees(String def1, String def2) throws InvalidInputException {
        try {
            this.tree1 = this.parseTree(def1, "1_");
            this.tree2 = this.parseTree(def2, "2_");
        } catch (Exception e){
            throw new InvalidInputException();
        }
    }

    @Override
    public boolean saveTrees(String filename1, String filename2) {
        return (saveTree(tree1, filename1) && saveTree(tree2, filename2));
    }

    @Override
    public Tree getTree1() {
        return tree1;
    }

    @Override
    public Tree getTree2() {
        return tree2;
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

    private static class TreeLine {
        Tree head;
        List<String> childrenNames = new ArrayList<>();

        TreeLine(String input, String prefix){
            String[] words = input.trim().split(" ");
            head = new Tree(prefix + words[0]);
            if(words[1] != null ){
                for(int i = 2; i < (2 + Integer.parseInt(words[1])); i++){
                    childrenNames.add(prefix + words[i]);
                }
            }
        }
    }

    private Tree parseTree(String input, String prefix){
        List<String> lines = input.lines().filter(l -> l.trim().length() > 0).collect(Collectors.toList());
        int length = Integer.parseInt(lines.get(0).split(" ")[1]);
        List<TreeLine> tl = lines
                .subList(1, length + 1)
                .stream()
                .map(l -> new TreeLine(l, prefix))
                .collect(Collectors.toList());
        Tree root = tl.get(0).head;
        for (TreeLine treeline : tl) {
            Tree node = root.getSubtree(treeline.head.name);
            for (String n: treeline.childrenNames) {
                node.addChild(tl.stream().filter(l->l.head.name.equals(n)).findFirst().get().head);
            }
        }
        return root;
    }

    private boolean saveTree(Tree tree, String filename){
        List<String> lines = new ArrayList<>();
        List<String> nodeLines = serializeNode(tree);
        lines.add("@tasks " + nodeLines.size());
        lines.addAll(nodeLines);
        try {
            PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8);
            for (String line : lines) {
                writer.println(line);
            }
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<String> serializeNode(Tree node){
        List<String> lines = new ArrayList<>();
        String name = node.name;
        int childrenCount = node.children.size();
        List<String> childrenNames = node.children.stream().map(c->c.name).collect(Collectors.toList());
        String line = name + " " + childrenCount + " " + String.join(" ", childrenNames);
        lines.add(line);
        for (Tree child: node.children) {
            lines.addAll(serializeNode(child));
        }
        return lines;
    }

    private TreeManagerImpl(){}

    private static TreeManager instance = new TreeManagerImpl();

    public static TreeManager getInstance(){return instance;}
}
