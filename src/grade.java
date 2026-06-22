public class grade {

    // ─────────────────────────────────────────────
    // ResultProcessor Interface
    interface ResultProcessor {
        double calculateGPA(double[] marks);
        String getGrade(double gpa);
        boolean hasPassed(double[] marks);
        void displayResult(String studentName, double[] marks);
    }

    // ─────────────────────────────────────────────
    // Engineering Department Implementation
    static class EngineeringDepartment implements ResultProcessor {

        private static final double PASS_MARK     = 45.0; // each subject
        private static final double PASS_GPA      =  2.0;

        // GPA on 4.0 scale — strict curve for Engineering
        @Override
        public double calculateGPA(double[] marks) {
            double total = 0;
            for (double m : marks) total += m;
            double avg = total / marks.length;

            if      (avg >= 90) return 4.0;
            else if (avg >= 80) return 3.7;
            else if (avg >= 70) return 3.3;
            else if (avg >= 60) return 3.0;
            else if (avg >= 50) return 2.5;
            else if (avg >= 45) return 2.0;
            else                return 1.0;
        }

        @Override
        public String getGrade(double gpa) {
            if      (gpa >= 4.0) return "A+  (Distinction)";
            else if (gpa >= 3.7) return "A   (Excellent)";
            else if (gpa >= 3.3) return "B+  (Very Good)";
            else if (gpa >= 3.0) return "B   (Good)";
            else if (gpa >= 2.5) return "C+  (Above Average)";
            else if (gpa >= 2.0) return "C   (Average)";
            else                 return "F   (Fail)";
        }

        // Must pass EVERY subject individually
        @Override
        public boolean hasPassed(double[] marks) {
            for (double m : marks)
                if (m < PASS_MARK) return false;
            return true;
        }

        @Override
        public void displayResult(String studentName, double[] marks) {
            String[] subjects = {"Mathematics", "Physics", "Programming", "Circuits", "Thermodynamics"};
            double   gpa      = calculateGPA(marks);

            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║        ENGINEERING DEPARTMENT RESULT         ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.printf ("║  Student : %-33s║%n", studentName);
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  Subject Breakdown:                          ║");
            System.out.println("╠══════════════════════════════════════════════╣");

            double total = 0;
            for (int i = 0; i < marks.length; i++) {
                String status = marks[i] >= PASS_MARK ? "✔" : "✘";
                System.out.printf("║  %s %-18s :  %5.1f / 100          ║%n",
                        status, subjects[i], marks[i]);
                total += marks[i];
            }

            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.printf ("║  Average  : %-32.1f║%n", total / marks.length);
            System.out.printf ("║  GPA      : %-32.1f║%n", gpa);
            System.out.printf ("║  Grade    : %-32s║%n", getGrade(gpa));
            System.out.printf ("║  Status   : %-32s║%n",
                    hasPassed(marks) ? "PASSED ✔" : "FAILED ✘");
            System.out.println("╚══════════════════════════════════════════════╝");
        }
    }

    // ─────────────────────────────────────────────
    // Management Department Implementation
    static class ManagementDepartment implements ResultProcessor {

        private static final double PASS_MARK  = 50.0; // average-based
        private static final double PASS_GPA   =  2.0;

        // GPA on 4.0 scale — lenient curve for Management
        @Override
        public double calculateGPA(double[] marks) {
            // Weighted: last subject (Capstone) carries double weight
            double weighted = 0;
            double totalWeight = 0;
            for (int i = 0; i < marks.length; i++) {
                double weight = (i == marks.length - 1) ? 2.0 : 1.0;
                weighted     += marks[i] * weight;
                totalWeight  += weight;
            }
            double avg = weighted / totalWeight;

            if      (avg >= 85) return 4.0;
            else if (avg >= 75) return 3.5;
            else if (avg >= 65) return 3.0;
            else if (avg >= 55) return 2.5;
            else if (avg >= 50) return 2.0;
            else                return 1.0;
        }

        @Override
        public String getGrade(double gpa) {
            if      (gpa >= 4.0) return "A   (Outstanding)";
            else if (gpa >= 3.5) return "B+  (Very Good)";
            else if (gpa >= 3.0) return "B   (Good)";
            else if (gpa >= 2.5) return "C+  (Satisfactory)";
            else if (gpa >= 2.0) return "C   (Pass)";
            else                 return "F   (Fail)";
        }

        // Passes based on weighted average — not per-subject
        @Override
        public boolean hasPassed(double[] marks) {
            return calculateGPA(marks) >= PASS_GPA;
        }

        @Override
        public void displayResult(String studentName, double[] marks) {
            String[] subjects = {"Accounting", "Marketing", "Business Law",
                    "HR Management", "Capstone Project"};
            double   gpa      = calculateGPA(marks);

            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║        MANAGEMENT DEPARTMENT RESULT          ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.printf ("║  Student : %-33s║%n", studentName);
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  Subject Breakdown:             (Weight)     ║");
            System.out.println("╠══════════════════════════════════════════════╣");

            for (int i = 0; i < marks.length; i++) {
                String weight = (i == marks.length - 1) ? "×2" : "×1";
                System.out.printf("║  %-18s :  %5.1f / 100   [%s]   ║%n",
                        subjects[i], marks[i], weight);
            }

            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.printf ("║  Weighted GPA : %-28.1f║%n", gpa);
            System.out.printf ("║  Grade        : %-28s║%n", getGrade(gpa));
            System.out.printf ("║  Status       : %-28s║%n",
                    hasPassed(marks) ? "PASSED ✔" : "FAILED ✘");
            System.out.println("╚══════════════════════════════════════════════╝");
        }
    }

    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        ResultProcessor engDept  = new EngineeringDepartment();
        ResultProcessor mgmtDept = new ManagementDepartment();

        double[] aliceMarks = {88, 76, 92, 65, 81};  // Engineering student
        double[] bobMarks   = {72, 65, 48, 79, 90};  // Engineering student (fails Physics)
        double[] carolMarks = {78, 85, 60, 72, 91};  // Management student
        double[] davidMarks = {48, 52, 45, 49, 55};  // Management student (borderline)

        System.out.println("\n========== UNIVERSITY RESULT SHEET ==========\n");

        engDept.displayResult("Alice Sharma", aliceMarks);
        System.out.println();
        engDept.displayResult("Bob Thapa", bobMarks);
        System.out.println();
        mgmtDept.displayResult("Carol Rai", carolMarks);
        System.out.println();
        mgmtDept.displayResult("David KC", davidMarks);
    }
}