import java.util.ArrayList;
import java.util.List;

// a Tree object has a list of smaller trees, recursively, from the root all the way to leaves

public class Tree {
    public String name;
    public List<Tree> children = new ArrayList<>();
    public Tree parent;

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

    Boolean addChild(Tree child){
        if((!this.name.equals(child.name)) && this.children.stream().noneMatch(c->c.name.equals(name))){
            child.parent = this;
            this.children.add(child);
            return true;
        } else {
            return false;
        }
    }

    Tree removeChild(String name){
        for (int i = 0; i<this.children.size(); i++){
            Tree current = this.children.get(i);
            if(this.children.get(i).name.equals(name)){
                current.parent = null;
                this.children.remove(i);
                return current;
            }
        }
        return null;
    }
}
