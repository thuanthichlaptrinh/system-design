# Học System Design qua dự án đặt vé sự kiện

Một dự án duy nhất, nâng cấp dần qua các giai đoạn.

**Mới vào? Mở [hướng dẫn giai đoạn 0](docs/giai-doan-0/README.md) và làm từ bước 1.**

**Stack:** Java 21, Spring Boot 3+, PostgreSQL, Docker Compose, k6. Redis và Kafka thêm ở các giai đoạn sau.

## Cấu trúc

```
system-design/
├── README.md
├── docs/
│   ├── lo-trinh.md          # toàn bộ 4 giai đoạn
│   ├── giai-doan-0/
│   │   ├── README.md        # làm gì, theo thứ tự nào
│   │   ├── ly-thuyet/       # 8 bài, đọc theo hướng dẫn giai đoạn 0
│   │   ├── yeu-cau.md       # quy tắc nghiệp vụ và API
│   │   ├── database.md      # schema PostgreSQL
│   │   └── bai-thuc-hanh.md # thí nghiệm
│   └── adr/                 # ghi lại quyết định
├── ticketing/               # mã nguồn (tạo ở bước 2)
├── load-test/               # script k6 (tạo khi làm Bài 1)
└── docker-compose.yml       # tạo ở bước 3
```

Tài liệu của giai đoạn 1–3 viết khi đến giai đoạn đó.
