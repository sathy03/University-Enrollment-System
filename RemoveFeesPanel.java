import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class RemoveFeesPanel extends JPanel {
    public RemoveFeesPanel(JFrame parentFrame) {
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

        JLabel titleLabel = new JLabel("Remove Fees", SwingConstants.CENTER);
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

        // Add dropdown for fee names
        JLabel feeNameLabel = new JLabel("Fees Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(feeNameLabel, gbc);

        JComboBox<String> feeNameComboBox = new JComboBox<>(getFeeNames());
        feeNameComboBox.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(feeNameComboBox, gbc);

        // Add button to remove fee
        JButton removeButton = new JButton("Remove Fee");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(removeButton, gbc);

        removeButton.addActionListener(e -> {
            String selectedFee = (String) feeNameComboBox.getSelectedItem();
            if (selectedFee != null) {
                removeFee(selectedFee);
                feeNameComboBox.setModel(new DefaultComboBoxModel<>(getFeeNames()));
                JOptionPane.showMessageDialog(this, "Fee removed successfully!");
            }
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

    private String[] getFeeNames() {
        ArrayList<String> feeNames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/fee.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                feeNames.add(values[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feeNames.toArray(new String[0]);
    }

    private void removeFee(String feeName) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/fee.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(feeName + ",")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/fee.csv"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}