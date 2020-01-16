import java.util.ArrayList;
import java.util.List;

// a Tree object has a list of smaller trees, recursively, from the root all the way to leaves

public class Tree {
    public String name;
    public List<Tree> children = new ArrayList<>();

    Tree getSubtree(String subTreeName){
        if(subTreeName.equals(this.name)){ return this; }
        for (Tree child: this.children) {
            Tree searchResult = child.getSubtree(subTreeName);
            if(searchResult != null){ return searchResult; }
        }
        return null;
    }

    Tree(String name){
        this.name = name;
    }
}
