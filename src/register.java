import java.util.ArrayList;
import java.util.List;

public class register {

    // Course class
    static class Course {
        private String courseCode;
        private String courseName;
        private int creditHours;

        // Constructor
        public Course(String courseCode, String courseName, int creditHours) {
            this.courseCode   = courseCode;
            this.courseName   = courseName;
            this.creditHours  = creditHours;
        }

        // Getters
        public String getCourseCode()  { return courseCode; }
        public String getCourseName()  { return courseName; }
        public int getCreditHours()    { return creditHours; }

        // Display single course info
        public void displayCourse() {
            System.out.printf("  [%s] %-30s | Credits: %d%n",
                    courseCode, courseName, creditHours);
        }
    }

    // ─────────────────────────────────────────────
    // Student class
    static class Student {
        private String name;
        private int rollNumber;
        private List<Course> registeredCourses;

        // Constructor
        public Student(String name, int rollNumber) {
            this.name               = name;
            this.rollNumber         = rollNumber;
            this.registeredCourses  = new ArrayList<>();
        }

        // Getters
        public String getName()      { return name; }
        public int getRollNumber()   { return rollNumber; }

        // Register a course
        public void registerCourse(Course course) {
            registeredCourses.add(course);
            System.out.println("✔ \"" + course.getCourseName()
                    + "\" registered for " + name);
        }

        // Drop a course
        public void dropCourse(String courseCode) {
            boolean removed = registeredCourses
                    .removeIf(c -> c.getCourseCode().equals(courseCode));
            if (removed)
                System.out.println("✘ Course " + courseCode + " dropped by " + name);
            else
                System.out.println("⚠ Course " + courseCode + " not found.");
        }

        // Total credit hours
        public int totalCredits() {
            return registeredCourses.stream()
                    .mapToInt(Course::getCreditHours)
                    .sum();
        }

        // Display all registered courses
        public void displayRegisteredCourses() {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.printf ("║  Student : %-30s║%n", name);
            System.out.printf ("║  Roll No : %-30d║%n", rollNumber);
            System.out.println("╠══════════════════════════════════════════╣");

            if (registeredCourses.isEmpty()) {
                System.out.println("║  No courses registered yet.              ║");
            } else {
                System.out.println("║  Registered Courses:                     ║");
                System.out.println("╠══════════════════════════════════════════╣");
                for (Course c : registeredCourses) {
                    c.displayCourse();
                }
                System.out.println("╠══════════════════════════════════════════╣");
                System.out.printf ("  Total Credit Hours : %d%n", totalCredits());
            }
            System.out.println("╚══════════════════════════════════════════╝");
        }
    }

    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        // Create courses
        Course c1 = new Course("CS101", "Introduction to Programming",  3);
        Course c2 = new Course("MA102", "Calculus II",                  4);
        Course c3 = new Course("PH103", "Physics for Engineers",        3);
        Course c4 = new Course("EN104", "Technical Writing",            2);

        // Create students
        Student s1 = new Student("Alice Sharma",  101);
        Student s2 = new Student("Bob Thapa",     102);

        System.out.println("=== Course Registration ===\n");

        // Register courses for s1
        s1.registerCourse(c1);
        s1.registerCourse(c2);
        s1.registerCourse(c3);

        // Register courses for s2
        s2.registerCourse(c1);
        s2.registerCourse(c4);

        // Display profiles
        s1.displayRegisteredCourses();
        s2.displayRegisteredCourses();

        // Drop a course and re-display
        System.out.println("\n=== Dropping CS101 for Alice ===");
        s1.dropCourse("CS101");
        s1.displayRegisteredCourses();
    }
}