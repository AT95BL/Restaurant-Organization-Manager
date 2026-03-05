package restoran.dao;

import restoran.db.DatabaseConnection;
import restoran.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ─────────────────────────────────────────────────────────────
// EmployeeDAO
// ─────────────────────────────────────────────────────────────
public class EmployeeDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Employee> getAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        CallableStatement cs = getConn().prepareCall("{CALL get_all_employees()}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            Employee e = new Employee(
                rs.getInt("id"), rs.getString("name"),
                rs.getString("email"), rs.getString("phone"),
                rs.getDouble("salary"), rs.getInt("Role_id"),
                rs.getString("role_name")
            );
            list.add(e);
        }
        rs.close(); cs.close();
        return list;
    }

    public void add(Employee e) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL add_employee(?,?,?,?,?)}");
        cs.setString(1, e.getName());
        cs.setString(2, e.getEmail());
        cs.setString(3, e.getPhone());
        cs.setDouble(4, e.getSalary());
        cs.setInt(5, e.getRoleId());
        cs.execute();
        cs.close();
    }

    public void update(Employee e) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL update_employee(?,?,?,?,?,?)}");
        cs.setInt(1, e.getId());
        cs.setString(2, e.getName());
        cs.setString(3, e.getEmail());
        cs.setString(4, e.getPhone());
        cs.setDouble(5, e.getSalary());
        cs.setInt(6, e.getRoleId());
        cs.execute();
        cs.close();
    }

    public void delete(int id) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL delete_employee(?)}");
        cs.setInt(1, id);
        cs.execute();
        cs.close();
    }

    public List<Role> getRoles() throws SQLException {
        List<Role> list = new ArrayList<>();
        CallableStatement cs = getConn().prepareCall("{CALL get_all_roles()}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            list.add(new Role(rs.getInt("id"), rs.getString("name")));
        }
        rs.close(); cs.close();
        return list;
    }
}
