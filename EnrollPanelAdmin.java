import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class EnrollPanelAdmin extends JPanel {
    private JComboBox<String> studentIdComboBox;
    private JComboBox<String> courseComboBox;

    public EnrollPanelAdmin(JFrame parentFrame) {
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
                Main mainFrame = new Main(true, null);
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

        JLabel titleLabel = new JLabel("Enroll Student", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields for enrollment details
        JLabel studentIdLabel = new JLabel("Student ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(studentIdLabel, gbc);

        studentIdComboBox = new JComboBox<>(getStudentIds());
        studentIdComboBox.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(studentIdComboBox, gbc);

        JLabel courseLabel = new JLabel("Course:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(courseLabel, gbc);

        courseComboBox = new JComboBox<>(getCourses());
        courseComboBox.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(courseComboBox, gbc);

        // Add button to enroll
        JButton enrollButton = new JButton("Enroll");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(enrollButton, gbc);

        enrollButton.addActionListener(e -> {
            String studentId = (String) studentIdComboBox.getSelectedItem();
            String course = (String) courseComboBox.getSelectedItem();

            // Save enrollment details
            saveEnrollmentDetails(studentId, course);

            // Save to pending.csv
            savePendingDetails(studentId, course.split(" - ")[0]);

            JOptionPane.showMessageDialog(this, "Student enrolled successfully!");
        });

        // Add space below the enroll button
        gbc.gridy = 3;
        formPanel.add(Box.createVerticalStrut(20), gbc);

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

    private String[] getStudentIds() {
        List<String> studentIds = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("id")) { // Skip header
                    String[] values = line.split(",");
                    studentIds.add(values[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return studentIds.toArray(new String[0]);
    }

    private String[] getCourses() {
        List<String> courses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/course.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("id")) { // Skip header
                    String[] values = line.split(",");
                    courses.add(values[0] + " - " + values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses.toArray(new String[0]);
    }

    private void saveEnrollmentDetails(String studentId, String course) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    if (values[7].contains(course.split(" - ")[0])) {
                        JOptionPane.showMessageDialog(this, "Student is already enrolled in this course!");
                        return;
                    }
                    if (values[7].equals("none")) {
                        values[7] = course.split(" - ")[0];
                    } else {
                        values[7] += ";" + course.split(" - ")[0];
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
    }

    private void savePendingDetails(String studentId, String courseId) {
        File file = new File("data/pending.csv");
        List<String> lines = new ArrayList<>();
        boolean studentFound = false;
        boolean headerPresent = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Student ID")) {
                    headerPresent = true;
                }
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

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) { // Open in append mode
            if (!headerPresent) {
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
