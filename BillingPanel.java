import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class BillingPanel extends JPanel {
    private String studentId;
    private JLabel totalAmountLabel;
    private JLabel subtotalLabel;
    private JTextArea tuitionFeeTextArea;
    private JTextArea mandatoryFeesTextArea;
    private JTextArea extraChargesTextArea;

    public BillingPanel(JFrame parentFrame, String studentId) {
        this.studentId = studentId;
        setPreferredSize(new Dimension(800, 800)); // Set preferred size of the panel
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

        // Create a panel for the title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ImageIcon icon = resizeIcon("style/billing_icon.png", 30, 30);
        JLabel iconLabel = new JLabel(icon);
        titlePanel.add(iconLabel);
        JLabel titleLabel = new JLabel("Billing", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        titlePanel.add(titleLabel);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(titlePanel, BorderLayout.NORTH);
        add(wrapperPanel, BorderLayout.CENTER);

        // Create a panel for the form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adjust gap between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tuition Fee:"), gbc);
        gbc.gridx = 1;
        tuitionFeeTextArea = new JTextArea(5, 30); // Initial height and width
        tuitionFeeTextArea.setEditable(false); // Make it read-only
        JScrollPane tuitionScrollPane = new JScrollPane(tuitionFeeTextArea); // Add a scroll pane
        formPanel.add(tuitionScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel("RM 0.0");
        formPanel.add(totalAmountLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Mandatory Fees:"), gbc);
        gbc.gridx = 1;
        mandatoryFeesTextArea = new JTextArea(2, 30); // Adjust height and width
        mandatoryFeesTextArea.setEditable(false); // Make it read-only
        JScrollPane mandatoryScrollPane = new JScrollPane(mandatoryFeesTextArea); // Add a scroll pane
        formPanel.add(mandatoryScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Extra Charges:"), gbc);
        gbc.gridx = 1;
        extraChargesTextArea = new JTextArea(1, 30); // Adjust height and width
        extraChargesTextArea.setEditable(false); // Make it read-only
        JScrollPane extraScrollPane = new JScrollPane(extraChargesTextArea); // Add a scroll pane
        formPanel.add(extraScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Subtotal:"), gbc);
        gbc.gridx = 1;
        subtotalLabel = new JLabel("RM 0.0");
        formPanel.add(subtotalLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton payButton = new JButton("Pay");
        payButton.addActionListener(e -> handlePayment());
        formPanel.add(payButton, gbc);

        wrapperPanel.add(formPanel, BorderLayout.CENTER);

        loadBillingData();
    }

    private void loadBillingData() {
        List<String> pendingCourses = new ArrayList<>();
        double totalAmount = 0.0;
        double mandatoryFees = 0.0;
        double extraCharges = 0.0;

        // Load pending courses from pending.csv
        try (BufferedReader br = new BufferedReader(new FileReader("data/pending.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    for (int i = 1; i < values.length; i++) {
                        String[] courses = values[i].split(";");
                        for (String courseId : courses) {
                            double courseFee = getCourseFee(courseId);
                            pendingCourses.add(courseId + " -> RM" + courseFee);
                            totalAmount += courseFee;
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load mandatory fees from fee.csv
        List<String> mandatoryFeesList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/fee.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                String feeName = values[0];
                double feeAmount = Double.parseDouble(values[1]);
                mandatoryFeesList.add(feeName + " -> RM" + feeAmount);
                mandatoryFees += feeAmount;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load extra fees from student_credential.csv
        try (BufferedReader br = new BufferedReader(new FileReader("data/student_credential.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(studentId)) {
                    if (values.length > 8 && values[8].equals("yes")) {
                        extraCharges = 2000.0; // Accommodation Fee
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tuitionFeeTextArea.setRows(pendingCourses.size());
        tuitionFeeTextArea.setText(String.join("\n", pendingCourses));
        totalAmountLabel.setText("RM " + totalAmount);
        mandatoryFeesTextArea.setColumns(20); // Adjust column width for better alignment
        mandatoryFeesTextArea.setText(String.join("\n", mandatoryFeesList));
        extraChargesTextArea.setColumns(20); // Adjust column width for better alignment
        extraChargesTextArea.setRows(extraCharges > 0.0 ? 1 : 0);
        extraChargesTextArea.setText(extraCharges > 0.0 ? "Accommodation Fee -> RM2000.0" : "");

        double subtotal = totalAmount + mandatoryFees + extraCharges;
        subtotalLabel.setText("RM " + subtotal);
    }

    private double getCourseFee(String courseId) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/course.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(courseId)) {
                    return Double.parseDouble(values[4]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void handlePayment() {
        // Remove student data from pending.csv and add to paid.csv
        List<String> lines = new ArrayList<>();
        List<String[]> paidRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/pending.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (!values[0].equals(studentId)) {
                    lines.add(line);
                } else {
                    paidRecords.add(values);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/pending.csv"))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File paidFile = new File("data/paid.csv");
        boolean fileExists = paidFile.exists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(paidFile, true))) {
            if (!fileExists || paidFile.length() == 0) {
                bw.write("Student ID,Course ID\n");
            }
            for (String[] record : paidRecords) {
                String studentId = record[0];
                String courses = record[1];
                boolean studentExists = false;
                List<String> paidLines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader("data/paid.csv"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(studentId + ",")) {
                            studentExists = true;
                            line = line + ";" + courses;
                        }
                        paidLines.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!studentExists) {
                    bw.write(studentId + "," + courses);
                    bw.newLine();
                } else {
                    try (BufferedWriter bw2 = new BufferedWriter(new FileWriter("data/paid.csv"))) {
                        for (String l : paidLines) {
                            bw2.write(l);
                            bw2.newLine();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Payment Successful!");

        // Refresh the billing data
        loadBillingData();
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