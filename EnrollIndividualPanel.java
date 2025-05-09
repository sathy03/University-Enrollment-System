import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class EnrollIndividualPanel extends JPanel {
    private JFrame parentFrame;
    private String studentId;
    private JComboBox<String> levelComboBox;
    private JComboBox<String> courseComboBox;

    public EnrollIndividualPanel(JFrame parentFrame, String studentId) {
        this.parentFrame = parentFrame;
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10)); // Add space between window border and contents

        // Create a panel for the back button
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
        add(backPanel, BorderLayout.NORTH);

        // Create a panel for the title and icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Load and resize the icon
        ImageIcon enrollIcon = resizeIcon("style/enroll_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(enrollIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Enroll", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields for enrollment details
        JLabel levelLabel = new JLabel("Level:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(levelLabel, gbc);

        levelComboBox = new JComboBox<>(getLevels());
        levelComboBox.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(levelComboBox, gbc);

        JLabel courseLabel = new JLabel("Course:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(courseLabel, gbc);

        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(courseComboBox, gbc);

        levelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedLevel = (String) levelComboBox.getSelectedItem();
                updateCourseComboBox(selectedLevel);
            }
        });

        // Add button to enroll
        JButton enrollButton = new JButton("Enroll");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(enrollButton, gbc);

        enrollButton.addActionListener(e -> {
            String selectedCourse = (String) courseComboBox.getSelectedItem();
            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "Please select a course!");
                return;
            }

            String courseId = selectedCourse.split(" - ")[0];
            enrollStudent(courseId);
        });

        add(formPanel, BorderLayout.SOUTH);
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

    private String[] getLevels() {
        ArrayList<String> levels = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/level.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                levels.add(values[0] + " - " + values[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return levels.toArray(new String[0]);
    }

    private void updateCourseComboBox(String level) {
        courseComboBox.removeAllItems();
        String levelNumber = level.split(" - ")[0];
        try (BufferedReader br = new BufferedReader(new FileReader("data/course.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                if (values[3].equals(levelNumber)) {
                    courseComboBox.addItem(values[0] + " - " + values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enrollStudent(String courseId) {
        // Save enrollment details to student_credential.csv and pending.csv
        if (saveEnrollmentDetails(studentId, courseId)) {
            savePendingDetails(studentId, courseId);
            JOptionPane.showMessageDialog(this, "Student enrolled successfully!");
        }
    }

    private boolean saveEnrollmentDetails(String studentId, String courseId) {
        boolean enrolled = false;
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    if (values[7].contains(courseId)) {
                        JOptionPane.showMessageDialog(this, "Student is already enrolled in " + courseId + "!");
                        return false;
                    }
                    if (values[7].equals("none")) {
                        values[7] = courseId;
                    } else {
                        values[7] += ";" + courseId;
                    }
                    enrolled = true;
                    line = String.join(",", values);
                }
                lines.add(line);
            }
            if (enrolled) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/student_credential.csv"))) {
                    for (String l : lines) {
                        bw.write(l);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enrolled;
    }

    private void savePendingDetails(String studentId, String courseId) {
        File file = new File("data/pending.csv");
        List<String> lines = new ArrayList<>();
        boolean studentFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    studentFound = true;
                    if (!values[1].contains(courseId)) {
                        values[1] += ";" + courseId;
                    }
                    line = String.join(",", values);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!studentFound) {
            lines.add(studentId + "," + courseId);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}