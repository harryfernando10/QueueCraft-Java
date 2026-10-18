package com.queuecraft.ui;

import com.queuecraft.concurrent.SimulationRunner;
import com.queuecraft.engine.SimulationEngine;
import com.queuecraft.model.CounterSnapshot;
import com.queuecraft.model.ServiceCounter;
import com.queuecraft.model.ServiceType;
import com.queuecraft.service.CounterValidator;
import com.queuecraft.service.SimulationReportExporter;
import com.queuecraft.service.SimulationStats;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/** Main dashboard for configuring and observing the QueueCraft simulation. */
public final class QueueCraftFrame extends JFrame {
  private static final Color BACKGROUND = new Color(13, 21, 34);
  private static final Color SURFACE = new Color(24, 36, 54);
  private static final Color SURFACE_LIGHT = new Color(31, 47, 68);
  private static final Color TEXT = new Color(235, 242, 250);
  private static final Color MUTED = new Color(160, 177, 197);
  private static final Color TEAL = new Color(44, 190, 177);
  private static final Color BLUE = new Color(83, 156, 238);
  private static final Color GREEN = new Color(88, 195, 137);
  private static final Color AMBER = new Color(240, 183, 82);
  private static final Color RED = new Color(230, 113, 113);

  private final SimulationEngine engine = new SimulationEngine();
  private final SimulationRunner runner = new SimulationRunner(engine, this::refresh);
  private final DefaultListModel<CounterSnapshot> counterModel = new DefaultListModel<>();
  private final JList<CounterSnapshot> counterList = new JList<>(counterModel);
  private final JTextArea queues = new JTextArea();
  private final JTextArea log = new JTextArea();
  private final JLabel simulationState = new JLabel();
  private final JLabel generatedValue = new JLabel("0");
  private final JLabel waitingValue = new JLabel("0");
  private final JLabel servingValue = new JLabel("0");
  private final JLabel completedValue = new JLabel("0");
  private final JLabel averageWaitValue = new JLabel("0.0 ticks");
  private final JLabel footerStatus = new JLabel("Ready");
  private final JComboBox<ServiceType> service = new JComboBox<>(ServiceType.values());
  private final JSpinner batch = new JSpinner(new SpinnerNumberModel(6, 1, 100, 1));
  private final JSpinner waitThreshold = new JSpinner(new SpinnerNumberModel(12, 1, 1000, 1));
  private final JButton availability = new JButton("Toggle availability");

  public QueueCraftFrame() {
    super("QueueCraft | Campus Service Queue Simulator");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setMinimumSize(new Dimension(920, 680));
    setSize(1200, 820);
    setLocationRelativeTo(null);
    build();
    seed();
    refresh();
    addWindowListener(
        new java.awt.event.WindowAdapter() {
          @Override
          public void windowClosing(java.awt.event.WindowEvent event) {
            runner.close();
            dispose();
          }
        });
  }

