package com.geocreator;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * It's a menu that pops up when you click on a {@link PolyShape} in {@link GeometricCanvas}.
 * Allows you to choose color from {@link JColorChooser} for active figure, delete that figure it and move it to front or bottom layer.
 * @see PolyShape
 * @see GeometricCanvas
 * @see JColorChooser
 */
public class PopupShapeEditMenu extends JPopupMenu implements ChangeListener {
    private MainWindow outerWindow;
    private JColorChooser jcc = new JColorChooser();
    private JMenu submenuColorPicker;

    /**
     * Loads image icon file to menu items
     * @param path path of the file
     * @param description icon description
     * @return ImageIcon resource
     * @see ImageIcon
     * @see java.net.URL
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
     * Sets menu item color and palette corresponding to the color of the current active PolyShape (probe it).
     * @param color Color to be changed to
     */
    public void setPickerColor(Color color) {
        submenuColorPicker.setBackground(color);
        jcc.setColor(color);
    }

    /**
     * Default constructor.
     * Builds whole menu.
     * @param name menu name
     * @param mw reference to window frame
     */
    public PopupShapeEditMenu(String name, MainWindow mw) {
        super(name);
        outerWindow = mw;

        AbstractColorChooserPanel[] oldPanels = jcc.getChooserPanels();
        for (int i = 0; i < oldPanels.length; i++) {
            String clsName = oldPanels[i].getDisplayName();
            // Select only HSV chooser panel
            if (!clsName.equals("HSV")) {
                jcc.removeChooserPanel(oldPanels[i]);
            }
        }
        jcc.getSelectionModel().addChangeListener(this);
        jcc.setPreviewPanel(new JPanel());
        jcc.setBorder(BorderFactory.createTitledBorder("Choose Shape Color"));

        submenuColorPicker = new JMenu("Shape color");
        submenuColorPicker.setIcon(createImageIcon("/img/paint_bucket.png", "Paint"));
        submenuColorPicker.setOpaque(true);
        submenuColorPicker.add(jcc);
        add(submenuColorPicker);

        JMenuItem menuItem;

        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(actionEvent -> {
            outerWindow.cvs.removePolyShape(outerWindow.cvs.getActualPolyShape());
        });
        menuItem.setIcon(createImageIcon("/img/delete.png", "Delete shape"));
        add(menuItem);

        menuItem = new JMenuItem("Move to front");
        menuItem.addActionListener(actionEvent -> {
            outerWindow.cvs.movePolyShapeToFront(outerWindow.cvs.getActualPolyShape());
        });
        menuItem.setIcon(createImageIcon("/img/layer_front.png", "Move to front"));
        add(menuItem);

        menuItem = new JMenuItem("Move to bottom");
        menuItem.addActionListener(actionEvent -> {
            outerWindow.cvs.movePolyShapeToBottom(outerWindow.cvs.getActualPolyShape());
        });
        menuItem.setIcon(createImageIcon("/img/layer_bottom.png", "Move to bottom"));
        add(menuItem);
    }

    /**
     * Updates shape color and menu item color corresponding to the color chosen from the palette.
     */
    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        submenuColorPicker.setBackground(jcc.getColor());
        outerWindow.cvs.getActualPolyShape().setBackgroundColor(jcc.getColor());
        outerWindow.cvs.repaint();
    }
}
