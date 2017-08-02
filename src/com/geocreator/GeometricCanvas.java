package com.geocreator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

/**
 * {@link GeometricCanvas} is destined to hold 2D shapes and render them.<br>
 * It supports polygons, rectangles, circles of type {@link PolyShape}.<br>
 * Uses mouse events to get mouse position to put points on a canvas.<br>
 * Uses keyboard events to end current figure and create new.
 *
 * @author Adrian Mucha
 * @version 1.0
 *
 * @see PolyShape
 * @see MainWindow
 */
public class GeometricCanvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    private LinkedList<PolyShape> polyShapes;   // holds all shapes to render
    private PolyShape actualPolyShape;      // current active PolyShape
    private long nextFreePolyShapeID = 0;    // next ID to be assigned to newly created PolyShape
    private MainWindow outerWindow;         // Access components from MainWindow
    private MainWindow.Mode lastMode;       // used to check from which mode was changed
    private Point mousePos = new Point(0,0);
    private boolean hideMousePosAndCanvasDimension = false;

    // true if user pressed mouse inside of any PolyShape
    // false otherwise
    private boolean pressIn = false;

    // last mouse position that event called
    private int last_x;
    private int last_y;

    // shape rotation key modifier
    private boolean rotateModeCtrlKeyPressed = false;

    /**
     * Prepares a helper object containing current drawn shapes and next free ID as a serializable object
     * @return Canvas saved state
     */
    private GeometricCanvasSavedState getSaveState() {
        return new GeometricCanvasSavedState(polyShapes, nextFreePolyShapeID);
    }

    /**
     * Writes what canvas has on itself to file with extension PNG
     * @param path file path to export to
     */
    public void exportToPNG(String path)
    {
        BufferedImage bImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = bImg.createGraphics();
        this.hideMousePosAndCanvasDimension = true;
        this.paintAll(cg);
        try {
            ImageIO.write(bImg, "png", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hideMousePosAndCanvasDimension = false;
    }

    /**
     * Serializes canvas state and saves it to specified file
     * @param path path to save to
     * @throws IOException
     */
    public void saveStateToFile(String path) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fileOut);
        oos.writeObject(getSaveState());
        oos.close();
        fileOut.close();
        System.out.println("[GeometricCanvas] Successfully saved state to file " + path);
    }

    /**
     * Loads serialized state from file to canvas and repaints from that data
     * @param path path to load from
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadStateFromFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(fileIn);
        GeometricCanvasSavedState gcss = (GeometricCanvasSavedState)ois.readObject();
        polyShapes = gcss.polyShapes;
        nextFreePolyShapeID = gcss.nextFreePolyShapeID;
        repaint();
        addNewPolyShape(outerWindow.getActiveMode());
        ois.close();
        fileIn.close();
        System.out.println("[GeometricCanvas] Successfully loaded state from file " + path);
    }

    /**
     * Last mode is used to check what mode was previously used before it was changed
     * @return Last mode
     * @see com.geocreator.MainWindow.Mode
     */
    public MainWindow.Mode getLastMode() {
        return lastMode;
    }

    /**
     * Sets last mode
     * @param lastMode mode to be set
     * @see com.geocreator.MainWindow.Mode
     */
    public void setLastMode(MainWindow.Mode lastMode) {
        this.lastMode = lastMode;
    }

    /**
     * Marks previous actual shape inactive and creates new {@link PolyShape} with certain {@link com.geocreator.MainWindow.Mode}
     * and adds it to LinkedList as last element (front layer) and sets it as active.
     * @param md mode in which {@link PolyShape} should be created
     * @see PolyShape
     * @see com.geocreator.MainWindow.Mode
     * @see LinkedList
     */
    public void addNewPolyShape(MainWindow.Mode md) {
        if(actualPolyShape != null) {
            actualPolyShape.setActive(false);   // set old one to inactive
        }
        actualPolyShape = new PolyShape(nextFreePolyShapeID, md);
        actualPolyShape.setActive(true);    // set new one to active
        polyShapes.addLast(actualPolyShape);
        nextFreePolyShapeID++;
        System.out.println("[GeometricCanvas] Added new PolyShape");
    }

    /**
     * Removes specified {@link PolyShape} object from {@link LinkedList}
     * and sets active to none (null). Repaints canvas after.
     * @param polyShape
     */
    public void removePolyShape(PolyShape polyShape) {
        if(polyShape != null) {
            polyShapes.remove(polyShape);
            actualPolyShape = null;
            repaint();
            System.out.println("[GeometricCanvas] Removed PolyShape");
        }
    }

    /**
     * Moves (removes and adds) specified {@link PolyShape} to front layer making it appear on top of all other polyshapes
     * @param polyShape specified object
     */
    public void movePolyShapeToFront(PolyShape polyShape) {
        if(polyShape != null) {
            polyShapes.remove(polyShape);
            polyShapes.addLast(polyShape);
            repaint();
            System.out.println("[GeometricCanvas] PolyShape moved to top");
        }
    }

    /**
     * Moves (removes and adds) specified {@link PolyShape} to bottom layer making it appear under all other polyshapes
     * @param polyShape specified object
     */
    public void movePolyShapeToBottom(PolyShape polyShape) {
        if(polyShape != null) {
            polyShapes.remove(polyShape);
            polyShapes.addFirst(polyShape);
            repaint();
            System.out.println("[GeometricCanvas] PolyShape moved to bottom");
        }
    }

    /**
     * Returns actual {@link PolyShape}
     * @return actual {@link PolyShape}
     */
    public PolyShape getActualPolyShape() {
        return actualPolyShape;
    }

    /**
     * Default constructor.
     * Builds {@link JPanel} as a canvas and adds mouse and keyboard listeners.
     */
    public GeometricCanvas(MainWindow ow) {
        super();
        outerWindow = ow;

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
    }

    /**
     * Initializes canvas with no shapes. Removes current shapes and resets ID counter to 0 and adds first {@link PolyShape}
     * Leaves with current mode or initializes with CREATING_POLYGONS
     * Repaints after.
     */
    public void clearCanvas() {
        if( getLastMode() == null ) {
            setLastMode(MainWindow.Mode.CREATING_POLYGONS);
        }

        polyShapes = new LinkedList<>();
        nextFreePolyShapeID = 0;
        addNewPolyShape(outerWindow.getActiveMode());
        repaint();
    }

    /**
     * Draws all of the {@link PolyShape} objects stored in {@link LinkedList}
     * based on their transform factors and color and {@link com.geocreator.MainWindow.Mode}.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        for(int i = 0; i < polyShapes.size(); i++) {
            if(polyShapes.get(i).isDrawable()) {
                //set background color
                g2d.setPaint(polyShapes.get(i).getBackGroundColor());
                g2d.fill(polyShapes.get(i).getShape());

                // set border lines color to bold if its active
                if(outerWindow.getActiveMode() == MainWindow.Mode.EDITING && polyShapes.get(i).isActive()) {
                    g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(new Color(0,196,0));
                } else {
                    g2d.setColor(new Color(0));
                }
                g2d.draw(polyShapes.get(i).getShape());
                g2d.setStroke(new BasicStroke());
            }
        }

        g2d.setColor(new Color(0));

        // Hide params before exporting to PNG file
        if(!hideMousePosAndCanvasDimension) {
            g2d.drawString("PosX: " + (int)mousePos.getX() + "px | PosY: " + (int)mousePos.getY() + "px", 5, (float)this.getSize().getHeight()-5);
            g2d.drawString("Canvas: " + (int)this.getSize().getWidth() + " x " + (int)this.getSize().getHeight(), (float)this.getSize().getWidth() - 135, (float)this.getSize().getHeight()-5);
        }
    }

    /**
    *   Updates mouse position right after mouse button is pressed.
     *  Left click adds coordinates to {@link PolyShape} and allows to manipulate shapes.
     *  Checks whether any {@link PolyShape} contains current mouse position - making it active if {@link com.geocreator.MainWindow.Mode} EDITING is active.
     *  Adds next {@link PolyShape} if needed.
     *  Repaints after.
     *  @see MouseListener
     *  @see MouseEvent
     *  @see com.geocreator.MainWindow.Mode
     *  @see PolyShape
    */
    @Override
    public void mousePressed(MouseEvent e) {
        last_x = e.getX();
        last_y = e.getY();

        if(e.getButton() == MouseEvent.BUTTON1) {
            if (outerWindow.getActiveMode() == MainWindow.Mode.CREATING_POLYGONS) {
                actualPolyShape.addPointCoord(e.getX(), e.getY());
            }

            if (outerWindow.getActiveMode() == MainWindow.Mode.CREATING_CIRCLES) {
                if (actualPolyShape.getCoordinatesSize() >= 2) {
                    addNewPolyShape(MainWindow.Mode.CREATING_CIRCLES);
                }
                actualPolyShape.addPointCoord(e.getX(), e.getY());
            }

            if (outerWindow.getActiveMode() == MainWindow.Mode.CREATING_RECTANGLES) {
                if (actualPolyShape.getCoordinatesSize() >= 2) {
                    addNewPolyShape(MainWindow.Mode.CREATING_RECTANGLES);
                }
                actualPolyShape.addPointCoord(e.getX(), e.getY());
            }
        }

        if(outerWindow.getActiveMode() == MainWindow.Mode.EDITING) {
            if(actualPolyShape != null) {
                actualPolyShape.setActive(false);
                actualPolyShape = null;
            }
            Point point = e.getPoint();
            // Start from last because it's painted on stack
            for (int i = polyShapes.size()-1; i >= 0; i--) {
                PolyShape pls = polyShapes.get(i);
                if(pls.isDrawable()) {
                    if(pls.getShape().contains(point)) {
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            pressIn = true;
                        }
                        actualPolyShape = pls;
                        actualPolyShape.setActive(true);
                        polyShapes.remove(pls);
                        polyShapes.addLast(pls); // move current shape to the top
                        break;
                    }
                }
            }
        }
        repaint();
    }

    /**
     * While holding left mouse button and being in EDITING {@link com.geocreator.MainWindow.Mode}
     * updates location of actual shape if mouse is over that shape.
     * @see PolyShape
     * @see com.geocreator.MainWindow.Mode
     */
    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        mousePos = mouseEvent.getPoint();
        if(pressIn) {
            double diff_x = last_x - mouseEvent.getX();
            double diff_y = last_y - mouseEvent.getY();

            actualPolyShape.move(diff_x, diff_y);

            last_x = mouseEvent.getX();
            last_y = mouseEvent.getY();
        }
        repaint();
    }

    /**
     * Updates left bottom corner with current mouse position relative to canvas when mouse changes its position on canvas
     */
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        mousePos = mouseEvent.getPoint();
        repaint();
    }

    /**
     * Sets flag that user is no longer holding mouse button
     */
    @Override
    public void mouseReleased(MouseEvent e) {  pressIn = false; }

    /**
     * If current mode is EDITING, moving mouse wheel makes shapes scale (or rotate if CTRL is pressed)
     * @see PolyShape
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        if(actualPolyShape != null && outerWindow.getActiveMode() == MainWindow.Mode.EDITING) {
            int notches = mouseWheelEvent.getWheelRotation();
            if(rotateModeCtrlKeyPressed) {
                actualPolyShape.modifyRotateTransformIndex(notches);
            } else {
                actualPolyShape.modifyScaleTransformFactor(notches*0.025);
            }
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {    }
    @Override
    public void mouseExited(MouseEvent e) {    }

    /**
     * Opens popup menu at current mouse position if active mode is EDITING and mouse is over shape.
     * Also makes that shape active. Event is fired after click.
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
       if(outerWindow.getActiveMode() == MainWindow.Mode.EDITING && actualPolyShape != null && e.getButton() == MouseEvent.BUTTON3) {
           outerWindow.getPopupShapeEditMenu().setPickerColor(actualPolyShape.getBackGroundColor());
           outerWindow.showPopupShapeEditMenuAt(e.getX(), e.getY());
       }
    }

    /**
     * SPACE key press adds new polygon and DEL key press deletes actual polyshape from canvas when one of them is pressed
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        rotateModeCtrlKeyPressed = (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL || rotateModeCtrlKeyPressed);
        if(outerWindow.getActiveMode() != MainWindow.Mode.EDITING) {
            switch(keyEvent.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    addNewPolyShape(outerWindow.getActiveMode());
                    break;
            }
        } else {
            switch(keyEvent.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                    removePolyShape(actualPolyShape);
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {    }

    /**
     * Marks CTRL key modifier as not pressed when releasing CTRL key
     */
    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
            rotateModeCtrlKeyPressed = false;
        }
    }
}
