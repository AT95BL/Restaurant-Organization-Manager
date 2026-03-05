package restoran.dao;

import restoran.db.DatabaseConnection;
import restoran.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private Connection getConn() { return DatabaseConnection.getInstance().getConnection(); }

    public List<Reservation> getAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        CallableStatement cs = getConn().prepareCall("{CALL get_all_reservations()}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            Reservation r = new Reservation();
            r.setId(rs.getInt("id")); r.setDate(rs.getString("date")); r.setTime(rs.getString("time"));
            r.setDuration(rs.getInt("duration")); r.setNote(rs.getString("note")); r.setStatus(rs.getString("status"));
            r.setTableId(rs.getInt("table_id")); r.setTableCapacity(rs.getInt("capacity"));
            r.setTableLocation(rs.getString("location")); r.setCustomerId(rs.getInt("customer_id"));
            r.setCustomerName(rs.getString("customer_name")); r.setCustomerPhone(rs.getString("customer_phone"));
            list.add(r);
        }
        rs.close(); cs.close();
        return list;
    }

    public void add(String date, String time, int duration, String note,
                    int tableId, int customerId) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL add_reservation(?,?,?,?,?,?)}");
        cs.setString(1, date); cs.setString(2, time); cs.setInt(3, duration);
        cs.setString(4, note); cs.setInt(5, tableId); cs.setInt(6, customerId);
        cs.execute(); cs.close();
    }

    public void cancel(int id) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL cancel_reservation(?)}");
        cs.setInt(1, id); cs.execute(); cs.close();
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

    public int addCustomer(String name, String email, String phone) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL add_customer(?,?,?,?)}");
        cs.setString(1, name); cs.setString(2, email); cs.setString(3, phone);
        cs.registerOutParameter(4, Types.INTEGER);
        cs.execute();
        int id = cs.getInt(4);
        cs.close();
        return id;
    }
}
