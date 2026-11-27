# Học System Design qua dự án đặt vé sự kiện

Một dự án duy nhất, nâng cấp dần qua các giai đoạn.

| Tài liệu | Nội dung |
|---|---|
| [00-lo-trinh.md](00-lo-trinh.md) | Lộ trình và checklist |
| [01-yeu-cau.md](01-yeu-cau.md) | Yêu cầu nghiệp vụ và API của giai đoạn 0 |
| [02-thiet-ke-database.md](02-thiet-ke-database.md) | Schema PostgreSQL |
| [03-bai-thuc-hanh.md](03-bai-thuc-hanh.md) | Thí nghiệm tranh chấp dữ liệu và đo index |

## Lý thuyết giai đoạn 0

| Bài | Đọc trước khi |
|---|---|
| [System design là gì](ly-thuyet/00-system-design-la-gi.md) | Bắt đầu |
| [Mạng và HTTP](ly-thuyet/01-mang-va-http.md) | Viết API |
| [Database và index](ly-thuyet/03-database-va-index.md) | Viết migration; đọc lại trước Bài 3 |
| [REST API và bảo mật](ly-thuyet/05-api-va-bao-mat.md) | Làm đăng ký, đăng nhập |
| [Concurrency và I/O](ly-thuyet/02-concurrency-va-io.md) | Làm đặt ghế |
| [Transaction và lock](ly-thuyet/04-transaction.md) | Làm Bài 1 và 2 |

Không cần đọc hết một lượt, đến việc nào đọc bài đó. Java cơ bản (OOP, collections, exception) không nằm trong bộ này.

**Stack:** Java 21, Spring Boot 3+, PostgreSQL, Docker Compose, k6. Redis và Kafka thêm ở các giai đoạn sau.

**Thứ tự đọc:** bài lý thuyết đầu tiên, rồi 00 → 01 → 02. Code xong API rồi làm 03.

## Ghi lại quyết định (ADR)

Mỗi quyết định lớn tạo một file `adr/NNN-ten-quyet-dinh.md`:

```markdown
# NNN. Tên quyết định
## Bối cảnh
Vấn đề là gì, có ràng buộc nào.
## Các phương án
Mỗi phương án được gì, mất gì.
## Quyết định
Chọn gì và vì sao.
## Hệ quả
Phải chấp nhận điều gì, thành phần này chết thì sao, khi nào cần xem lại.
```
