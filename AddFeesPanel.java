import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class AddFeesPanel extends JPanel {
    public AddFeesPanel(JFrame parentFrame) {
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
        ImageIcon feesIcon = resizeIcon("style/fees_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(feesIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Add Fees", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);

        // Create a panel for the title row and add it to the center
        JPanel titleRowPanel = new JPanel(new BorderLayout());
        titleRowPanel.add(titlePanel, BorderLayout.CENTER);
        add(titleRowPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields for fee details
        JLabel feeNameLabel = new JLabel("Fees Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(feeNameLabel, gbc);

        JTextField feeNameField = new JTextField();
        feeNameField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(feeNameField, gbc);

        JLabel amountLabel = new JLabel("Amount (RM):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(amountLabel, gbc);

        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        // Add button to add fee
        JButton addButton = new JButton("Add Fee");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String feeName = feeNameField.getText();
            String amountStr = amountField.getText();

            if (feeName.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Missing inputs!");
                return;
            }

            if (!feeName.matches("[a-zA-Z\\s]+")) {
                JOptionPane.showMessageDialog(this, "Fees Name must contain only letters and spaces.");
                return;
            }

            if (!amountStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
                return;
            }

            double amount = Double.parseDouble(amountStr);

            if (isFeeNameExists(feeName)) {
                JOptionPane.showMessageDialog(this, "Fee already exists!");
                return;
            }

            // Save fee details
            saveFeeDetails(feeName, amount);

            JOptionPane.showMessageDialog(this, "Fee added successfully!");
        });

        // Add space below the add button
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

    private boolean isFeeNameExists(String feeName) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/fee.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equalsIgnoreCase(feeName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveFeeDetails(String feeName, double amount) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/fee.csv", true))) {
            File file = new File("data/fee.csv");
            if (file.length() == 0) {
                bw.write("Fees Name,Amount\n");
            }
            bw.write(feeName + "," + amount + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}