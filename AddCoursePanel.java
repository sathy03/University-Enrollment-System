import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class AddCoursePanel extends JPanel {
    public AddCoursePanel(JFrame parentFrame) {
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
        ImageIcon courseIcon = resizeIcon("style/course_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(courseIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Add Course", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields for course details
        JLabel idLabel = new JLabel("Course ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(idLabel, gbc);

        JTextField idField = new JTextField();
        idField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        JLabel nameLabel = new JLabel("Course Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(descriptionLabel, gbc);

        JTextField descriptionField = new JTextField();
        descriptionField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);

        JLabel levelLabel = new JLabel("Level:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(levelLabel, gbc);

        String[] levels = getLevels();
        JComboBox<String> levelComboBox = new JComboBox<>(levels);
        gbc.gridx = 1;
        formPanel.add(levelComboBox, gbc);

        JLabel feeLabel = new JLabel("Fee:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(feeLabel, gbc);

        JTextField feeField = new JTextField();
        feeField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(feeField, gbc);

        // Add button to add course
        JButton addButton = new JButton("Add Course");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();
            String level = ((String) levelComboBox.getSelectedItem()).split(" - ")[0]; // Get only the level number
            String feeStr = feeField.getText();

            if (id.isEmpty() || name.isEmpty() || description.isEmpty() || feeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Missing inputs!");
                return;
            }

            if (id.length() != 7) {
                JOptionPane.showMessageDialog(this, "Course ID must be exactly 7 characters long.");
                return;
            }

            if (!feeStr.matches("\\d+(\\.\\d{1,2})?")) {
                JOptionPane.showMessageDialog(this, "Fee must be a valid number.");
                return;
            }

            double fee = Double.parseDouble(feeStr);

            // Check if course ID already exists
            if (courseIdExists(id)) {
                JOptionPane.showMessageDialog(this, "Course with this ID already exists!");
                return;
            }

            // Save course details
            saveCourseDetails(id, name, description, level, fee);

            JOptionPane.showMessageDialog(this, "Course added successfully!");
        });

        // Add space below the add button
        gbc.gridy = 6;
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

    private boolean courseIdExists(String courseId) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/course.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(courseId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveCourseDetails(String id, String name, String description, String level, double fee) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/course.csv", true))) {
            File file = new File("data/course.csv");
            if (file.length() == 0) {
                bw.write("id,name,description,level,fee\n");
            }
            bw.write(id + "," + name + "," + description + "," + level + "," + fee + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}