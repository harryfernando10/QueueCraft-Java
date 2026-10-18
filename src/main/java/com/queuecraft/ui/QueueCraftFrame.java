package com.queuecraft.ui;

import com.queuecraft.concurrent.SimulationRunner;
import com.queuecraft.engine.SimulationEngine;
import com.queuecraft.model.Customer;
import com.queuecraft.model.ServiceCounter;
import com.queuecraft.model.ServiceType;
import com.queuecraft.service.CounterValidator;
import com.queuecraft.service.SimulationStats;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/** Main dashboard for configuring and observing the QueueCraft simulation. */
public final class QueueCraftFrame extends JFrame {
  private static final Color BACKGROUND = new Color(244, 247, 251);
  private static final Color INK = new Color(31, 48, 68);
  private static final Color MUTED = new Color(91, 108, 128);
  private static final Color ACCENT = new Color(33, 105, 158);

  private final SimulationEngine engine = new SimulationEngine();
  private final SimulationRunner runner = new SimulationRunner(engine, this::refresh);
  private final DefaultListModel<String> counterModel = new DefaultListModel<>();
  private final JList<String> counterList = new JList<>(counterModel);
  private final JTextArea queues = new JTextArea();
  private final JTextArea log = new JTextArea();
  private final JLabel summary = new JLabel();
  private final JLabel stats = new JLabel();
  private final JComboBox<ServiceType> service = new JComboBox<>(ServiceType.values());
  private final JSpinner batch = new JSpinner(new SpinnerNumberModel(6, 1, 100, 1));

