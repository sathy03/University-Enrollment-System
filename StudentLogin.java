import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class StudentLogin extends JFrame {
    public StudentLogin() {
        setTitle("STUDENT LOGIN");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Add space between window border and contents

        // Create a panel for the back button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel backLabel = new JLabel("<< Back");
        backLabel.setForeground(Color.BLACK);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Login login = new Login();
                login.setVisible(true);
                login.setLocationRelativeTo(null);
                dispose();
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

        JLabel titleLabel = new JLabel("STUDENT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel studentIdLabel = new JLabel("Student ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(studentIdLabel, gbc);

        JTextField studentIdField = new JTextField();
        studentIdField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        contentPanel.add(studentIdField, gbc);

        JLabel studentPasswordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(studentPasswordLabel, gbc);

        JPasswordField studentPasswordField = new JPasswordField();
        studentPasswordField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        contentPanel.add(studentPasswordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        contentPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String id = studentIdField.getText();
            String password = new String(studentPasswordField.getPassword());
            if (!id.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid ID!");
                return;
            }
            if (validateCredentials("data/student_credential.csv", id, password)) {
                dispose();
                Main mainFrame = new Main(false, id); // Pass the student ID
                mainFrame.setVisible(true);
            }
        });

        // Add space under the login button
        gbc.gridy = 3;
        contentPanel.add(Box.createVerticalStrut(20), gbc);

        add(contentPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null); // Center the window
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

    private boolean validateCredentials(String filePath, String id, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(id)) {
                    if (values[1].equals(password)) {
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid credentials!");
                        return false;
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Username not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentLogin studentLoginWindow = new StudentLogin();
            studentLoginWindow.setVisible(true);
            studentLoginWindow.setLocationRelativeTo(null); // Center the login window
        });
    }
}