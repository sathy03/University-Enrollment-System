import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class EnrollStackedPanel extends JPanel {
    private JFrame parentFrame;
    private String studentId;
    private List<JCheckBox> courseCheckBoxes;

    public EnrollStackedPanel(JFrame parentFrame, String studentId) {
        this.parentFrame = parentFrame;
        this.studentId = studentId;
        this.courseCheckBoxes = new ArrayList<>();

        setLayout(new BorderLayout(10, 10)); // Add space between window border and contents

        // Create a panel for the back button
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel backLabel = new JLabel("<< Back");
        backLabel.setForeground(Color.BLACK);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.dispose();
                Main mainFrame = new Main(false, studentId);
                mainFrame.setVisible(true);
            }
        });
        backPanel.add(backLabel);
        topPanel.add(backPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        // Create a panel for the title and icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ImageIcon enrollIcon = resizeIcon("style/enroll_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(enrollIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Enroll", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);

        topPanel.add(titlePanel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel

        // Add the content panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Populate the content panel with levels and courses
        populateContentPanel(contentPanel);

        // Add enroll button
        JButton enrollButton = new JButton("Enroll");
        enrollButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        enrollButton.addActionListener(e -> enrollStudent());
        add(enrollButton, BorderLayout.SOUTH);

        // Increase the height of the window
        parentFrame.setSize(parentFrame.getWidth(), 700);
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateContentPanel(JPanel contentPanel) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/level.csv"))) {
            String line;
            boolean isFirstLine = true;
            boolean isFirstLevel = true; // Track if it's the first level
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] levelData = line.split(",");
                String levelNumber = levelData[0];
                String levelName = levelData[1];

                if (!isFirstLevel) {
                    contentPanel.add(Box.createVerticalStrut(20)); // Add space above each level except the first one
                }
                isFirstLevel = false; // Set to false after the first level is processed

                JLabel levelLabel = new JLabel(levelNumber + " - " + levelName);
                levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
                contentPanel.add(levelLabel);

                addCoursesForLevel(contentPanel, levelNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCoursesForLevel(JPanel contentPanel, String levelNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/course.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] courseData = line.split(",");
                if (courseData[3].equals(levelNumber)) {
                    JPanel coursePanel = new JPanel();
                    coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));

                    JCheckBox checkBox = new JCheckBox(courseData[0]);
                    courseCheckBoxes.add(checkBox);
                    coursePanel.add(checkBox);

                    JLabel courseNameLabel = new JLabel("       " + courseData[1]);
                    coursePanel.add(courseNameLabel);

                    JLabel courseDescLabel = new JLabel("       " + courseData[2]);
                    coursePanel.add(courseDescLabel);

                    JLabel courseFeeLabel = new JLabel("       RM " + courseData[4]);
                    coursePanel.add(courseFeeLabel);

                    contentPanel.add(coursePanel);
                    contentPanel.add(Box.createVerticalStrut(10)); // Add space between courses
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enrollStudent() {
        List<String> selectedCourses = new ArrayList<>();
        for (JCheckBox checkBox : courseCheckBoxes) {
            if (checkBox.isSelected()) {
                String courseId = checkBox.getText().split(" - ")[0];
                selectedCourses.add(courseId);
            }
        }

        // Check if any courses are already enrolled
        if (!saveEnrollmentDetails(studentId, selectedCourses)) {
            return; // Exit the method if already enrolled courses are found
        }

        // Save pending details if no errors occurred
        savePendingDetails(studentId, selectedCourses);

        JOptionPane.showMessageDialog(this, "Student enrolled successfully!");
    }

    private boolean saveEnrollmentDetails(String studentId, List<String> courseIds) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    for (String courseId : courseIds) {
                        if (values[7].contains(courseId)) {
                            JOptionPane.showMessageDialog(this, "Student is already enrolled in " + courseId + "!");
                            return false;
                        }
                        if (values[7].equals("none")) {
                            values[7] = courseId;
                        } else {
                            values[7] += ";" + courseId;
                        }
                    }
                    line = String.join(",", values);
                }
                lines.add(line);
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/student_credential.csv"))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void savePendingDetails(String studentId, List<String> courseIds) {
        File file = new File("data/pending.csv");
        List<String> lines = new ArrayList<>();
        boolean studentFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    studentFound = true;
                    for (String courseId : courseIds) {
                        if (!values[1].contains(courseId)) {
                            values[1] += ";" + courseId;
                        }
                    }
                    line = String.join(",", values);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!studentFound) {
            StringBuilder newEntry = new StringBuilder(studentId + ",");
            for (String courseId : courseIds) {
                newEntry.append(courseId).append(";");
            }
            // Remove the trailing semicolon
            if (newEntry.length() > 0 && newEntry.charAt(newEntry.length() - 1) == ';') {
                newEntry.setLength(newEntry.length() - 1);
            }
            lines.add(newEntry.toString());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            // Check if the file is empty before writing the header
            if (file.length() == 0 || !lines.get(0).startsWith("Student ID")) {
                bw.write("Student ID,Course ID\n");
            }
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}