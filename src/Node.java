public class Node {

    private double g;
    private double h;
    private Node prev;
    private Point p;

    Node(Point p, Node prev, double g, double h){
        this.p = p;
        this.g = g;
        this.h = h;
        this.prev = prev;
    }

    public Point getPoint() {
        return p;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public void setG(double g) {
        this.g = g;
    }

    public Node getPrev() {
        return prev;
    }

    public double getF(){
        return g+h;
    }

    public int compareTo(Node a){
        double first = this.getF() - a.getF();
        if (first==0){
            first = this.getG()-a.getG();
        }
        return (int) first;
    }
}
