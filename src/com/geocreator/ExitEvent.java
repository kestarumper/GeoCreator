package com.geocreator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Event fired to close window with return code 0. Logs that action in terminal.
 */
public class ExitEvent implements ActionListener {
    public ExitEvent() {

    }

    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("[Event] " + this.getClass().getSimpleName());
        System.exit(0);
    }
}
