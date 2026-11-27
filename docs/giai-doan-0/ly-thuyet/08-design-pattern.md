# Design pattern

Pattern là cách giải đã được đặt tên cho một vấn đề hay lặp lại. Hiểu **vấn đề** trước, rồi mới đến cách giải.

## Dependency Injection

**Vấn đề:** lớp tự `new` thứ nó cần thì khó thay và khó test.

**Cách giải:** thứ cần dùng được đưa từ ngoài vào. Spring tạo các đối tượng (bean) và truyền qua constructor.

```java
@Service
class BookingService {
    private final BookingRepository bookings;

    BookingService(BookingRepository bookings) {
        this.bookings = bookings;
    }
}
```

DI là kỹ thuật. Dependency Inversion trong [SOLID](07-solid.md) là nguyên tắc. DI giúp thực hiện nguyên tắc đó.

## Repository

**Vấn đề:** SQL nằm rải rác trong code nghiệp vụ.

**Cách giải:** gom việc đọc ghi dữ liệu vào một chỗ.

```java
interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
```

Spring Data tự sinh phần cài đặt.

## DTO

**Vấn đề:** trả thẳng entity thì lộ trường nội bộ, và đổi database làm đổi luôn API.

**Cách giải:** dùng đối tượng riêng cho request và response.

```java
record BookingRequest(Long eventId, List<Long> seatIds) { }
record BookingResponse(Long id, String status, long totalAmount, List<Long> seatIds) { }
```

## Proxy

**Vấn đề:** muốn thêm việc (mở transaction, ghi log) quanh một phương thức mà không sửa nó.

**Cách giải:** đặt một đối tượng đứng giữa. Spring đưa cho bạn proxy thay vì đối tượng thật:

```
Controller → Proxy (mở transaction) → BookingService thật → Proxy (commit hoặc rollback)
```

Hệ quả quan trọng:

```java
@Service
class BookingService {
    public void bookAll() {
        this.book();          // gọi thẳng, KHÔNG qua proxy
    }

    @Transactional
    public void book() { }    // nên annotation không có tác dụng ở đường gọi trên
}
```

Gọi trong cùng class thì không đi qua proxy. Phương thức `private` cũng không được áp dụng. Cách sửa thường dùng: đặt `@Transactional` ở phương thức được gọi từ bên ngoài, hoặc tách sang class khác.

## Strategy

**Vấn đề:** nhiều cách làm cùng một việc, chọn bằng `if-else` ngày càng dài.

**Cách giải:** mỗi cách là một lớp, chung một interface.

```java
interface SeatReservationStrategy {
    void reserve(long eventId, List<Long> seatIds);
}

@Service
@ConditionalOnProperty(name = "booking.strategy", havingValue = "pessimistic")
class PessimisticLockStrategy implements SeatReservationStrategy {
    public void reserve(long eventId, List<Long> seatIds) { }
}
```

Đổi `booking.strategy` trong cấu hình là đổi cách chạy, nơi gọi không phải sửa. Giá trị không khớp lớp nào thì ứng dụng không khởi động được.

## Khi nào không dùng

Chỉ có một cách làm và chưa thấy lý do sẽ có cách thứ hai thì viết thẳng. Thêm pattern sau cũng được, miễn là có test.

## Tự kiểm tra

1. Vì sao truyền qua constructor dễ test hơn `new` bên trong lớp?
2. `@Transactional` không chạy dù đã khai báo. Bạn kiểm tra gì đầu tiên?
3. Strategy được gì và mất gì so với `if-else`?
