import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public Dashboard() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0x003366));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Removed the title JLabel "Library"
        sidebar.add(Box.createVerticalStrut(20));

        // Sidebar buttons
        String[] buttons = {"Add Student", "Add Book", "Issue Book", "Return Book",
                "View Books", "View Students", "View Issued Books", "Logout"};
        for (String btnText : buttons) {
            JButton button = new JButton(btnText);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0x003366));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(0x336699));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(0x003366));
                }
            });
            button.addActionListener(e -> handleNavigation(btnText));
            button.setToolTipText(btnText);
            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(10));
        }

        // Content area
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(new Color(0xF5F5F5));

        // Initialize content panels
        contentPanel.add(new Student(), "Add Student");
        contentPanel.add(new Book(), "Add Book");
        contentPanel.add(new IssueBook(), "Issue Book");
        contentPanel.add(new ReturnBook(), "Return Book");
        contentPanel.add(new ViewBooks(), "View Books");
        contentPanel.add(new ViewStudents(), "View Students");
        contentPanel.add(new ViewIssuedBooks(), "View Issued Books");

        // Add components to frame
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void handleNavigation(String section) {
        if (section.equals("Logout")) {
            dispose();
            new Login();
        } else {
            cardLayout.show(contentPanel, section);
        }
    }
}