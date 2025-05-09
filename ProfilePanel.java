import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ProfilePanel extends JPanel {
    private String studentId;

    public ProfilePanel(JFrame parentFrame, String studentId) {
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
        ImageIcon profileIcon = resizeIcon("style/student_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(profileIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Load student data
        loadStudentData();

        // Add fields for student details
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(getStudentDetail("name")), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(studentId), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(getStudentDetail("dob")), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(getStudentDetail("phone")), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(getStudentDetail("address")), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Level:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(getStudentDetail("level")), gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Courses:"), gbc);
        gbc.gridx = 1;

        // Create a JTextArea for displaying courses
        JTextArea coursesTextArea = new JTextArea(getNumberedCourses(getStudentDetail("courses")));
        coursesTextArea.setEditable(false); // Make it read-only
        JScrollPane scrollPane = new JScrollPane(coursesTextArea); // Add a scroll pane
        formPanel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Accommodation:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(getStudentDetail("accommodation")), gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadStudentData() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    // Handle other fields if needed
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStudentDetail(String detail) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    switch (detail) {
                        case "name":
                            return values[2];
                        case "dob":
                            return values[3];
                        case "phone":
                            return values[4];
                        case "address":
                            return values[5];
                        case "level":
                            return values[6];
                        case "courses":
                            return values[7];
                        case "accommodation":
                            return values.length > 8 ? values[8] : "no";
                        default:
                            return "";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getNumberedCourses(String courses) {
        if (courses.equals("none")) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        String[] coursesArray = courses.split(";");
        for (int i = 0; i < coursesArray.length; i++) {
            sb.append(i + 1).append(". ").append(coursesArray[i]).append("\n");
        }
        return sb.toString();
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
}