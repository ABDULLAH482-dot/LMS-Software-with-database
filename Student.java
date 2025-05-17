import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Student extends JPanel implements ActionListener {
    private JTextField idField, nameField, courseField, yearField;
    private JButton submitBtn;

    public Student() {
        setBackground(new Color(0xFFFFFF));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card-like panel with enhanced padding
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        formPanel.setLayout(new GridBagLayout());

        // Student ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        idField.setToolTipText("Enter a unique Student ID (numeric)");
        formPanel.add(idField, gbc);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        nameField.setToolTipText("Enter the student's full name");
        formPanel.add(nameField, gbc);

        // Course field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        courseField = new JTextField(20);
        courseField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        courseField.setToolTipText("Enter the student's course (e.g., Computer Science)");
        formPanel.add(courseField, gbc);

        // Year field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        yearField = new JTextField(20);
        yearField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        yearField.setToolTipText("Enter the student's year (e.g., 1, 2, 3, 4)");
        formPanel.add(yearField, gbc);

        // Submit button
        gbc.gridx = 1;
        gbc.gridy = 4;
        submitBtn = new JButton("Submit");
        submitBtn.setBackground(new Color(0x339966));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this);
        submitBtn.setToolTipText("Submit student details");
        formPanel.add(submitBtn, gbc);

        add(formPanel);
    }

    public void actionPerformed(ActionEvent e) {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String course = courseField.getText().trim();
        String yearText = yearField.getText().trim();

        if (idText.isEmpty() || name.isEmpty() || course.isEmpty() || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = null;
        PreparedStatement pst = null, checkStmt = null;
        ResultSet rs = null;
        try {
            int id = Integer.parseInt(idText);
            int year = Integer.parseInt(yearText);

            // Check if student_id already exists
            con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            checkStmt = con.prepareStatement("SELECT id FROM students WHERE id = ?");
            checkStmt.setInt(1, id);
            rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Student ID " + id + " already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert student with specified ID
            String query = "INSERT INTO students (id, name, course, year) VALUES (?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, course);
            pst.setInt(4, year);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                        "Student Added Successfully\nStudent ID: " + id + "\nUse this ID to issue books.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                idField.setText("");
                nameField.setText("");
                courseField.setText("");
                yearField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID and Year must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}