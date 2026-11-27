# Học System Design qua dự án đặt vé sự kiện

Một dự án duy nhất, nâng cấp dần qua các giai đoạn.

**Mới vào? Mở [hướng dẫn giai đoạn 0](docs/giai-doan-0/README.md) và làm từ bước 1.**

**Stack:** Java 21, Spring Boot 4, PostgreSQL, Docker Compose, k6. Redis và Kafka thêm ở các giai đoạn sau.

## Chạy dự án

Chỉ cần Docker.

```bash
make init    # build và khởi động app (8081) và PostgreSQL (5433)
make test    # chạy test
make help    # các lệnh khác
```

Kiểm tra: `curl http://localhost:8081/actuator/health`

## Cấu trúc

```
system-design/
├── src/                     # mã nguồn Spring Boot
├── docker/                  # Dockerfile, script khởi tạo database
├── compose.yml
├── Makefile
├── pom.xml
├── load-test/               # script k6 (tạo khi làm Bài 1)
└── docs/
    ├── lo-trinh.md          # toàn bộ 4 giai đoạn
    ├── kien-truc.md         # cách tổ chức code
    ├── giai-doan-0/         # hướng dẫn, lý thuyết, yêu cầu, database, bài thực hành
    └── adr/                 # ghi lại quyết định
```

Tài liệu của giai đoạn 1–3 viết khi đến giai đoạn đó.
