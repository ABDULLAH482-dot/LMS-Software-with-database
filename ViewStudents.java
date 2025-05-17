import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ViewStudents extends JPanel {
    private JTable table;
    private JTextField searchField;
    private DefaultTableModel tableModel;

    public ViewStudents() {
        setBackground(new Color(0xFFFFFF));
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        searchField.addActionListener(e -> loadStudents(searchField.getText().trim()));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0x339966));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> loadStudents(searchField.getText().trim()));
        searchBtn.setToolTipText("Search students by name or course");
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0x339966));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudents("");
        });
        refreshBtn.setToolTipText("Reload all students");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Course", "Year"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        loadStudents("");
    }

    private void loadStudents(String search) {
        tableModel.setRowCount(0);
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String query = "SELECT * FROM students WHERE name LIKE ? OR course LIKE ?";
            pst = con.prepareStatement(query);
            String searchTerm = "%" + search + "%";
            pst.setString(1, searchTerm);
            pst.setString(2, searchTerm);
            rs = pst.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getInt("year")
                });
            }
            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No students found in the database", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}