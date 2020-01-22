import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TreeIOImpl implements TreeIO {

    @Override
    public Tree loadTree(File input, String prefix) throws IOException {
        FileInputStream inputStream = new FileInputStream(input);
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine().trim();
            if(line.length() > 0){
                lines.add(line);
            }
        }
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


    @Override
    public boolean saveTree(File file, Tree tree){
        List<String> lines = new ArrayList<>();
        List<String> nodeLines = serializeNode(tree);
        lines.add("@tasks " + nodeLines.size());
        lines.addAll(nodeLines);
        try {
            PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
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
}
