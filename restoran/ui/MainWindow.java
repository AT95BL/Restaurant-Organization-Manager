package restoran.ui;

import restoran.model.LoggedUser;

import javax.swing.*;
import java.awt.*;

/**
 * Glavni prozor aplikacije — sadrži tabove za sve module.
 */
public class MainWindow extends JFrame {

    private JTabbedPane tabs;

    public MainWindow() {
        LoggedUser user = LoggedUser.getInstance();
        setTitle("Restoran — " + user.getEmployeeName() + " [" + user.getRoleName() + "]");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        initComponents(user);
    }

    private void initComponents(LoggedUser user) {
        // Gornji header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel title = new JLabel("🍽  RESTORAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(230, 180, 60));

        JLabel userInfo = new JLabel(user.getEmployeeName() + "  |  " + user.getRoleName());
        userInfo.setForeground(new Color(180, 180, 180));
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnLogout = new JButton("Odjavi se");
        btnLogout.setBackground(new Color(180, 60, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            user.logout();
            dispose();
            new LoginForm().setVisible(true);
        });

        header.add(title, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.CENTER);
        header.add(btnLogout, BorderLayout.EAST);

        // Tabovi
        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(new Color(45, 45, 45));
        tabs.setForeground(Color.WHITE);

        tabs.addTab("📋  Narudžbe",    new OrderPanel());
        tabs.addTab("📅  Rezervacije", new ReservationPanel());
        tabs.addTab("🍕  Meni",        new MenuPanel());

        // Samo manager vidi upravljanje zaposlenima
        if (user.isManager()) {
            tabs.addTab("👥  Zaposleni", new EmployeePanel());
        }

        // Layout
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        // Status bar
        JLabel status = new JLabel("  Ulogovani ste kao: " + user.getUsername());
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(new Color(140, 140, 140));
        status.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        add(status, BorderLayout.SOUTH);
    }
}
