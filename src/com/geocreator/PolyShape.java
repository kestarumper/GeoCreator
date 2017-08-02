package com.geocreator;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

import static com.geocreator.MainWindow.Mode.*;

/**
 * Geometric figures displayed on GeometricCanvas.
 * Contains data about shape, shape's color, all of it point coordinates and mode it was created with.
 * Based on mode, objects are created differently. Circles ({@link Ellipse2D}) and Rectangles ({@link Rectangle2D})
 * are created from a set of 2 coordinates while polygons ({@link GeneralPath}) might have any number of them.
 * Each PolyShape has it's own ID that distinguishes it from other objects.
 * Shape can be set to active, and then if they are rendered in canvas - they have their border coloured green and widened.<br>
 * WARNING: Coordinates might not reflect their rendered position in canvas because of {@link AffineTransform} applied to them (scale, rotate, translate).
 * @see Ellipse2D
 * @see Rectangle2D
 * @see GeneralPath
 * @see Color
 * @see com.geocreator.MainWindow.Mode
 * @see AffineTransform
 * @see GeometricCanvas
 * @see Serializable
 */
public class PolyShape implements Serializable {
    private Vector<Point> coordinates = new Vector<>();
    private Shape shape;
    private Color backgroundColor = new Color((new Random()).nextInt());
    private MainWindow.Mode mode;
    private final long ID;
    private double scaleTransformFactor = 1.0;
    private int rotateTransformIndex = 0;
    private boolean active = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PolyShape polyShape = (PolyShape) o;

