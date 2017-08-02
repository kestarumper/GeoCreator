package com.geocreator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Event fired when changing current {@link com.geocreator.MainWindow.Mode} either from
 * menu level or button tool level or from hotkey. Updates window title bar adequate to current Mode and working file.
 * @see com.geocreator.MainWindow.Mode
 * @see MainWindow
 */
public class ModeChangedEvent implements ActionListener {
    MainWindow outerWindow;
    MainWindow.Mode mode;

    public ModeChangedEvent(MainWindow mw, MainWindow.Mode md) {
        outerWindow = mw;
        mode = md;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        outerWindow.setActiveMode(mode);

        if(outerWindow.getActiveMode() != MainWindow.Mode.EDITING) {
            outerWindow.cvs.addNewPolyShape(outerWindow.getActiveMode());
        }

        System.out.println("[Event] " + this.getClass().getSimpleName() + " to " + outerWindow.getActiveMode().name());
        outerWindow.updateTitle();

        outerWindow.cvs.repaint();
    }
}
