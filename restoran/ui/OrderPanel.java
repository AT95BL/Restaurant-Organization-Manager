package restoran.ui;

import restoran.dao.OrderDAO;
import restoran.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel za upravljanje narudžbama.
 */
public class OrderPanel extends JPanel {

    private JTable tblOrders, tblItems;
    private DefaultTableModel modelOrders, modelItems;
    private OrderDAO dao = new OrderDAO();
    private List<Order> orders;

    private JLabel lblTotal;
    private JButton btnNew, btnClose, btnRefresh;

    public OrderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadOrders();
    }

    private void initComponents() {
        // Naslov + dugmad (gore)
        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(new Color(50, 50, 50));
        JLabel title = new JLabel("📋  Narudžbe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(230, 180, 60));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(new Color(50, 50, 50));
        btnNew     = makeBtn("➕ Nova narudžba",  new Color(60, 160, 60));
        btnClose   = makeBtn("✔ Zatvori narudžbu", new Color(230, 180, 60));
        btnRefresh = makeBtn("🔄 Osvježi",          new Color(80, 80, 80));
        btnClose.setForeground(Color.BLACK);

        btnNew.addActionListener(e -> openNewOrderDialog());
        btnClose.addActionListener(e -> closeSelectedOrder());
        btnRefresh.addActionListener(e -> loadOrders());

        btnPanel.add(btnNew);
        btnPanel.add(btnClose);
        btnPanel.add(btnRefresh);
        north.add(title, BorderLayout.WEST);
        north.add(btnPanel, BorderLayout.EAST);
        north.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(north, BorderLayout.NORTH);

        // Gornja tabela — narudžbe
        String[] colsO = {"ID", "Status", "Konobar", "Kupac", "Stol", "Popust%", "Plaćeno KM", "Plaćanje", "Datum/Vr."};
        modelOrders = new DefaultTableModel(colsO, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblOrders = new JTable(modelOrders);
        styleTable(tblOrders);
        tblOrders.getSelectionModel().addListSelectionListener(e -> loadOrderItems());

        JScrollPane scrollO = new JScrollPane(tblOrders);
        scrollO.getViewport().setBackground(new Color(35, 35, 35));
        scrollO.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)), "Sve narudžbe",
                0, 0, new Font("Segoe UI", Font.BOLD, 11), new Color(200, 200, 200)));

        // Donja tabela — stavke odabrane narudžbe
        String[] colsI = {"ID", "Artikal", "Kol.", "Iznos KM"};
        modelItems = new DefaultTableModel(colsI, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblItems = new JTable(modelItems);
        styleTable(tblItems);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(50, 50, 50));
        JScrollPane scrollI = new JScrollPane(tblItems);
        scrollI.getViewport().setBackground(new Color(35, 35, 35));
        scrollI.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)), "Stavke narudžbe",
                0, 0, new Font("Segoe UI", Font.BOLD, 11), new Color(200, 200, 200)));

        lblTotal = new JLabel("  Ukupno: 0.00 KM");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(new Color(230, 180, 60));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        bottomPanel.add(scrollI, BorderLayout.CENTER);
        bottomPanel.add(lblTotal, BorderLayout.SOUTH);

        // SplitPane
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollO, bottomPanel);
        split.setDividerLocation(320);
        split.setBackground(new Color(50, 50, 50));
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private void loadOrders() {
        try {
            modelOrders.setRowCount(0);
            orders = dao.getAll();
            for (Order o : orders) {
                modelOrders.addRow(new Object[]{
                        o.getId(), o.getStatus(), o.getEmployeeName(),
                        o.getCustomerName(), o.getTableId() > 0 ? "Stol " + o.getTableId() : "-",
                        String.format("%.1f%%", o.getDiscountPct()),
                        String.format("%.2f", o.getPaidAmount()),
                        o.getPaymentType(), o.getTimestamp()
                });
            }
        } catch (Exception ex) {
            showError("Greška: " + ex.getMessage());
        }
    }

    private void loadOrderItems() {
        int row = tblOrders.getSelectedRow();
        modelItems.setRowCount(0);
        lblTotal.setText("  Ukupno: 0.00 KM");
        if (row < 0) return;
        Order o = orders.get(row);
        try {
            List<OrderedItem> items = dao.getOrderedItems(o.getId());
            double total = 0;
            for (OrderedItem oi : items) {
                modelItems.addRow(new Object[]{
                        oi.getId(), oi.getItemName(), oi.getQuantity(),
                        String.format("%.2f", oi.getPrice())
                });
                total += oi.getPrice();
            }
            lblTotal.setText(String.format("  Ukupno: %.2f KM", total));
        } catch (Exception ex) {
            showError("Greška: " + ex.getMessage());
        }
    }

    private void closeSelectedOrder() {
        int row = tblOrders.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Odaberite narudžbu."); return; }
        Order o = orders.get(row);
        if ("plaćeno".equals(o.getStatus())) {
            JOptionPane.showMessageDialog(this, "Ova narudžba je već zatvorena.");
            return;
        }
        int c = JOptionPane.showConfirmDialog(this, "Zatvoriti narudžbu #" + o.getId() + "?", "Potvrda", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            dao.closeOrder(o.getId());
            loadOrders();
            JOptionPane.showMessageDialog(this, "Narudžba zatvorena, plaćanje obrađeno!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    // ─── Dijalog za novu narudžbu ─────────────────────────────
    private void openNewOrderDialog() {
        try {
            List<Item> menuItems    = new restoran.dao.ItemDAO().getMenuItems();
            List<Customer> customers = dao.getCustomers();
            List<RestaurantTable> tables = dao.getAllTables();
            List<Discount> discounts = dao.getDiscounts();
            List<PaymentType> payTypes = dao.getPaymentTypes();

            JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nova narudžba", true);
            dlg.setSize(600, 520);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout(8, 8));
            dlg.getContentPane().setBackground(new Color(45, 45, 45));

            // Gornji panel - parametri
            JPanel top = new JPanel(new GridLayout(0, 2, 8, 6));
            top.setBackground(new Color(45, 45, 45));
            top.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

            JComboBox<Customer> cbCust = new JComboBox<>();
            cbCust.addItem(new Customer(0, "— Gost —", "", ""));
            for (Customer c : customers) cbCust.addItem(c);

            JComboBox<RestaurantTable> cbTable = new JComboBox<>();
            cbTable.addItem(new RestaurantTable(0, 0, "—", ""));
            for (RestaurantTable t : tables) cbTable.addItem(t);

            JComboBox<Discount> cbDisc = new JComboBox<>();
            for (Discount d : discounts) cbDisc.addItem(d);

            JComboBox<PaymentType> cbPay = new JComboBox<>();
            for (PaymentType pt : payTypes) cbPay.addItem(pt);

            JTextField tfNote = new JTextField();

            styleCombo(cbCust); styleCombo(cbTable);
            styleCombo(cbDisc); styleCombo(cbPay);
            styleFieldDlg(tfNote);

            top.add(lbl("Kupac:")); top.add(cbCust);
            top.add(lbl("Stol:")); top.add(cbTable);
            top.add(lbl("Popust:")); top.add(cbDisc);
            top.add(lbl("Način plaćanja:")); top.add(cbPay);
            top.add(lbl("Napomena:")); top.add(tfNote);

            // Sredina - odabir stavki
            JPanel mid = new JPanel(new BorderLayout(5, 5));
            mid.setBackground(new Color(45, 45, 45));
            mid.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(80, 80, 80)),
                    "Stavke narudžbe", 0, 0,
                    new Font("Segoe UI", Font.BOLD, 11), new Color(200, 200, 200)));

            // Lista za odabir artikla
            JComboBox<Item> cbItem = new JComboBox<>();
            for (Item i : menuItems) cbItem.addItem(i);
            styleCombo(cbItem);

            JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

            JButton btnAddItem = makeBtn("➕ Dodaj u narudžbu", new Color(60, 160, 60));

            // Tabela odabranih stavki
            String[] cols = {"Artikal", "Kol.", "Cijena KM"};
            DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable tbl = new JTable(mdl);
            styleTable(tbl);
            // mapa order items (item_id -> [qty, price, name])
            java.util.LinkedHashMap<Integer, int[]> cart = new java.util.LinkedHashMap<>();
            java.util.Map<Integer, Item> itemMap = new java.util.HashMap<>();
            for (Item it : menuItems) itemMap.put(it.getId(), it);

            JLabel lblCartTotal = new JLabel("Ukupno: 0.00 KM");
            lblCartTotal.setForeground(new Color(230, 180, 60));
            lblCartTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));

            btnAddItem.addActionListener(ev -> {
                Item sel = (Item) cbItem.getSelectedItem();
                if (sel == null) return;
                int qty = (int) spQty.getValue();
                if (cart.containsKey(sel.getId())) cart.get(sel.getId())[0] += qty;
                else cart.put(sel.getId(), new int[]{qty});
                refreshCart(mdl, cart, itemMap, lblCartTotal);
            });

            JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            itemRow.setBackground(new Color(45, 45, 45));
            itemRow.add(cbItem);
            itemRow.add(lbl("Kol:"));
            itemRow.add(spQty);
            itemRow.add(btnAddItem);

            mid.add(itemRow, BorderLayout.NORTH);
            JScrollPane sc = new JScrollPane(tbl);
            sc.getViewport().setBackground(new Color(35, 35, 35));
            mid.add(sc, BorderLayout.CENTER);
            mid.add(lblCartTotal, BorderLayout.SOUTH);

            // Dugme za potvrdu
            JButton btnConfirm = makeBtn("✔ Kreiraj narudžbu", new Color(230, 180, 60));
            btnConfirm.setForeground(Color.BLACK);
            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            south.setBackground(new Color(45, 45, 45));
            south.add(btnConfirm);

            btnConfirm.addActionListener(ev -> {
                if (cart.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Dodajte barem jednu stavku!");
                    return;
                }
                try {
                    Customer selCust = (Customer) cbCust.getSelectedItem();
                    RestaurantTable selTable = (RestaurantTable) cbTable.getSelectedItem();
                    Discount selDisc = (Discount) cbDisc.getSelectedItem();
                    PaymentType selPay = (PaymentType) cbPay.getSelectedItem();

                    int[] orderId = new int[1];
                    orderId[0] = dao.createOrder(
                            LoggedUser.getInstance().getEmployeeId(),
                            selCust != null && selCust.getId() > 0 ? selCust.getId() : 0,
                            selDisc != null ? selDisc.getId() : 0,
                            selTable != null && selTable.getId() > 0 ? selTable.getId() : 0,
                            selPay != null ? selPay.getId() : 1,
                            tfNote.getText().trim()
                    );
                    for (java.util.Map.Entry<Integer, int[]> entry : cart.entrySet()) {
                        dao.addItemToOrder(orderId[0], entry.getKey(), entry.getValue()[0]);
                    }
                    JOptionPane.showMessageDialog(dlg, "Narudžba #" + orderId[0] + " kreirana!", "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                    loadOrders();
                } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
            });

            dlg.add(top, BorderLayout.NORTH);
            dlg.add(mid, BorderLayout.CENTER);
            dlg.add(south, BorderLayout.SOUTH);
            dlg.setVisible(true);

        } catch (Exception ex) { showError("Greška: " + ex.getMessage()); }
    }

    private void refreshCart(DefaultTableModel m, java.util.LinkedHashMap<Integer, int[]> cart,
                             java.util.Map<Integer, Item> itemMap, JLabel lblTotal) {
        m.setRowCount(0);
        double total = 0;
        for (java.util.Map.Entry<Integer, int[]> e : cart.entrySet()) {
            Item it = itemMap.get(e.getKey());
            if (it == null) continue;
            double subtotal = it.getPrice() * e.getValue()[0];
            total += subtotal;
            m.addRow(new Object[]{it.getName(), e.getValue()[0], String.format("%.2f", subtotal)});
        }
        lblTotal.setText(String.format("Ukupno: %.2f KM", total));
    }

    // ── Stil ──────────────────────────────────────────────────
    private void styleTable(JTable t) {
        t.setBackground(new Color(35, 35, 35)); t.setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(80, 120, 180));
        t.setGridColor(new Color(60, 60, 60)); t.setRowHeight(24);
        t.getTableHeader().setBackground(new Color(30, 30, 30));
        t.getTableHeader().setForeground(new Color(230, 180, 60));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(new Color(60, 60, 60)); cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    private void styleFieldDlg(JTextField f) {
        f.setBackground(new Color(60, 60, 60)); f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90)),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
    }
    private JLabel lbl(String t) {
        JLabel l = new JLabel(t); l.setForeground(new Color(200, 200, 200));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12)); return l;
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