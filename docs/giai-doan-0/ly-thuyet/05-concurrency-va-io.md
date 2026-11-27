# Concurrency và I/O

## Process và thread

- **Process:** một chương trình đang chạy, có bộ nhớ riêng. Ứng dụng Spring Boot là một process.
- **Thread:** một luồng thực thi trong process. Các thread **dùng chung bộ nhớ**.

Máy chủ web thường dùng mỗi thread xử lý một request. Spring Boot với Tomcat mặc định có tối đa 200 thread, nên nhiều request chạy cùng lúc trong cùng một ứng dụng.

## Concurrency và parallelism

- **Concurrency:** nhiều việc cùng đang dở dang, xen kẽ nhau.
- **Parallelism:** nhiều việc chạy đúng cùng một thời điểm trên nhiều CPU.

## Race condition

Xảy ra khi kết quả phụ thuộc vào thứ tự chạy ngẫu nhiên của các thread. Mẫu hay gặp nhất là **đọc, kiểm tra, rồi ghi**:

```java
int stock = readStock();      // thread A và B cùng đọc được 1
if (stock > 0) {              // cả hai đều thấy còn hàng
    writeStock(stock - 1);    // cả hai đều bán
}
```

Giữa lúc đọc và lúc ghi, người khác có thể chen vào. Cách chống:

- Gộp kiểm tra và ghi thành một thao tác không chia cắt được (atomic).
- Khoá, để mỗi lúc chỉ một bên được làm.
- Ghi lạc quan: lúc ghi mới kiểm tra xem dữ liệu có bị đổi chưa.

`synchronized` trong Java chỉ khoá được **trong một process**. Khi chạy nhiều instance, khoá này không còn tác dụng giữa các instance, nên thường phải dựa vào database.

## Deadlock

A giữ khoá 1 và chờ khoá 2. B giữ khoá 2 và chờ khoá 1. Không ai đi tiếp được.

## Blocking và non-blocking I/O

I/O là đọc ghi mạng, đĩa, database. Nó chậm hơn CPU rất nhiều.

- **Blocking:** thread đứng chờ đến khi có kết quả. Code dễ viết, nhưng 200 thread cùng chờ thì request thứ 201 phải xếp hàng.
- **Non-blocking:** gửi yêu cầu rồi đi làm việc khác, có kết quả thì xử lý tiếp. Ít thread phục vụ được nhiều kết nối, code khó hơn.
- **Virtual thread (Java 21):** thread rất nhẹ. Viết code kiểu blocking nhưng khi chờ I/O không chiếm thread của hệ điều hành. Spring Boot không bật mặc định.

## Connection pool

Mở kết nối database tốn kém, nên ứng dụng giữ sẵn một số kết nối để dùng lại. Spring Boot mặc định dùng HikariCP với 10 kết nối. Hết kết nối thì request phải chờ, kể cả khi còn thread rảnh.

## Việc nặng CPU và việc nặng I/O

| Loại | Ví dụ | Thêm thread có giúp không |
|---|---|---|
| Nặng CPU | Băm mật khẩu, xử lý ảnh | Ít, bị giới hạn bởi số CPU |
| Nặng I/O | Truy vấn database, gọi API | Có, vì phần lớn thời gian là chờ |

## Tự kiểm tra

1. Vì sao hai thread cộng cùng một biến đếm có thể ra kết quả sai?
2. 200 thread, 10 kết nối database, mỗi truy vấn mất 100 ms. Tối đa khoảng bao nhiêu truy vấn mỗi giây? (Đáp án: khoảng 100.)
3. Làm sao tránh deadlock khi cần khoá nhiều thứ?
