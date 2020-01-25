public class Main {

    public static void main(String[] args)
    {
        TreeManager treeManager = new TreeManagerImpl(new TreeProcessorImpl(), new TreeIOImpl());
        TreeDisplay treeDisplay = new TreeDisplay(treeManager);
    }
}
