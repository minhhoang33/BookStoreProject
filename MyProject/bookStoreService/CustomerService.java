package MyProject.bookStoreService;

import MyProject.InvalidPhoneNumberException;
import MyProject.bookStore.entities.Book;
import MyProject.bookStore.entities.Ticket;
import MyProject.bookStore.entities.TicketRentBook;
import MyProject.bookStore.entities.TicketReturnBook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CustomerService {
    // xem truyen
    public void viewBooks(ArrayList<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Chưa có truyện nào trong kho T_T");
        }
        books.sort(Comparator.comparingDouble(Book::getRating).reversed());
        System.out.println("Danh sách truyện:");
        for (Book book : books) {
            System.out.println("ID: "+ book.getId() +  " ,Tên: " + book.getTitle() + ", Thể loại: " + book.getGenre() +
                    ", Giá: " + book.getPrice() + ", Đánh giá: " + book.getRating() +
                    ", Số lượng trong kho: " + book.getStock() + ",Tình trạng truyện:" + book.getCondition());
        }
    }
    // danh gia truyen
    public void rateBook(Scanner scanner, ArrayList<Book> books) {
        System.out.print("Nhập tên truyện muốn đánh giá: ");
        String title = scanner.nextLine();
        Book book = AdminService.findBookByTitle(title,books);

        if (book != null) {
            System.out.print("Nhập đánh giá của bạn (từ 1 đến 5): ");
            double customerRating = Double.parseDouble(scanner.nextLine());

            if (customerRating >= 1 && customerRating <= 5) {
                book.addRating(customerRating);
                System.out.println("Cảm ơn bạn đã đánh giá! Đánh giá trung bình hiện tại của truyện là: " + book.getRating());
            } else {
                System.out.println("Đánh giá không hợp lệ. Vui lòng nhập giá trị từ 1 đến 5.");
            }
        } else {
            System.out.println("Truyện không tồn tại.");
        }
    }

