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

public class AdminLogin extends JFrame {
    public AdminLogin() {
        setTitle("ADMIN LOGIN");
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
                // Open the login window and close the current window
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
        ImageIcon adminIcon = resizeIcon("style/admin_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(adminIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("ADMINISTRATOR", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel adminIdLabel = new JLabel("Admin ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(adminIdLabel, gbc);

        JTextField adminIdField = new JTextField();
        adminIdField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        contentPanel.add(adminIdField, gbc);

        JLabel adminPasswordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(adminPasswordLabel, gbc);

        JPasswordField adminPasswordField = new JPasswordField();
        adminPasswordField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        contentPanel.add(adminPasswordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        contentPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String id = adminIdField.getText();
            String password = new String(adminPasswordField.getPassword());
            if (validateCredentials("data/admin_credential.csv", id, password)) {
                dispose();
                Main mainFrame = new Main(true, null);
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
            AdminLogin adminLoginWindow = new AdminLogin();
            adminLoginWindow.setVisible(true);
            adminLoginWindow.setLocationRelativeTo(null); // Center the login window
        });
    }
}