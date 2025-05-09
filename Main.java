import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main extends JFrame {
    private boolean isAdmin;
    private String studentId;

    public Main(boolean isAdmin, String studentId) {
        this.isAdmin = isAdmin;
        this.studentId = studentId;
        setTitle("MMU | MENU");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add logout hyperlink
        JLabel logoutLabel = new JLabel("<< Logout");
        logoutLabel.setForeground(Color.BLACK);
        logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Login login = new Login();
                login.setVisible(true);
                login.setLocationRelativeTo(null);
                dispose();
            }
        });
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.add(logoutLabel);
        add(logoutPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 20, 10); // Add extra space at the bottom
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        if (isAdmin) {
            addButton(buttonPanel, "Add Student", "style/student_icon.png", e -> openAddStudentWindow(), gbc);
            addButton(buttonPanel, "Add Course", "style/course_icon.png", e -> openAddCourseWindow(), gbc);
            addButton(buttonPanel, "Enroll Student", "style/enroll_icon.png", e -> openEnrollStudentWindow(), gbc);
            addButton(buttonPanel, "Add Level", "style/level_icon.png", e -> openAddLevelWindow(), gbc);
            addButton(buttonPanel, "Fees", "style/fees_icon.png", e -> chooseFeesAction(), gbc);
        } else {
            addButton(buttonPanel, "Profile", "style/student_icon.png", e -> openProfileWindow(studentId), gbc);
            addButton(buttonPanel, "Accommodation", "style/accommodation_icon.png", e -> openAccommodationWindow(studentId), gbc);
            addButton(buttonPanel, "Enroll", "style/enroll_icon.png", e -> chooseEnrollmentType(studentId), gbc);
            addButton(buttonPanel, "Billing", "style/billing_icon.png", e -> openBillingWindow(studentId), gbc);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the window
    }

    private void addButton(JPanel panel, String text, String iconPath, ActionListener action, GridBagConstraints gbc) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(200, 40)); // Set the size of the buttons

        if (iconPath != null) {
            try {
                BufferedImage img = ImageIO.read(new File(iconPath));
                Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImg));
                button.setHorizontalTextPosition(SwingConstants.RIGHT);
                button.setIconTextGap(10);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        panel.add(button, gbc);
    }

    private void openAddStudentWindow() {
        JFrame frame = new JFrame("MMU | ADD STUDENT");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 350);
        frame.add(new AddStudentPanel(frame));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openAddCourseWindow() {
        JFrame frame = new JFrame("MMU | ADD COURSE");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 380);
        frame.add(new AddCoursePanel(frame));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openEnrollStudentWindow() {
        JFrame frame = new JFrame("MMU | ENROLL STUDENT");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 330);
        frame.add(new EnrollPanelAdmin(frame));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openAddLevelWindow() {
        JFrame frame = new JFrame("MMU | ADD LEVEL");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 330);
        frame.add(new AddLevelPanel(frame));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openProfileWindow(String studentId) {
        JFrame frame = new JFrame("MMU | PROFILE");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 460); // Increase the height to fit the content
        frame.add(new ProfilePanel(frame, studentId));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openAccommodationWindow(String studentId) {
        JFrame frame = new JFrame("MMU | ACCOMMODATION");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(new AccommodationPanel(frame, studentId));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void chooseEnrollmentType(String studentId) {
        Object[] options = {"Individual", "Stacked"};
        int choice = JOptionPane.showOptionDialog(this, "Choose Enrollment Type", "Enrollment Type",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            openEnrollIndividualWindow(studentId);
        } else if (choice == JOptionPane.NO_OPTION) {
            openEnrollStackedWindow(studentId);
        }
    }

    private void openEnrollIndividualWindow(String studentId) {
        JFrame frame = new JFrame("MMU | ENROLL INDIVIDUAL");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(new EnrollIndividualPanel(frame, studentId));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openEnrollStackedWindow(String studentId) {
        JFrame frame = new JFrame("MMU | ENROLL STACKED");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(new EnrollStackedPanel(frame, studentId));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void chooseFeesAction() {
        Object[] options = {"Add", "Remove"};
        int choice = JOptionPane.showOptionDialog(this, "Choose Fees Action", "Fees Action",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            openAddFeesWindow();
        } else if (choice == JOptionPane.NO_OPTION) {
            openRemoveFeesWindow();
        }
    }

    private void openAddFeesWindow() {
        JFrame frame = new JFrame("MMU | ADD FEES");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(new AddFeesPanel(frame));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openRemoveFeesWindow() {
        JFrame frame = new JFrame("MMU | REMOVE FEES");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(new RemoveFeesPanel(frame));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    private void openBillingWindow(String studentId) {
        JFrame frame = new JFrame("MMU | BILLING");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.add(new BillingPanel(frame, studentId));
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
            login.setLocationRelativeTo(null); // Center the login window
        });
    }
}