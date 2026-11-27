# Mạng và HTTP

## Chuyện gì xảy ra khi gọi `https://ticket.example/api/events`

1. **DNS:** đổi tên miền thành địa chỉ IP.
2. **TCP:** mở kết nối tới IP đó, cổng 443.
3. **TLS:** xác minh máy chủ và thoả thuận khoá mã hoá.
4. **HTTP:** gửi request, nhận response.
5. Máy chủ xử lý: ứng dụng truy vấn database rồi trả JSON.

Bước 1–3 tốn thời gian đi về qua mạng, nên người ta giữ lại kết nối để dùng cho nhiều request (keep-alive).

## DNS

Giống danh bạ. Trình duyệt hỏi lần lượt bộ nhớ đệm của nó, của hệ điều hành, rồi máy chủ DNS. Kết quả được nhớ trong một khoảng thời gian gọi là **TTL**, nên đổi IP không có hiệu lực ngay.

## TCP và UDP

| | TCP | UDP |
|---|---|---|
| Kết nối | Bắt tay 3 bước trước khi gửi | Gửi luôn |
| Đảm bảo | Đủ, đúng thứ tự, gửi lại nếu mất | Không đảm bảo |
| Dùng cho | Web, database | Gọi video, game, DNS |

## TLS và HTTPS

HTTPS là HTTP chạy trên kênh đã mã hoá bằng TLS. Nó cho ba thứ: người ngoài không đọc được, không sửa được mà không bị phát hiện, và bạn biết mình đang nói chuyện với đúng máy chủ (nhờ chứng chỉ).

## HTTP

Một request gồm method, đường dẫn, header, body. Response gồm mã trạng thái, header, body.

| Method | Dùng để | Idempotent |
|---|---|---|
| GET | Đọc | Có |
| POST | Tạo mới, hành động | Không |
| PUT | Thay thế | Có |
| DELETE | Xoá, huỷ | Có |

**Idempotent:** gọi nhiều lần thì tác động lên server giống như gọi một lần. Mã trả về có thể khác nhau. Khái niệm này rất quan trọng vì mạng lỗi thì client sẽ gửi lại: gửi lại `POST /api/bookings` có thể tạo thêm đơn.

| Nhóm mã | Nghĩa |
|---|---|
| 2xx | Thành công (200 OK, 201 Created) |
| 3xx | Chuyển hướng |
| 4xx | Lỗi do phía gọi (400, 401, 403, 404, 409, 422) |
| 5xx | Lỗi do máy chủ (500, 503) |

HTTP **không nhớ trạng thái**: mỗi request độc lập, nên request nào cũng phải tự mang thông tin đăng nhập.

Về phiên bản: HTTP/2 cho nhiều request chạy song song trên một kết nối. HTTP/3 chạy trên QUIC, xây trên UDP.

## WebSocket

HTTP thường là client hỏi, server trả lời. WebSocket mở một kết nối hai chiều giữ lâu, server tự đẩy dữ liệu được, ví dụ cập nhật sơ đồ ghế theo thời gian thực. Đổi lại server phải giữ rất nhiều kết nối đang mở.

## Tự kiểm tra

1. Kể lại các bước từ lúc gõ URL đến lúc thấy kết quả.
2. 401 khác 403 thế nào?
3. Client gửi đặt ghế nhưng mất mạng trước khi nhận phản hồi. Client có biết đơn đã tạo chưa? Gửi lại thì sao?

## Thử ngay

```bash
curl -v https://example.com
```

Tìm trong kết quả các dòng cho thấy kết nối TCP, bắt tay TLS, header request và response.
