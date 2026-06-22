public class stuctures {

    // ─────────────────────────────────────────────
    // Abstract base class
    abstract static class Student {
        protected String name;
        protected int rollNumber;
        protected String faculty;

        public Student(String name, int rollNumber, String faculty) {
            this.name       = name;
            this.rollNumber = rollNumber;
            this.faculty    = faculty;
        }

        // Abstract method — must be implemented by subclasses
        public abstract double calculateFee();

        // Common display method
        public void displayProfile() {
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.printf ("║  %-40s  ║%n", getStudentType() + " Student");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.printf ("║  Name       : %-27s║%n", name);
            System.out.printf ("║  Roll No    : %-27d║%n", rollNumber);
            System.out.printf ("║  Faculty    : %-27s║%n", faculty);
            displayExtraDetails();
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.printf ("║  Total Fee  : $%-26.2f║%n", calculateFee());
            System.out.println("╚══════════════════════════════════════════╝");
        }

        // Overridden by subclasses to inject extra detail rows
        protected void displayExtraDetails() {}

        // Overridden to return student type label
        public abstract String getStudentType();
    }

    // ─────────────────────────────────────────────
    // Undergraduate subclass
    static class Undergraduate extends Student {
        private static final double BASE_FEE        = 3000.0;
        private static final double FEE_PER_CREDIT  =   150.0;

        private int creditHours;
        private boolean hasScholarship;

        public Undergraduate(String name, int rollNumber,
                             String faculty, int creditHours,
                             boolean hasScholarship) {
            super(name, rollNumber, faculty);
            this.creditHours    = creditHours;
            this.hasScholarship = hasScholarship;
        }

        @Override
        public double calculateFee() {
            double fee = BASE_FEE + (creditHours * FEE_PER_CREDIT);
            if (hasScholarship) fee *= 0.75; // 25% scholarship discount
            return fee;
        }

        @Override
        public String getStudentType() { return "Undergraduate"; }

        @Override
        protected void displayExtraDetails() {
            System.out.printf("║  Credits    : %-27d║%n", creditHours);
            System.out.printf("║  Scholarship: %-27s║%n",
                    hasScholarship ? "Yes (25% discount)" : "No");
        }
    }

    // ─────────────────────────────────────────────
    // Graduate subclass
    static class Graduate extends Student {
        private static final double BASE_FEE         = 6000.0;
        private static final double FEE_PER_CREDIT   =   300.0;
        private static final double RESEARCH_FEE     =  1500.0;

        private int    creditHours;
        private boolean isResearchStudent;

        public Graduate(String name, int rollNumber,
                        String faculty, int creditHours,
                        boolean isResearchStudent) {
            super(name, rollNumber, faculty);
            this.creditHours       = creditHours;
            this.isResearchStudent = isResearchStudent;
        }

        @Override
        public double calculateFee() {
            double fee = BASE_FEE + (creditHours * FEE_PER_CREDIT);
            if (isResearchStudent) fee += RESEARCH_FEE; // extra research fee
            return fee;
        }

        @Override
        public String getStudentType() { return "Graduate"; }

        @Override
        protected void displayExtraDetails() {
            System.out.printf("║  Credits    : %-27d║%n", creditHours);
            System.out.printf("║  Research   : %-27s║%n",
                    isResearchStudent ? "Yes (+$1500 research fee)" : "No");
        }
    }

    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        // Create students
        Student s1 = new Undergraduate("Alice Sharma", 101, "Engineering",  18, false);
        Student s2 = new Undergraduate("Bob Thapa",    102, "Arts",         15, true);
        Student s3 = new Graduate     ("Carol Rai",    201, "Computer Sci", 12, false);
        Student s4 = new Graduate     ("David KC",     202, "Physics",       9, true);

        System.out.println("\n===== UNIVERSITY FEE REPORT =====\n");

        // Polymorphic call — same method, different results
        for (Student s : new Student[]{s1, s2, s3, s4}) {
            s.displayProfile();
            System.out.println();
        }

        // Fee summary
        System.out.println("===== FEE SUMMARY =====");
        System.out.printf("%-20s | $%.2f%n", s1.name, s1.calculateFee());
        System.out.printf("%-20s | $%.2f%n", s2.name, s2.calculateFee());
        System.out.printf("%-20s | $%.2f%n", s3.name, s3.calculateFee());
        System.out.printf("%-20s | $%.2f%n", s4.name, s4.calculateFee());
    }
}