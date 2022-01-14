package de.hsbo.pollenwarner.location;

import java.util.List;

public class Polygon {

    private List<Point> nodes;
    private String Region;
    private int id;

    public Polygon(List<Point> nodes) {
        this.nodes = nodes;
    }

    public Polygon(int id, List<Point> nodes, String region) {
        this.id = id;
        this.nodes = nodes;
        Region = region;
    }

    public String getRegion() {
        return Region;
    }

    // Raycasting Algorithm
    public Boolean PointInPolygon(Point point){

        List<Point> nodes = this.nodes;

        //A point is in a polygon if a line from the point to infinity crosses the polygon an odd number of times
        boolean odd = false;
        // int totalCrosses = 0; // this is just used for debugging
        //For each edge (In this case for each point of the polygon and the previous one)
        for (int i = 0, j = nodes.size() - 1; i < nodes.size(); i++) { // Starting with the edge from the last to the first node
            //If a line from the point into infinity crosses this edge
            if (((nodes.get(i).getY() > point.getY()) != (nodes.get(j).getY() > point.getY())) // One point needs to be above, one below our y coordinate
                    // ...and the edge doesn't cross our Y corrdinate before our x coordinate (but between our x coordinate and infinity)
                    && (point.getX() < (nodes.get(j).getX() - nodes.get(i).getX()) * (point.getY() - nodes.get(i).getY()) / (nodes.get(j).getY() - nodes.get(i).getY()) + nodes.get(i).getX())) {
                // Invert odd
                // System.out.println("Point crosses edge " + (j + 1));
                // totalCrosses++;
                odd = !odd;
            }
            //else {System.out.println("Point does not cross edge " + (j + 1));}
            j = i;
        }
        // System.out.println("Total number of crossings: " + totalCrosses);
        //If the number of crossings was odd, the point is in the polygon
        return odd;
    }

}
