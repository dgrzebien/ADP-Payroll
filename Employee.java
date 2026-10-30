public class Employee {

    String firstName;
    String lastName;
    String position;
    int department;
    double regularHours;
    double overtimeHours;

    public void setEmployee(double regularHours, double overtimeHours, String position) {
        setRegularHours(regularHours);
        setOvertimeHours(overtimeHours);
        setDepartment(position);
        setPosition(position);
    }

    public void setFirstName(String first) {
        firstName = first;
    }

    public void setLastName(String last) {
        lastName = last;
    }

    public void setDepartment(String position) {
        switch(position) {
            case "Owner":
                department = 101;
                break;
            case "Team Captain":
                department = 401;
                break;
            case "Team Member":
                department = 402;
                break;
            default:
                break;

        }
    }

    public void setPosition(String pos) {
        position = pos;
    }

    public void setRegularHours(double hours) {
        regularHours = hours;
    }

    public void setOvertimeHours(double hours) {
        overtimeHours = hours;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position;
    }

    public int getDepartment() {
        return department;
    }

    public double getRegularHours() {
        return regularHours;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

}
