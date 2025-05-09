public class Student {
    private String id;
    private String password;
    private String name;
    private String dob;
    private String phone;
    private String address;
    private String level;
    private String courses;

    public Student(String id, String password, String name, String dob, String phone, String address) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.address = address;
        this.level = "none";
        this.courses = "none";
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }
}