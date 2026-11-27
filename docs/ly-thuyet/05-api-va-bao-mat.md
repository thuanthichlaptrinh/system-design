# REST API và bảo mật

## REST

Coi hệ thống là các **tài nguyên** (sự kiện, ghế, đơn), thao tác bằng method của HTTP.

- Đường dẫn dùng danh từ: `/api/bookings`, không phải `/api/createBooking`.
- Trả đúng mã trạng thái, không trả 200 kèm thông báo lỗi.
- Lỗi có định dạng thống nhất. Spring hỗ trợ `ProblemDetail`.
- Không trả thẳng entity của database, dùng DTO để không lộ trường nội bộ như `password_hash`.

## Phân trang

Không bao giờ trả toàn bộ bảng.

| Cách | Ví dụ | Được | Mất |
|---|---|---|---|
| Offset | `?page=100&size=20` | Đơn giản, nhảy tới trang bất kỳ | Trang càng sâu càng chậm; dữ liệu mới chen vào có thể làm lặp hoặc sót |
| Keyset (cursor) | `?after=<mốc cuối>&size=20` | Nhanh ổn định khi có index | Không nhảy trang tuỳ ý |

Dự án dùng offset cho đơn giản.

## Authentication và authorization

- **Authentication:** bạn là ai. Sai thì 401.
- **Authorization:** bạn được làm gì. Không đủ quyền thì 403.

Kiểm tra vai trò thôi chưa đủ. Hai user đều có vai trò `USER`, nhưng A không được huỷ đơn của B. Phải kiểm tra **quyền sở hữu**. Quên bước này là một trong những lỗ hổng phổ biến nhất.

## Lưu mật khẩu

Không lưu mật khẩu gốc, không mã hoá hai chiều. Lưu **bản băm** bằng thuật toán cố ý chậm như BCrypt. BCrypt tự thêm salt, nên hai người cùng mật khẩu có bản băm khác nhau.

## Session và JWT

| | Session | JWT |
|---|---|---|
| Server lưu gì | Thông tin phiên | Không cần lưu |
| Client giữ gì | Mã phiên | Token chứa thông tin, có chữ ký |
| Nhiều instance | Cần kho phiên dùng chung | Instance nào cũng kiểm tra được |
| Thu hồi sớm | Dễ | Khó, thường đặt hạn ngắn |

JWT gồm `header.payload.signature`. Payload **chỉ được ký, không được mã hoá**: ai cầm token cũng đọc được, nên không để dữ liệu nhạy cảm trong đó. Server phải kiểm tra chữ ký và hạn.

Gửi kèm request:

```
Authorization: Bearer <token>
```

## Vài nguyên tắc an toàn

- Không tin dữ liệu từ client: kiểm tra mọi đầu vào, giá tiền lấy từ database chứ không lấy từ request.
- Dùng tham số hoá truy vấn, không nối chuỗi SQL, để tránh SQL injection.
- Secret (khoá ký JWT, mật khẩu database) không đưa vào git.
- Không log mật khẩu hay token.

## Tự kiểm tra

1. Vì sao BCrypt cố ý chậm?
2. User bị khoá tài khoản nhưng JWT còn hạn thì sao?
3. Request đặt ghế gửi kèm `totalAmount`. Server nên làm gì với nó?
