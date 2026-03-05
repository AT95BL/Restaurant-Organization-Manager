package restoran.ui;

import restoran.dao.ReservationDAO;
import restoran.model.Customer;
import restoran.model.Reservation;
import restoran.model.RestaurantTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel za upravljanje rezervacijama.
 */
public class ReservationPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private ReservationDAO dao = new ReservationDAO();
    private List<Reservation> reservations;

    private JButton btnNew, btnCancel, btnRefresh;

    public ReservationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Naslov + dugmad
        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(new Color(50, 50, 50));
        north.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("📅  Rezervacije stolova");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(230, 180, 60));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(new Color(50, 50, 50));

        btnNew     = makeBtn("➕ Nova rezervacija",   new Color(60, 160, 60));
        btnCancel  = makeBtn("❌ Otkaži rezervaciju", new Color(200, 60, 60));
        btnRefresh = makeBtn("🔄 Osvježi",             new Color(80, 80, 80));

        btnNew.addActionListener(e -> openNewReservationDialog());
        btnCancel.addActionListener(e -> cancelSelected());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnNew); btnPanel.add(btnCancel); btnPanel.add(btnRefresh);
        north.add(title, BorderLayout.WEST);
        north.add(btnPanel, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // Tabela
        String[] cols = {"ID", "Datum", "Vrijeme", "Trajanje (min)", "Status",
                         "Stol", "Kapacitet", "Lokacija", "Gost", "Telefon", "Napomena"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(35, 35, 35));
        add(scroll, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(40, 40, 40));
        JLabel hint = new JLabel("💡 Odaberite rezervaciju i kliknite 'Otkaži' za otkazivanje");
        hint.setForeground(new Color(150, 150, 150));
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(hint);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            reservations = dao.getAll();
            for (Reservation r : reservations) {
                tableModel.addRow(new Object[]{
                    r.getId(), r.getDate(), r.getTime(),
                    r.getDuration() > 0 ? r.getDuration() : "-",
                    r.getStatus(),
                    "Stol " + r.getTableId(),
                    r.getTableCapacity(),
                    r.getTableLocation(),
                    r.getCustomerName(),
                    r.getCustomerPhone(),
                    r.getNote() != null ? r.getNote() : ""
                });
            }
        } catch (Exception ex) {
            showError("Greška pri učitavanju: " + ex.getMessage());
        }
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Odaberite rezervaciju."); return; }
        Reservation r = reservations.get(row);
        if (!"aktivna".equals(r.getStatus())) {
            JOptionPane.showMessageDialog(this, "Ova rezervacija je već otkazana ili završena.");
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Otkazati rezervaciju #" + r.getId() + " za " + r.getCustomerName() + "?",
            "Potvrda", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            dao.cancel(r.getId());
            loadData();
            JOptionPane.showMessageDialog(this, "Rezervacija otkazana.", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    private void openNewReservationDialog() {
        try {
            List<RestaurantTable> tables = dao.getAllTables();
            List<Customer> customers = dao.getCustomers();

            JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nova rezervacija", true);
            dlg.setSize(440, 420);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout(8, 8));
            dlg.getContentPane().setBackground(new Color(45, 45, 45));

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(new Color(45, 45, 45));
            form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.HORIZONTAL;
            g.insets = new Insets(6, 5, 6, 5);
            g.weightx = 1;

            // Datum
            JTextField tfDate = newFieldDlg();
            tfDate.setText(LocalDate.now().toString());
            // Vrijeme
            JTextField tfTime = newFieldDlg();
            tfTime.setText("19:00");
            // Trajanje
            JSpinner spDur = new JSpinner(new SpinnerNumberModel(60, 15, 480, 15));
            // Napomena
            JTextField tfNote = newFieldDlg();

            // Stol
            JComboBox<RestaurantTable> cbTable = new JComboBox<>();
            for (RestaurantTable t : tables) cbTable.addItem(t);
            styleCombo(cbTable);

            // Kupac
            JComboBox<Customer> cbCust = new JComboBox<>();
            for (Customer c : customers) cbCust.addItem(c);
            styleCombo(cbCust);

            // Novi kupac
            JButton btnNewCust = makeBtn("+ Novi gost", new Color(80, 80, 180));

            btnNewCust.addActionListener(ev -> {
                String name  = JOptionPane.showInputDialog(dlg, "Ime gosta:");
                if (name == null || name.trim().isEmpty()) return;
                String phone = JOptionPane.showInputDialog(dlg, "Telefon:");
                String email = JOptionPane.showInputDialog(dlg, "Email (opciono):");
                try {
                    int newId = dao.addCustomer(name.trim(),
                        email != null ? email.trim() : "",
                        phone != null ? phone.trim() : "");
                    Customer nc = new Customer(newId, name.trim(),
                        email != null ? email.trim() : "",
                        phone != null ? phone.trim() : "");
                    cbCust.addItem(nc);
                    cbCust.setSelectedItem(nc);
                } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
            });

            addRow(form, g, 0, "Datum (YYYY-MM-DD):", tfDate);
            addRow(form, g, 1, "Vrijeme (HH:mm):", tfTime);
            addRow(form, g, 2, "Trajanje (min):", spDur);
            addRow(form, g, 3, "Stol:", cbTable);
            addRow(form, g, 4, "Gost:", cbCust);
            g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
            form.add(btnNewCust, g);
            g.gridy = 6; g.gridwidth = 1;
            addRow(form, g, 6, "Napomena:", tfNote);

            JButton btnSave = makeBtn("✔ Sačuvaj rezervaciju", new Color(60, 160, 60));
            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            south.setBackground(new Color(45, 45, 45));
            south.add(btnSave);

            btnSave.addActionListener(ev -> {
                String date = tfDate.getText().trim();
                String time = tfTime.getText().trim();
                if (date.isEmpty() || time.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Datum i vrijeme su obavezni."); return;
                }
                RestaurantTable sel = (RestaurantTable) cbTable.getSelectedItem();
                Customer selC = (Customer) cbCust.getSelectedItem();
                if (sel == null || selC == null) {
                    JOptionPane.showMessageDialog(dlg, "Odaberite stol i gosta."); return;
                }
                try {
                    dao.add(date, time + ":00", (int) spDur.getValue(),
                        tfNote.getText().trim(), sel.getId(), selC.getId());
                    JOptionPane.showMessageDialog(dlg, "Rezervacija kreirana!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                    loadData();
                } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
            });

            dlg.add(form, BorderLayout.CENTER);
            dlg.add(south, BorderLayout.SOUTH);
            dlg.setVisible(true);

        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    // ── Stil ──────────────────────────────────────────────────
    private void styleTable(JTable t) {
        t.setBackground(new Color(35, 35, 35)); t.setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(80, 120, 180));
        t.setGridColor(new Color(60, 60, 60)); t.setRowHeight(26);
        t.getTableHeader().setBackground(new Color(30, 30, 30));
        t.getTableHeader().setForeground(new Color(230, 180, 60));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    private JTextField newFieldDlg() {
        JTextField f = new JTextField(14);
        f.setBackground(new Color(60, 60, 60)); f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 90)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        return f;
    }
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(new Color(60, 60, 60)); cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, JComponent f) {
        JLabel l = new JLabel(lbl); l.setForeground(new Color(200, 200, 200));
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; p.add(l, g);
        g.gridx = 1; p.add(f, g);
    }
    private JButton makeBtn(String t, Color bg) {
        JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12)); return b;
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Greška", JOptionPane.ERROR_MESSAGE);
    }
}
