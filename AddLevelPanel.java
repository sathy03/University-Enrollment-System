import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class AddLevelPanel extends JPanel {
    public AddLevelPanel(JFrame parentFrame) {
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
        ImageIcon levelIcon = resizeIcon("style/level_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(levelIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Add Level", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields for level details
        JLabel numberLabel = new JLabel("Level Number:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(numberLabel, gbc);

        JSpinner numberSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        numberSpinner.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(numberSpinner, gbc);

        JLabel nameLabel = new JLabel("Level Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 25)); // Smaller height for input fields
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Add button to add level
        JButton addButton = new JButton("Add Level");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            int levelNumber = (int) numberSpinner.getValue();
            String levelName = nameField.getText();

            if (levelName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Missing inputs!");
                return;
            }

            if (!levelName.matches("[a-zA-Z\\s]+")) {
                JOptionPane.showMessageDialog(this, "Invalid level name!");
                return;
            }

            // Check if level name already exists
            if (levelNameExists(levelName)) {
                JOptionPane.showMessageDialog(this, "Level name already exists!");
                return;
            }

            // Save level details
            saveLevelDetails(levelNumber, levelName);

            JOptionPane.showMessageDialog(this, "Level added successfully!");
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

    private boolean levelNameExists(String levelName) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/level.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[1].equalsIgnoreCase(levelName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveLevelDetails(int levelNumber, String levelName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/level.csv", true))) {
            File file = new File("data/level.csv");
            if (file.length() == 0) {
                bw.write("number,name,course\n");
            }
            bw.write(levelNumber + "," + levelName + ",none\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}