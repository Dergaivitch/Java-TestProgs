package cyclefinder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CycleFinder {

    static List<GraphNode> list;
    static List<List<Integer>> paths;

    public class GraphNode {

        GraphNode(int num, boolean isNew, List<GraphNode> links) {
            this.isNew = isNew;
            this.links = links;
            this.name = num;
        }
        public boolean isNew;
        public List<GraphNode> links;
        int name;
    }

    class Dot {

        Dot(boolean exists, int dotname, int index) {

            this.dotname = dotname;
            this.exists = exists;
            this.index = index;
        }

        boolean exists;
        int dotname;
        int index;
    }

    class Graph {

        Graph() {
            this.nodes = new ArrayList<>();
        }

        public List<GraphNode> nodes;

        void ParseEdge(String fileName) throws FileNotFoundException {
            Scanner newscan = new Scanner(new FileInputStream(new File(fileName)));

            Dot from = new Dot(true, 0, 0);
            Dot to = new Dot(true, 0, 0);
            if (!newscan.hasNextInt()) {
                System.out.println("log + файл пуст");
                return;
            }

            while (newscan.hasNextInt()) {
                from.dotname = newscan.nextInt();
                from.exists = false;
                if (!newscan.hasNextInt()) {
                    System.out.println("не четно");
                    return;
                }
                to.dotname = newscan.nextInt();
                to.exists = false;
                for (int m = 0; m < nodes.size(); m++) {
                    if (nodes.get(m).name == from.dotname) {
                        from.exists = true;
                        from.index = m;
                    }
                    if (nodes.get(m).name == to.dotname) {
                        to.exists = true;
                        to.index = m;
                    }

                }

                if (!from.exists) {
                    nodes.add(new GraphNode(from.dotname, true, new ArrayList<>()));
                    from.index = nodes.size()-1;
                }
                if (!to.exists) {
                    nodes.add(new GraphNode(to.dotname, true, new ArrayList<>()));
                    to.index = nodes.size()-1;
                }

                nodes.get(from.index).links.add(nodes.get(to.index));

            }

        }
    }

    public static List<List<Integer>> Search(Graph graph) {
        list = new ArrayList<>();    
        paths = new ArrayList<>(); 

        for (GraphNode node : graph.nodes) {
            node.isNew = true;
        }

        list.add(graph.nodes.get(0)); 

        boolean done = false;
        while (!done) {
            while (list.size() > 0) {
                CircuitFind(list.get(list.size() - 1));
            }

            done = true;
            for (GraphNode node : graph.nodes) {
                if (node.isNew) {
                    list.add(node);
                    done = false;
                    break;
                }
            }
        }
        return paths;
    }

    static void CircuitFind(GraphNode node) {
        node.isNew = false;

        for (GraphNode nextNode : node.links) 
        {
            if (nextNode.isNew) {
                list.add(nextNode);
                CircuitFind(nextNode);
            } else if (list.indexOf(nextNode) != -1) 
            {
                List<Integer> newPath = new ArrayList<>();
                int firstElement = list.indexOf(nextNode);

                for (int i = firstElement; i < list.size(); i++) {
                    newPath.add(list.get(i).name);
                }
                newPath.add(newPath.get(0));
                paths.add(newPath);

            }
        }
        list.remove(node);
    }

    public static void main(String[] args) throws FileNotFoundException {

        CycleFinder letsGo = new CycleFinder();
        Graph a = letsGo.new Graph();
        a.ParseEdge(args [0]);

        List<List<Integer>> printOut;
        printOut = (Search(a));
        for (List<Integer> curPath : printOut) {
            for (int k : curPath) {
                System.out.print(k + " ");
            }
            System.out.println();
        }
    }
}
