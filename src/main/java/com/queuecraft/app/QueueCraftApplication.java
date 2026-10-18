package com.queuecraft.app;

import com.queuecraft.ui.QueueCraftFrame;
import javax.swing.SwingUtilities;

public final class QueueCraftApplication {
  private QueueCraftApplication() {}

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new QueueCraftFrame().setVisible(true));
  }
}
