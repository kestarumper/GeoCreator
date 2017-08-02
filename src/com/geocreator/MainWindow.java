package com.geocreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * {@link MainWindow} is extension for {@link JFrame} with some additional features.
 * It is responsible for holding all components such as {@link JMenuBar}
 * with all menu buttons and {@link GeometricCanvas} and {@link PopupShapeEditMenu}.
 * Also assigns buttons their {@link ActionEvent}.
 *
 * @author Adrian Mucha
 * @version 1.0
 * @see         JFrame
 * @see         JMenuBar
 * @see         GeometricCanvas
 * @see         PopupShapeEditMenu
 * @see         ActionEvent
 */
public class MainWindow extends JFrame {
    /**
     * {@link Mode} has three enumerated types to tell apart different modes on {@link GeometricCanvas}:
     * <br>- CREATING_POLYGONS - create shapes from connecting points
     * <br>- CREATING_CIRCLES - create circles from 2 points - center and radius
     * <br>- EDITING - scaling and moving shapes
     *
     *  @see        GeometricCanvas
     *  @see        MainWindow
     */
    public enum Mode {
        CREATING_POLYGONS(KeyEvent.VK_F1, KeyEvent.VK_P, "Creating Polygons"),
        CREATING_RECTANGLES(KeyEvent.VK_F2, KeyEvent.VK_R, "Creating Rectangles"),
        CREATING_CIRCLES(KeyEvent.VK_F3, KeyEvent.VK_C, "Creating Circles"),
        EDITING(KeyEvent.VK_F4, KeyEvent.VK_E, "Editing");

        private int hotkey;
        private int mnemokey;
        private String name;

        /**
         * Hotkey launches events without any focus
         * @return Hotkey
         */
        public int getHotkey() {
            return hotkey;
        }

        /**
         * Mnemonic key launches events when menu is active
         * @return Mnemonic key
         */
        public int getMnemokey() {
            return mnemokey;
        }

        Mode(int ke, int mne, String n) {
            hotkey = ke;
            mnemokey = mne;
            name = n;
        }

        @Override
        public String toString() {
            return name;
        }
    };

    private Mode activeMode = Mode.CREATING_POLYGONS;       // Current mode application is working
    private String currentFile = "New File";                // Current active working file which we will save to
    private boolean newFileFlag = true;                     // Flag indicating that there is no existing file and we work on new file

    private PopupShapeEditMenu popupShapeEditMenu = new PopupShapeEditMenu("Shape Editor", this);       // Menu under right click

    private JMenuBar menuBar = new JMenuBar();                                                          // Top menu
    private ButtonGroup modes = new ButtonGroup();                                                      // Holds Mode radio buttons
    private JRadioButtonMenuItem rbModeCreatingPolygons;
    private JRadioButtonMenuItem rbModeCreatingRectangles;
    private JRadioButtonMenuItem rbModeCreatingCircles;
    private JRadioButtonMenuItem rbModeEdit;

    public GeometricCanvas cvs;     // Main canvas we are painting and operating on

    /**
     * Checks if file we are working on is new or already exists
     * @return true if file is new, false otherwise
     */
    public boolean isNewFileFlag() {
        return newFileFlag;
    }

    /**
     * Changes new file flag indicating new file or existing one
     * @param newFileFlag true if new file, false otherwise
     */
    public void setNewFileFlag(boolean newFileFlag) {
        this.newFileFlag = newFileFlag;
    }

    /**
     * Returns current active working file which we will save to
     * @return active working file path
     */
    public String getCurrentFile() {
        return currentFile;
    }

