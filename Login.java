import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Login extends JFrame {
    public Login() {
        setTitle("LOGIN");
        setSize(600, 300); // Adjust the size to fit the image and components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a panel for the label with vertical space above and below
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(Box.createVerticalStrut(20), BorderLayout.NORTH); // Add vertical space above the label

        // "I am a" label with larger font size
        JLabel label = new JLabel("I am a", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20)); // Increase font size
        labelPanel.add(label, BorderLayout.CENTER);

        labelPanel.add(Box.createVerticalStrut(20), BorderLayout.SOUTH); // Add vertical space below the label
        add(labelPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Add horizontal space between buttons

        // Load and resize images
        ImageIcon adminIcon = resizeImage("style/admin_icon.png", 80, 80);
        ImageIcon studentIcon = resizeImage("style/student_icon.png", 80, 80);

        JButton adminButton = new JButton("Administrator", adminIcon);
        adminButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        adminButton.setHorizontalTextPosition(SwingConstants.CENTER);
        adminButton.addActionListener(e -> {
            dispose();
            AdminLogin adminLoginWindow = new AdminLogin();
            adminLoginWindow.setVisible(true);
        });
        buttonPanel.add(adminButton);

        JButton studentButton = new JButton("Student", studentIcon);
        studentButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        studentButton.setHorizontalTextPosition(SwingConstants.CENTER);
        studentButton.addActionListener(e -> {
            dispose();
            StudentLogin studentLoginWindow = new StudentLogin();
            studentLoginWindow.setVisible(true);
        });
        buttonPanel.add(studentButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private ImageIcon resizeImage(String imagePath, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login loginWindow = new Login();
            loginWindow.setVisible(true);
        });
    }
}