public class Course {
    private String id;
    private String name;
    private String description;
    private String level;
    private double fee;
    private boolean isStacked;
    private String stackId;

    public Course(String id, String name, String description, String level, double fee, boolean isStacked, String stackId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.fee = fee;
        this.isStacked = isStacked;
        this.stackId = stackId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isStacked() {
        return isStacked;
    }

    public void setStacked(boolean isStacked) {
        this.isStacked = isStacked;
    }

    public String getStackId() {
        return stackId;
    }

    public void setStackId(String stackId) {
        this.stackId = stackId;
    }
}
