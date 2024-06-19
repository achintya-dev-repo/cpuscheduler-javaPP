import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessSchedulerApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomeScreenUI().setVisible(true);
        });
    }
}

class WelcomeScreenUI extends JFrame {
    public WelcomeScreenUI() {
        setTitle("Welcome");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.black); // Set background color
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel welcomeLabel = new JLabel("Welcome to Process Scheduler");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.yellow);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        JButton startButton = new JButton("Start");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(startButton, gbc);

        startButton.addActionListener(e -> {
            dispose();
            new ProcessSchedulerUI().setVisible(true);
        });

        add(panel);
        setLocationRelativeTo(null);
    }
}

class ProcessSchedulerUI extends JFrame {
    public ProcessSchedulerUI() {
        setTitle("Process Scheduler");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.CYAN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("PROCESS SCHEDULER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel burstTimeLabel = new JLabel("Burst Time");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(burstTimeLabel, gbc);

        JTextField burstTimeField = new JTextField();
        burstTimeField.setBackground(Color.LIGHT_GRAY);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(burstTimeField, gbc);

        JLabel arrivalTimeLabel = new JLabel("Arrival Time");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(arrivalTimeLabel, gbc);

        JTextField arrivalTimeField = new JTextField();
        arrivalTimeField.setBackground(Color.LIGHT_GRAY);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(arrivalTimeField, gbc);

        JLabel priorityLabel = new JLabel("Priority");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(priorityLabel, gbc);

        JTextField priorityField = new JTextField();
        priorityField.setBackground(Color.LIGHT_GRAY);
        priorityField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(priorityField, gbc);

        JButton runButton = new JButton("Run");
        runButton.setBackground(Color.YELLOW);
        runButton.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(runButton, gbc);

        JTextArea resultArea = new JTextArea(20, 50);
        resultArea.setBackground(Color.LIGHT_GRAY);
        resultArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(new JScrollPane(resultArea), gbc);

        // DocumentListener to update priority field based on burst time and arrival
        // time
        DocumentListener updatePriority = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateAndSetPriority();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateAndSetPriority();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateAndSetPriority();
            }

            private void calculateAndSetPriority() {
                String burstTimeText = burstTimeField.getText().trim();
                String arrivalTimeText = arrivalTimeField.getText().trim();

                if (!burstTimeText.isEmpty() && !arrivalTimeText.isEmpty()) {
                    String[] burstTimes = burstTimeText.split(",");
                    String[] arrivalTimes = arrivalTimeText.split(",");

                    if (burstTimes.length == arrivalTimes.length) {
                        try {
                            StringBuilder priorityValues = new StringBuilder();
                            for (int i = 0; i < burstTimes.length; i++) {
                                int burstTime = Integer.parseInt(burstTimes[i].trim());
                                int arrivalTime = Integer.parseInt(arrivalTimes[i].trim());
                                double priority = (burstTime + arrivalTime) / 2.0;
                                priorityValues.append(priority);
                                if (i < burstTimes.length - 1) {
                                    priorityValues.append(", ");
                                }
                            }
                            priorityField.setText(priorityValues.toString());
                        } catch (NumberFormatException ex) {
                            priorityField.setText("Invalid input");
                        }
                    } else {
                        priorityField.setText("Mismatched input lengths");
                    }
                } else {
                    priorityField.setText("");
                }
            }
        };

        burstTimeField.getDocument().addDocumentListener(updatePriority);
        arrivalTimeField.getDocument().addDocumentListener(updatePriority);

