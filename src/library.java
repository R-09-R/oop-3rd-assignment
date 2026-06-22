import java.util.ArrayList;
import java.util.List;

public class library {

    // ─────────────────────────────────────────────
    // Member superclass
    static class Member {
        protected int    memberId;
        protected String name;
        protected String email;
        protected List<String> borrowedBooks;

        public Member(int memberId, String name, String email) {
            this.memberId     = memberId;
            this.name         = name;
            this.email        = email;
            this.borrowedBooks = new ArrayList<>();
        }

        // To be overridden — each member type sets its own limit
        public int getBorrowLimit()    { return 0; }
        public int getLoanDays()       { return 0; }
        public double getFinePerDay()  { return 0.0; }
        public String getMemberType()  { return "Member"; }

        // Borrow a book
        public boolean borrowBook(String bookTitle) {
            if (borrowedBooks.size() >= getBorrowLimit()) {
                System.out.printf("✘ [%s] Borrow limit of %d reached. Cannot borrow \"%s\"%n",
                        name, getBorrowLimit(), bookTitle);
                return false;
            }
            borrowedBooks.add(bookTitle);
            System.out.printf("✔ [%s] Borrowed: \"%s\"  (%d/%d slots used)%n",
                    name, bookTitle, borrowedBooks.size(), getBorrowLimit());
            return true;
        }

        // Return a book
        public boolean returnBook(String bookTitle) {
            if (borrowedBooks.remove(bookTitle)) {
                System.out.printf("↩ [%s] Returned: \"%s\"  (%d/%d slots used)%n",
                        name, bookTitle, borrowedBooks.size(), getBorrowLimit());
                return true;
            }
            System.out.printf("⚠ [%s] \"%s\" not found in borrowed list.%n",
                    name, bookTitle);
            return false;
        }

        // Calculate fine
        public double calculateFine(int overdueDays) {
            return overdueDays > 0 ? overdueDays * getFinePerDay() : 0.0;
        }

        // Display member card
        public void displayMemberCard() {
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.printf ("║  %-44s║%n", "📚 " + getMemberType() + " Library Card");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.printf ("║  Member ID   : %-29d║%n", memberId);
            System.out.printf ("║  Name        : %-29s║%n", name);
            System.out.printf ("║  Email       : %-29s║%n", email);
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.printf ("║  Borrow Limit: %-29d║%n", getBorrowLimit());
            System.out.printf ("║  Loan Period : %-29s║%n", getLoanDays() + " days");
            System.out.printf ("║  Fine/Day    : $%-28.2f║%n", getFinePerDay());
            displayExtraPrivileges();
            System.out.println("╠══════════════════════════════════════════════╣");

            if (borrowedBooks.isEmpty()) {
                System.out.println("║  Borrowed    : None                          ║");
            } else {
                System.out.printf("║  Borrowed (%d/%d):%-29s║%n",
                        borrowedBooks.size(), getBorrowLimit(), "");
                for (String book : borrowedBooks)
                    System.out.printf("║    • %-39s║%n", book);
            }
            System.out.println("╚══════════════════════════════════════════════╝");
        }

        // Hook — subclasses inject extra privilege rows
        protected void displayExtraPrivileges() {}
    }

    // ─────────────────────────────────────────────
    // StudentMember subclass
    static class StudentMember extends Member {
        private String programme;   // e.g. "BSc Computer Science"
        private int    yearOfStudy;

        private static final int    BORROW_LIMIT  = 3;
        private static final int    LOAN_DAYS     = 14;
        private static final double FINE_PER_DAY  = 0.50;

        public StudentMember(int memberId, String name,
                             String email, String programme, int yearOfStudy) {
            super(memberId, name, email);
            this.programme   = programme;
            this.yearOfStudy = yearOfStudy;
        }

        @Override public int    getBorrowLimit()  { return BORROW_LIMIT; }
        @Override public int    getLoanDays()     { return LOAN_DAYS; }
        @Override public double getFinePerDay()   { return FINE_PER_DAY; }
        @Override public String getMemberType()   { return "Student"; }