  private void build() {
    JPanel root = new JPanel(new BorderLayout(16, 16));
    root.setBorder(new EmptyBorder(22, 24, 18, 24));
    root.setBackground(BACKGROUND);
    setContentPane(root);
    root.add(buildHeader(), BorderLayout.NORTH);

    JSplitPane dashboard =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildCounters(), buildInsights());
    dashboard.setResizeWeight(0.44);
    dashboard.setDividerLocation(480);
    dashboard.setBorder(null);
    dashboard.setOpaque(false);
    root.add(dashboard, BorderLayout.CENTER);
    root.add(buildControls(), BorderLayout.SOUTH);
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new BorderLayout(12, 14));
    header.setOpaque(false);
    JPanel titleBlock = new JPanel(new BorderLayout(4, 5));
    titleBlock.setOpaque(false);
    JLabel title = label("QueueCraft", 27, Font.BOLD, TEXT);
    JLabel subtitle =
        label("Explore how service rates and counter capacity shape simulated queues.", 13, Font.PLAIN, MUTED);
    titleBlock.add(title, BorderLayout.NORTH);
    titleBlock.add(subtitle, BorderLayout.CENTER);
    simulationState.setFont(new Font("Segoe UI", Font.BOLD, 12));
    simulationState.setForeground(TEAL);
    simulationState.setHorizontalAlignment(SwingConstants.RIGHT);
    header.add(titleBlock, BorderLayout.CENTER);
    header.add(simulationState, BorderLayout.EAST);

    JPanel kpis = new JPanel(new GridLayout(1, 5, 10, 0));
    kpis.setOpaque(false);
    kpis.add(kpi("GENERATED", generatedValue, BLUE));
    kpis.add(kpi("WAITING", waitingValue, AMBER));
    kpis.add(kpi("SERVING", servingValue, TEAL));
    kpis.add(kpi("COMPLETED", completedValue, GREEN));
    kpis.add(kpi("AVERAGE WAIT", averageWaitValue, new Color(178, 145, 235)));
    header.add(kpis, BorderLayout.SOUTH);
    return header;
  }

  private JPanel kpi(String title, JLabel value, Color accent) {
    JPanel panel = surfacePanel();
    panel.setLayout(new BorderLayout(2, 6));
    panel.setBorder(new EmptyBorder(12, 14, 12, 14));
    JLabel caption = label(title, 10, Font.BOLD, MUTED);
    value.setFont(new Font("Segoe UI", Font.BOLD, 21));
    value.setForeground(accent);
    panel.add(caption, BorderLayout.NORTH);
    panel.add(value, BorderLayout.CENTER);
    return panel;
  }

  private JPanel buildCounters() {
    JPanel panel = card("Service counters", "Select a counter to manage availability or remove it.");
    panel.setLayout(new BorderLayout(8, 10));
    counterList.setBackground(SURFACE);
    counterList.setSelectionBackground(SURFACE_LIGHT);
    counterList.setSelectionForeground(TEXT);
    counterList.setFixedCellHeight(104);
    counterList.setCellRenderer(new CounterRenderer());
    counterList.setToolTipText("Each card shows service, capacity, current work, and estimated wait.");
    JScrollPane scroll = new JScrollPane(counterList);
    styleScroll(scroll);
    panel.add(scroll, BorderLayout.CENTER);

    JPanel actions = new JPanel(new GridLayout(1, 3, 8, 0));
    actions.setOpaque(false);
    JButton add = secondaryButton("Add counter");
    availability.setToolTipText("Disabling preserves active and waiting customers; re-enable the counter to resume its queue.");
    availability.setForeground(TEAL);
    JButton remove = secondaryButton("Remove idle counter");
    add.setToolTipText("Add a counter with a unique name and service duration from 1 to 100 ticks.");
    remove.setToolTipText("Remove a counter only when it has no active or waiting customers.");
    actions.add(add);
    actions.add(availability);
    actions.add(remove);
    panel.add(actions, BorderLayout.SOUTH);
    add.addActionListener(event -> addCounter());
    availability.addActionListener(event -> toggleSelectedCounter());
    remove.addActionListener(event -> removeSelectedCounter());
    counterList.addListSelectionListener(event -> updateAvailabilityButton());
    return panel;
  }

  private JPanel buildInsights() {
    JPanel panel = card("Queue view and activity", "Customer queues are FIFO; service counters advance in parallel.");
    panel.setLayout(new BorderLayout(8, 8));
    configureTextArea(queues);
    configureTextArea(log);
    queues.setToolTipText("Active and waiting customers grouped by service counter.");
    log.setToolTipText("Recent simulation events in tick order.");
    JScrollPane queuePane = new JScrollPane(queues);
    JScrollPane logPane = new JScrollPane(log);
    styleScroll(queuePane);
    styleScroll(logPane);
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, titledPane("CUSTOMERS BY COUNTER", queuePane), titledPane("RECENT EVENTS", logPane));
    split.setResizeWeight(0.59);
    split.setBorder(null);
    split.setOpaque(false);
    panel.add(split, BorderLayout.CENTER);
    return panel;
  }

  private JPanel buildControls() {
    JPanel footer = surfacePanel();
    footer.setLayout(new BorderLayout(8, 8));
    footer.setBorder(new EmptyBorder(12, 14, 12, 14));
    JPanel controlRows = new JPanel(new GridLayout(2, 1, 0, 6));
    controlRows.setOpaque(false);
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actions.setOpaque(false);
    options.setOpaque(false);

    JButton start = primaryButton("Start / resume");
    JButton pause = secondaryButton("Pause");
    JButton generateOne = secondaryButton("Generate 1 customer");
    JButton generateBatch = secondaryButton("Generate batch");
    JButton reset = destructiveButton("Reset simulation");
    JButton export = secondaryButton("Export CSV");
    start.setToolTipText("Advance the simulation by one tick every 0.4 seconds.");
    pause.setToolTipText("Stop tick progression and keep current queues and customers.");
    generateOne.setToolTipText("Add one customer for the selected service.");
    generateBatch.setToolTipText("Add the selected number of customers (1–100) for this service.");
    reset.setToolTipText("Clear all customers, event history, and metrics while keeping configured counters.");
    export.setToolTipText("Save the current simulation and per-counter summary as a UTF-8 CSV file.");
    service.setToolTipText("Service requested by the next generated customer or batch.");
    batch.setToolTipText("Number of customers to generate in one batch (1–100).");
    waitThreshold.setToolTipText("Show a bottleneck warning at or above this estimated wait (ticks).");

    JLabel thresholdLabel = new JLabel("Warn at estimated wait ≥");
    thresholdLabel.setForeground(MUTED);
    actions.add(start);
    actions.add(pause);
    actions.add(service);
    actions.add(generateOne);
    actions.add(batch);
    actions.add(generateBatch);
    options.add(thresholdLabel);
    options.add(waitThreshold);
    options.add(reset);
    options.add(export);
    controlRows.add(actions);
    controlRows.add(options);
    footerStatus.setForeground(MUTED);
    footerStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    footer.add(controlRows, BorderLayout.CENTER);
    footer.add(footerStatus, BorderLayout.SOUTH);

    start.addActionListener(event -> runner.resume());
    pause.addActionListener(
        event -> {
          runner.pause();
          refresh();
        });
    generateOne.addActionListener(event -> generateCustomers(1));
    generateBatch.addActionListener(event -> generateCustomers((Integer) batch.getValue()));
    reset.addActionListener(event -> confirmReset());
    export.addActionListener(event -> exportCsv());
    service.setForeground(TEXT);
    waitThreshold.addChangeListener(event -> refreshCounters());
    return footer;
  }

  private JPanel card(String title, String subtitle) {
    JPanel panel = surfacePanel();
    panel.setBorder(new EmptyBorder(14, 14, 14, 14));
    JPanel heading = new JPanel(new BorderLayout(3, 4));
    heading.setOpaque(false);
    heading.add(label(title, 16, Font.BOLD, TEXT), BorderLayout.NORTH);
    heading.add(label(subtitle, 11, Font.PLAIN, MUTED), BorderLayout.CENTER);
    panel.add(heading, BorderLayout.NORTH);
    return panel;
  }

  private JPanel surfacePanel() {
    JPanel panel = new JPanel();
    panel.setBackground(SURFACE);
    panel.setOpaque(true);
    return panel;
  }

  private JPanel titledPane(String title, Component content) {
    JPanel panel = new JPanel(new BorderLayout(0, 6));
    panel.setOpaque(false);
    panel.add(label(title, 10, Font.BOLD, MUTED), BorderLayout.NORTH);
    panel.add(content, BorderLayout.CENTER);
    return panel;
  }

  private JLabel label(String text, int size, int style, Color color) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", style, size));
    label.setForeground(color);
    return label;
  }

  private JButton primaryButton(String text) {
    JButton button = new JButton(text);
    button.setBackground(TEAL);
    button.setForeground(BACKGROUND);
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    button.setFocusPainted(false);
    return button;
  }

  private JButton secondaryButton(String text) {
    JButton button = new JButton(text);
    button.setBackground(SURFACE_LIGHT);
    button.setForeground(TEXT);
    button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    button.setFocusPainted(false);
    return button;
  }

  private JButton destructiveButton(String text) {
    JButton button = secondaryButton(text);
    button.setForeground(RED);
    return button;
  }

  private void styleScroll(JScrollPane scroll) {
    scroll.setBorder(BorderFactory.createLineBorder(new Color(47, 63, 83)));
    scroll.getViewport().setBackground(SURFACE);
  }

  private void configureTextArea(JTextArea area) {
    area.setEditable(false);
    area.setFont(new Font("Consolas", Font.PLAIN, 12));
    area.setForeground(TEXT);
    area.setBackground(new Color(18, 29, 44));
    area.setCaretColor(TEXT);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setBorder(new EmptyBorder(10, 10, 10, 10));
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
    name.setToolTipText("Use a unique 2–30 character name starting with a letter.");
    type.setToolTipText("The service type this counter can handle.");
    ticks.setToolTipText("How many simulated ticks one customer needs (1–100).");
    JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
    form.add(new JLabel("Counter name"));
    form.add(name);
    form.add(new JLabel("Service type"));
    form.add(type);
    form.add(new JLabel("Service duration (ticks)"));
    form.add(ticks);
    int choice =
        JOptionPane.showConfirmDialog(
            this, form, "Add service counter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) return;
    try {
      ServiceCounter counter =
          CounterValidator.create(
              name.getText(), (ServiceType) type.getSelectedItem(), (Integer) ticks.getValue(), engine.getCounters());
      engine.addCounter(counter);
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void toggleSelectedCounter() {
    CounterSnapshot selected = counterList.getSelectedValue();
    if (selected == null) {
      showSelectionMessage();
      return;
    }
    try {
      engine.setCounterEnabled(selected.name(), !selected.enabled());
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void removeSelectedCounter() {
    CounterSnapshot selected = counterList.getSelectedValue();
    if (selected == null) {
      showSelectionMessage();
      return;
    }
    int choice =
        JOptionPane.showConfirmDialog(
            this,
            "Remove " + selected.name() + "? Only an idle counter can be removed.",
            "Remove counter",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) return;
    try {
      engine.removeCounter(selected.name());
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void generateCustomers(int count) {
    try {
      engine.generateBatch((ServiceType) service.getSelectedItem(), count);
      footerStatus.setText(count == 1 ? "Generated 1 customer." : "Generated " + count + " customers.");
      refresh();
    } catch (Exception exception) {
      showError(exception);
    }
  }

  private void confirmReset() {
    int choice =
        JOptionPane.showConfirmDialog(
            this,
            "Reset clears all customers, event history, and metrics, then pauses. Counters are kept. Continue?",
            "Reset simulation",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
    if (choice == JOptionPane.OK_OPTION) {
      runner.reset();
      footerStatus.setText("Simulation reset and paused; configured counters were kept.");
    }
  }

  private void exportCsv() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Export simulation summary");
    chooser.setSelectedFile(new java.io.File("queuecraft-summary.csv"));
    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
    Path destination = chooser.getSelectedFile().toPath();
    if (Files.isDirectory(destination)) {
      showError(new IOException("Choose a file name, not a folder, for the CSV summary."));
      return;
    }
    if (!destination.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv")) {
      destination = destination.resolveSibling(destination.getFileName() + ".csv");
    }
    if (Files.exists(destination)) {
      int choice =
          JOptionPane.showConfirmDialog(
              this,
              "Replace the existing file " + destination.getFileName() + "?",
              "Confirm CSV overwrite",
              JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.WARNING_MESSAGE);
      if (choice != JOptionPane.OK_OPTION) return;
    }
    try {
      SimulationReportExporter.export(destination, engine.stats(), engine.getCounterSnapshots());
      footerStatus.setText("CSV summary saved: " + destination.getFileName());
    } catch (IOException exception) {
      showError(exception);
    }
  }

  private void updateAvailabilityButton() {
    CounterSnapshot selected = counterList.getSelectedValue();
    availability.setText(selected == null ? "Toggle availability" : selected.enabled() ? "Disable selected" : "Enable selected");
  }

  private void showSelectionMessage() {
    JOptionPane.showMessageDialog(this, "Select a counter first.", "QueueCraft", JOptionPane.INFORMATION_MESSAGE);
  }

  private void refresh() {
    SimulationStats stats = engine.stats();
    simulationState.setText("●  " + (engine.isPaused() ? "PAUSED" : "RUNNING") + "    TICK " + engine.getTick());
    generatedValue.setText(Integer.toString(stats.generated()));
    waitingValue.setText(Integer.toString(stats.waiting()));
    servingValue.setText(Integer.toString(stats.serving()));
    completedValue.setText(Integer.toString(stats.completed()));
    averageWaitValue.setText(String.format("%.1f ticks", stats.averageWait()));
    refreshCounters();
    renderQueues(engine.getCounterSnapshots());
    renderEvents(engine.getEvents());
  }

  private void refreshCounters() {
    List<CounterSnapshot> snapshots = engine.getCounterSnapshots();
    CounterSnapshot previous = counterList.getSelectedValue();
    String selectedName = previous == null ? null : previous.name();
    counterModel.clear();
    for (CounterSnapshot snapshot : snapshots) counterModel.addElement(snapshot);
    if (selectedName != null) {
      for (int index = 0; index < counterModel.size(); index++) {
        if (counterModel.get(index).name().equals(selectedName)) counterList.setSelectedIndex(index);
      }
    }
    if (snapshots.isEmpty()) {
      footerStatus.setText("No counters configured. Add a counter to begin a scenario.");
    }
    updateAvailabilityButton();
    counterList.repaint();
  }

  private void renderQueues(List<CounterSnapshot> snapshots) {
    StringBuilder text = new StringBuilder();
    for (CounterSnapshot counter : snapshots) {
      text.append(counter.name()).append("  ·  ").append(counter.enabled() ? "Enabled" : "Disabled").append('\n');
      text.append("  Serving: ");
      if (counter.activeCustomer() == null) text.append("nobody");
      else text.append(counter.activeCustomer()).append(" (").append(counter.remainingTicks()).append(" ticks left)");
      text.append("\n  Waiting: ");
      text.append(counter.waitingCustomers().isEmpty() ? "nobody" : String.join(", ", counter.waitingCustomers()));
      text.append("\n\n");
    }
    queues.setText(text.length() == 0 ? "No service counters configured.\nAdd a counter to start exploring a queue scenario." : text.toString());
    queues.setCaretPosition(0);
  }

  private void renderEvents(List<String> events) {
    StringBuilder text = new StringBuilder();
    for (String event : events) text.append(event).append('\n');
    log.setText(text.length() == 0 ? "No events yet. Start the simulation or generate customers to see activity." : text.toString());
    log.setCaretPosition(log.getDocument().getLength());
  }

  private void showError(Exception exception) {
    String message =
        exception.getMessage() == null ? "The requested action could not be completed." : exception.getMessage();
    JOptionPane.showMessageDialog(this, message, "QueueCraft", JOptionPane.WARNING_MESSAGE);
  }

  private final class CounterRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean selected, boolean focused) {
      CounterSnapshot counter = (CounterSnapshot) value;
      JPanel card = new JPanel(new BorderLayout(8, 5));
      card.setBorder(new EmptyBorder(10, 12, 10, 12));
      card.setBackground(selected ? SURFACE_LIGHT : SURFACE);
      JPanel top = new JPanel(new BorderLayout());
      top.setOpaque(false);
      JLabel name = label(counter.name(), 14, Font.BOLD, TEXT);
      JLabel status = label(counter.enabled() ? "ENABLED" : "DISABLED", 10, Font.BOLD, counter.enabled() ? GREEN : MUTED);
      top.add(name, BorderLayout.WEST);
      top.add(status, BorderLayout.EAST);
      JLabel serviceLabel = label(counter.serviceType().getLabel() + "  ·  " + counter.serviceTicks() + " ticks per customer", 11, Font.PLAIN, MUTED);
      String active = counter.activeCustomer() == null ? "Idle" : counter.activeCustomer() + " · " + counter.remainingTicks() + " ticks left";
      JLabel workload = label("Serving: " + active + "     Waiting: " + counter.waitingCustomers().size() + "     Completed: " + counter.completed(), 11, Font.PLAIN, TEXT);
      boolean warning = counter.estimatedWaitTicks() >= (Integer) waitThreshold.getValue() && counter.load() > 0;
      JLabel estimate = label(
          warning ? "BOTTLENECK  ·  estimated wait " + counter.estimatedWaitTicks() + " ticks (threshold " + waitThreshold.getValue() + ")"
              : "Estimated wait " + counter.estimatedWaitTicks() + " ticks",
          10, Font.BOLD, warning ? AMBER : TEAL);
      JPanel details = new JPanel(new GridLayout(3, 1, 2, 2));
      details.setOpaque(false);
      details.add(serviceLabel);
      details.add(workload);
      details.add(estimate);
      card.add(top, BorderLayout.NORTH);
      card.add(details, BorderLayout.CENTER);
      return card;
    }
  }
}
