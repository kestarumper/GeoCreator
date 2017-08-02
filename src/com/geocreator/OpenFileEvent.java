package com.geocreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StreamCorruptedException;

/**
 * Event fired to choose an existing file with saved shapes
 * to load that data into GeometricCanvas.
 * @see GeometricCanvas
 * @see GeometricCanvasSavedState
 * @see JFileChooser
 */
public class OpenFileEvent implements ActionListener {
    private MainWindow outerWindow;
    public OpenFileEvent(MainWindow mw) {
        this.outerWindow = mw;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        try {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(outerWindow);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getPath();
                outerWindow.cvs.loadStateFromFile(path);
                outerWindow.setCurrentFile(path);
            } else {
                throw new IOException("No file approved.");
            }
            outerWindow.setNewFileFlag(false);
            JOptionPane.showMessageDialog(outerWindow, "File loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }  catch(StreamCorruptedException e) {
            JOptionPane.showMessageDialog(null, "Wrong file type or corrupted data.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Could not open file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("[Event] " + this.getClass().getSimpleName());
    }
}
