import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class IssueBook extends JPanel implements ActionListener {
    private JTextField studentIdField, bookIdField;
    private JButton issueBtn;

    public IssueBook() {
        setBackground(new Color(0xFFFFFF));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.setLayout(new GridBagLayout());

        // Instruction label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel instructionLabel = new JLabel("Enter Student ID and Book ID from Add Student/Book panels");
        instructionLabel.setForeground(new Color(0x003366));
        formPanel.add(instructionLabel, gbc);
        gbc.gridwidth = 1;

        // Student ID field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        studentIdField = new JTextField(15);
        studentIdField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        studentIdField.setToolTipText("Enter the Student ID from the Add Student panel");
        formPanel.add(studentIdField, gbc);

        // Book ID field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1;
        bookIdField = new JTextField(15);
        bookIdField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        bookIdField.setToolTipText("Enter the Book ID from the Add Book panel");
        formPanel.add(bookIdField, gbc);

        // Issue button
        gbc.gridx = 1;
        gbc.gridy = 3;
        issueBtn = new JButton("Issue");
        issueBtn.setBackground(new Color(0x339966));
        issueBtn.setForeground(Color.WHITE);
        issueBtn.addActionListener(this);
        issueBtn.setToolTipText("Issue book to student");
        formPanel.add(issueBtn, gbc);

        add(formPanel);
    }

    public void actionPerformed(ActionEvent e) {
        String studentIdText = studentIdField.getText().trim();
        String bookIdText = bookIdField.getText().trim();

        if (studentIdText.isEmpty() || bookIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = null;
        PreparedStatement studentCheck = null, bookCheck = null, pst = null, updateBook = null;
        ResultSet studentRs = null, bookRs = null;
        try {
            int studentId = Integer.parseInt(studentIdText);
            int bookId = Integer.parseInt(bookIdText);

            con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            studentCheck = con.prepareStatement("SELECT name FROM students WHERE id = ?");
            studentCheck.setInt(1, studentId);
            studentRs = studentCheck.executeQuery();
            if (!studentRs.next()) {
                JOptionPane.showMessageDialog(this, "Invalid Student ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String studentName = studentRs.getString("name");

            bookCheck = con.prepareStatement("SELECT title, quantity FROM books WHERE id = ?");
            bookCheck.setInt(1, bookId);
            bookRs = bookCheck.executeQuery();
            if (!bookRs.next()) {
                JOptionPane.showMessageDialog(this, "Invalid Book ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String bookTitle = bookRs.getString("title");
            int quantity = bookRs.getInt("quantity");
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Book is out of stock", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Issue '" + bookTitle + "' to " + studentName + "?",
                    "Confirm Issue", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String query = "INSERT INTO issued_books (student_id, book_id, issue_date) VALUES (?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setInt(1, studentId);
            pst.setInt(2, bookId);
            pst.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            pst.executeUpdate();

            updateBook = con.prepareStatement("UPDATE books SET quantity = quantity - 1 WHERE id = ?");
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Issued Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            studentIdField.setText("");
            bookIdField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "IDs must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error issuing book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (studentRs != null) studentRs.close();
                if (bookRs != null) bookRs.close();
                if (studentCheck != null) studentCheck.close();
                if (bookCheck != null) bookCheck.close();
                if (pst != null) pst.close();
                if (updateBook != null) updateBook.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}