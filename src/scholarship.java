import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class scholarship {

    // ─────────────────────────────────────────────
    // Student profile passed to scholarship checks
    static class StudentProfile {
        String name;
        String programme;
        double gpa;             // 0.0 – 4.0
        double familyIncome;    // annual, in USD
        int    communityHours;  // volunteer hours
        boolean hasDisability;
        boolean isFirstGen;     // first-generation college student

        public StudentProfile(String name, String programme, double gpa,
                              double familyIncome, int communityHours,
                              boolean hasDisability, boolean isFirstGen) {
            this.name           = name;
            this.programme      = programme;
            this.gpa            = gpa;
            this.familyIncome   = familyIncome;
            this.communityHours = communityHours;
            this.hasDisability  = hasDisability;
            this.isFirstGen     = isFirstGen;
        }

        public void displayProfile() {
            System.out.printf("  %-20s │ GPA: %.2f │ Income: $%-8.0f │ " +
                            "Hours: %-3d │ Disability: %-3s │ FirstGen: %s%n",
                    name, gpa, familyIncome, communityHours,
                    hasDisability ? "Yes" : "No",
                    isFirstGen    ? "Yes" : "No");
        }
    }

    // ─────────────────────────────────────────────
    // Award record produced by a successful application
    static class ScholarshipAward {
        String scholarshipName;
        String recipientName;
        double amount;
        String reason;

        ScholarshipAward(String scholarshipName, String recipientName,
                         double amount, String reason) {
            this.scholarshipName = scholarshipName;
            this.recipientName   = recipientName;
            this.amount          = amount;
            this.reason          = reason;
        }

        public void display() {
            System.out.println("  ┌──────────────────────────────────────────────────┐");
            System.out.printf ("  │  🏅 %-45s│%n", scholarshipName);
            System.out.println("  ├──────────────────────────────────────────────────┤");
            System.out.printf ("  │  Recipient : %-36s│%n", recipientName);
            System.out.printf ("  │  Amount    : $%-35.2f│%n", amount);
            System.out.printf ("  │  Reason    : %-36s│%n", reason);
            System.out.println("  └──────────────────────────────────────────────────┘");
        }
    }

    // ─────────────────────────────────────────────
    // Abstract Scholarship base class
    abstract static class Scholarship {
        protected String scholarshipName;
        protected double baseAmount;
        protected int    maxRecipients;
        protected List<ScholarshipAward> awards = new ArrayList<>();

        public Scholarship(String scholarshipName,
                           double baseAmount, int maxRecipients) {
            this.scholarshipName = scholarshipName;
            this.baseAmount      = baseAmount;
            this.maxRecipients   = maxRecipients;
        }

        // ── Abstract methods — each type defines its own rules ──
        public abstract boolean isEligible(StudentProfile student);
        public abstract double  calculateAmount(StudentProfile student);
        public abstract String  getEligibilitySummary();
        public abstract String  getScholarshipType();

        // ── Concrete method — shared application logic ──────────
        public boolean apply(StudentProfile student) {
            if (awards.size() >= maxRecipients) {
                System.out.printf("  ⚠ [%s] Quota full. %s not considered.%n",
                        scholarshipName, student.name);
                return false;
            }
            if (!isEligible(student)) {
                System.out.printf("  ✘ [%s] %s does not meet criteria.%n",
                        scholarshipName, student.name);
                return false;
            }
            double amount = calculateAmount(student);
            String reason = buildReason(student);
            awards.add(new ScholarshipAward(
                    scholarshipName, student.name, amount, reason));
            System.out.printf("  ✔ [%s] %s awarded $%.2f%n",
                    scholarshipName, student.name, amount);
            return true;
        }

        // Subclasses may override for a custom reason string
        protected String buildReason(StudentProfile s) {
            return "Meets " + getScholarshipType() + " criteria";
        }

        // Display all awards granted by this scholarship
        public void displayAwards() {
            System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
            System.out.printf ("  ║  %-52s║%n", "🎓 " + scholarshipName);
            System.out.printf ("  ║  Type    : %-42s║%n", getScholarshipType());
            System.out.printf ("  ║  Budget  : $%-41.2f║%n", baseAmount * maxRecipients);
            System.out.printf ("  ║  Quota   : %-4d │ Awarded: %-33d║%n",
                    maxRecipients, awards.size());
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  Criteria: %-42s║%n", getEligibilitySummary());
            System.out.println("  ╠══════════════════════════════════════════════════════╣");

            if (awards.isEmpty()) {
                System.out.println("  ║  No awards granted yet.                              ║");
            } else {
                double total = 0;
                System.out.printf("  ║  %-22s %12s  %-15s║%n",
                        "Recipient", "Amount", "Notes");
                System.out.println("  ╠══════════════════════════════════════════════════════╣");
                for (ScholarshipAward a : awards) {
                    System.out.printf("  ║  %-22s $%,-11.2f  %-15s║%n",
                            a.recipientName, a.amount,
                            a.reason.length() > 15
                                    ? a.reason.substring(0, 13) + ".." : a.reason);
                    total += a.amount;
                }
                System.out.println("  ╠══════════════════════════════════════════════════════╣");
                System.out.printf ("  ║  Total Disbursed : $%-33.2f║%n", total);
            }
            System.out.println("  ╚══════════════════════════════════════════════════════╝");
        }

        // Getters
        public String getScholarshipName() { return scholarshipName; }
        public int    getAwardCount()      { return awards.size(); }
        public double getTotalDisbursed()  {
            return awards.stream().mapToDouble(a -> a.amount).sum();
        }
    }

    // ─────────────────────────────────────────────
    // Merit Scholarship — academic excellence
    static class MeritScholarship extends Scholarship {

        private final double minGpa;
        private final int    minCommunityHours;

        // Bonus tiers
        private static final double DISTINCTION_BONUS  = 0.25;  // +25% if GPA ≥ 3.9
        private static final double COMMUNITY_BONUS    = 0.10;  // +10% if hours ≥ 100

        public MeritScholarship(String name, double baseAmount,
                                int maxRecipients, double minGpa,
                                int minCommunityHours) {
            super(name, baseAmount, maxRecipients);
            this.minGpa            = minGpa;
            this.minCommunityHours = minCommunityHours;
        }

        @Override
        public boolean isEligible(StudentProfile s) {
            return s.gpa >= minGpa && s.communityHours >= minCommunityHours;
        }

        // Amount scales with GPA tier + bonus multipliers
        @Override
        public double calculateAmount(StudentProfile s) {
            double amount = baseAmount;
            if (s.gpa >= 3.9) amount += amount * DISTINCTION_BONUS;  // distinction bonus
            if (s.communityHours >= 100) amount += amount * COMMUNITY_BONUS; // service bonus
            return amount;
        }

        @Override
        public String getEligibilitySummary() {
            return String.format("GPA ≥ %.1f, Community hrs ≥ %d", minGpa, minCommunityHours);
        }

        @Override public String getScholarshipType() { return "Merit-Based"; }

        @Override
        protected String buildReason(StudentProfile s) {
            List<String> reasons = new ArrayList<>();
            reasons.add(String.format("GPA %.2f", s.gpa));
            if (s.gpa >= 3.9)           reasons.add("+Distinction");
            if (s.communityHours >= 100) reasons.add("+Community");
            return String.join(", ", reasons);
        }
    }

    // ─────────────────────────────────────────────
    // Need-Based Scholarship — financial hardship
    static class NeedBasedScholarship extends Scholarship {

        private final double maxFamilyIncome;
        private final double fullGrantIncome;  // below this → full amount

        private static final double DISABILITY_BONUS  = 0.20;  // +20%
        private static final double FIRST_GEN_BONUS   = 0.15;  // +15%

        public NeedBasedScholarship(String name, double baseAmount,
                                    int maxRecipients, double maxFamilyIncome,
                                    double fullGrantIncome) {
            super(name, baseAmount, maxRecipients);
            this.maxFamilyIncome = maxFamilyIncome;
            this.fullGrantIncome = fullGrantIncome;
        }

        @Override
        public boolean isEligible(StudentProfile s) {
            return s.familyIncome <= maxFamilyIncome;
        }

        // Sliding scale: lower income → higher award
        @Override
        public double calculateAmount(StudentProfile s) {
            double amount;

            if (s.familyIncome <= fullGrantIncome) {
                amount = baseAmount;                               // full grant
            } else {
                // Linear taper between fullGrantIncome and maxFamilyIncome
                double ratio = 1.0 - ((s.familyIncome - fullGrantIncome)
                        / (maxFamilyIncome - fullGrantIncome));
                amount = baseAmount * ratio;
            }

            if (s.hasDisability) amount += baseAmount * DISABILITY_BONUS;
            if (s.isFirstGen)    amount += baseAmount * FIRST_GEN_BONUS;
            return amount;
        }

        @Override
        public String getEligibilitySummary() {
            return String.format("Family income ≤ $%.0f/yr", maxFamilyIncome);
        }

        @Override public String getScholarshipType() { return "Need-Based"; }

        @Override
        protected String buildReason(StudentProfile s) {
            List<String> reasons = new ArrayList<>();
            reasons.add(String.format("Income $%.0f", s.familyIncome));
            if (s.hasDisability) reasons.add("+Disability");
            if (s.isFirstGen)    reasons.add("+FirstGen");
            return String.join(", ", reasons);
        }
    }

    // ─────────────────────────────────────────────
    // ScholarshipOffice — processes all applications
    static class ScholarshipOffice {
        private final List<Scholarship>    scholarships = new ArrayList<>();
        private final List<StudentProfile> applicants   = new ArrayList<>();

        public void addScholarship(Scholarship s) {
            scholarships.add(s);
            System.out.printf("  + Registered: %-30s [%s]%n",
                    s.getScholarshipName(), s.getScholarshipType());
        }

        public void addApplicant(StudentProfile p) {
            applicants.add(p);
        }

        // Process every applicant against every scholarship
        public void processAllApplications() {
            System.out.println("\n  Processing " + applicants.size()
                    + " applicants across " + scholarships.size()
                    + " scholarships...\n");
            for (Scholarship sc : scholarships) {
                System.out.println("  ── " + sc.getScholarshipName() + " ──");
                for (StudentProfile sp : applicants) sc.apply(sp);
                System.out.println();
            }
        }

        // Summary table across all scholarships
        public void displaySummary() {
            System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
            System.out.println("  ║              SCHOLARSHIP OFFICE SUMMARY              ║");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  Total Scholarships : %-30d║%n", scholarships.size());
            System.out.printf ("  ║  Total Applicants   : %-30d║%n", applicants.size());
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  %-25s %8s  %12s║%n",
                    "Scholarship", "Awards", "Disbursed");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");

            double grandTotal = 0;
            for (Scholarship sc : scholarships) {
                System.out.printf("  ║  %-25s %8d  $%,-11.2f║%n",
                        sc.getScholarshipName(),
                        sc.getAwardCount(),
                        sc.getTotalDisbursed());
                grandTotal += sc.getTotalDisbursed();
            }

            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  Grand Total Disbursed : $%-27.2f║%n", grandTotal);
            System.out.println("  ╚══════════════════════════════════════════════════════╝");
        }

        // Top award across all scholarships
        public void displayTopRecipients() {
            System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
            System.out.println("  ║              TOP RECIPIENTS                          ║");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");

            scholarships.stream()
                    .flatMap(sc -> sc.awards.stream())
                    .sorted(Comparator.comparingDouble(
                            (ScholarshipAward a) -> a.amount).reversed())
                    .limit(5)
                    .forEach(a -> System.out.printf(
                            "  ║  %-20s  %-22s $%,-8.2f║%n",
                            a.recipientName, a.scholarshipName, a.amount));

            System.out.println("  ╚══════════════════════════════════════════════════════╝");
        }
    }

    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      UNIVERSITY SCHOLARSHIP SYSTEM           ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // ── Define scholarships ────────────────────────
        System.out.println("━━━━ 1. REGISTERING SCHOLARSHIPS ━━━━\n");

        MeritScholarship     merit1 = new MeritScholarship(
                "Dean's Excellence Award", 5000, 3, 3.7, 50);

        MeritScholarship     merit2 = new MeritScholarship(
                "STEM Leadership Grant",  3000, 2, 3.5, 80);

        NeedBasedScholarship need1  = new NeedBasedScholarship(
                "Financial Aid Bursary",  4000, 4, 40000, 15000);

        NeedBasedScholarship need2  = new NeedBasedScholarship(
                "Equity Access Award",    2500, 3, 55000, 20000);

        ScholarshipOffice office = new ScholarshipOffice();
        office.addScholarship(merit1);
        office.addScholarship(merit2);
        office.addScholarship(need1);
        office.addScholarship(need2);

        // ── Register applicants ────────────────────────
        System.out.println("\n━━━━ 2. APPLICANT PROFILES ━━━━\n");

        //                        name              prog    GPA   income   hrs  dis    1stGen
        StudentProfile p1 = new StudentProfile("Alice Sharma",  "Eng",  3.95, 12000,  120, false, true);
        StudentProfile p2 = new StudentProfile("Bob Thapa",     "Mgt",  3.72, 45000,   60, false, false);
        StudentProfile p3 = new StudentProfile("Carol Rai",     "Med",  3.40, 18000,   30, true,  true);
        StudentProfile p4 = new StudentProfile("David KC",      "Sci",  3.85, 62000,   90, false, false);
        StudentProfile p5 = new StudentProfile("Eve Shrestha",  "Arts", 2.90, 22000,   10, false, true);
        StudentProfile p6 = new StudentProfile("Frank Gurung",  "Eng",  3.55, 8000,    85, true,  true);

        for (StudentProfile p : List.of(p1, p2, p3, p4, p5, p6))
            p.displayProfile();

        // ── Process applications ───────────────────────
        System.out.println("\n━━━━ 3. PROCESSING APPLICATIONS ━━━━");

        office.addApplicant(p1);
        office.addApplicant(p2);
        office.addApplicant(p3);
        office.addApplicant(p4);
        office.addApplicant(p5);
        office.addApplicant(p6);
        office.processAllApplications();

        // ── Award breakdowns ───────────────────────────
        System.out.println("━━━━ 4. AWARD BREAKDOWNS ━━━━");
        merit1.displayAwards();
        merit2.displayAwards();
        need1.displayAwards();
        need2.displayAwards();

        // ── Summary & top recipients ───────────────────
        office.displaySummary();
        office.displayTopRecipients();
    }
}