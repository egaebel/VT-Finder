package vt.finder.quadtree;

import vt.finder.schedule.Point;


/**
 * Class which holds the bounds for a given world, provides methods to provide
 * for division of these bounds into their respective sub boundaries, as well as
 * methods dealing with checking the bounds of these quadrants and getting the
 * bounds object associated with it all things boundaries are handled by this
 * class
 * 
 * @author ethan
 * 
 */
public class ChangingBounds {

    // ~DataFields-------------------------------------------------------------
    /**
     * the X boundaries for the world that this class will be dealing with
     */
    private Bounds worldBounds;

    // ~Constructors-----------------------------------------------------------
    /**
     * constructor that initializes the worldBounds in the X and Y coordinates
     */
    public ChangingBounds(int boundMaxX, int boundMaxY, int boundMinX,
            int boundMinY) {

        worldBounds = new Bounds(boundMaxX, boundMaxY, boundMinX, boundMinY);
    }

    /**
     * sets the worldBOunds to a passed in bounds
     * 
     * @param newBounds
     *            new bounds to set worldBOunds to
     */
    public ChangingBounds(Bounds newBounds) {

        worldBounds = newBounds;
    }

    // ~Methods----------------------------------------------------------------
    /**
     * returns a Bounds object that serves as the NW of the current Bounds
     * 
     * @return a new Bounds object holding the upper and lower corner bounds
     */
    public Bounds calcNW() {

        return new Bounds((worldBounds.getMaxX() + worldBounds.getMinX()) / 2,
                (worldBounds.getMaxY() + worldBounds.getMinY()) / 2,
                worldBounds.getMinX(), worldBounds.getMinY());
    }

    /**
     * returns a Bounds object that serves as the NE of the current Bounds
     * 
     * @return a new Bounds object holding the upper and lower corner bounds
     */
    public Bounds calcNE() {

        return new Bounds(worldBounds.getMaxX(),
                (worldBounds.getMinY() + worldBounds.getMaxY()) / 2,
                (worldBounds.getMaxX() + worldBounds.getMinX()) / 2,
                worldBounds.getMinY());
    }

    /**
     * returns a Bounds object that serves as the SE of the current Bounds
     * 
     * @return a new Bounds object holding the upper and lower corner bounds
     */
    public Bounds calcSE() {

        return new Bounds(worldBounds.getMaxX(), worldBounds.getMaxY(),
                (worldBounds.getMaxX() + worldBounds.getMinX()) / 2,
                (worldBounds.getMaxY() + worldBounds.getMinY()) / 2);
    }

    /**
     * returns a Bounds object that serves as the SW of the current Bounds
     * 
     * @return a new Bounds object holding the upper and lower corner bounds
     */
    public Bounds calcSW() {

        return new Bounds((worldBounds.getMaxX() + worldBounds.getMinX()) / 2,
                worldBounds.getMaxY(), worldBounds.getMinX(),
                (worldBounds.getMaxY() + worldBounds.getMinY()) / 2);
    }

    /**
     * determines in which quadrant the location stored within the node belongs
     * in
     * 
     * _______ | 0| 1 | |------| | 2| 3 | --------
     * 
     * @param node
     *            the node to be inserted
     * @return an integer indicating the quadrant the node goes in
     */
    public int placement(LeafNode node) {

        int retInt;

        if (node == null) {

            retInt = -1;
        }
        else if (node.getRecord() == null) {

            retInt = -1;
        }
        else {

            // x,y values from the record being placed
            int xVal = node.getRecord().getCoursePoint().getX();
            int yVal = node.getRecord().getCoursePoint().getY();

            if (!withinBounds(new Point(xVal, yVal))) {

                retInt = -1;
            }
            // if in NW
            else if (xVal < ((worldBounds.getMaxX() + worldBounds.getMinX()) / 2)
                    && yVal < ((worldBounds.getMaxY() + worldBounds.getMinY()) / 2)) {

                retInt = 0;
            }
            // if in NE
            else if (xVal > ((worldBounds.getMaxX() + worldBounds.getMinX()) / 2)
                    && yVal < ((worldBounds.getMaxY() + worldBounds.getMinY()) / 2)) {

                retInt = 1;
            }
            // if in SW
            else if (xVal < ((worldBounds.getMaxX() + worldBounds.getMinX()) / 2)
                    && yVal > ((worldBounds.getMaxY() + worldBounds.getMinY()) / 2)) {

                retInt = 2;
            }
            // if in SE
            else {

                retInt = 3;
            }
        }

        return retInt;
    }

    /**
     * takes in a point and checks to see if that point is within the bounds of
     * the world, all bounds are all inclusive, the bounds themselves are included
     * 
     * @param check
     *            the point to check against the world bounds
     * @return true if withinBounds, false if not within bounds
     */
    public boolean withinBounds(Point check) {

        boolean value;

        // if x is larger or equal to the max, not within bounds
        if (check.getX() > worldBounds.getMaxX()) {

            value = false;
        }
        // if y is larger or equal to max, not within bounds
        else if (check.getY() > worldBounds.getMaxY()) {

            value = false;
        }
        // if x is less than the max, not within bounds
        else if (check.getX() < worldBounds.getMinX()) {

            value = false;
        }
        // if y is less than the max, not within bounds
        else if (check.getY() < worldBounds.getMinY()) {

            value = false;
        }
        else {

            value = true;
        }

        return value;
    }

