package restoran.dao;

import restoran.db.DatabaseConnection;
import restoran.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private Connection getConn() { return DatabaseConnection.getInstance().getConnection(); }

    public List<Item> getAll() throws SQLException {
        List<Item> list = new ArrayList<>();
        CallableStatement cs = getConn().prepareCall("{CALL get_all_items()}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            list.add(new Item(rs.getInt("id"), rs.getString("item_name"),
                rs.getDouble("price"), rs.getBoolean("on_menu"), rs.getString("description"),
                rs.getInt("category_id"), rs.getString("category_name"), rs.getString("picture")));
        }
        rs.close(); cs.close();
        return list;
    }

    public List<Item> getMenuItems() throws SQLException {
        List<Item> list = new ArrayList<>();
        PreparedStatement ps = getConn().prepareStatement("SELECT * FROM allmenuitems");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Item(rs.getInt("id"), rs.getString("item_name"),
                rs.getDouble("price"), true, rs.getString("description"),
                rs.getInt("category_id"), rs.getString("category_name"), rs.getString("picture")));
        }
        rs.close(); ps.close();
        return list;
    }

    public void add(Item item) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL add_item(?,?,?,?,?,?)}");
        cs.setString(1, item.getName()); cs.setDouble(2, item.getPrice());
        cs.setInt(3, item.isOnMenu() ? 1 : 0); cs.setString(4, item.getDescription());
        cs.setInt(5, item.getCategoryId()); cs.setString(6, item.getPicture());
        cs.execute(); cs.close();
    }

    public void update(Item item) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL update_item(?,?,?,?,?,?,?)}");
        cs.setInt(1, item.getId()); cs.setString(2, item.getName()); cs.setDouble(3, item.getPrice());
        cs.setInt(4, item.isOnMenu() ? 1 : 0); cs.setString(5, item.getDescription());
        cs.setInt(6, item.getCategoryId()); cs.setString(7, item.getPicture());
        cs.execute(); cs.close();
    }

    public void delete(int id) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL delete_item(?)}");
        cs.setInt(1, id); cs.execute(); cs.close();
    }

    public List<Category> getCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        PreparedStatement ps = getConn().prepareStatement("SELECT * FROM allcategories");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(new Category(rs.getInt("id"), rs.getString("name")));
        rs.close(); ps.close();
        return list;
    }

    public void addCategory(String name) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL add_category(?)}");
        cs.setString(1, name); cs.execute(); cs.close();
    }
}
