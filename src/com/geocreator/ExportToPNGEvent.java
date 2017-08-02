package com.geocreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Event fired to export current image drawn on canvas to file. File chooser is opened to point file you want to save to.
 * Shows message dialogs with either Success or Error message if operation was successfull or not.
 */
public class ExportToPNGEvent implements ActionListener {
    private MainWindow outerWindow;

    public ExportToPNGEvent(MainWindow mw) {
        this.outerWindow = mw;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        try {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(outerWindow);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getPath();
                outerWindow.cvs.exportToPNG(path);
            } else {
                throw new IOException("No file approved.");
            }
            JOptionPane.showMessageDialog(outerWindow, "File exported sucessfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File export failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("[Event] " + this.getClass().getSimpleName());
    }
}
