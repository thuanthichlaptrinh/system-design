# Lộ trình

Thời gian ước tính với 8–10 giờ/tuần. Tên Junior, Middle, Senior chỉ là nhãn độ khó, học xong checklist chưa tương đương cấp bậc đó.

## Giai đoạn 0: Nền tảng (1–2 tháng)

- [ ] HTTP/HTTPS, TCP/UDP, DNS, TLS, WebSocket
- [ ] Process, thread, concurrency, blocking và non-blocking I/O
- [ ] SQL, index, transaction, ACID, isolation level
- [ ] Data modeling: bảng, quan hệ, ràng buộc, chuẩn hoá
- [ ] REST, phân trang, authentication, authorization
- [ ] Java: OOP, collections, exception, thread cơ bản

**Dự án:** đăng ký, đăng nhập, quản lý sự kiện, đặt ghế.

**Hoàn thành khi:** đạt [tiêu chí của giai đoạn 0](01-yeu-cau.md#hoàn-thành-khi).

**Đọc:** [bài lý thuyết](README.md#lý-thuyết-giai-đoạn-0), Use The Index, Luke.

## Giai đoạn 1: Junior (3–4 tháng)

- [ ] Monolith và phân tầng ứng dụng
- [ ] Ước lượng QPS, dung lượng, băng thông
- [ ] Log, metric cơ bản, load test
- [ ] Connection pool
- [ ] Scale dọc, scale ngang, stateless service
- [ ] Load balancer, reverse proxy
- [ ] Cache-aside, TTL, eviction, invalidation, cache stampede, CDN
- [ ] Object storage, SQL và NoSQL
- [ ] Rate limiting
- [ ] Bảo mật cơ bản: OWASP Top 10, quản lý secret

**Dự án:** thêm metric và đo hệ thống hiện tại trước, sau đó thêm Redis cache, chạy nhiều instance sau Nginx, upload poster, giới hạn tần suất API đăng nhập và đặt ghế. Thử thêm cách khoá ghế bằng Redis lock.

**Thí nghiệm:** tắt Redis khi đang có tải; tắt một instance giữa chừng.

**Hoàn thành khi:**

- [ ] Có bảng so sánh p95 và throughput trước và sau khi thêm cache
- [ ] Tắt một instance hoặc Redis, API đọc vẫn trả kết quả đúng
- [ ] Test 100 request giành một ghế vẫn đúng khi chạy nhiều instance

**Bài thiết kế:** URL shortener, Pastebin, rate limiter.

**Đọc:** `system-design-primer`, System Design Interview tập 1.

## Giai đoạn 2: Middle (3–4 tháng)

- [ ] Replication, sharding, consistent hashing
- [ ] CAP, PACELC, các mô hình consistency
- [ ] RabbitMQ hoặc Kafka, idempotency, retry, dead letter queue
- [ ] Outbox
- [ ] Timeout, circuit breaker, backpressure
- [ ] Distributed tracing
- [ ] CI/CD, rolling và canary deploy

**Dự án:** giữ ghế 10 phút, thanh toán giả lập, gửi vé qua email, read replica.

**Thí nghiệm:**

- Tắt consumer hoặc broker giữa chừng, xem có mất hoặc gửi trùng email không
- Đọc ngay sau khi ghi qua read replica để thấy replication lag
- Viết consistent hashing nhỏ, đo tỉ lệ key phải chuyển khi thêm một node

**Hoàn thành khi:**

- [ ] Ghế hết hạn giữ được nhả tự động
- [ ] Gửi lại request thanh toán không trừ tiền hai lần
- [ ] Broker tắt rồi bật lại, không đơn nào bị mất email

**Bài thiết kế:** news feed, chat, notification, xử lý file.

**Đọc:** Designing Data-Intensive Applications, System Design Interview tập 2.

## Giai đoạn 3: Senior (4–5 tháng)

Phần này rộng, làm xong mục bắt buộc rồi mới sang mục đọc thêm.

**Bắt buộc:**

- [ ] Leader election, distributed lock
- [ ] Saga, CDC, so sánh với 2PC
- [ ] Microservices và ranh giới service theo DDD
- [ ] SLI/SLO, error budget, disaster recovery
- [ ] Migration không downtime
- [ ] Viết RFC và ADR

**Đọc thêm:**

- Raft chi tiết, các lab của MIT 6.5840
- Event sourcing, CQRS
- Multi-region
- Chi phí, bảo mật nâng cao

**Dự án:** tách service, hàng đợi ảo khi mở bán, hoàn tiền.

**Thí nghiệm:** làm một service chết giữa luồng đặt vé và thanh toán, xem dữ liệu có về trạng thái đúng không.

**Hoàn thành khi:**

- [ ] Đơn lỗi giữa chừng được bù trừ, không có ghế bị treo
- [ ] Có SLO cho luồng đặt vé và dashboard theo dõi
- [ ] Có ADR giải thích vì sao tách, hoặc không tách, từng service

**Bài thiết kế:** thanh toán, ride-hailing, YouTube, distributed key-value store.

**Đọc:** Site Reliability Engineering (Google), MIT 6.5840 Distributed Systems.

## Khung làm một bài thiết kế

Yêu cầu → ước lượng tải → API → data model → sơ đồ tổng thể → điểm nghẽn → đánh đổi.
