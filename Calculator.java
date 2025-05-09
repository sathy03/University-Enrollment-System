import java.util.List;

public class Calculator {
    public static double calculateTotalFee(List<String> courses) {
        double total = 0;
        for (String courseId : courses) {
            Course course = CourseRepository.getCourseById(courseId);
            total += course.getFee();
        }
        return total;
    }

    public static double applyDiscount(double totalFee, List<String> courses) {
        boolean hasStacked = false;
        for (String courseId : courses) {
            Course course = CourseRepository.getCourseById(courseId);
            if (course.isStacked()) {
                hasStacked = true;
                break;
            }
        }
        return hasStacked ? totalFee * 0.9 : totalFee; // 10% discount for stacked courses
    }
}
