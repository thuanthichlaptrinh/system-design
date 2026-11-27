# SOLID

Năm nguyên tắc để code dễ sửa và dễ test. Đây là gợi ý, không phải luật. Giai đoạn 0 ưu tiên **S** và **D**.

## S: Single Responsibility

Một lớp chỉ nên có **một lý do để thay đổi**.

```java
// Xấu: một lớp làm mọi thứ
class BookingController {
    // đọc request, kiểm tra quy tắc, viết SQL, gửi email
}

// Tốt
class BookingController { }   // nhận request, trả response
class BookingService { }      // quy tắc nghiệp vụ
class BookingRepository { }   // đọc ghi database
```

Đổi cách trả lỗi thì chỉ sửa controller, đổi truy vấn thì chỉ sửa repository.

## O: Open/Closed

Thêm hành vi mới bằng cách **thêm code**, hạn chế sửa code đang chạy đúng.

Ví dụ: thêm cổng thanh toán mới là thêm một lớp, không phải thêm một nhánh `if` vào giữa hàm thanh toán.

## L: Liskov Substitution

Lớp con phải thay được lớp cha mà nơi gọi không bị bất ngờ.

Ví dụ xấu: một lớp implement `SeatReservationStrategy` nhưng khi ghế đã bán thì im lặng bỏ qua, trong khi các lớp khác ném lỗi. Nơi gọi tưởng đã đặt thành công.

## I: Interface Segregation

Đừng bắt một lớp phụ thuộc vào những phương thức nó không dùng. Nhiều interface nhỏ tốt hơn một interface khổng lồ.

Ví dụ: phần đặt ghế chỉ cần biết thông tin sự kiện thì không nên phụ thuộc vào interface có cả `createEvent`, `deleteEvent`.

## D: Dependency Inversion

Phần nghiệp vụ phụ thuộc vào **abstraction**, không phụ thuộc vào chi tiết cụ thể.

```java
interface PaymentGateway {
    PaymentResult charge(long bookingId, long amount);
}

@Service
class PaymentService {
    private final PaymentGateway gateway;   // không biết là cổng nào

    PaymentService(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

Khi test, đưa vào một cổng giả. Khi đổi nhà cung cấp, `PaymentService` không phải sửa.

## Đừng làm quá

- Không tạo interface cho mọi lớp khi chỉ có một cách làm và test vẫn dễ.
- Ba lớp để làm việc mà một hàm 10 dòng làm được là khó đọc hơn, không phải sạch hơn.
- Chỉ tách khi thấy đau: lớp quá dài, khó test, sửa một chỗ hỏng chỗ khác.

## Tự kiểm tra

1. `BookingService` vừa đặt vé vừa tự soạn nội dung email. Vi phạm gì, tách thế nào?
2. Abstraction giúp việc test dễ hơn ra sao?
3. Khi nào một interface là thừa?
