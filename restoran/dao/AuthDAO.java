package restoran.dao;

import restoran.db.DatabaseConnection;
import restoran.model.LoggedUser;

import java.sql.*;

public class AuthDAO {
    private Connection getConn() { return DatabaseConnection.getInstance().getConnection(); }

    public LoggedUser login(String username, String password) throws SQLException {
        CallableStatement cs = getConn().prepareCall("{CALL login_user(?,?)}");
        cs.setString(1, username);
        cs.setString(2, password);
        ResultSet rs = cs.executeQuery();
        if (rs.next()) {
            LoggedUser user = LoggedUser.getInstance();
            user.login(rs.getInt("id"), rs.getString("username"),
                rs.getString("employee_name"), rs.getInt("employee_id"), rs.getString("role_name"));
            rs.close(); cs.close();
            return user;
        }
        rs.close(); cs.close();
        return null;
    }
}