        return ID == polyShape.ID;
    }

    @Override
    public int hashCode() {
        return (int) (ID ^ (ID >>> 32));
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Sets PolyShape as active
     * @param active true if active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Sets background color to specified {@link Color} object.
     * @param backgroundColor background color to be changed to
     * @see Color
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Returns drawable shape object. Might be either: {@link Rectangle2D}, {@link Ellipse2D} or {@link GeneralPath}
     * @return drawable object
     * @see Rectangle2D
     * @see Ellipse2D
     * @see GeneralPath
     * @see Shape
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Returns shape's background color
     * @return shape's background color
     * @see Color
     */
    public Color getBackGroundColor() {
        return backgroundColor;
    }

    /**
     * Returns factor by which shape is scaled.
     * @return scale transform factor
     * @see AffineTransform
     */
    public double getScaleTransformFactor() {
        return scaleTransformFactor;
    }

    /**
     * Updates scale factor. Scale factor can't be less than 0.025. Updates shape object after.
     * @param diff how much increase / decrease
     * @see AffineTransform
     * @see Shape
     */
    public void modifyScaleTransformFactor(double diff) {
        if(scaleTransformFactor+diff >= 0.025) {
            this.scaleTransformFactor += diff;
            reShape();
        }
    }

    /**
     * Updates rotate index. Updates shape object after.
     * @param diff how much increase / decrease
     * @see AffineTransform
     * @see Shape
     */
    public void modifyRotateTransformIndex(int diff) {
        this.rotateTransformIndex += diff;
        reShape();
    }

    /**
     * Returns central point of shape.
     * Sums all x's and divides them by their number. Same goes for y's.
     * @return mid point
     */
    public Point.Double calculateMidPoint() {
        if(getMode() == CREATING_CIRCLES) {
            return new Point.Double(coordinates.get(0).getX(), coordinates.get(0).getY());
        } else {
            double sumX = 0;
            double sumY = 0;
            for(int i = 0; i < coordinates.size(); i++) {
                sumX += coordinates.get(i).getX();
                sumY += coordinates.get(i).getY();
            }
            return new Point.Double(sumX/coordinates.size(), sumY/coordinates.size());
        }
    }

    /**
     * Moves figure by x and y in a certain direction.
     * Moves each point by the passed parameters. Simply adds value to each point.
     * @param x adds to actual point.x value of x
     * @param y adds to actual point.y value of y
     */
    public void move(double x, double y) {
        for(Point p: coordinates) {
            p.setLocation(p.getX()-x, p.getY()-y);
        }
        reShape();
    }

    /**
     * Adds new vertice to shape (adds new coordinate)
     * @param x point's x
     * @param y point's y
     */
    public void addPointCoord(int x, int y) {
        addPointCoord(new Point(x, y));
    }

    /**
     * Adds new vertice to shape (adds new coordinate)
     * @param p Point to be added
     * @see Point
     */
    public void addPointCoord(Point p) {
        coordinates.add(p);
        reShape();
    }

    /**
     * Returns size of coordinates vector
     * @return coordinates vector size
     */
    public int getCoordinatesSize() {
        return coordinates.size();
    }

    /**
     * Returns mode the shape was created with.
     * @return mode
     * @see com.geocreator.MainWindow.Mode
     */
    public MainWindow.Mode getMode() {
        return mode;
    }

    /**
     * Applies transformation to current shape object. Modifies it's scale based on scale factor and rotates it by it's rotate index.
     * It's also translated (moved) to central point because otherwise scaling resulted in moving it to top left corner of canvas.
     * @param shape shape to be transformed
     * @return transformed shape
     * @see AffineTransform
     */
    private Shape applyTransform(Shape shape) {
        AffineTransform transform = new AffineTransform();
        // Rotate by certain amount of radians around central point
        transform.rotate(rotateTransformIndex * (Math.PI / 128), calculateMidPoint().getX(), calculateMidPoint().getY());
        // Move to "center" in order to counter scaling moving
        transform.translate(
                calculateMidPoint().getX()*(1-getScaleTransformFactor()),
                calculateMidPoint().getY()*(1-getScaleTransformFactor())
        );
        // Scaling
        transform.scale(getScaleTransformFactor(), getScaleTransformFactor());

        return transform.createTransformedShape(shape);
    }

    /**
     * Generates circle as a Shape based on central point and calculated radius.
     * Has applied transformations.
     * @return generated circle from this PolyShape object
     * @see Ellipse2D
     * @see Shape
     */
    private Shape generateCircle() {
        double x0 = coordinates.get(0).getX();
        double y0 = coordinates.get(0).getY();

        double x1 = coordinates.get(1).getX();
        double y1 = coordinates.get(1).getY();

        double radius = Math.sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));

        Ellipse2D circle = new Ellipse2D.Double(coordinates.get(0).getX()-(radius), coordinates.get(0).getY()-(radius), 2*radius, 2*radius);

        return applyTransform(circle);
    }

    /**
     * Generates {@link GeneralPath} (polygon) as a {@link Shape} from vertices (coordinates) by connecting them with lines.
     * Has applied transformations.
     * @return generated polygon from this PolyShape object
     * @see GeneralPath
     * @see Shape
     */
    private Shape generatePolygon() {
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, coordinates.size());
        polygon.moveTo(coordinates.get(0).getX(), coordinates.get(0).getY());

        for (int index = 1; index < coordinates.size(); index++) {
            polygon.lineTo(coordinates.get(index).getX(), coordinates.get(index).getY());
        };

        polygon.closePath();

        return applyTransform(polygon);
    }

    /**
     * Generates {@link Rectangle2D} as a {@link Shape} based on 2 points and calculating width and height from them.
     * Has applied transformations.
     * @return generated rectangle from this PolyShape object
     * @see Rectangle2D
     * @see Shape
     */
    private Shape generateRectangle() {
        double x1 = coordinates.get(0).getX();
        double x2 = coordinates.get(1).getX();
        double y1 = coordinates.get(0).getY();
        double y2 = coordinates.get(1).getY();

        double height = y1 - y2;
        double width = x1 - x2;

        // get absolute value
        height = height > 0 ? height : -height;
        width = width > 0 ? width : -width;

        Rectangle2D rect = new Rectangle2D.Double( (x1 < x2 ? x1 : x2), (y1 < y2 ? y1 : y2), width, height);

        return applyTransform(rect);
    }

    /**
     * Based on {@link com.geocreator.MainWindow.Mode} of this object, creates {@link Shape} and updates current figure
     * by launching appropriate generate function. If there are not enough points - no {@link Shape} is created.
     */
    private void reShape() {
        if( isDrawable() ) {
            switch(getMode()) {
                case CREATING_POLYGONS:
                    shape = generatePolygon();
                    break;
                case CREATING_CIRCLES:
                    shape = generateCircle();
                    break;
                case CREATING_RECTANGLES:
                    shape = generateRectangle();
                    break;
            }
        }
    }

    /**
     * Checks whether a shape can be drawn. All shapes need at least 2 coordinates.
     * @return true if able, false otherwise
     */
    public boolean isDrawable() {
        if(coordinates.size() >= 2)
            return true;
        return false;
    }

    /**
     * Default constructor
     * @param id distinguishing ID
     * @param m mode, the shape will be created with
     * @see com.geocreator.MainWindow.Mode
     */
    public PolyShape(long id, MainWindow.Mode m) {
        ID = id;
        mode = m;
        System.out.println("[PolyShape] Created new with ID = " + ID);
    }
}
