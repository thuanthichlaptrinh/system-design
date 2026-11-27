# Kiến trúc code

Project Spring Boot chuẩn, chia tầng theo clean architecture.

```
com.example.ticketing
├── TicketingApplication.java
├── domain/              # nghiệp vụ, Java thuần
│   ├── model/
│   └── exception/
├── application/         # use case
│   ├── usecase/
│   ├── port/            # interface mà use case cần
│   └── dto/
├── infrastructure/      # chi tiết kỹ thuật
│   ├── persistence/     # entity JPA, repository, hiện thực port
│   ├── security/
│   └── config/
└── presentation/        # REST API
    ├── controller/
    ├── dto/
    └── exception/
```

## Chiều phụ thuộc

```
presentation ──► application ──► domain
infrastructure ──► application ──► domain
```

- `domain` không phụ thuộc Spring, JPA hay tầng khác.
- `application` chỉ dùng `domain` và port của mình, không biết JPA hay HTTP. Được dùng `@Service` và `@Transactional`.
- `presentation` và `infrastructure` không gọi nhau.

Các quy tắc này được kiểm tra bằng `ArchitectureTest`.

Ví dụ đặt ghế: `BookingController` gọi `BookSeatsUseCase`, use case dùng port `SeatRepository`, còn `infrastructure/persistence` hiện thực port đó.
