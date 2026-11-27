# Giai đoạn 0: làm từ đâu

Làm lần lượt từ trên xuống. Mỗi bước: đọc, tự trả lời phần "Tự kiểm tra" (nếu có), rồi làm.

| Bước | Đọc | Làm | Xong khi |
|---|---|---|---|
| 1 | [System design là gì](ly-thuyet/01-system-design-la-gi.md), [lộ trình](../lo-trinh.md) | Cài JDK 21 | `java -version` hiện 21 |
| 2 | [Yêu cầu](yeu-cau.md) | Tạo project trong `ticketing/` | Project build được |
| 3 | [Database và index](ly-thuyet/03-database-va-index.md), [thiết kế database](database.md) | Viết `docker-compose.yml` và migration đầu tiên | Ứng dụng khởi động, database có đủ 5 bảng |
| 4 | [Mạng và HTTP](ly-thuyet/02-mang-va-http.md), [API và bảo mật](ly-thuyet/04-api-va-bao-mat.md), [SOLID](ly-thuyet/07-solid.md), [design pattern](ly-thuyet/08-design-pattern.md) (đến hết phần DTO) | Đăng ký, đăng nhập, seed ADMIN | Gọi API kèm JWT được, sai quyền bị từ chối |
| 5 | [Yêu cầu](yeu-cau.md) | API sự kiện và ghế | Admin tạo và mở bán được, khách xem được |
| 6 | [Concurrency và I/O](ly-thuyet/05-concurrency-va-io.md), [Transaction](ly-thuyet/06-transaction.md), [Proxy và Strategy](ly-thuyet/08-design-pattern.md#proxy) | Đặt ghế theo cách ngây thơ, đúng như Bài 1 bước 1 | Một người đặt được ghế |
| 7 | [Bài thực hành](bai-thuc-hanh.md), Bài 1 | Cài k6, tạo lỗi, thử từng cách sửa, viết ADR | Có bảng kết quả và một ADR |
| 8 | Bài 1 bước 5, Bài 2 | Huỷ đơn, huỷ sự kiện | Các test đồng thời đều qua |
| 9 | Bài 3 | Đo index | Giải thích được kết quả đo |

Cuối cùng, đối chiếu với [tiêu chí hoàn thành](yeu-cau.md#hoàn-thành-khi).

## Lưu ý

- Ở bước 3, **chưa tạo** unique index và ba index truy vấn. Chúng dành cho Bài 1 và 3.
- Bước 2 chọn trên [start.spring.io](https://start.spring.io): Maven, Java 21, Web, Data JPA, PostgreSQL Driver, Flyway, Validation, Security.
- Spring Security mặc định chặn mọi request. Gặp 401 trước bước 4 là bình thường.
- SOLID và design pattern: viết cho chạy đúng, có test, rồi mới refactor.
- Java cơ bản (OOP, collections, exception) không nằm trong bộ lý thuyết này.
