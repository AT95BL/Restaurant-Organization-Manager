package restoran.ui;

import restoran.dao.AuthDAO;
import restoran.model.LoggedUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ekran za prijavu u aplikaciju.
 */
public class LoginForm extends JFrame {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginForm() {
        setTitle("Restoran — Prijava");
        setSize(400, 280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        // Glavni panel
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(40, 40, 40));

        // Header
        JLabel header = new JLabel("🍽  RESTORAN", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(new Color(230, 180, 60));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        main.add(header, BorderLayout.NORTH);

        // Forma
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(40, 40, 40));
        form.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        JLabel lblUser = new JLabel("Korisničko ime:");
        lblUser.setForeground(Color.WHITE);
        JLabel lblPass = new JLabel("Lozinka:");
        lblPass.setForeground(Color.WHITE);

        tfUsername = new JTextField(15);
        pfPassword = new JPasswordField(15);

        styleField(tfUsername);
        styleField(pfPassword);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(lblUser, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tfUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(lblPass, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(pfPassword, gbc);

        main.add(form, BorderLayout.CENTER);

        // Donji panel
        JPanel south = new JPanel(new GridLayout(2, 1));
        south.setBackground(new Color(40, 40, 40));
        south.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

        btnLogin = new JButton("Prijavi se");
        styleButton(btnLogin, new Color(230, 180, 60), Color.BLACK);
        btnLogin.addActionListener(e -> doLogin());

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(new Color(220, 80, 80));

        south.add(btnLogin);
        south.add(lblStatus);
        main.add(south, BorderLayout.SOUTH);

        // Enter key
        getRootPane().setDefaultButton(btnLogin);

        add(main);
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(60, 60, 60));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void styleButton(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
    }

    private void doLogin() {
        String user = tfUsername.getText().trim();
        String pass = new String(pfPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblStatus.setText("Unesite korisničko ime i lozinku.");
            return;
        }

        try {
            AuthDAO dao = new AuthDAO();
            LoggedUser logged = dao.login(user, pass);
            if (logged != null) {
                dispose();
                new MainWindow().setVisible(true);
            } else {
                lblStatus.setText("Pogrešno korisničko ime ili lozinka.");
                pfPassword.setText("");
            }
        } catch (Exception ex) {
            lblStatus.setText("Greška pri konekciji s bazom.");
            ex.printStackTrace();
        }
    }
}