        runButton.addActionListener(e -> {
            String burstTimeString = burstTimeField.getText().trim();
            String arrivalTimeString = arrivalTimeField.getText().trim();
            String priorityString = priorityField.getText().trim();

            if (burstTimeString.isEmpty() || arrivalTimeString.isEmpty() || priorityString.isEmpty()) {
                resultArea.setText("Please enter values in all fields.");
                return;
            }

            String[] burstTimesArray = burstTimeString.split(",");
            String[] arrivalTimesArray = arrivalTimeString.split(",");
            String[] prioritiesArray = priorityString.split(",");

            if (burstTimesArray.length != arrivalTimesArray.length
                    || burstTimesArray.length != prioritiesArray.length) {
                resultArea.setText("Mismatched input lengths");
                return;
            }

            List<Process> processes = new ArrayList<>();

            for (int i = 0; i < burstTimesArray.length; i++) {
                try {
                    int burstTime = Integer.parseInt(burstTimesArray[i].trim());
                    int arrivalTime = Integer.parseInt(arrivalTimesArray[i].trim());
                    double priority = Double.parseDouble(prioritiesArray[i].trim());

                    if (burstTime < 0 || arrivalTime < 0 || priority < 0) {
                        resultArea.setText("Invalid input at index " + i + ": Please enter non-negative values.\n");
                        return;
                    } else {
                        Process process = new Process("P" + (i + 1), arrivalTime, burstTime, priority);
                        processes.add(process);
                    }
                } catch (NumberFormatException ex) {
                    resultArea.setText(
                            "Invalid input at index " + i + ": Please enter valid integer values in all fields.\n");
                    return;
                }
            }

            Scheduler scheduler = new Scheduler();
            for (Process process : processes) {
                scheduler.addProcess(process);
            }
            scheduler.run();

            List<GanttChartEntry> ganttChart = scheduler.getGanttChart();
            double averageWaitingTime = scheduler.getAverageWaitingTime();
            double averageTurnaroundTime = scheduler.getAverageTurnaroundTime();

            StringBuilder resultText = new StringBuilder("Gantt Chart:\n");

            for (GanttChartEntry entry : ganttChart) {
                resultText.append(entry.getId())
                        .append(" [")
                        .append(entry.getStartTime())
                        .append(", ")
                        .append(entry.getEndTime())
                        .append("] | ");
            }

            resultText.append("\nAverage Waiting Time: ").append(averageWaitingTime)
                    .append("\nAverage Turnaround Time: ").append(averageTurnaroundTime);

            resultArea.setText(resultText.toString());
        });

        add(panel);
        setLocationRelativeTo(null);
    }
}

class Process {
    private String id;
    private int arrivalTime;
    private int burstTime;
    private double priority;
    private int startTime = -1; // Initialize to -1 to distinguish unstarted processes
    private int completionTime;
    private int remainingTime;
    private int turnaroundTime;
    private int waitingTime;

    public Process(String id, int arrivalTime, int burstTime, double priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }

    public String getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public double getPriority() {
        return priority;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    @Override
    public String toString() {
        return "Process{id='" + id + "', arrivalTime=" + arrivalTime + ", burstTime=" + burstTime + ", priority="
                + priority + '}';
    }
}

class GanttChartEntry {
    private String id;
    private int startTime;
    private int endTime;

    public GanttChartEntry(String id, int startTime, int endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "GanttChartEntry{id='" + id + "', startTime=" + startTime + ", endTime=" + endTime + '}';
    }
}

class Scheduler {
    private List<Process> processes;
    private List<GanttChartEntry> ganttChart;
    private double averageWaitingTime;
    private double averageTurnaroundTime;

    public Scheduler() {
        this.processes = new ArrayList<>();
        this.ganttChart = new ArrayList<>();
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public void run() {
        int currentTime = 0;
        int completedProcesses = 0;
        int n = processes.size();

        while (completedProcesses < n) {
            // Get the process with the highest priority that has arrived and is not yet
            // completed
            Process highestPriorityProcess = null;
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime && process.getRemainingTime() > 0) {
                    if (highestPriorityProcess == null
                            || process.getPriority() < highestPriorityProcess.getPriority()) {
                        highestPriorityProcess = process;
                    }
                }
            }

            if (highestPriorityProcess != null) {
                // If the process hasn't started yet, set the start time
                if (highestPriorityProcess.getStartTime() == -1) {
                    highestPriorityProcess.setStartTime(currentTime);
                }

                // Process the task for one time unit
                highestPriorityProcess.setRemainingTime(highestPriorityProcess.getRemainingTime() - 1);

                // Update Gantt chart
                if (ganttChart.isEmpty()
                        || !ganttChart.get(ganttChart.size() - 1).getId().equals(highestPriorityProcess.getId())) {
                    ganttChart.add(new GanttChartEntry(highestPriorityProcess.getId(), currentTime, currentTime + 1));
                } else {
                    GanttChartEntry lastEntry = ganttChart.get(ganttChart.size() - 1);
                    lastEntry = new GanttChartEntry(lastEntry.getId(), lastEntry.getStartTime(), currentTime + 1);
                    ganttChart.set(ganttChart.size() - 1, lastEntry);
                }

                currentTime++;

                // If the process is completed, update its completion time
                if (highestPriorityProcess.getRemainingTime() == 0) {
                    highestPriorityProcess.setCompletionTime(currentTime);
                    completedProcesses++;
                }
            } else {
                // If no process is ready to execute, just advance the time
                currentTime++;
            }
        }

        calculateTimes();
    }

    private void calculateTimes() {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process process : processes) {
            process.setTurnaroundTime(process.getCompletionTime() - process.getArrivalTime());
            process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());

            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        averageWaitingTime = (double) totalWaitingTime / processes.size();
        averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();
    }

    public List<GanttChartEntry> getGanttChart() {
        return ganttChart;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }
}
