# Bài thực hành

## Bài 1: Bán trùng ghế

### Bước 1: tạo lỗi

Viết `book()` theo cách ngây thơ, trong một `@Transactional`:

1. Đọc ghế.
2. Nếu `AVAILABLE` thì đi tiếp.
3. Tạo `booking` và `booking_items`.
4. Cập nhật ghế thành `SOLD`.

Ở bước này, **chưa dùng `@Version`** và chưa tạo unique index. Có thể thêm `Thread.sleep(100)` sau bước 2 để lỗi dễ xuất hiện.

Dùng k6 cho 100 user ảo cùng đặt một ghế, rồi kiểm tra:

```sql
SELECT seat_id, count(*)
FROM booking_items
WHERE cancelled_at IS NULL
GROUP BY seat_id
HAVING count(*) > 1;
```

**Câu hỏi:** đã có `@Transactional`, sao vẫn bán trùng?

### Bước 2: sửa bằng từng cách

Mỗi cách làm trên một branch, reset dữ liệu trước khi đo.

| Cách | Gợi ý |
|---|---|
| Pessimistic lock | `SELECT ... FOR UPDATE` |
| Optimistic lock | `@Version`, đổi lỗi xung đột version thành 409 |
| Update có điều kiện | `UPDATE seats SET status='SOLD' WHERE id=? AND status='AVAILABLE'`, kiểm tra số dòng bị ảnh hưởng |
| Unique index | Index trong tài liệu database, đổi lỗi vi phạm thành 409 |

Với mọi cách, một ghế thất bại thì cả đơn phải rollback.

Cách dùng Redis lock làm ở giai đoạn Junior.

### Bước 3: ghi kết quả

| Cách | Số đơn thành công | p95 | Số lỗi 500 | Nhận xét |
|---|---|---|---|---|
| Ngây thơ | | | | |
| Pessimistic | | | | |
| Optimistic | | | | |
| Update có điều kiện | | | | |
| Unique index | | | | |

Ngoài việc bán trùng, kiểm tra thêm trạng thái ghế có khớp với đơn không. Truy vấn sau phải trả về rỗng:

```sql
SELECT s.id, s.status
FROM seats s
LEFT JOIN booking_items bi ON bi.seat_id = s.id AND bi.cancelled_at IS NULL
WHERE (s.status = 'SOLD' AND bi.id IS NULL)
   OR (s.status = 'AVAILABLE' AND bi.id IS NOT NULL);
```

### Bước 4: tạo deadlock

Request A đặt `[10, 11]`, request B đặt `[11, 10]`, gửi cùng lúc. Với pessimistic lock, khoá **từng ghế một theo đúng thứ tự request gửi lên** và chèn `Thread.sleep(100)` giữa hai lần khoá. Nếu khoá bằng một câu `WHERE id IN (...) FOR UPDATE`, thứ tự khoá do database quyết định nên có thể không thấy lỗi.

**Câu hỏi:** điều gì xảy ra? Khoá theo `id` tăng dần có giải quyết được không?

### Bước 5: huỷ đơn hai lần

Viết `cancel()` ngây thơ: đọc đơn, không kiểm tra trạng thái, đánh dấu huỷ và nhả ghế. Sau đó:

1. User A đặt ghế 10 rồi huỷ.
2. User B đặt ghế 10.
3. User A gọi huỷ đơn cũ lần nữa.

**Câu hỏi:** ghế 10 đang ở trạng thái nào? Sửa thế nào để đúng cả khi hai lệnh huỷ chạy đồng thời?

### Bước 6: viết ADR

Viết ADR đầu tiên: chọn cách chống bán trùng nào và vì sao.

## Bài 2: Huỷ sự kiện khi đang có người đặt

1. Trong `book()`, chèn `Thread.sleep(2000)` sau bước kiểm tra sự kiện đang `PUBLISHED`.
2. Gửi request đặt ghế, trong lúc đó admin chuyển sự kiện sang `CANCELLED`.
3. Kiểm tra xem đơn có được tạo sau khi sự kiện đã huỷ không.

**Câu hỏi:**

- Vì sao khoá trên bảng `seats` không ngăn được lỗi này?
- Thử `SELECT ... FOR SHARE` trên dòng `events` khi đặt ghế. Lệnh huỷ sự kiện lúc này hành xử thế nào?
- Nếu dùng `FOR UPDATE` thay cho `FOR SHARE`, các đơn của cùng một sự kiện bị ảnh hưởng ra sao?

## Bài 3: Đo hiệu quả của index

1. Sinh dữ liệu mẫu: 10.000 user, 1.000 sự kiện, mỗi sự kiện 500 ghế, 200.000 đơn, mỗi đơn ít nhất một dòng `booking_items`. Dùng `generate_series`.
2. Chạy `ANALYZE;`.
3. Chạy `EXPLAIN (ANALYZE, BUFFERS)` cho ba truy vấn: đơn của một user, các ghế trong một đơn, sự kiện sắp diễn ra.
4. Tạo index, chạy lại.
5. So sánh kiểu scan, số buffer, thời gian.

**Câu hỏi:**

- Truy vấn nào cải thiện nhiều nhất? Truy vấn nào gần như không đổi, vì sao?
- Index làm chậm thao tác nào?
