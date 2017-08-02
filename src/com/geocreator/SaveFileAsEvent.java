package com.geocreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Event fired to choose an existing (overwrite) or new file (create)
 * to save data from GeometricCanvas.
 * @see GeometricCanvas
 * @see GeometricCanvasSavedState
 * @see JFileChooser
 */
public class SaveFileAsEvent implements ActionListener {
    private MainWindow outerWindow;

    public SaveFileAsEvent(MainWindow mw) {
        this.outerWindow = mw;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        try {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(outerWindow);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getPath();
                outerWindow.cvs.saveStateToFile(path);
                outerWindow.setCurrentFile(path);
            } else {
                throw new IOException("No file approved.");
            }
            outerWindow.setNewFileFlag(false);
            JOptionPane.showMessageDialog(outerWindow, "File saved sucessfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File save failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("[Event] " + this.getClass().getSimpleName());
    }
}
