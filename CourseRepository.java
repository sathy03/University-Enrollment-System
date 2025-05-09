import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CourseRepository {
    private static Map<String, Course> courseMap = new HashMap<>();

    static {
        loadCourses();
    }

    private static void loadCourses() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/course.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("id")) { // Skip header
                    String[] values = line.split(",");
                    String id = values[0];
                    String name = values[1];
                    String description = values[2];
                    String level = values[3];
                    double fee = Double.parseDouble(values[4]);
                    Course course = new Course(id, name, description, level, fee, false, level);
                    courseMap.put(id, course);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Course getCourseById(String courseId) {
        return courseMap.get(courseId);
    }
}
