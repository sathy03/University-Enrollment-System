import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.imageio.ImageIO;
import com.toedter.calendar.JDateChooser;

public class AddStudentPanel extends JPanel {
    public AddStudentPanel(JFrame parentFrame) {
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
        ImageIcon studentIcon = resizeIcon("style/student_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(studentIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Add Student", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields for student details
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        JLabel dobLabel = new JLabel("Date of Birth:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(dobLabel, gbc);

        JDateChooser dobField = new JDateChooser();
        dobField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(dobField, gbc);

        JLabel phoneLabel = new JLabel("Phone:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField();
        phoneField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        JLabel addressLabel = new JLabel("Address:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(addressLabel, gbc);

        JTextField addressField = new JTextField();
        addressField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        // Add button to add student
        JButton addButton = new JButton("Add Student");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            Date dob = dobField.getDate();
            String phone = phoneField.getText();
            String address = addressField.getText();

            if (name.isEmpty() || dob == null || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Missing inputs!");
                return;
            }

            if (!name.matches("[a-zA-Z\\s]+")) {
                JOptionPane.showMessageDialog(this, "Name must contain only letters and spaces.");
                return;
            }

            if (!phone.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Phone number must contain only digits.");
                return;
            }

            // Format date of birth
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dobFormatted = sdf.format(dob);

            // Generate student ID and password
            String studentId = generateStudentId();
            String password = generatePassword();

            // Save student details
            saveStudentDetails(studentId, password, name, dobFormatted, phone, address);

            JOptionPane.showMessageDialog(this, "Student added successfully!\nStudent ID: " + studentId + "\nPassword: " + password);
        });

        // Add space below the add button
        gbc.gridy = 5;
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

    private String generateStudentId() {
        // Logic to generate student ID
        String lastId = getLastStudentId();
        if (lastId == null) {
            return "1211100001";
        } else {
            long id = Long.parseLong(lastId) + 1;
            return String.valueOf(id);
        }
    }

    private String getLastStudentId() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            String line;
            String lastLine = null;
            while ((line = br.readLine()) != null) {
                lastLine = line;
            }
            if (lastLine != null) {
                return lastLine.split(",")[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generatePassword() {
        // Logic to generate a random password
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    private void saveStudentDetails(String studentId, String password, String name, String dob, String phone, String address) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/student_credential.csv", true))) {
            File file = new File("data/student_credential.csv");
            if (file.length() == 0) {
                bw.write("id,password,name,dob,phone,address,level,course,accomodation\n");
            }
            bw.write(studentId + "," + password + "," + name + "," + dob + "," + phone + "," + address + ",none,none,no\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}