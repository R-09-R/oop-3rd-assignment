public class information {

    static class Student {
        // Private fields (Encapsulation)
        private String name;
        private int rollNumber;
        private String faculty;

        // Constructor
        public Student(String name, int rollNumber, String faculty) {
            this.name = name;
            this.rollNumber = rollNumber;
            this.faculty = faculty;
        }

        // Getters
        public String getName()     { return name; }
        public int getRollNumber()  { return rollNumber; }
        public String getFaculty()  { return faculty; }

        // Setters
        public void setName(String name)          { this.name = name; }
        public void setRollNumber(int rollNumber)  { this.rollNumber = rollNumber; }
        public void setFaculty(String faculty)     { this.faculty = faculty; }

        // Display student profile
        public void displayProfile() {
            System.out.println("================================");
            System.out.println("      STUDENT ADMISSION PROFILE");
            System.out.println("================================");
            System.out.println("Name        : " + name);
            System.out.println("Roll Number : " + rollNumber);
            System.out.println("Faculty     : " + faculty);
            System.out.println("================================");
        }
    }

    public static void main(String[] args) {
        // Create student using constructor
        Student s1 = new Student("Alice Sharma", 101, "Engineering");

        // Display original profile
        s1.displayProfile();

        // Update faculty using setter
        s1.setFaculty("Computer Science");
        System.out.println("\nAfter updating faculty...\n");

        // Display updated profile
        s1.displayProfile();

        // Access individual fields via getters
        System.out.println("Getter Check → Name: " + s1.getName()
                + ", Roll: " + s1.getRollNumber()
                + ", Faculty: " + s1.getFaculty());
    }
}