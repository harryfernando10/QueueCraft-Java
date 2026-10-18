package com.queuevanta.app;

import com.queuevanta.ui.QueuevantaFrame;
import javax.swing.SwingUtilities;

/** Starts the Queuevanta desktop application on Swing's Event Dispatch Thread. */
public final class QueuevantaApplication {
    private QueuevantaApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QueuevantaFrame().setVisible(true));
    }
}
