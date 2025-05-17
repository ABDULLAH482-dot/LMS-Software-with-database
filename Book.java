import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Book extends JPanel implements ActionListener {
    private JTextField idField, titleField, authorField, quantityField;
    private JButton submitBtn;

    public Book() {
        setBackground(new Color(0xFFFFFF));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card-like panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.setLayout(new GridBagLayout());

        // Book ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(15);
        idField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        idField.setToolTipText("Enter a unique Book ID (numeric)");
        formPanel.add(idField, gbc);

        // Title field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(15);
        titleField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(titleField, gbc);

        // Author field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        authorField = new JTextField(15);
        authorField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(authorField, gbc);

        // Quantity field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantityField = new JTextField(15);
        quantityField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(quantityField, gbc);

        // Submit button
        gbc.gridx = 1;
        gbc.gridy = 4;
        submitBtn = new JButton("Submit");
        submitBtn.setBackground(new Color(0x339966));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this);
        submitBtn.setToolTipText("Submit book details");
        formPanel.add(submitBtn, gbc);

        add(formPanel);
    }

    public void actionPerformed(ActionEvent e) {
        String idText = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (idText.isEmpty() || title.isEmpty() || author.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = null;
        PreparedStatement pst = null, checkStmt = null;
        ResultSet rs = null;
        try {
            int id = Integer.parseInt(idText);
            int quantity = Integer.parseInt(quantityText);

            // Check if book_id already exists
            con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            checkStmt = con.prepareStatement("SELECT id FROM books WHERE id = ?");
            checkStmt.setInt(1, id);
            rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Book ID " + id + " already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert book with specified ID
            String query = "INSERT INTO books (id, title, author, quantity) VALUES (?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setInt(1, id);
            pst.setString(2, title);
            pst.setString(3, author);
            pst.setInt(4, quantity);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                        "Book Added Successfully\nBook ID: " + id + "\nUse this ID to issue books.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                idField.setText("");
                titleField.setText("");
                authorField.setText("");
                quantityField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID and Quantity must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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