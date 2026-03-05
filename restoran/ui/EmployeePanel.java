package restoran.ui;

import restoran.dao.EmployeeDAO;
import restoran.model.Employee;
import restoran.model.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel za upravljanje zaposlenima (samo Manager).
 */
public class EmployeePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private EmployeeDAO dao = new EmployeeDAO();
    private List<Employee> employees;
    private List<Role> roles;

    // Polja forme
    private JTextField tfName, tfEmail, tfPhone, tfSalary;
    private JComboBox<Role> cbRole;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private int selectedId = -1;

    public EmployeePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // ─── Tabela ───────────────────────────────────────────
        String[] cols = {"ID", "Ime", "Email", "Telefon", "Plata (KM)", "Uloga"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelect());

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(35, 35, 35));
        add(scroll, BorderLayout.CENTER);

        // ─── Panel za formu (desno) ───────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            "Detalji zaposlenog", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(230, 180, 60)));
        formPanel.setPreferredSize(new Dimension(260, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 8, 5, 8);
        g.weightx = 1;

        tfName   = newField(); tfEmail  = newField();
        tfPhone  = newField(); tfSalary = newField();
        cbRole   = new JComboBox<>();
        styleCombo(cbRole);

        addFormRow(formPanel, g, 0, "Ime:",         tfName);
        addFormRow(formPanel, g, 1, "Email:",        tfEmail);
        addFormRow(formPanel, g, 2, "Telefon:",      tfPhone);
        addFormRow(formPanel, g, 3, "Plata (KM):",   tfSalary);
        addFormRow(formPanel, g, 4, "Uloga:",         cbRole);

        // Dugmad
        btnAdd     = makeBtn("➕ Dodaj",   new Color(60, 160, 60));
        btnUpdate  = makeBtn("✏ Izmijeni", new Color(60, 100, 200));
        btnDelete  = makeBtn("🗑 Obriši",  new Color(200, 60, 60));
        btnRefresh = makeBtn("🔄 Osvježi", new Color(80, 80, 80));

        btnAdd.addActionListener(e -> addEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnRefresh.addActionListener(e -> { loadData(); clearForm(); });
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        g.insets = new Insets(10, 8, 2, 8);
        formPanel.add(btnAdd, g);
        g.gridy = 6; g.insets = new Insets(2, 8, 2, 8);
        formPanel.add(btnUpdate, g);
        g.gridy = 7;
        formPanel.add(btnDelete, g);
        g.gridy = 8;
        formPanel.add(btnRefresh, g);

        // Filler
        g.gridy = 9; g.weighty = 1;
        formPanel.add(new JLabel(), g);

        add(formPanel, BorderLayout.EAST);

        // ─── Naslov ───────────────────────────────────────────
        JLabel title = new JLabel("👥  Upravljanje zaposlenima");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(230, 180, 60));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(title, BorderLayout.NORTH);
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            employees = dao.getAll();
            roles = dao.getRoles();

            cbRole.removeAllItems();
            for (Role r : roles) cbRole.addItem(r);

            for (Employee e : employees) {
                tableModel.addRow(new Object[]{
                    e.getId(), e.getName(), e.getEmail(),
                    e.getPhone(), String.format("%.2f", e.getSalary()), e.getRoleName()
                });
            }
        } catch (Exception ex) {
            showError("Greška pri učitavanju: " + ex.getMessage());
        }
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Employee e = employees.get(row);
        selectedId = e.getId();
        tfName.setText(e.getName());
        tfEmail.setText(e.getEmail());
        tfPhone.setText(e.getPhone());
        tfSalary.setText(String.valueOf(e.getSalary()));
        // Postavi ulogu u combo
        for (int i = 0; i < cbRole.getItemCount(); i++) {
            if (cbRole.getItemAt(i).getId() == e.getRoleId()) {
                cbRole.setSelectedIndex(i); break;
            }
        }
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void addEmployee() {
        try {
            Employee e = buildFromForm(-1);
            dao.add(e);
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "Zaposleni uspješno dodan!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Greška: " + ex.getMessage());
        }
    }

    private void updateEmployee() {
        if (selectedId < 0) return;
        try {
            Employee e = buildFromForm(selectedId);
            dao.update(e);
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "Podaci ažurirani!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Greška: " + ex.getMessage());
        }
    }

    private void deleteEmployee() {
        if (selectedId < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Sigurno brišete zaposlenog?", "Potvrda", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(selectedId);
            loadData(); clearForm();
        } catch (Exception ex) {
            showError("Greška: " + ex.getMessage());
        }
    }

    private Employee buildFromForm(int id) {
        String name   = tfName.getText().trim();
        String email  = tfEmail.getText().trim();
        String phone  = tfPhone.getText().trim();
        double salary = Double.parseDouble(tfSalary.getText().trim());
        Role role     = (Role) cbRole.getSelectedItem();
        if (name.isEmpty() || email.isEmpty())
            throw new IllegalArgumentException("Ime i email su obavezni.");
        Employee e = new Employee();
        e.setId(id); e.setName(name); e.setEmail(email);
        e.setPhone(phone); e.setSalary(salary);
        e.setRoleId(role.getId());
        return e;
    }

    private void clearForm() {
        selectedId = -1;
        tfName.setText(""); tfEmail.setText("");
        tfPhone.setText(""); tfSalary.setText("");
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    // ─── Pomocne metode za stil ───────────────────────────────

    private void styleTable(JTable t) {
        t.setBackground(new Color(35, 35, 35));
        t.setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(80, 120, 180));
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 60));
        t.setRowHeight(26);
        t.getTableHeader().setBackground(new Color(30, 30, 30));
        t.getTableHeader().setForeground(new Color(230, 180, 60));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JTextField newField() {
        JTextField f = new JTextField(14);
        f.setBackground(new Color(60, 60, 60));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 90)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        return f;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(new Color(60, 60, 60));
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void addFormRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(200, 200, 200));
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(lbl, g);
        g.gridx = 1;
        p.add(field, g);
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
        return b;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Greška", JOptionPane.ERROR_MESSAGE);
    }
}
