package restoran.model;

// ─────────────────────────────────────────────────────────────
// Employee
// ─────────────────────────────────────────────────────────────
public class Employee {
    private int id;
    private String name;
    private String email;
    private String phone;
    private double salary;
    private int roleId;
    private String roleName;

    public Employee() {}
    public Employee(int id, String name, String email, String phone, double salary, int roleId, String roleName) {
        this.id = id; this.name = name; this.email = email;
        this.phone = phone; this.salary = salary;
        this.roleId = roleId; this.roleName = roleName;
    }

    public int getId()          { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public double getSalary()   { return salary; }
    public int getRoleId()      { return roleId; }
    public String getRoleName() { return roleName; }

    public void setId(int id)             { this.id = id; }
    public void setName(String name)      { this.name = name; }
    public void setEmail(String email)    { this.email = email; }
    public void setPhone(String phone)    { this.phone = phone; }
    public void setSalary(double salary)  { this.salary = salary; }
    public void setRoleId(int roleId)     { this.roleId = roleId; }
    public void setRoleName(String r)     { this.roleName = r; }

    @Override public String toString() { return name + " (" + roleName + ")"; }
}
