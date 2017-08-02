package com.geocreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Event fired update (overwrite) current active file that has been saved before.
 * If file hasn't been saved yet (eg. new file) it will show dialog to choose file where to save it.
 * @see GeometricCanvas
 * @see GeometricCanvasSavedState
 * @see JFileChooser
 */
public class SaveFileEvent implements ActionListener {
    private MainWindow outerWindow;

    public SaveFileEvent(MainWindow mw) {
        this.outerWindow = mw;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        try {
            String path = outerWindow.getCurrentFile();
            if(outerWindow.isNewFileFlag()) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(outerWindow);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    path = fc.getSelectedFile().getPath();
                } else {
                    throw new IOException("No file approved.");
                }
            }
            outerWindow.cvs.saveStateToFile(path);
            outerWindow.setCurrentFile(path);
            outerWindow.setNewFileFlag(false);
            JOptionPane.showMessageDialog(outerWindow, "File saved sucessfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File save failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("[Event] " + this.getClass().getSimpleName());
    }
}
