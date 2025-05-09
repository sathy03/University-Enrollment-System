import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class AccommodationPanel extends JPanel {
    private String studentId;
    private boolean hasAccommodation;
    private JCheckBox accommodationCheckBox;

    public AccommodationPanel(JFrame parentFrame, String studentId) {
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

        // Create a panel for the title and image
        JPanel titleImagePanel = new JPanel(new BorderLayout());
        titleImagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ImageIcon accommodationIcon = resizeIcon("style/accommodation_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(accommodationIcon);
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Accommodation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);
        titleImagePanel.add(titlePanel, BorderLayout.NORTH);

        JLabel imageLabel = new JLabel(resizeIcon("style/hostel.png", 300, 200));
        titleImagePanel.add(imageLabel, BorderLayout.CENTER);

        // Create a panel for the form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add accommodation option below the image
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(Box.createVerticalStrut(20), gbc); // Add space between the image and checkbox

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Accommodation:"), gbc);

        gbc.gridx = 1;
        accommodationCheckBox = new JCheckBox("Opt for Accommodation");
        accommodationCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                hasAccommodation = accommodationCheckBox.isSelected();
                saveStudentData();
            }
        });
        formPanel.add(accommodationCheckBox, gbc);

        titleImagePanel.add(formPanel, BorderLayout.SOUTH);

        add(titleImagePanel, BorderLayout.CENTER);

        // Load student data
        loadStudentData();

        // Set checkbox based on loaded data
        accommodationCheckBox.setSelected(hasAccommodation);

        // Increase window height
        parentFrame.setSize(500, 600);
    }

    private void loadStudentData() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    hasAccommodation = values.length > 8 && values[8].equals("yes");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveStudentData() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    if (values.length > 8) {
                        values[8] = hasAccommodation ? "yes" : "no";
                    } else {
                        String[] newValues = new String[9];
                        System.arraycopy(values, 0, newValues, 0, values.length);
                        newValues[8] = hasAccommodation ? "yes" : "no";
                        values = newValues;
                    }
                    line = String.join(",", values);
                }
                lines.add(line);
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/student_credential.csv"))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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