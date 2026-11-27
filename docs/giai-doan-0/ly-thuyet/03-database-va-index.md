# Database: thiết kế bảng và index

## Khái niệm cơ bản

| Khái niệm | Nghĩa |
|---|---|
| Primary key | Định danh duy nhất một dòng |
| Foreign key | Trỏ sang dòng ở bảng khác, database đảm bảo dòng đó tồn tại |
| Unique | Không cho trùng |
| Not null, check | Bắt buộc có giá trị, giá trị phải thoả điều kiện |

Ràng buộc đặt ở database là chốt chặn cuối: code có lỗi hay nhiều request chen nhau thì dữ liệu sai vẫn không vào được.

## Quan hệ

- **Một–nhiều:** một sự kiện có nhiều ghế. Khoá ngoại nằm ở phía "nhiều" (`seats.event_id`).
- **Nhiều–nhiều:** cần bảng trung gian. Một ghế có thể nằm trong nhiều đơn theo thời gian (đơn cũ đã huỷ), một đơn có nhiều ghế, nên có `booking_items`.

## Chuẩn hoá

Mục tiêu: mỗi sự thật chỉ lưu một chỗ.

Ví dụ xấu: lưu tên sự kiện vào từng ghế. Đổi tên phải sửa hàng trăm dòng, sót một dòng là dữ liệu mâu thuẫn. Cách đúng là ghế chỉ lưu `event_id`.

Đôi khi cố ý lưu lặp:

- `booking_items.price` chép giá lúc mua. Đây không phải lặp thừa, vì "giá đã trả" khác "giá hiện tại".
- **Phi chuẩn hoá** để đọc nhanh, ví dụ lưu sẵn số ghế còn trống. Đổi lại phải tự giữ cho nó luôn đúng.

## JOIN

Ghép dữ liệu từ nhiều bảng:

```sql
SELECT b.id, e.name, b.total_amount
FROM bookings b
JOIN events e ON e.id = b.event_id
WHERE b.user_id = 1;
```

`JOIN` chỉ giữ dòng khớp ở cả hai bên. `LEFT JOIN` giữ mọi dòng bên trái, bên phải không khớp thì là `NULL`.

## Index

Không có index, database phải đọc cả bảng (**sequential scan**). Index giống mục lục cuối sách: cấu trúc được sắp xếp sẵn (thường là B-tree) để nhảy thẳng tới dòng cần tìm.

**Index nhiều cột** xếp theo cột đầu trước, rồi đến cột sau. Với index `(user_id, created_at)`:

| Điều kiện | Dùng index hiệu quả |
|---|---|
| `user_id = 1` | Có |
| `user_id = 1 ORDER BY created_at DESC` | Có, khỏi phải sắp xếp lại |
| `created_at > ...` | Thường là không |

**Partial index** chỉ đánh index một phần bảng, ví dụ chỉ sự kiện `PUBLISHED`.

**Cái giá:** mỗi lần thêm, sửa, xoá có thể phải cập nhật index, và index tốn dung lượng.

**Database không phải lúc nào cũng dùng index.** Bảng rất nhỏ, hoặc điều kiện khớp phần lớn bảng, thì đọc tuần tự rẻ hơn.

## Xem database chạy truy vấn thế nào

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM bookings WHERE user_id = 1;
```

`EXPLAIN ANALYZE` chạy truy vấn thật, cẩn thận khi dùng với `UPDATE` hoặc `DELETE`.

Nhìn vào: kiểu scan (`Seq Scan`, `Index Scan`, `Bitmap Heap Scan`), số dòng ước lượng so với thực tế, `Buffers` (số trang đã đọc), thời gian.

## Lỗi N+1

Lấy 20 đơn bằng 1 truy vấn, rồi mỗi đơn lại truy vấn riêng để lấy ghế, thành 21 truy vấn. Rất hay gặp khi dùng JPA. Phát hiện bằng cách bật log SQL.

## Tự kiểm tra

1. Vì sao không đánh index mọi cột?
2. Vì sao `booking_items.price` không vi phạm tinh thần chuẩn hoá?
3. Index `(event_id, label)` có giúp truy vấn `WHERE event_id = 5` không? Còn `WHERE label = 'A1'`?