    /**
     * takes in a point and checks to see if that point is within the bounds of
     * the world, all bounds are all inclusive, the bounds themselves are included
     * 
     * @param check
     *            the point to check against the world bounds
     * @return true if withinBounds, false if not within bounds
     */
    public boolean withinBoundsExclusionary(Point check) {

        boolean value;

        // if x is larger than the max, not within bounds
        if (check.getX() >= worldBounds.getMaxX()) {

            value = false;
        }
        // if y is larger or equal to max, not within bounds
        else if (check.getY() >= worldBounds.getMaxY()) {

            value = false;
        }
        // if x is less than the max, not within bounds
        else if (check.getX() <= worldBounds.getMinX()) {

            value = false;
        }
        // if y is less than the max, not within bounds
        else if (check.getY() <= worldBounds.getMinY()) {

            value = false;
        }
        else {

            value = true;
        }

        return value;
    }
    
    /**
     * takes a top left point (xy cords), the width and height of a rectangle
     * then determines if the rectangle goes through the current worldBounds
     * 
     * @param x
     *            top left x cord
     * @param y
     *            top left y cord
     * @param width
     *            width of rectangle
     * @param height
     *            height of rectangle
     * @return true if a the rectangle crosses through this world, false
     *         otherwise
     */
    private boolean rectangleWithinBounds(int x, int y, int width, int height) {

        // checks if any of the points of the rectangle are within the bounds of
        // this current world
        boolean value = withinBounds(new Point(x, y))
                || withinBounds(new Point(x + width, y))
                || withinBounds(new Point(x, y + height))
                || withinBounds(new Point(x + width, y + height));

        // if the min of a square is between x, x +width and y, y + height
        if ((x < worldBounds.getMinX() && worldBounds.getMinX() < x + width)
                && (y < worldBounds.getMinY() && worldBounds.getMinY() < y
                        + height)) {

            value = true;
        }
        // if the max of a square is between x, x +width and y, y + height
        if ((x < worldBounds.getMaxX() && worldBounds.getMaxX() < x + width)
                && (y < worldBounds.getMaxY() && worldBounds.getMaxY() < y
                        + height)) {

            value = true;
        }

        // if the max of the rectangle is on the bounds of the square....then
        // the min must surely be farther
        // than one unit away
        if ((worldBounds.getMaxX() == (x + width))
                && ((worldBounds.getMaxY() > y && worldBounds.getMinY() </*=*/ y) || (worldBounds
                        .getMaxY() > y + height && worldBounds.getMinY() </*=*/ y
                        + height))) {

            value = true;
        }
        if ((worldBounds.getMaxY() == (y + height))
                && ((worldBounds.getMaxX() > x && worldBounds.getMinX() </*=*/ x) || (worldBounds
                        .getMaxX() > x + width && worldBounds.getMinX() </*=*/ x
                        + width))) {

            value = true;
        }
        if(worldBounds.getMaxX() == (x + width) && worldBounds.getMaxY() == (y + height)) {
            
            value = true;
        }

        return value;
    }

    /**
     * tests to see if the rectangle specified by x, y width and height
     * intersects with the quadrant specified by either 0,1,2,3
     * 
     * nw = 0; ne = 1; sw= 2; se = 3;
     * 
     * @param x
     *            the min x cord of the rectangle
     * @param y
     *            the min y cord of the rectangle
     * @param width
     *            the width of the rectangle
     * @param height
     *            the height of the rectangle
     * @param quadrant
     *            number specifying which quadrant to compare to
     * @return true if an intersection between the specified quadrant and the
     *         rectangle occurs false otherwise
     */
    public boolean intersects(int x, int y, int width, int height, int quadrant) {

        boolean value = false;

        Bounds area = null;

        // switch that ids which quadrant of the current bounds are being
        // checked using the quadrant param
        switch (quadrant) {

            case 0:

                area = calcNW();
                break;
            case 1:

                area = calcNE();
                break;
            case 2:

                area = calcSW();
                break;
            case 3:

                area = calcSE();
                break;
        }

        // if there was indeed a valid case number passed in
        if (area != null) {

            // if any of the corners of the rectangle are within the bounds of
            // the specified quadrant
            if (new ChangingBounds(area).rectangleWithinBounds(x, y, width,
                    height)) {

                value = true;
            }
        }

        return value;
    }

    /**
     * sets the value of the worldBOunds to a passed in value
     * 
     * @param newWorld
     *            the new value to set worldBOunds to
     */
    public void setBounds(Bounds newWorld) {

        worldBounds = newWorld;
    }

    /**
     * gets the value of the worldBOunds variable
     * 
     * @return worldBound the bounds object that represents the world
     */
    public Bounds getBounds() {

        return worldBounds;
    }

    /**
     * Calculates the center point of the bounds
     * 
     * @return the Point object that is the center of the bounds
     */
    public Point getCenter() {

        return new Point((worldBounds.getMaxX() + worldBounds.getMinX()) / 2,
                (worldBounds.getMaxY() + worldBounds.getMinY()) / 2);
    }
}