  public QueueCraftFrame() {
    super("QueueCraft — Campus Service Queue & Bottleneck Simulator");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setMinimumSize(new Dimension(900, 680));
    setSize(1180, 820);
    setLocationRelativeTo(null);
    build();
    seed();
    refresh();
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent event) {
            runner.close();
            dispose();
          }
        });
  }

  private void build() {
    JPanel root = new JPanel(new BorderLayout(14, 14));
    root.setBorder(new EmptyBorder(20, 22, 20, 22));
    root.setBackground(BACKGROUND);
    setContentPane(root);
    root.add(buildHeader(), BorderLayout.NORTH);

    JPanel body = new JPanel(new GridLayout(1, 2, 14, 0));
    body.setOpaque(false);
    body.add(buildCounters());
    body.add(buildActivity());
    root.add(body, BorderLayout.CENTER);
    root.add(buildFooter(), BorderLayout.SOUTH);
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new GridLayout(2, 1, 2, 2));
    header.setOpaque(false);
    JLabel title = new JLabel("QueueCraft");
    title.setFont(new Font("Segoe UI", Font.BOLD, 28));
    title.setForeground(INK);
    JLabel subtitle =
        new JLabel(
            "Explore how service rates, queues, and parallel counters interact. One tick = 0.4"
                + " seconds.");
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    subtitle.setForeground(MUTED);
    header.add(title);
    header.add(subtitle);
    return header;
  }

  private JPanel buildCounters() {
    JPanel panel = card("Service counters");
    panel.setLayout(new BorderLayout(10, 10));
    counterList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    counterList.setFixedCellHeight(56);
    counterList.setToolTipText("Select a counter to enable, disable, or remove it.");
    panel.add(new JScrollPane(counterList), BorderLayout.CENTER);

    JPanel controls = new JPanel(new GridLayout(0, 1, 7, 7));
    controls.setOpaque(false);
    JButton add = new JButton("Add counter…");
    JButton toggle = new JButton("Enable / disable selected");
    JButton remove = new JButton("Remove selected");
    add.setToolTipText("Create a counter with a service type and duration.");
    toggle.setToolTipText("Disabled counters do not receive new customers.");
    remove.setToolTipText("Only an idle counter can be removed.");
    controls.add(add);
    controls.add(toggle);
    controls.add(remove);
    panel.add(controls, BorderLayout.SOUTH);

    add.addActionListener(event -> addCounter());
    toggle.addActionListener(event -> toggleSelectedCounter());
    remove.addActionListener(event -> removeSelectedCounter());
    return panel;
  }

  private JPanel buildActivity() {
    JPanel panel = card("Queues and event history");
    panel.setLayout(new BorderLayout(8, 8));
    configureTextArea(queues);
    configureTextArea(log);
    queues.setToolTipText("Current service and waiting customers, grouped by counter.");
    log.setToolTipText("Recent simulation events, newest at the end.");
    JScrollPane queuePane = new JScrollPane(queues);
    queuePane.setBorder(BorderFactory.createTitledBorder("Customers by counter"));
    JScrollPane logPane = new JScrollPane(log);
    logPane.setBorder(BorderFactory.createTitledBorder("Recent events"));
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queuePane, logPane);
    split.setResizeWeight(0.58);
    split.setBorder(null);
    panel.add(split, BorderLayout.CENTER);
    return panel;
  }

  private JPanel buildFooter() {
    JPanel footer = new JPanel(new BorderLayout(8, 10));
    footer.setOpaque(false);
    JPanel metrics = new JPanel(new GridLayout(2, 1, 3, 3));
    metrics.setOpaque(false);
    summary.setFont(new Font("Segoe UI", Font.BOLD, 14));
    summary.setForeground(INK);
    stats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    stats.setForeground(MUTED);
    metrics.add(summary);
    metrics.add(stats);
    footer.add(metrics, BorderLayout.NORTH);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actions.setOpaque(false);
    JButton run = new JButton("Start / resume");
    JButton pause = new JButton("Pause");
    JButton reset = new JButton("Reset");
    JButton generate = new JButton("Generate batch");
    run.setBackground(ACCENT);
    run.setForeground(Color.WHITE);
    run.setToolTipText("Advance the simulation every 0.4 seconds.");
    pause.setToolTipText("Stop advancing the simulation; existing queues are kept.");
    reset.setToolTipText("Clear customers and statistics while preserving counters.");
    service.setToolTipText("Choose the service requested by the new batch.");
    batch.setToolTipText("Choose a batch size from 1 to 100 customers.");
    actions.add(run);
    actions.add(pause);
    actions.add(reset);
    actions.add(service);
    actions.add(batch);
    actions.add(generate);
    footer.add(actions, BorderLayout.SOUTH);

    run.addActionListener(event -> runner.resume());
    pause.addActionListener(
        event -> {
          runner.pause();
          refresh();
        });
    reset.addActionListener(event -> runner.reset());
    generate.addActionListener(event -> generateBatch());
    return footer;
  }

  private JPanel card(String title) {
    JPanel panel = new JPanel();
    panel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title), new EmptyBorder(8, 8, 8, 8)));
    panel.setBackground(Color.WHITE);
    return panel;
  }

  private void configureTextArea(JTextArea area) {
    area.setEditable(false);
    area.setFont(new Font("Consolas", Font.PLAIN, 12));
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setBackground(new Color(250, 252, 254));
    area.setBorder(new EmptyBorder(8, 8, 8, 8));
  }

  private void seed() {
    try {
      engine.addCounter(new ServiceCounter("Canteen A", ServiceType.CANTEEN, 3));
      engine.addCounter(new ServiceCounter("Canteen B", ServiceType.CANTEEN, 5));
      engine.addCounter(new ServiceCounter("Library Desk", ServiceType.LIBRARY, 4));
      engine.addCounter(new ServiceCounter("Student Office", ServiceType.STUDENT_OFFICE, 6));
      engine.addCounter(new ServiceCounter("IT Help Desk", ServiceType.IT_HELP, 3));
      engine.generateBatch(ServiceType.CANTEEN, 10);
      engine.generateBatch(ServiceType.LIBRARY, 4);
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void addCounter() {
    JTextField name = new JTextField();
    JComboBox<ServiceType> type = new JComboBox<>(ServiceType.values());
    JSpinner ticks = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
    JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
    form.add(new JLabel("Name"));
    form.add(name);
    form.add(new JLabel("Service"));
    form.add(type);
    form.add(new JLabel("Duration (ticks)"));
    form.add(ticks);
    int choice =
        JOptionPane.showConfirmDialog(
            this,
            form,
            "Add service counter",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) {
      return;
    }
    try {
      ServiceCounter counter =
          CounterValidator.create(
              name.getText(),
              (ServiceType) type.getSelectedItem(),
              (Integer) ticks.getValue(),
              engine.getCounters());
      engine.addCounter(counter);
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void toggleSelectedCounter() {
    String name = selectedName();
    if (name == null) {
      showSelectionMessage();
      return;
    }
    try {
      ServiceCounter counter = findCounter(name);
      engine.setCounterEnabled(name, !counter.isEnabled());
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void removeSelectedCounter() {
    String name = selectedName();
    if (name == null) {
      showSelectionMessage();
      return;
    }
    try {
      engine.removeCounter(name);
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void generateBatch() {
    try {
      engine.generateBatch((ServiceType) service.getSelectedItem(), (Integer) batch.getValue());
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private ServiceCounter findCounter(String name) {
    return engine.getCounters().stream()
        .filter(counter -> counter.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Selected counter no longer exists."));
  }

  private String selectedName() {
    String selected = counterList.getSelectedValue();
    return selected == null ? null : selected.substring(0, selected.indexOf("  |"));
  }

  private void showSelectionMessage() {
    JOptionPane.showMessageDialog(
        this, "Select a counter first.", "QueueCraft", JOptionPane.INFORMATION_MESSAGE);
  }

  private void refresh() {
    SimulationStats snapshot = engine.stats();
    summary.setText(
        "Tick "
            + engine.getTick()
            + "  ·  "
            + (engine.isPaused() ? "Paused" : "Running")
            + "  ·  "
            + snapshot.generated()
            + " customers generated");
    stats.setText(
        String.format(
            "Waiting %d     Serving %d     Completed %d     Avg wait %.1f ticks"
                + "     Avg service %.1f ticks     Max queue %d",
            snapshot.waiting(),
            snapshot.serving(),
            snapshot.completed(),
            snapshot.averageWait(),
            snapshot.averageService(),
            snapshot.maxQueue()));

    counterModel.clear();
    for (ServiceCounter counter : engine.getCounters()) {
      String active =
          counter.getCurrentCustomer() == null
              ? "Idle"
              : counter.getCurrentCustomer().getDisplayName()
                  + " ("
                  + counter.getRemainingTicks()
                  + " ticks left)";
      counterModel.addElement(
          counter.getName()
              + "  |  "
              + (counter.isEnabled() ? "Enabled" : "Disabled")
              + "  ·  "
              + counter.getServiceType()
              + "  ·  "
              + counter.getServiceTicks()
              + " ticks  ·  "
              + active
              + "  ·  waiting "
              + counter.getWaitingCount());
    }
    renderQueues();
    renderEvents();
  }

  private void renderQueues() {
    StringBuilder text = new StringBuilder();
    for (ServiceCounter counter : engine.getCounters()) {
      text.append(counter.getName())
          .append(" — ")
          .append(counter.isEnabled() ? "Enabled" : "Disabled")
          .append('\n');
      Customer active = counter.getCurrentCustomer();
      text.append("  Serving: ");
      if (active == null) {
        text.append("nobody");
      } else {
        text.append(active.getDisplayName())
            .append(" ( ")
            .append(counter.getRemainingTicks())
            .append(" ticks remaining )");
      }
      text.append("\n  Waiting: ");
      if (counter.getWaitingCount() == 0) {
        text.append("nobody");
      } else {
        boolean first = true;
        for (Customer customer : counter.getWaiting()) {
          if (!first) {
            text.append(", ");
          }
          text.append(customer.getDisplayName());
          first = false;
        }
      }
      text.append("\n\n");
    }
    queues.setText(text.length() == 0 ? "No service counters configured." : text.toString());
    queues.setCaretPosition(0);
  }

  private void renderEvents() {
    StringBuilder text = new StringBuilder();
    for (String event : engine.getEvents()) {
      text.append(event).append('\n');
    }
    log.setText(text.length() == 0 ? "No events yet." : text.toString());
    log.setCaretPosition(log.getDocument().getLength());
  }

  private void showError(Exception exception) {
    String message =
        exception.getMessage() == null
            ? "The requested action could not be completed."
            : exception.getMessage();
    JOptionPane.showMessageDialog(this, message, "QueueCraft", JOptionPane.WARNING_MESSAGE);
  }
}
