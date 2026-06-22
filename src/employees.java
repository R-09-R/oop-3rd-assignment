public class employees {
    // Base Employee class
    static class Employee {
        protected int id;
        protected String name;
        protected double salary;

        public Employee(int id, String name, double salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        public void displayDetails() {
            System.out.println("Employee ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Salary: $" + salary);
        }
    }

    // Teacher subclass
    static class Teacher extends Employee {
        private String subject;
        private String department;

        public Teacher(int id, String name, double salary, String subject, String department) {
            super(id, name, salary);
            this.subject = subject;
            this.department = department;
        }

        @Override
        public void displayDetails() {
            System.out.println("--- Teaching Staff ---");
            super.displayDetails();
            System.out.println("Subject: " + subject);
            System.out.println("Department: " + department);
        }
    }

    // AdminStaff subclass
    static class AdminStaff extends Employee {
        private String role;
        private String office;

        public AdminStaff(int id, String name, double salary, String role, String office) {
            super(id, name, salary);
            this.role = role;
            this.office = office;
        }

        @Override
        public void displayDetails() {
            System.out.println("--- Administrative Staff ---");
            super.displayDetails();
            System.out.println("Role: " + role);
            System.out.println("Office: " + office);
        }
    }

    public static void main(String[] args) {
        Teacher teacher = new Teacher(101, "Dr. Alice Johnson", 75000, "Computer Science", "Engineering");
        AdminStaff admin = new AdminStaff(201, "Bob Smith", 45000, "Registrar", "Admin Block B");

        teacher.displayDetails();
        System.out.println();
        admin.displayDetails();
    }
}