package restoran.model;

public class LoggedUser {
    private static LoggedUser instance;
    private int accountId;
    private String username;
    private String employeeName;
    private int employeeId;
    private String roleName;

    private LoggedUser() {}

    public static LoggedUser getInstance() {
        if (instance == null) instance = new LoggedUser();
        return instance;
    }

    public void login(int accountId, String username, String employeeName,
                      int employeeId, String roleName) {
        this.accountId = accountId;
        this.username = username;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.roleName = roleName;
    }

    public void logout() { instance = null; }

    public int getAccountId()       { return accountId; }
    public String getUsername()     { return username; }
    public String getEmployeeName() { return employeeName; }
    public int getEmployeeId()      { return employeeId; }
    public String getRoleName()     { return roleName; }
    public boolean isManager()      { return "Manager".equalsIgnoreCase(roleName); }
}
