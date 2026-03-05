package restoran.dao;

import restoran.db.DatabaseConnection;
import restoran.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection getConn() { return DatabaseConnection.getInstance().getConnection(); }

    public List<Order> getAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        CallableStatement cs = getConn().prepareCall("{CALL get_all_orders()}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            Order o = new Order();
            o.setId(rs.getInt("id")); o.setStatus(rs.getString("status"));
            o.setTimestamp(rs.getString("timestamp")); o.setNote(rs.getString("note"));
            o.setEmployeeName(rs.getString("employee_name")); o.setCustomerName(rs.getString("customer_name"));
            o.setDiscountPct(rs.getDouble("discount_pct")); o.setPaidAmount(rs.getDouble("paid_amount"));
            o.setPaymentType(rs.getString("payment_type")); o.setTableId(rs.getInt("table_id"));
            list.add(o);
        }
        rs.close(); cs.close();
        return list;
    }

    public int createOrder(int employeeId, int customerId, int discountId,
                           int tableId, int payTypeId, String note) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL create_order(?,?,?,?,?,?,?)}");
        cs.setInt(1, employeeId);
        if (customerId > 0) cs.setInt(2, customerId); else cs.setNull(2, Types.INTEGER);
        if (discountId > 0) cs.setInt(3, discountId); else cs.setNull(3, Types.INTEGER);
        if (tableId    > 0) cs.setInt(4, tableId);    else cs.setNull(4, Types.INTEGER);
        cs.setInt(5, payTypeId); cs.setString(6, note);
        cs.registerOutParameter(7, Types.INTEGER);
        cs.execute();
        int newId = cs.getInt(7);
        cs.close();
        return newId;
    }

    public void addItemToOrder(int orderId, int itemId, int qty) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL add_item_to_order(?,?,?)}");
        cs.setInt(1, orderId); cs.setInt(2, itemId); cs.setInt(3, qty);
        cs.execute(); cs.close();
    }

    public void closeOrder(int orderId) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL close_order(?)}");
        cs.setInt(1, orderId); cs.execute(); cs.close();
    }

    public List<OrderedItem> getOrderedItems(int orderId) throws SQLException {
        List<OrderedItem> list = new ArrayList<>();
        CallableStatement cs = getConn().prepareCall("{CALL ordered_items_by_order_id(?)}");
        cs.setInt(1, orderId);
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            list.add(new OrderedItem(rs.getInt("id"), rs.getInt("quantity"),
                rs.getDouble("price"), rs.getString("item_name")));
        }
        rs.close(); cs.close();
        return list;
    }

    public List<Discount> getDiscounts() throws SQLException {
        List<Discount> list = new ArrayList<>();
        PreparedStatement ps = getConn().prepareStatement("SELECT id, code, percentage FROM discount");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new Discount(rs.getInt("id"), rs.getString("code"), rs.getDouble("percentage")));
        rs.close(); ps.close();
        return list;
    }

    public List<PaymentType> getPaymentTypes() throws SQLException {
        List<PaymentType> list = new ArrayList<>();
        PreparedStatement ps = getConn().prepareStatement("SELECT * FROM allpaymenttypes");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new PaymentType(rs.getInt("id"), rs.getString("name")));
        rs.close(); ps.close();
        return list;
    }

    public List<RestaurantTable> getAllTables() throws SQLException {
        List<RestaurantTable> list = new ArrayList<>();
        PreparedStatement ps = getConn().prepareStatement(
            "SELECT id, capacity, location, status FROM _table ORDER BY id");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new RestaurantTable(rs.getInt("id"), rs.getInt("capacity"),
                rs.getString("location"), rs.getString("status")));
        rs.close(); ps.close();
        return list;
    }

    public List<Customer> getCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        CallableStatement cs = getConn().prepareCall("{CALL get_all_customers()}");
        ResultSet rs = cs.executeQuery();
        while (rs.next())
            list.add(new Customer(rs.getInt("id"), rs.getString("name"),
                rs.getString("email"), rs.getString("phone")));
        rs.close(); cs.close();
        return list;
    }
}
