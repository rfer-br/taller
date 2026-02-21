import java.util.*;

/*
Scenario:
You are asked to implement a small Java program that processes a list of Payment objects. Each payment has:
- `id` (String)
- `amount` (double)
- `currency` (String)
- `status` (enum: `PENDING`, `SUCCESS`, `FAILED`)

Requirements:
1. Create the `Payment` class with proper encapsulation and `toString()`.
2. Implement a `PaymentProcessor` class with methods to:
- Add a new payment.
- Retrieve all payments.
- Retrieve payments filtered by status.
- Calculate statistics:
- Total number of payments.
- Total amount of successful payments.
- Average amount of successful payments.
3. Demonstrate usage in a `main()` method:
- Create a few sample payments.
- Process them.
- Print statistics.
4. Bonus (if time allows):
- Sort payments by amount (descending).
- Use Java Streams to implement filtering and statistics.
- Add a simple concurrency simulation: process payments in parallel using `CompletableFuture` or `parallelStream()`.
* */

public class PaymentCalculator {

    public enum PaymentStatus{
        PENDING,
        SUCCESS,
        FAILED
    }

    static class Payment {

        private String id;
        private double amount;
        private String currency;
        private volatile PaymentStatus status;

        public Payment(String id, double amount, String currency, PaymentStatus status) {
            this.id = id;
            this.amount = amount;
            this.currency = currency;
            this.status = Objects.requireNonNull(status);
        }

        public String getId(){
            return this.id;
        }
        public double getAmount(){
            return this.amount;
        }
        public String getCurrency(){
            return this.currency;
        }
        public PaymentStatus getStatus(){
            return this.status;
        }

        @Override
        public String toString() {
            return "Payment{" +
                    "id='" + id + '\'' +
                    ", amount=" + amount +
                    ", currency='" + currency + '\'' +
                    ", status=" + status +
                    '}';
        }

    }

    static class PaymentProcessor {

        private List<Payment> payments = Collections.synchronizedList(new ArrayList<>());

        // add a new payment
        public void addNewPayment(Payment payment){
            Objects.requireNonNull(payment, "Payment must not be null");
            this.payments.add(payment);
        }

        // retrieve all payments
        public List<Payment> getAllPayments(){
            synchronized (payments){
                return List.copyOf(this.payments);
            }
        }

        // filter payments by status
        public List<Payment> getAllPaymentByStatus(PaymentStatus status){
            Objects.requireNonNull(status, "Status must not be null");
            return getAllPayments().stream().filter(p -> p.getStatus() == status).toList();
        }

        // Total number of payments.
        public Integer getTotalNumberOfPayments(){
            return getAllPayments().size();
        }

        // Total amount of successful payments
        public double getTotalAmountOfSuccessfulPayments(){
            return getAllPayments().stream()
                    .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                    .mapToDouble(Payment::getAmount)
                    .sum();
        }

        // Average amount of successful payments
        public double getAverageAmountOfSuccessfulPayments() {
            OptionalDouble avg = getAllPayments().stream()
                    .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                    .mapToDouble(Payment::getAmount)
                    .average();
            return avg.orElse(0.0);
        }

        // Sort payments by amount (descending)
        public List<Payment> getPaymentsSortedByAmountDesc() {
            return getAllPayments().stream()
                    .sorted(Comparator.comparingDouble(Payment::getAmount).reversed())
                    .toList();
        }
    }

    public static void main(String[] args){

        PaymentProcessor processor = new PaymentProcessor();

        // Sample payments
        processor.addNewPayment(new Payment("1", 120.0, "USD", PaymentStatus.SUCCESS));
        processor.addNewPayment(new Payment("2", 75.5, "USD", PaymentStatus.PENDING));
        processor.addNewPayment(new Payment("3", 300.0, "USD", PaymentStatus.FAILED));
        processor.addNewPayment(new Payment("4", 20.0, "USD", PaymentStatus.SUCCESS));
        processor.addNewPayment(new Payment("5", 250.0, "USD", PaymentStatus.PENDING));

        System.out.println("=== ALL PAYMENTS ===");
        processor.getAllPayments().forEach(System.out::println);

        System.out.println("\n=== PENDING PAYMENTS ===");
        processor.getAllPaymentByStatus(PaymentStatus.PENDING)
                .forEach(System.out::println);

        System.out.println("\n=== SORTED BY AMOUNT (DESC) ===");
        processor.getPaymentsSortedByAmountDesc()
                .forEach(System.out::println);

        System.out.println("\n=== STATISTICS ===");
        System.out.println("Total payments: " +
                processor.getTotalNumberOfPayments());
        System.out.println("Total SUCCESS amount: " +
                processor.getTotalAmountOfSuccessfulPayments());
        System.out.println("Average SUCCESS amount: " +
                processor.getAverageAmountOfSuccessfulPayments());

    }

}