        @Override
        protected void displayExtraPrivileges() {
            System.out.printf("║  Programme   : %-29s║%n", programme);
            System.out.printf("║  Year        : Year %-24d║%n", yearOfStudy);
        }
    }

    // ─────────────────────────────────────────────
    // TeacherMember subclass
    static class TeacherMember extends Member {
        private String department;
        private String designation;   // e.g. "Associate Professor"
        private boolean canRequestPurchase;

        private static final int    BORROW_LIMIT  = 10;
        private static final int    LOAN_DAYS     = 30;
        private static final double FINE_PER_DAY  = 0.25;  // lower fine rate

        public TeacherMember(int memberId, String name, String email,
                             String department, String designation,
                             boolean canRequestPurchase) {
            super(memberId, name, email);
            this.department         = department;
            this.designation        = designation;
            this.canRequestPurchase = canRequestPurchase;
        }

        @Override public int    getBorrowLimit()  { return BORROW_LIMIT; }
        @Override public int    getLoanDays()     { return LOAN_DAYS; }
        @Override public double getFinePerDay()   { return FINE_PER_DAY; }
        @Override public String getMemberType()   { return "Teacher"; }

        // Teacher-only privilege
        public void requestBookPurchase(String bookTitle) {
            if (canRequestPurchase)
                System.out.printf("📦 [%s] Purchase request submitted for: \"%s\"%n",
                        name, bookTitle);
            else
                System.out.printf("⚠ [%s] Purchase requests not enabled for this account.%n",
                        name);
        }

        @Override
        protected void displayExtraPrivileges() {
            System.out.printf("║  Department  : %-29s║%n", department);
            System.out.printf("║  Designation : %-29s║%n", designation);
            System.out.printf("║  Buy Request : %-29s║%n",
                    canRequestPurchase ? "Enabled ✔" : "Disabled ✘");
        }
    }

    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        // Create members
        StudentMember s1 = new StudentMember(
                1001, "Alice Sharma", "alice@uni.edu",
                "BSc Computer Science", 2);

        StudentMember s2 = new StudentMember(
                1002, "Bob Thapa", "bob@uni.edu",
                "BA Economics", 3);

        TeacherMember t1 = new TeacherMember(
                2001, "Dr. Carol Rai", "carol@uni.edu",
                "Engineering", "Associate Professor", true);

        System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========\n");

        // ── Alice borrows up to her limit ──────────────
        System.out.println("--- Alice's Transactions ---");
        s1.borrowBook("Clean Code");
        s1.borrowBook("The Pragmatic Programmer");
        s1.borrowBook("Introduction to Algorithms");
        s1.borrowBook("Design Patterns");          // exceeds limit

        // ── Bob borrows and returns ─────────────────────
        System.out.println("\n--- Bob's Transactions ---");
        s2.borrowBook("Economics: Principles");
        s2.returnBook("Economics: Principles");
        s2.returnBook("Microeconomics");           // never borrowed

        // ── Teacher borrows freely, requests purchase ───
        System.out.println("\n--- Dr. Carol's Transactions ---");
        t1.borrowBook("Advanced Machine Learning");
        t1.borrowBook("Deep Learning with Python");
        t1.borrowBook("Pattern Recognition");
        t1.requestBookPurchase("Transformers for NLP");

        // ── Display member cards ────────────────────────
        System.out.println("\n========== MEMBER CARDS ==========\n");
        s1.displayMemberCard();
        System.out.println();
        t1.displayMemberCard();

        // ── Fine calculation ────────────────────────────
        System.out.println("\n========== FINE CALCULATOR ==========");
        int overdueDays = 5;
        System.out.printf("%nIf %d days overdue:%n", overdueDays);
        System.out.printf("  %-20s → $%.2f%n",
                s1.name, s1.calculateFine(overdueDays));
        System.out.printf("  %-20s → $%.2f%n",
                t1.name, t1.calculateFine(overdueDays));
    }
}