import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewPath implements PathingStrategy {

    @Override
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new LinkedList<>();
        Point current = start;
        List<Point> possible = Arrays.asList(new Point(current.x+1, current.y), new Point(current.x-1, current.y),
                new Point(current.x, current.y-1), new Point(current.x, current.y+1));

        possible = possible.stream().filter(canPassThrough).collect(Collectors.toList());

        List<Point> visited = new LinkedList<>();

        visited.add(current);

        int count = 0;
        boolean found = false;


        while(!found){
            if(withinReach.test(current, end)){
                found = true;
                break;
            }
            if (possible.size()==1){
                current = possible.get(0);
            } else {

                Random rand = new Random();
                int rad = rand.nextInt(possible.size()+1);
                if(rad >= possible.size()){
                    rad = 0;
                }
                System.out.println("Rad: "+rad);
                System.out.println("Possible: "+possible);
                if(visited.contains(possible.get(rad))){
                    rad = redo(possible, visited);
                }
                current = possible.get(rad);
            }
            path.add(current);
            visited.add(current);

//            possible.clear();

            possible = Arrays.asList(new Point(current.x+1, current.y), new Point(current.x-1, current.y),
                    new Point(current.x, current.y-1), new Point(current.x, current.y+1));

            //possible = possible.stream().filter(canPassThrough).collect(Collectors.toList());

//            System.out.println(current);
//
//            possible = potentialNeighbors.apply(current).filter(canPassThrough).collect(Collectors.toList());
//            System.out.println(possible.size());

            if (count==5){
                break;
            }
            count ++;
        }


//        for(int i = path.size()-1; i>= 0; i--){
//            backwards.add(path.get(i));
//        }

        return path;
    }

    public int redo(List<Point> possible, List<Point>visited){
        Random rand = new Random();
        int rad = rand.nextInt(possible.size()+1);
        if(rad >= possible.size()){
            rad = 0;
        }
        if(visited.contains(possible.get(rad))){
            rad = redo(possible, visited);
        }
        return rad;
    }


}
