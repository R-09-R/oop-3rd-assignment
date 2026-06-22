import java.util.ArrayList;
import java.util.List;

public class id {

    // ─────────────────────────────────────────────
    // Student class with auto-generated unique ID
    static class Student {

        // ── Static fields — shared across ALL instances ──
        private static int    idCounter    = 1000;   // starts at 1000
        private static int    totalStudents = 0;
        private static final String ID_PREFIX = "STU";

        // ── Instance fields — unique per student ─────────
        private final  int    uniqueId;              // auto-assigned
        private        String name;
        private        String faculty;
        private        String email;
        private        int    enrollmentYear;

        // Constructor — ID generated automatically
        public Student(String name, String faculty,
                       String email, int enrollmentYear) {
            this.uniqueId       = ++idCounter;       // increment then assign
            this.name           = name;
            this.faculty        = faculty;
            this.email          = email;
            this.enrollmentYear = enrollmentYear;
            totalStudents++;                         // track total
        }

        // ── Formatted ID string  e.g.  STU-2026-1001 ────
        public String getStudentId() {
            return String.format("%s-%d-%d",
                    ID_PREFIX, enrollmentYear, uniqueId);
        }

        // ── Static utility methods ───────────────────────
        public static int    getTotalStudents()  { return totalStudents; }
        public static int    getLastCounter()    { return idCounter; }
        public static String getIdPrefix()       { return ID_PREFIX; }

        // Reset counter (e.g. for a new academic year)
        public static void resetCounter(int startFrom) {
            idCounter     = startFrom;
            totalStudents = 0;
            System.out.printf("  ↺ Counter reset to %d%n%n", startFrom);
        }

        // ── Getters / Setters ────────────────────────────
        public int    getRawId()          { return uniqueId; }
        public String getName()           { return name; }
        public String getFaculty()        { return faculty; }
        public String getEmail()          { return email; }
        public int    getEnrollmentYear() { return enrollmentYear; }

        public void setName(String name)   { this.name = name; }
        public void setEmail(String email) { this.email = email; }

        // ── Display student card ─────────────────────────
        public void displayCard() {
            System.out.println("  ┌─────────────────────────────────────────┐");
            System.out.printf ("  │  ID     : %-31s│%n", getStudentId());
            System.out.printf ("  │  Name   : %-31s│%n", name);
            System.out.printf ("  │  Faculty: %-31s│%n", faculty);
            System.out.printf ("  │  Email  : %-31s│%n", email);
            System.out.printf ("  │  Year   : %-31d│%n", enrollmentYear);
            System.out.println("  └─────────────────────────────────────────┘");
        }

        @Override
        public String toString() {
            return String.format("[%s] %s — %s", getStudentId(), name, faculty);
        }
    }

    // ─────────────────────────────────────────────
    // StudentRegistry — manages the student roster
    static class StudentRegistry {
        private final List<Student> students = new ArrayList<>();

        public Student enroll(String name, String faculty,
                              String email, int year) {
            Student s = new Student(name, faculty, email, year);
            students.add(s);
            System.out.printf("  ✔ Enrolled: %s%n", s);
            return s;
        }

        public void remove(String studentId) {
            boolean removed = students.removeIf(
                    s -> s.getStudentId().equals(studentId));
            System.out.printf("  %s ID %s%n",
                    removed ? "✘ Removed:" : "⚠ Not found:", studentId);
        }

        public Student findById(String studentId) {
            return students.stream()
                    .filter(s -> s.getStudentId().equals(studentId))
                    .findFirst()
                    .orElse(null);
        }

        public void displayAll() {
            System.out.println("\n  ╔═══════════════════════════════════════════════╗");
            System.out.println("  ║           STUDENT REGISTRY                    ║");
            System.out.println("  ╠═══════════════════════════════════════════════╣");
            System.out.printf ("  ║  Total Enrolled : %-27d║%n",
                    Student.getTotalStudents());
            System.out.printf ("  ║  ID Range       : %s-%d to %s-%d%-10s║%n",
                    Student.getIdPrefix(), 1001,
                    Student.getIdPrefix(), Student.getLastCounter(), "");
            System.out.println("  ╠═══════════════════════════════════════════════╣");

            if (students.isEmpty()) {
                System.out.println("  ║  No students enrolled.                        ║");
            } else {
                System.out.printf("  ║  %-16s %-18s %-10s║%n",
                        "Student ID", "Name", "Faculty");
                System.out.println("  ╠═══════════════════════════════════════════════╣");
                for (Student s : students) {
                    System.out.printf("  ║  %-16s %-18s %-10s║%n",
                            s.getStudentId(), s.getName(), s.getFaculty());
                }
            }
            System.out.println("  ╚═══════════════════════════════════════════════╝");
        }
    }

    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      UNIVERSITY STUDENT ID SYSTEM            ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        StudentRegistry registry = new StudentRegistry();

        // ── Batch enrolment ────────────────────────────
        System.out.println("━━━━ 1. ENROLLING STUDENTS ━━━━\n");
        Student s1 = registry.enroll("Alice Sharma",  "Engineering",  "alice@uni.edu",  2026);
        Student s2 = registry.enroll("Bob Thapa",     "Management",   "bob@uni.edu",    2026);
        Student s3 = registry.enroll("Carol Rai",     "Medical",      "carol@uni.edu",  2026);
        Student s4 = registry.enroll("David KC",      "Engineering",  "david@uni.edu",  2026);
        Student s5 = registry.enroll("Eve Shrestha",  "Arts",         "eve@uni.edu",    2026);

        // ── Static counter state ───────────────────────
        System.out.println("\n━━━━ 2. COUNTER STATE ━━━━\n");
        System.out.printf("  Total Students : %d%n",  Student.getTotalStudents());
        System.out.printf("  Last ID issued : %d%n",  Student.getLastCounter());
        System.out.printf("  ID Prefix      : %s%n",  Student.getIdPrefix());

        // ── Display individual cards ───────────────────
        System.out.println("\n━━━━ 3. STUDENT ID CARDS ━━━━\n");
        s1.displayCard();
        s3.displayCard();
        s5.displayCard();

        // ── Registry overview ──────────────────────────
        System.out.println("\n━━━━ 4. FULL REGISTRY ━━━━");
        registry.displayAll();

        // ── Search by ID ───────────────────────────────
        System.out.println("\n━━━━ 5. SEARCH BY ID ━━━━\n");
        String searchId = s3.getStudentId();
        Student found   = registry.findById(searchId);
        if (found != null) {
            System.out.printf("  🔍 Found: %s%n", found);
            found.displayCard();
        } else {
            System.out.printf("  ⚠ No student with ID %s%n", searchId);
        }

        // ── Remove a student ───────────────────────────
        System.out.println("\n━━━━ 6. REMOVE STUDENT ━━━━\n");
        registry.remove(s2.getStudentId());
        registry.remove("STU-2026-9999");     // non-existent ID

        // ── IDs remain unique after removal ───────────
        System.out.println("\n━━━━ 7. NEW ENROLMENT AFTER REMOVAL ━━━━\n");
        Student s6 = registry.enroll("Frank Gurung", "Science", "frank@uni.edu", 2026);
        System.out.printf("%n  Note: Bob's ID (STU-2026-1002) is retired.%n");
        System.out.printf("  Frank receives next available: %s%n", s6.getStudentId());

        // ── Final registry ─────────────────────────────
        System.out.println("\n━━━━ 8. FINAL REGISTRY ━━━━");
        registry.displayAll();
    }
}