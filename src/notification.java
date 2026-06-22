import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class notification {

    // ─────────────────────────────────────────────
    // Notification Interface
    interface Notification {
        boolean send(String recipient, String subject, String message);
        boolean sendBulk(List<String> recipients, String subject, String message);
        String  getDeliveryStatus(String recipient);
        void    displayLog();
        String  getChannelName();
    }

    // ─────────────────────────────────────────────
    // Delivery record — tracks each send attempt
    static class DeliveryRecord {
        String    recipient;
        String    subject;
        String    status;      // SENT / FAILED
        String    timestamp;
        String    channel;

        DeliveryRecord(String recipient, String subject,
                       String status, String channel) {
            this.recipient = recipient;
            this.subject   = subject;
            this.status    = status;
            this.channel   = channel;
            this.timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        }
    }

    // ─────────────────────────────────────────────
    // Email Notification Implementation
    static class EmailNotification implements Notification {

        private final String        senderEmail;
        private final String        smtpServer;
        private final List<DeliveryRecord> log = new ArrayList<>();

        // Simulated invalid domains for demo
        private static final List<String> BLOCKED_DOMAINS =
                List.of("spam.com", "invalid.net");

        public EmailNotification(String senderEmail, String smtpServer) {
            this.senderEmail = senderEmail;
            this.smtpServer  = smtpServer;
        }

        // Validate email format
        private boolean isValidEmail(String email) {
            return email != null
                    && email.contains("@")
                    && email.contains(".")
                    && BLOCKED_DOMAINS.stream()
                    .noneMatch(d -> email.endsWith("@" + d));
        }

        @Override
        public boolean send(String recipient, String subject, String message) {
            System.out.println("\n  📧 Sending Email...");
            System.out.printf ("     To      : %s%n", recipient);
            System.out.printf ("     Subject : %s%n", subject);
            System.out.printf ("     Server  : %s%n", smtpServer);
            System.out.printf ("     Body    : %.60s%s%n",
                    message, message.length() > 60 ? "..." : "");

            boolean success = isValidEmail(recipient);
            String  status  = success ? "SENT" : "FAILED";
            String  icon    = success ? "✔" : "✘";

            System.out.printf ("     Status  : %s %s%n", icon, status);
            log.add(new DeliveryRecord(recipient, subject, status, getChannelName()));
            return success;
        }

        @Override
        public boolean sendBulk(List<String> recipients,
                                String subject, String message) {
            System.out.println("\n  📧 Bulk Email Dispatch");
            System.out.printf ("     Subject    : %s%n", subject);
            System.out.printf ("     Recipients : %d%n", recipients.size());
            System.out.println("     ─────────────────────────────────");

            long successCount = recipients.stream()
                    .filter(r -> send(r, subject, message))
                    .count();

            System.out.printf("%n     ✔ %d/%d delivered successfully.%n",
                    successCount, recipients.size());
            return successCount == recipients.size();
        }

        @Override
        public String getDeliveryStatus(String recipient) {
            return log.stream()
                    .filter(r -> r.recipient.equals(recipient))
                    .reduce((a, b) -> b)           // latest record
                    .map(r -> r.status)
                    .orElse("NO RECORD FOUND");
        }

        @Override
        public void displayLog() {
            System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
            System.out.println("  ║           EMAIL DELIVERY LOG                         ║");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  Sender : %-42s║%n", senderEmail);
            System.out.printf ("  ║  Server : %-42s║%n", smtpServer);
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  %-28s %-10s %-12s║%n", "Recipient", "Status", "Time");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");

            if (log.isEmpty()) {
                System.out.println("  ║  No records yet.                                     ║");
            } else {
                for (DeliveryRecord r : log) {
                    String icon = r.status.equals("SENT") ? "✔" : "✘";
                    System.out.printf("  ║  %s %-27s %-10s %-12s║%n",
                            icon, r.recipient, r.status, r.timestamp.substring(11));
                }
            }

            long sent   = log.stream().filter(r -> r.status.equals("SENT")).count();
            long failed = log.size() - sent;
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  Total: %-4d  ✔ Sent: %-4d  ✘ Failed: %-12d║%n",
                    log.size(), sent, failed);
            System.out.println("  ╚══════════════════════════════════════════════════════╝");
        }

        @Override
        public String getChannelName() { return "Email"; }
    }

    // ─────────────────────────────────────────────
    // SMS Notification Implementation
    static class SMSNotification implements Notification {

        private final String        senderId;
        private final String        gateway;
        private final List<DeliveryRecord> log = new ArrayList<>();

        private static final int    MAX_SMS_LENGTH  = 160;
        private static final List<String> VALID_PREFIXES =
                List.of("+977", "+1", "+44", "+91"); // Nepal, US, UK, India

        public SMSNotification(String senderId, String gateway) {
            this.senderId = senderId;
            this.gateway  = gateway;
        }

        // Validate phone number
        private boolean isValidPhone(String phone) {
            return phone != null
                    && VALID_PREFIXES.stream().anyMatch(phone::startsWith)
                    && phone.replaceAll("[^0-9]", "").length() >= 7;
        }

        // SMS has 160-char limit; auto-split into parts
        private int smsPartsNeeded(String message) {
            return (int) Math.ceil(message.length() / (double) MAX_SMS_LENGTH);
        }

        @Override
        public boolean send(String recipient, String subject, String message) {
            int parts = smsPartsNeeded(message);
            System.out.println("\n  📱 Sending SMS...");
            System.out.printf ("     To      : %s%n", recipient);
            System.out.printf ("     Subject : %s%n", subject);
            System.out.printf ("     Gateway : %s%n", gateway);
            System.out.printf ("     Message : %.60s%s%n",
                    message, message.length() > 60 ? "..." : "");
            System.out.printf ("     Parts   : %d SMS part(s) (%d chars)%n",
                    parts, message.length());

            boolean success = isValidPhone(recipient);
            String  status  = success ? "SENT" : "FAILED";
            String  icon    = success ? "✔" : "✘";

            System.out.printf ("     Status  : %s %s%n", icon, status);
            log.add(new DeliveryRecord(recipient, subject, status, getChannelName()));
            return success;
        }

        @Override
        public boolean sendBulk(List<String> recipients,
                                String subject, String message) {
            System.out.println("\n  📱 Bulk SMS Dispatch");
            System.out.printf ("     Subject    : %s%n", subject);
            System.out.printf ("     Recipients : %d%n", recipients.size());
            System.out.println("     ─────────────────────────────────");

            long successCount = recipients.stream()
                    .filter(r -> send(r, subject, message))
                    .count();

            System.out.printf("%n     ✔ %d/%d delivered successfully.%n",
                    successCount, recipients.size());
            return successCount == recipients.size();
        }

        @Override
        public String getDeliveryStatus(String recipient) {
            return log.stream()
                    .filter(r -> r.recipient.equals(recipient))
                    .reduce((a, b) -> b)
                    .map(r -> r.status)
                    .orElse("NO RECORD FOUND");
        }

        @Override
        public void displayLog() {
            System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
            System.out.println("  ║             SMS DELIVERY LOG                         ║");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  Sender  : %-42s║%n", senderId);
            System.out.printf ("  ║  Gateway : %-42s║%n", gateway);
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  %-28s %-10s %-12s║%n", "Recipient", "Status", "Time");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");

            if (log.isEmpty()) {
                System.out.println("  ║  No records yet.                                     ║");
            } else {
                for (DeliveryRecord r : log) {
                    String icon = r.status.equals("SENT") ? "✔" : "✘";
                    System.out.printf("  ║  %s %-27s %-10s %-12s║%n",
                            icon, r.recipient, r.status, r.timestamp.substring(11));
                }
            }

            long sent   = log.stream().filter(r -> r.status.equals("SENT")).count();
            long failed = log.size() - sent;
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf ("  ║  Total: %-4d  ✔ Sent: %-4d  ✘ Failed: %-12d║%n",
                    log.size(), sent, failed);
            System.out.println("  ╚══════════════════════════════════════════════════════╝");
        }

        @Override
        public String getChannelName() { return "SMS"; }
    }

    // ─────────────────────────────────────────────
    // NotificationService — sends via multiple channels
    static class NotificationService {
        private final List<Notification> channels = new ArrayList<>();

        public void addChannel(Notification channel) {
            channels.add(channel);
            System.out.printf("  + Channel registered: %s%n",
                    channel.getChannelName());
        }

        // Broadcast same message across all channels
        public void broadcast(String recipient, String phone,
                              String subject, String message) {
            System.out.println("\n  ══ BROADCASTING NOTIFICATION ══");
            for (Notification n : channels) {
                String target = n.getChannelName().equals("SMS")
                        ? phone : recipient;
                n.send(target, subject, message);
            }
        }
    }

    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║    UNIVERSITY NOTIFICATION SYSTEM     ║");
        System.out.println("╚═══════════════════════════════════════╝");

        // Create channels
        EmailNotification email = new EmailNotification(
                "noreply@university.edu", "smtp.university.edu");

        SMSNotification sms = new SMSNotification(
                "UNI-ALERT", "sms.gateway.np");

        // ── Single sends ───────────────────────────
        System.out.println("\n━━━━ 1. EXAM RESULT NOTIFICATION ━━━━");
        email.send("alice@student.edu",
                "Exam Results Published",
                "Dear Alice, your semester results are now available. Log in to the portal to view your grades.");

        sms.send("+9779800000001",
                "Exam Results",
                "UNI: Your exam results are out! Visit portal.uni.edu to check grades.");

        // ── Invalid recipient ─────────────────────
        System.out.println("\n━━━━ 2. INVALID RECIPIENT TEST ━━━━");
        email.send("bob@spam.com",
                "Fee Reminder",
                "Your fee is due.");

        sms.send("12345",     // no valid prefix
                "Fee Reminder",
                "Your fee payment is due.");

        // ── Bulk notifications ────────────────────
        System.out.println("\n━━━━ 3. BULK NOTIFICATION ━━━━");
        List<String> emails = List.of(
                "carol@student.edu",
                "david@student.edu",
                "eve@invalid.net",       // blocked domain
                "frank@student.edu");

        List<String> phones = List.of(
                "+9779800000002",
                "+9779800000003",
                "+4412345678",
                "99999");                // invalid

        email.sendBulk(emails,
                "Holiday Notice",
                "The university will remain closed from Dec 25 to Jan 1.");

        sms.sendBulk(phones,
                "Holiday Notice",
                "UNI: Campus closed Dec 25 - Jan 1. Happy holidays!");

        // ── Multi-channel broadcast ───────────────
        System.out.println("\n━━━━ 4. MULTI-CHANNEL BROADCAST ━━━━");
        NotificationService service = new NotificationService();
        service.addChannel(email);
        service.addChannel(sms);
        service.broadcast(
                "grace@student.edu", "+9779800000005",
                "Emergency Alert",
                "URGENT: Campus closed tomorrow due to weather. All exams postponed.");

        // ── Status check ──────────────────────────
        System.out.println("\n━━━━ 5. DELIVERY STATUS CHECK ━━━━");
        System.out.printf("  Email status [alice@student.edu] : %s%n",
                email.getDeliveryStatus("alice@student.edu"));
        System.out.printf("  Email status [bob@spam.com]      : %s%n",
                email.getDeliveryStatus("bob@spam.com"));
        System.out.printf("  SMS   status [+9779800000001]    : %s%n",
                sms.getDeliveryStatus("+9779800000001"));

        // ── Delivery logs ─────────────────────────
        System.out.println("\n━━━━ 6. DELIVERY LOGS ━━━━");
        email.displayLog();
        sms.displayLog();
    }
}