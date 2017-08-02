package com.geocreator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Event fired in order to clear GeometricCanvas and set working file to blank new file.
 */
public class NewFileEvent implements ActionListener {
    private MainWindow outerWindow;
    public NewFileEvent(MainWindow mw) {
        this.outerWindow = mw;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        outerWindow.cvs.clearCanvas();
        outerWindow.setNewFileFlag(true);
        outerWindow.setCurrentFile("New File");
        System.out.println("[Event] " + this.getClass().getSimpleName());
    }
}
