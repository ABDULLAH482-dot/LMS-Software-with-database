import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ViewIssuedBooks extends JPanel {
    private JTable table;
    private JTextField searchField;
    private DefaultTableModel tableModel;

    public ViewIssuedBooks() {
        setBackground(new Color(0xFFFFFF));
        setLayout(new BorderLayout());

        // Card-like panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        searchField.addActionListener(e -> loadIssuedBooks(searchField.getText().trim()));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0x339966));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> loadIssuedBooks(searchField.getText().trim()));
        searchBtn.setToolTipText("Search issued books by student or book");
        // Added Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0x339966));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadIssuedBooks("");
        });
        refreshBtn.setToolTipText("Reload all issued books");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Issue ID", "Student", "Book", "Issue Date", "Return Date"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        loadIssuedBooks("");
    }

    private void loadIssuedBooks(String search) {
        tableModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT ib.id, s.name, b.title, ib.issue_date, ib.return_date " +
                    "FROM issued_books ib " +
                    "JOIN students s ON ib.student_id = s.id " +
                    "JOIN books b ON ib.book_id = b.id " +
                    "WHERE s.name LIKE ? OR b.title LIKE ?";
            PreparedStatement pst = con.prepareStatement(query);
            String searchTerm = "%" + search + "%";
            pst.setString(1, searchTerm);
            pst.setString(2, searchTerm);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading issued books", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}