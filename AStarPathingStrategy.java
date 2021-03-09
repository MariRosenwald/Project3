import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;

class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new LinkedList<>();
        HashMap<Point, Node> openList = new HashMap<>();
        HashMap<Point, Node> closedList = new HashMap<>();
        Node current = new Node(start, null, 0, 0);

        openList.put(start, current);

        while (!openList.isEmpty()) {
            if (withinReach.test(current.getPoint(), end)) {
                path = NTP(current, path);
                break;
            }
            Node finalCurrentNode = current;
            for (Node n : potentialNeighbors.apply(current.getPoint()).filter(canPassThrough).filter(p -> !closedList.containsKey(p))
                    .map(p -> new Node(p, finalCurrentNode, finalCurrentNode.getG() + 1, h(p, end))).collect(Collectors.toList())) {
                if (!openList.containsKey(n.getPoint())) {
                    openList.put(n.getPoint(), n);
                } else {
                    double g = finalCurrentNode.getG() + 1;
                    if (g > openList.get(n.getPoint()).getG()) {
                        openList.get(n.getPoint()).setG(g);
                    }
                }
            }
            closedList.put(current.getPoint(), current);
            openList.remove(current.getPoint());

            PriorityQueue<Node> yikes = new PriorityQueue<>(Node::compareTo);
            yikes.addAll(openList.values());
            current = yikes.poll();

        }

        ArrayList<Point> backwards = new ArrayList<>();
        for(int i = path.size()-1; i>= 0; i--){
            backwards.add(path.get(i));
        }

        //System.out.println(backwards.size());
        return backwards;
    }




    public double h(Point a, Point b){
        return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y));
    }

    public List<Point> NTP(Node a, List<Point> list){
        if(a.getPrev() != null) {
            list.add(a.getPoint());
            NTP(a.getPrev(), list);
        }return list;
    }
}
