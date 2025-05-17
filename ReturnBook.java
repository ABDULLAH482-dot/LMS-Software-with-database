import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class ReturnBook extends JPanel implements ActionListener {
    private JTextField issueIdField;
    private JButton returnBtn;

    public ReturnBook() {
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

        // Fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Issue ID:"), gbc);
        gbc.gridx = 1;
        issueIdField = new JTextField(15);
        issueIdField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(issueIdField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        returnBtn = new JButton("Return");
        returnBtn.setBackground(new Color(0x339966));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(this);
        returnBtn.setToolTipText("Return issued book");
        formPanel.add(returnBtn, gbc);

        add(formPanel);
    }

    public void actionPerformed(ActionEvent e) {
        String issueIdText = issueIdField.getText().trim();

        if (issueIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Issue ID is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int issueId = Integer.parseInt(issueIdText);

            // Verify issue exists and get details
            Connection con = DBConnection.getConnection();
            PreparedStatement issueCheck = con.prepareStatement(
                    "SELECT ib.book_id, b.title, ib.student_id, s.name " +
                            "FROM issued_books ib " +
                            "JOIN books b ON ib.book_id = b.id " +
                            "JOIN students s ON ib.student_id = s.id " +
                            "WHERE ib.id = ? AND ib.return_date IS NULL"
            );
            issueCheck.setInt(1, issueId);
            ResultSet rs = issueCheck.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Invalid or already returned Issue ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int bookId = rs.getInt("book_id");
            String bookTitle = rs.getString("title");
            String studentName = rs.getString("name");

            // Confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Return '" + bookTitle + "' for " + studentName + "?",
                    "Confirm Return", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            // Update return date
            String query = "UPDATE issued_books SET return_date = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            pst.setInt(2, issueId);
            int updated = pst.executeUpdate();

            // Update book quantity
            PreparedStatement updateBook = con.prepareStatement("UPDATE books SET quantity = quantity + 1 WHERE id = ?");
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Returned Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            issueIdField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Issue ID must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error returning book", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}