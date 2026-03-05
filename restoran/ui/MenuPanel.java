package restoran.ui;

import restoran.dao.ItemDAO;
import restoran.model.Category;
import restoran.model.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel za upravljanje stavkama menija i kategorijama.
 */
public class MenuPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private ItemDAO dao = new ItemDAO();
    private List<Item> items;
    private List<Category> categories;

    // Forma
    private JTextField tfName, tfPrice, tfDesc;
    private JCheckBox chkOnMenu;
    private JComboBox<Category> cbCategory;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnAddCat;
    private int selectedId = -1;

    public MenuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Naslov
        JLabel title = new JLabel("🍕  Upravljanje menijem");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(230, 180, 60));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(title, BorderLayout.NORTH);

        // Tabela
        String[] cols = {"ID", "Naziv", "Kategorija", "Cijena (KM)", "Na meniju", "Opis"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelect());

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(35, 35, 35));
        add(scroll, BorderLayout.CENTER);

        // Forma (desno)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            "Detalji stavke", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(230, 180, 60)));
        formPanel.setPreferredSize(new Dimension(270, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 8, 5, 8);
        g.weightx = 1;

        tfName  = newField(); tfPrice = newField(); tfDesc = newField();
        chkOnMenu  = new JCheckBox("Na meniju");
        chkOnMenu.setBackground(new Color(45, 45, 45));
        chkOnMenu.setForeground(Color.WHITE);
        chkOnMenu.setSelected(true);
        cbCategory = new JComboBox<>();
        styleCombo(cbCategory);

        addRow(formPanel, g, 0, "Naziv:",      tfName);
        addRow(formPanel, g, 1, "Cijena KM:",  tfPrice);
        addRow(formPanel, g, 2, "Kategorija:", cbCategory);
        addRow(formPanel, g, 3, "Opis:",       tfDesc);

        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        formPanel.add(chkOnMenu, g);

        btnAdd     = makeBtn("➕ Dodaj stavku",    new Color(60, 160, 60));
        btnUpdate  = makeBtn("✏ Izmijeni",         new Color(60, 100, 200));
        btnDelete  = makeBtn("🗑 Obriši",           new Color(200, 60, 60));
        btnRefresh = makeBtn("🔄 Osvježi",          new Color(80, 80, 80));
        btnAddCat  = makeBtn("📁 Nova kategorija",  new Color(140, 80, 180));

        btnAdd.addActionListener(e -> addItem());
        btnUpdate.addActionListener(e -> updateItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnRefresh.addActionListener(e -> { loadData(); clearForm(); });
        btnAddCat.addActionListener(e -> addCategory());
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        int row = 5;
        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnAddCat, btnRefresh}) {
            g.gridy = row++;
            g.insets = new Insets(3, 8, 3, 8);
            formPanel.add(b, g);
        }

        g.gridy = row; g.weighty = 1;
        formPanel.add(new JLabel(), g);
        add(formPanel, BorderLayout.EAST);
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            items = dao.getAll();
            categories = dao.getCategories();
            cbCategory.removeAllItems();
            for (Category c : categories) cbCategory.addItem(c);
            for (Item i : items) {
                tableModel.addRow(new Object[]{
                    i.getId(), i.getName(), i.getCategoryName(),
                    String.format("%.2f", i.getPrice()),
                    i.isOnMenu() ? "Da" : "Ne",
                    i.getDescription()
                });
            }
        } catch (Exception ex) {
            showError("Greška: " + ex.getMessage());
        }
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Item i = items.get(row);
        selectedId = i.getId();
        tfName.setText(i.getName());
        tfPrice.setText(String.valueOf(i.getPrice()));
        tfDesc.setText(i.getDescription() != null ? i.getDescription() : "");
        chkOnMenu.setSelected(i.isOnMenu());
        for (int ci = 0; ci < cbCategory.getItemCount(); ci++) {
            if (cbCategory.getItemAt(ci).getId() == i.getCategoryId()) {
                cbCategory.setSelectedIndex(ci); break;
            }
        }
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void addItem() {
        try {
            Item i = buildFromForm(-1);
            dao.add(i);
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "Stavka dodana na meni!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    private void updateItem() {
        if (selectedId < 0) return;
        try {
            Item i = buildFromForm(selectedId);
            dao.update(i);
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "Stavka ažurirana!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    private void deleteItem() {
        if (selectedId < 0) return;
        int c = JOptionPane.showConfirmDialog(this, "Brisati stavku?", "Potvrda", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(selectedId);
            loadData(); clearForm();
        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    private void addCategory() {
        String name = JOptionPane.showInputDialog(this, "Naziv nove kategorije:");
        if (name == null || name.trim().isEmpty()) return;
        try {
            dao.addCategory(name.trim());
            loadData();
            JOptionPane.showMessageDialog(this, "Kategorija dodana!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    private Item buildFromForm(int id) {
        String name  = tfName.getText().trim();
        String priceS = tfPrice.getText().trim();
        if (name.isEmpty() || priceS.isEmpty())
            throw new IllegalArgumentException("Naziv i cijena su obavezni.");
        double price = Double.parseDouble(priceS);
        Category cat = (Category) cbCategory.getSelectedItem();
        if (cat == null) throw new IllegalArgumentException("Odaberite kategoriju.");

        Item i = new Item();
        i.setId(id); i.setName(name); i.setPrice(price);
        i.setOnMenu(chkOnMenu.isSelected());
        i.setDescription(tfDesc.getText().trim());
        i.setCategoryId(cat.getId());
        return i;
    }

    private void clearForm() {
        selectedId = -1;
        tfName.setText(""); tfPrice.setText(""); tfDesc.setText("");
        chkOnMenu.setSelected(true);
        btnUpdate.setEnabled(false); btnDelete.setEnabled(false);
        table.clearSelection();
    }

    // ── Stil ──────────────────────────────────────────────────
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
    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, JComponent f) {
        JLabel l = new JLabel(lbl);
        l.setForeground(new Color(200, 200, 200));
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(l, g);
        g.gridx = 1; p.add(f, g);
    }
    private JButton makeBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setBackground(bg); b.setForeground(Color.WHITE);
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