// mua truyen
  public void purchaseOrRentBook(Scanner scanner,List<Ticket> tickets,ArrayList<Book> books) {
        System.out.print("Nhập tên truyện muốn mua: ");
        String title = scanner.nextLine();
        Book book = AdminService.findBookByTitle(title, books);

      if (book != null) {
          System.out.print("Nhập tên của bạn: ");
          String customerName = scanner.nextLine();

          String customerPhone;
          while (true) {
              System.out.print("Nhập số điện thoại của bạn: ");
              customerPhone = scanner.nextLine();
              try {
                  validatePhoneNumber(customerPhone);
                  break;
              } catch (InvalidPhoneNumberException e) {
                  System.out.println(e.getMessage());
              }
          }

            System.out.print("Nhập số lượng truyện muốn mua: ");
            int quantity = Integer.parseInt(scanner.nextLine());
          if (quantity > 0 && quantity <= book.getStock()) {
              Ticket ticket = new Ticket(customerName, customerPhone, title, quantity);
              double totalAmount = book.getPrice() * quantity; // Tong tien
              ticket.setTotalAmount(totalAmount);
              tickets.add(ticket);

              System.out.println("Số tiền bạn phải thanh toán là: " + totalAmount);
              System.out.println("Đỗ Minh Hoàng || Techcombank || 19037623898018");
              System.out.println("Khi bạn chuyển khoản thành công admin sẽ chấp nhận yêu cầu mua hàng.");
          } else {
              System.out.println("Số lượng không hợp lệ. Vui lòng kiểm tra lại.");
          }
        } else {
            System.out.println("Truyện không tồn tại.");
        }
    }
    // thue truyen
    public void RenBook(Scanner scanner, ArrayList<Book> books, List<TicketRentBook> ticketRentBooks) {
        System.out.print("Nhập tên truyện muốn thuê: ");
        String title = scanner.nextLine();
        Book book = AdminService.findBookByTitle(title, books);

        if (book != null) {
            System.out.print("Nhập tên của bạn: ");
            String customerName = scanner.nextLine();

            String customerPhone;
            while (true) {
                System.out.print("Nhập số điện thoại của bạn: ");
                customerPhone = scanner.nextLine();
                try {
                    validatePhoneNumber(customerPhone);
                    break;
                } catch (InvalidPhoneNumberException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.print("Nhập số lượng truyện muốn thuê: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            if (quantity > 0 && quantity <= book.getStock()) {
                LocalDate returnDate;
                while (true) {
                    System.out.print("Nhập ngày trả sách (yyyy-MM-dd): ");
                    String returnDateString = scanner.nextLine();
                    try {
                        returnDate = LocalDate.parse(returnDateString, DateTimeFormatter.ISO_LOCAL_DATE);
                        if (returnDate.isAfter(LocalDate.now())) {
                            break; // Ngay tra hop le, thoat khoi vong lap
                        } else {
                            System.out.println("Ngày trả phải lớn hơn ngày hiện tại. Vui lòng nhập lại.");
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd.");
                    }
                }

                TicketRentBook ticketRentBook = new TicketRentBook(customerName,customerPhone, title, quantity, returnDate);
                ticketRentBooks.add(ticketRentBook);
                // tinh tien coc
                double deposit = ((book.getPrice() *50/100))*quantity;
                // tinh phi thue truyen
                long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), returnDate);
                int retalFee = Math.toIntExact((daysBetween * 5000)* quantity);
                System.out.println("Số tiền bạn phải cọc trước là(phải cọc trước 50% gtri đơn hàng): " + deposit);
                System.out.println("Đỗ Minh Hoàng || Techcombank || 19037623898018");
                System.out.println("Số ngày bạn thuê truyện là " + daysBetween + " ngày");
                System.out.println("Phí thuê truyện của bạn là: " + retalFee + " Vui lòng thanh toán khi trả sách");
                System.out.println("Khi bạn chuyển khoản thành công admin sẽ chấp nhận yêu cầu thuê truyện");

            } else {
                System.out.println("Số lượng không hợp lệ. Vui lòng kiểm tra lại.");
            }
        } else {
            System.out.println("Truyện không tồn tại.");
        }
    }
    // tra truyen
    public TicketReturnBook returnBook(Scanner scanner, List<TicketRentBook> ticketRentBooks) {
        System.out.println("Nhập thông tin trả sách:");

        System.out.print("Nhập tên khách hàng: ");
        String customerName = scanner.nextLine();

        System.out.print("Nhập số điện thoại khách hàng: ");
        String customerPhone = scanner.nextLine();

        System.out.print("Nhập tên sách muốn trả: ");
        String bookTitle = scanner.nextLine();

        TicketRentBook matchingRentTicket = null;
        for (TicketRentBook rentTicket : ticketRentBooks) {
            if (rentTicket.getCustomerName().equalsIgnoreCase(customerName)
                    && rentTicket.getCustomerPhone().equals(customerPhone)
                    && rentTicket.getBookTitle().equalsIgnoreCase(bookTitle)) {
                matchingRentTicket = rentTicket;
                break;
            }
        }

        if (matchingRentTicket == null) {
            System.out.println("Không tìm thấy phiếu thuê tương ứng. Vui lòng kiểm tra lại.");
            return null;
        }

        int returnQuantity;
        while (true) {
            System.out.print("Nhập số lượng sách muốn trả: ");
            returnQuantity = Integer.parseInt(scanner.nextLine());

            if (returnQuantity > matchingRentTicket.getQuantity()) {
                System.out.println("Số lượng trả không được lớn hơn số lượng đã thuê (" + matchingRentTicket.getQuantity() + "). Vui lòng nhập lại.");
            } else {
                break;
            }
        }

        int condition;
        while (true) {
            System.out.print("Nhập tình trạng sách (1: Rách nặng, 5: Nguyên vẹn): ");
            condition = Integer.parseInt(scanner.nextLine());

            if (condition < 1 || condition > 5) {
                System.out.println("Tình trạng sách phải nằm trong khoảng từ 1 đến 5. Vui lòng nhập lại.");
            } else {
                break;
            }
        }
        TicketReturnBook returnTicket = new TicketReturnBook(customerName, customerPhone, bookTitle, returnQuantity, condition);

        System.out.println("Yêu cầu trả sách của bạn đã được gửi. Vui lòng chờ admin duyệt!");

        return returnTicket;

    }
    // lich su
    public void viewTransactionHistory(String customerName, String customerPhone,
                                       List<Ticket> tickets,
                                       List<TicketRentBook> ticketRentBooks,
                                       List<TicketReturnBook> ticketReturnBooks) {
        System.out.println("===== Lịch sử giao dịch của bạn =====");

        // Lich su mua sach
        System.out.println("Lịch sử mua sách:");
        boolean hasPurchase = false;
        for (Ticket ticket : tickets) {
            if (ticket.getCustomerName().equalsIgnoreCase(customerName)
                    && ticket.getCustomerPhone().equals(customerPhone)) {
                System.out.println("- Tên sách: " + ticket.getBookTitle() +
                        ", Số lượng: " + ticket.getQuantity() +
                        ", Tổng tiền: " + ticket.getTotalAmount());
                hasPurchase = true;
            }
        }
        if (!hasPurchase) {
            System.out.println("Không có giao dịch mua sách.");
        }

        // Lich su thue sach
        System.out.println("\nLịch sử thuê sách:");
        boolean hasRental = false;
        for (TicketRentBook rentTicket : ticketRentBooks) {
            if (rentTicket.getCustomerName().equalsIgnoreCase(customerName)
                    && rentTicket.getCustomerPhone().equals(customerPhone)) {
                System.out.println("- Tên sách: " + rentTicket.getBookTitle() +
                        ", Số lượng: " + rentTicket.getQuantity() +
                        ", Ngày trả: " + rentTicket.getReturnDate());
                hasRental = true;
            }
        }
        if (!hasRental) {
            System.out.println("Không có giao dịch thuê sách.");
        }

        // Lịch sử tra sach
        System.out.println("\nLịch sử trả sách:");
        boolean hasReturn = false;
        for (TicketReturnBook returnTicket : ticketReturnBooks) {
            if (returnTicket.getCustomerName().equalsIgnoreCase(customerName)
                    && returnTicket.getCustomerPhone().equals(customerPhone)) {
                System.out.println("- Tên sách: " + returnTicket.getBookTitle() +
                        ", Số lượng: " + returnTicket.getQuantity() +
                        ", Tình trạng: " + returnTicket.getCondition());
                hasReturn = true;
            }
        }
        if (!hasReturn) {
            System.out.println("Không có giao dịch trả sách.");
        }
    }



    // check phone number
    private static void validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException {

        String phonePattern = "\\d{10,15}";
        if (!Pattern.matches(phonePattern, phoneNumber)) {
            throw new InvalidPhoneNumberException("Số điện thoại không hợp lệ. Vui lòng nhập lại (chỉ chứa chữ số, từ 10 đến 15 ký tự).");
        }
    }

}