    /**
     * Sets current active working file which we will save to
     * @param currentFile file path
     */
    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
        updateTitle();
    }

    /**
     * Returns reference to right click popup menu
     * @return popup menu reference
     */
    public PopupShapeEditMenu getPopupShapeEditMenu() {
        return popupShapeEditMenu;
    }

    /**
     * Renders popup menu at specified point in GeometricCanvas
     * @param x canvas x coordinate
     * @param y canvas y coordinate
     */
    public void showPopupShapeEditMenuAt(int x, int y) {
        popupShapeEditMenu.show(cvs, x, y);
    }

    /**
     * Returns current active mode
     * @return current active mode
     * @see Mode
     */
    public Mode getActiveMode() {
        return activeMode;
    }

    /**
     * Sets current active mode and selects appropriate radio button in the menu
     * and updates window's frame title to display current mode and active file.
     * @param activeMode mode to be activated
     */
    public void setActiveMode(Mode activeMode) {
        this.activeMode = activeMode;
        modes.clearSelection();
        switch(getActiveMode()) {
            case CREATING_POLYGONS:
                rbModeCreatingPolygons.setSelected(true);
                break;
            case CREATING_RECTANGLES:
                rbModeCreatingRectangles.setSelected(true);
                break;
            case CREATING_CIRCLES:
                rbModeCreatingCircles.setSelected(true);
                break;
            case EDITING:
                rbModeEdit.setSelected(true);
                break;
        }
        updateTitle();
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path file name
     * @param description description of icon
     */
    private ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Updates MainWindow frame title bar adjusting current mode and working file name
     */
    public void updateTitle() {
        setTitle("GeoCreator - [" + getActiveMode() + "] - " + getCurrentFile());
    }

    /**
     * Default constructor.
     * Sets frame's title, size, default close operation, application icon
     * and creates canvas and menubar with menu items.
     *
     * Menu items are assigned events
     * @see NewFileEvent
     * @see OpenFileEvent
     * @see SaveFileEvent
     * @see SaveFileAsEvent
     * @see ExitEvent
     * @see ModeChangedEvent
     */
    public MainWindow() {
        super();
        updateTitle();
        setSize(new Dimension(1024, 720));
        setMinimumSize(new Dimension(495, 495));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(createImageIcon("/img/program_icon.png", "GeoCreator").getImage());

        JPanel buttonPanel = new JPanel();
        JButton btn;
        ImageIcon icon;

        icon = createImageIcon("/img/polygon.png", "Create polygons");
        btn = new JButton("Polygon", icon);
        btn.setFocusable(false);
        btn.addActionListener(new ModeChangedEvent(this, Mode.CREATING_POLYGONS));
        buttonPanel.add(btn);

        icon = createImageIcon("/img/rectangle.png", "Create rectangles");
        btn = new JButton("Rectangle", icon);
        btn.setFocusable(false);
        btn.addActionListener(new ModeChangedEvent(this, Mode.CREATING_RECTANGLES));
        buttonPanel.add(btn);

        icon = createImageIcon("/img/circle.png", "Create circles");
        btn = new JButton("Circle", icon);
        btn.setFocusable(false);
        btn.addActionListener(new ModeChangedEvent(this, Mode.CREATING_CIRCLES));
        buttonPanel.add(btn);

        icon = createImageIcon("/img/edit.png", "Move, scale, rotate, delete shapes");
        btn = new JButton("Edit", icon);
        btn.setFocusable(false);
        btn.addActionListener(new ModeChangedEvent(this, Mode.EDITING));
        buttonPanel.add(btn);

        add(buttonPanel, BorderLayout.NORTH);

        cvs = new GeometricCanvas(this);
        cvs.setFocusable(true);
        add(cvs, BorderLayout.CENTER);

        JMenu menuFileTab = new JMenu("File");
        menuFileTab.setMnemonic(KeyEvent.VK_F);
        JMenu menuModeTab = new JMenu("Mode");
        menuModeTab.setMnemonic(KeyEvent.VK_M);
        JMenu menuInfoTab = new JMenu("Info");
        menuInfoTab.setMnemonic(KeyEvent.VK_I);
        JMenuItem menuItem;

        menuItem = new JMenuItem("About");
        menuItem.addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(this,
                    "GeoCreator v1.0\n\n" +
                    "Simple graphic program designed to provide aid in creating basic shapes and editing them.\n" +
                    "It supports saving to and opening from file.\n\n" +
                    "Controls and Hotkeys:\n" +
                    "Ctrl-N - Creates blank new file\n" +
                    "Ctrl-O - Opens selected file\n" +
                    "Ctrl-S -Saves current working file\n" +
                    "Ctrl-Shift-S - Saves current file to other file\n" +
                    "Ctrl-E - Exits program\n" +
                    "Ctrl-F1 through Ctrl-F4 - changes active mode\n" +
                    "Holding Ctrl while scrolling - changes scaling to rotating.\n" +
                    "While creating polygons, Space press - creates new polygon\n\n" +
                    "Author: Adrian Mucha",
                    "About GeoCreator",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        menuInfoTab.add(menuItem);

        //Build the first menu.
        menuFileTab.getAccessibleContext().setAccessibleDescription("Save/load files and close program.");
        menuBar.add(menuFileTab);
        menuBar.add(menuModeTab);
        menuBar.add(menuInfoTab);

        // Menu item: New...
        menuItem = new JMenuItem("New...", KeyEvent.VK_T);
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new NewFileEvent(this));
        menuFileTab.add(menuItem);

        // Menu item: Open...
        menuItem = new JMenuItem("Open...", new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new OpenFileEvent(this));
        menuFileTab.add(menuItem);

        // Menu item: Save as...
        menuItem = new JMenuItem("Save as...", new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(new SaveFileAsEvent(this));
        menuFileTab.add(menuItem);

        // Menu item: Save
        menuItem = new JMenuItem("Save", new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new SaveFileEvent(this));
        menuFileTab.add(menuItem);

        // Menu item: Export to PNG
        menuItem = new JMenuItem("Export to PNG...");
        menuItem.addActionListener(new ExportToPNGEvent(this));
        menuFileTab.add(menuItem);

        //////////////////////////////////////////////////////////////////////////////////
        menuFileTab.addSeparator();

        // Menu item: Exit
        menuItem = new JMenuItem("Exit", new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ExitEvent());
        menuFileTab.add(menuItem);

        // Modes menu items
        // Mode Creating Polygons (default)
        rbModeCreatingPolygons = new JRadioButtonMenuItem("Creating Polygons");
        rbModeCreatingPolygons.setMnemonic(Mode.CREATING_POLYGONS.getMnemokey());
        rbModeCreatingPolygons.setAccelerator(KeyStroke.getKeyStroke(Mode.CREATING_POLYGONS.getHotkey(), ActionEvent.CTRL_MASK));
        rbModeCreatingPolygons.setSelected(true);
        rbModeCreatingPolygons.addActionListener(new ModeChangedEvent(this, Mode.CREATING_POLYGONS));
        menuModeTab.add(rbModeCreatingPolygons);
        modes.add(rbModeCreatingPolygons);

        // Mode Creating Rectangles
        rbModeCreatingRectangles = new JRadioButtonMenuItem("Creating Rectangles");
        rbModeCreatingRectangles.setMnemonic(Mode.CREATING_RECTANGLES.getMnemokey());
        rbModeCreatingRectangles.setAccelerator(KeyStroke.getKeyStroke(Mode.CREATING_RECTANGLES.getHotkey(), ActionEvent.CTRL_MASK));
        rbModeCreatingRectangles.addActionListener(new ModeChangedEvent(this, Mode.CREATING_RECTANGLES));
        menuModeTab.add(rbModeCreatingRectangles);
        modes.add(rbModeCreatingRectangles);

        // Mode Creating Circles
        rbModeCreatingCircles = new JRadioButtonMenuItem("Creating Circles");
        rbModeCreatingCircles.setMnemonic(Mode.CREATING_CIRCLES.getMnemokey());
        rbModeCreatingCircles.setAccelerator(KeyStroke.getKeyStroke(Mode.CREATING_CIRCLES.getHotkey(), ActionEvent.CTRL_MASK));
        rbModeCreatingCircles.addActionListener(new ModeChangedEvent(this, Mode.CREATING_CIRCLES));
        menuModeTab.add(rbModeCreatingCircles);
        modes.add(rbModeCreatingCircles);

        // Mode Editing
        rbModeEdit = new JRadioButtonMenuItem("Editing");
        rbModeEdit.setMnemonic(Mode.EDITING.getMnemokey());
        rbModeEdit.setAccelerator(KeyStroke.getKeyStroke(Mode.EDITING.getHotkey(), ActionEvent.CTRL_MASK));
        rbModeEdit.addActionListener(new ModeChangedEvent(this, Mode.EDITING));
        menuModeTab.add(rbModeEdit);
        modes.add(rbModeEdit);

        setJMenuBar(menuBar);

        cvs.clearCanvas(); // initialization with clear canvas

        setVisible(true);
    }
}
