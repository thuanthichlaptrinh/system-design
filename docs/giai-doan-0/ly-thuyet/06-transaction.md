# Transaction, isolation và lock

Đây là phần quan trọng nhất của giai đoạn 0. Đọc trước khi làm [Bài 1](../03-bai-thuc-hanh.md).

## Transaction

Một nhóm thao tác được coi là một đơn vị.

```sql
BEGIN;
INSERT INTO bookings ...;
INSERT INTO booking_items ...;
UPDATE seats SET status = 'SOLD' WHERE id = 10;
COMMIT;   -- hoặc ROLLBACK để bỏ hết
```

Trong Spring, `@Transactional` mở transaction khi vào phương thức và commit khi ra. Mặc định nó chỉ rollback với `RuntimeException` và `Error`, không rollback với checked exception.

## ACID

| Chữ | Nghĩa | Ví dụ |
|---|---|---|
| Atomicity | Tất cả hoặc không | Không có chuyện tạo đơn rồi mà ghế chưa đổi |
| Consistency | Ràng buộc luôn được giữ | Giá không bao giờ âm |
| Isolation | Các transaction chạy đồng thời ảnh hưởng nhau đến đâu | Xem bên dưới |
| Durability | Đã commit thì không mất | Mất điện, đơn vẫn còn |

## Các hiện tượng khi chạy đồng thời

| Hiện tượng | Mô tả |
|---|---|
| Dirty read | Đọc dữ liệu người khác chưa commit |
| Non-repeatable read | Đọc một dòng hai lần, ra hai giá trị |
| Phantom read | Chạy một truy vấn hai lần, số dòng khác nhau |
| Lost update | Hai bên cùng đọc rồi cùng ghi, một thay đổi bị đè mất |
| Write skew | Hai bên cùng kiểm tra một điều kiện rồi ghi vào các dòng khác nhau, kết hợp lại làm điều kiện sai |

## Mức cô lập trong PostgreSQL

| Mức | Cách hoạt động |
|---|---|
| Read Committed (mặc định) | Mỗi câu lệnh thấy dữ liệu đã commit tại lúc câu lệnh đó bắt đầu |
| Repeatable Read | Cả transaction nhìn một ảnh chụp dữ liệu; ghi đè lên dòng người khác vừa sửa thì bị lỗi |
| Serializable | Kết quả như thể các transaction chạy lần lượt; có xung đột thì một bên bị lỗi |

Ở hai mức sau, ứng dụng phải sẵn sàng **chạy lại** transaction bị lỗi. PostgreSQL chấp nhận `READ UNCOMMITTED` nhưng xử lý như Read Committed.

Mức cao hơn an toàn hơn, đổi lại nhiều lỗi phải chạy lại hơn.

## MVCC

PostgreSQL giữ nhiều phiên bản của một dòng. Vì vậy người đọc thường không chặn người ghi và ngược lại. Hệ quả: **đọc được một giá trị không có nghĩa là giá trị đó còn đúng khi bạn ghi.** `SELECT` bình thường không khoá gì cả.

## Lock

Hai người ghi cùng một dòng thì người sau phải chờ người trước commit hoặc rollback. Muốn khoá ngay từ lúc đọc:

| Lệnh | Tác dụng |
|---|---|
| `SELECT ... FOR UPDATE` | Khoá để sửa; người khác muốn khoá hoặc sửa dòng này phải chờ |
| `SELECT ... FOR SHARE` | Nhiều người cùng giữ được; chặn người muốn sửa |

Khoá được giữ đến hết transaction. Transaction càng dài, người khác chờ càng lâu.

Khi hai transaction chờ khoá của nhau (deadlock), PostgreSQL phát hiện và huỷ một bên.

## Ba hướng chống tranh chấp

| Cách | Ý tưởng | Hợp khi |
|---|---|---|
| Pessimistic lock | Khoá trước rồi mới làm | Tranh chấp nhiều |
| Optimistic lock | Cứ làm, lúc ghi kiểm tra `version` có đổi không | Tranh chấp ít |
| Ghi có điều kiện, ràng buộc | Để database từ chối trong một thao tác nguyên tử | Điều kiện đơn giản |

Ví dụ optimistic lock:

```sql
UPDATE products SET stock = 4, version = 8
WHERE id = 1 AND version = 7;
-- 0 dòng bị ảnh hưởng: có người đã sửa trước
```

## Thử ngay

Mở hai cửa sổ `psql` (A và B), tạo một bảng nháp:

```sql
CREATE TABLE demo (id INT PRIMARY KEY, qty INT);
INSERT INTO demo VALUES (1, 10);
```

1. A: `BEGIN; UPDATE demo SET qty = 5 WHERE id = 1;`
2. B: `SELECT qty FROM demo WHERE id = 1;` Thấy số mấy?
3. B: `UPDATE demo SET qty = qty - 1 WHERE id = 1;` Chuyện gì xảy ra?
4. A: `COMMIT;` Rồi xem lại ở B.

Xong thì `DROP TABLE demo;`.

## Tự kiểm tra

1. `@Transactional` đảm bảo điều gì, không đảm bảo điều gì?
2. Vì sao Read Committed vẫn có thể bị lost update?
3. Khi nào optimistic lock tệ hơn pessimistic lock?
