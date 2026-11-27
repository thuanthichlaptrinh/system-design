# Yêu cầu: giai đoạn 0

## Mục tiêu

Một monolith cho phép xem sự kiện và đặt ghế, **không bao giờ bán một ghế cho hai người**.

## Vai trò

| Vai trò | Quyền |
|---|---|
| Khách | Xem sự kiện và sơ đồ ghế |
| USER | Đặt ghế, xem và huỷ đơn của mình |
| ADMIN | Tạo, sửa, mở bán sự kiện và tạo ghế |

## Quy tắc nghiệp vụ

1. Email không trùng giữa các tài khoản, không phân biệt hoa thường. Email được chuyển về chữ thường khi đăng ký và đăng nhập.
2. Sự kiện có trạng thái `DRAFT`, `PUBLISHED`, `CANCELLED`. Chỉ `PUBLISHED` mới hiển thị và đặt được; với người dùng, sự kiện ở trạng thái khác coi như không tồn tại.
3. Không đặt được sự kiện đã bắt đầu.
4. Mỗi đơn gồm 1–4 ghế khác nhau của cùng một sự kiện.
5. Đặt nhiều ghế là tất cả hoặc không: một ghế đã bán thì cả đơn thất bại.
6. Giá được chép vào đơn lúc đặt. Đổi giá ghế sau đó không ảnh hưởng đơn cũ.
7. Chỉ huỷ được đơn của mình, trước giờ sự kiện bắt đầu. Huỷ xong ghế được bán lại.
8. Chỉ huỷ được đơn đang `CONFIRMED`. Việc kiểm tra và đổi trạng thái phải nguyên tử: huỷ lặp lại hoặc hai lệnh huỷ đồng thời không được nhả ghế lần thứ hai.
9. Sự kiện chuyển sang `CANCELLED` thì các đơn đã đặt được giữ nguyên. Hoàn tiền làm ở giai đoạn Senior.
10. Đăng ký luôn tạo tài khoản `USER`. Tài khoản `ADMIN` được seed bằng Flyway, chỉ dùng cho môi trường phát triển.
11. Trạng thái sự kiện chỉ chuyển `DRAFT → PUBLISHED`, `DRAFT → CANCELLED` hoặc `PUBLISHED → CANCELLED`.
12. Sau khi lệnh huỷ sự kiện hoàn tất, không đơn mới nào của sự kiện đó được tạo, kể cả request đang xử lý dở.
13. `size` khi phân trang tối đa là 100.

Giai đoạn này đặt là thành công ngay. Giữ ghế và thanh toán để dành cho giai đoạn Middle.

## API

| Method | Đường dẫn | Quyền | Mô tả |
|---|---|---|---|
| POST | `/api/auth/register` | Khách | Đăng ký |
| POST | `/api/auth/login` | Khách | Đăng nhập, trả JWT |
| GET | `/api/events?page=0&size=20` | Khách | Sự kiện sắp diễn ra |
| GET | `/api/events/{id}` | Khách | Chi tiết sự kiện |
| GET | `/api/events/{id}/seats` | Khách | Ghế và trạng thái |
| POST | `/api/admin/events` | ADMIN | Tạo sự kiện |
| PUT | `/api/admin/events/{id}` | ADMIN | Sửa, đổi trạng thái |
| POST | `/api/admin/events/{id}/seats` | ADMIN | Tạo ghế hàng loạt |
| PUT | `/api/admin/events/{id}/seats/{seatId}` | ADMIN | Đổi giá ghế |
| POST | `/api/bookings` | USER | Đặt ghế |
| GET | `/api/bookings` | USER | Đơn của tôi |
| DELETE | `/api/bookings/{id}` | USER | Huỷ đơn |

Ví dụ đặt ghế:

```json
POST /api/bookings
{ "eventId": 1, "seatIds": [10, 11] }
```

```json
201 Created
{ "id": 55, "status": "CONFIRMED", "totalAmount": 1000000, "seatIds": [10, 11] }
```

## Mã lỗi

| Mã | Khi nào |
|---|---|
| 400 | Dữ liệu sai, quá 4 ghế, `seatIds` bị trùng, ghế không tồn tại hoặc không thuộc sự kiện, giá âm, `size` quá 100 |
| 401 | Chưa đăng nhập, sai email hoặc mật khẩu, token sai hoặc hết hạn |
| 403 | Không đủ quyền |
| 404 | Không tìm thấy, kể cả đơn của người khác và sự kiện chưa mở bán hoặc đã huỷ |
| 409 | Email đã tồn tại, ghế đã bán, đơn đã huỷ trước đó, trùng `label` ghế, chuyển trạng thái sự kiện không hợp lệ |
| 422 | Đặt hoặc huỷ khi sự kiện đã bắt đầu |

## Yêu cầu phi chức năng

Đây là mục tiêu đặt ra để luyện đo đạc, đo trên máy cá nhân.

- 100 request đồng thời giành một ghế: đúng 1 thành công, 99 nhận 409.
- Hai lệnh huỷ đồng thời cho một đơn: đúng 1 thành công, 1 nhận 409.
- Hai request đăng ký cùng một email đồng thời: đúng 1 thành công, 1 nhận 409, không có lỗi 500.
- A đặt `[10, 11]` và B đặt `[11, 12]` đồng thời: đúng 1 đơn thành công, ghế chỉ thuộc đơn thua vẫn `AVAILABLE`.
- A huỷ đơn có ghế 10 trong lúc B đặt ghế 10: B thành công hoặc nhận 409, trạng thái ghế luôn khớp với đơn.
- API đọc: p95 dưới 200 ms ở 100 request/giây với dữ liệu mẫu.
- Mật khẩu lưu bằng BCrypt. Không log mật khẩu hay token.

## Cấu trúc code

Theo [kiến trúc code](../kien-truc.md): clean architecture gồm `domain`, `application`, `infrastructure`, `presentation`.

## Hoàn thành khi

- [ ] Các API trên chạy được với PostgreSQL qua Docker Compose
- [ ] Schema được quản lý bằng Flyway
- [ ] Có test cho các quy tắc đặt và huỷ ghế, gồm cả các trường hợp đồng thời ở trên
- [ ] Làm xong [bài thực hành](bai-thuc-hanh.md)
