# Srinaka — Backend (Spring Boot)

ระบบเว็บร้านสปา/นวด — เอกสาร requirement/design เต็มอยู่ที่ `D:\GIT\DOCUMENT\srinaka_document\docs`. ไฟล์นี้
สรุปเฉพาะสิ่งที่ backend ต้องรู้และ pattern ที่ต้องใช้ซ้ำทุกครั้งที่เขียนโค้ดใน repo นี้ — โครงสร้าง/pattern ทั้งหมด
ลอกมาจาก `D:\GIT\BACK-END\share_money_backend` (โปรเจคก่อนหน้าที่ทำเสร็จและ deploy จริงแล้ว) ดู `CLAUDE.md`
ของ repo นั้นประกอบถ้าต้องการบริบทเพิ่ม

## Tech Stack

| หมวด | เลือกใช้ |
|---|---|
| Framework | Spring Boot 3.3.5, Java 21 (LTS), Maven |
| Web | Spring Web (REST Controller) |
| Security | Spring Security 6 + JWT (access + refresh), `BCryptPasswordEncoder` |
| Data Access | Spring Data JPA + Hibernate |
| Database | PostgreSQL (default `public` schema — ไม่ได้แยก schema/role เหมือน share_money เพราะ DB นี้ไม่ได้ share กับแอปอื่น) |
| Migration | Flyway, versioned SQL (`V{n}__description.sql`) |
| Validation | Jakarta Bean Validation (`@Valid`, `@NotBlank`, ...) |
| Mapping | MapStruct (Entity ↔ DTO) |
| Docs | springdoc-openapi (Swagger UI ที่ `/swagger-ui.html`) |

## Package Structure

โมดูลแบ่งตาม domain ไม่แบ่งตาม layer ข้ามโมดูล — แต่ละโมดูลมี `controller/`, `service/`, `repository/`,
`entity/`, `dto/` ของตัวเอง

```
com.srinaka/
  common/   response wrapper, error code, exception handler, base entity, security util, audit
  auth/     login/refresh/logout/me/change-password, JWT issue/verify
  user/     User entity (Admin/Supervisor/Employee/Customer), registration, profile
  menu/     menu_items/menu_permissions — sidebar เท่านั้น ไม่ใช่ catalog สินค้า/บริการสปา
  admin/    login_logs viewer
```

โดเมนธุรกิจสปา (catalog, promotion, booking, payment, refund, rating, report, employee schedule) ยังไม่มี
module — เริ่มสร้างตอน Phase 2 เป็นต้นไปตาม `09-implementation-roadmap.md`

## Response Pattern (บังคับทุก endpoint)

ทุก endpoint คืนค่าเป็น `ApiResponse<T>` (`com.srinaka.common.response.ApiResponse`) ห้าม controller คืน
entity/DTO ดิบ ๆ โดยไม่ห่อ — โยน business error ด้วย `throw new BusinessException(ErrorCode.XXX)` จาก
service layer เท่านั้น ห้าม throw จาก controller, `GlobalExceptionHandler` จับแล้วแปลงเป็น response ให้
อัตโนมัติ ทุก error code ใหม่ต้องเพิ่มเป็น enum constant ใน `ErrorCode` ห้าม hardcode string กระจายหลายที่
(รายละเอียดรูปแบบ JSON/กติกาเลือก error code ดูที่ `share_money_backend/CLAUDE.md` หัวข้อ "Response Pattern"
— เหมือนกันทุกประการ)

## Security & Data Rules

- ทุก endpoint บังคับ role ด้วย `@PreAuthorize("hasRole('...')")` และตรวจ ownership เพิ่มที่ service layer
  ถ้ามีแนวคิด ownership ในโดเมนนั้น (Phase 1 ยังไม่มี — ระบบ single-shop ไม่มี `shop_id`/multi-tenant)
- Public endpoint (`/api/auth/login`, `/api/auth/refresh`, `/api/customers/register`, swagger,
  `/actuator/health`) ต้อง permitAll ที่ `SecurityConfig` ตั้งแต่ filter-chain level — ไม่ใช่แค่ optional-auth
- Password เก็บด้วย BCrypt เท่านั้น, JWT access token อายุสั้น (~30 นาที) + refresh token เก็บใน DB (hash)
  เพื่อ revoke ได้, rotate ทุกครั้งที่ `/refresh`
- Login ผิดครบ 5 ครั้งต่อ username → lock ชั่วคราว 15 นาที (`LoginAttemptService`, in-memory ต่อ instance —
  ถ้า scale เป็นหลาย instance ต้องย้ายไป shared store เช่น Redis)
- `JwtSecretGuard` เช็คตอน startup ว่าถ้า profile ≠ `dev` ห้ามใช้ `JWT_SECRET` ค่า default เด็ดขาด (fail fast)
- `server.port: ${PORT:${SERVER_PORT:8080}}` ต้องคงไว้แบบนี้เสมอ — Render inject `PORT` ตอน runtime ถ้าใช้
  แค่ `SERVER_PORT` health check จะ fail (บทเรียนจาก share_money)
- ห้าม hardcode secret ที่ดูเหมือนใช้งานได้จริงใน `application.yml` (บทเรียนจาก share_money ที่เคย leak
  Cloudinary key + DB password ผ่าน default fallback) — ใช้ placeholder ที่ดูปลอมชัดเจนเท่านั้น

## OTP / Phone Verification — ยังไม่ทำ (ตั้งใจ)

`users.phone_verified_at` มีอยู่ใน schema แล้วแต่**ไม่มี mechanism ทำให้ถูก set เป็นค่าที่ไม่ null เลยในตอนนี้**
— ทีมยังไม่ตัดสินใจว่าจะใช้ SMS OTP หรือเปลี่ยนไปใช้ Google login แทน (ดู `01-requirements.md` หัวข้อ "ยังไม่
ตัดสินใจ") Customer สมัครสมาชิกได้ปกติผ่าน `POST /api/customers/register` แต่ `phone_verified_at` จะเป็น
`null` ตลอดจนกว่าจะมีการตัดสินใจและ implement กลไก verify จริง — **ไม่กระทบ Phase 1** เพราะยังไม่มี booking
ที่ต้องเช็คเงื่อนไขนี้ (เงื่อนไข "ต้อง verify ก่อนจอง" อยู่ใน FR-6.1 ซึ่งเป็นงานของ Phase 2) ถ้าแก้เบอร์โทรผ่าน
`PUT /api/profile` ระบบจะ reset `phone_verified_at` เป็น `null` ให้อัตโนมัติถ้าเป็น role `CUSTOMER` และเบอร์
เปลี่ยนจริง (forward-compatible กับตอนที่ verify mechanism มาแล้ว)

## Domain Reference

- 4 role: `ADMIN` (ดูแลระบบเท่านั้น ห้ามยุ่งข้อมูลธุรกิจ — ดู FR-3.4), `SUPERVISOR` (เจ้าของ/ผู้ดูแลธุรกิจ),
  `EMPLOYEE` (พนักงาน), `CUSTOMER` (ลูกค้า)
- Admin สร้างบัญชี Supervisor (`POST /api/admin/supervisors`), Supervisor สร้างบัญชี Employee
  (`POST /api/supervisor/employees`) — ไม่มี ownership scoping ระหว่าง Supervisor คนไหนสร้าง Employee คนไหน
  เพราะเป็นระบบ single-shop
- Seed admin account: สร้างผ่านโค้ด (`user/service/AdminSeeder.java`, `ApplicationRunner` รันตอน startup)
  **ไม่ใช่** Flyway SQL — เพราะไม่อยากมี password hash คงที่ฝังอยู่ในไฟล์ migration ที่ commit ลง git
  **บังคับตั้ง env var `ADMIN_SEED_PASSWORD` เสมอ** (ทั้ง dev/prod) — ถ้ายังไม่มี ADMIN user ในระบบและไม่ได้
  ตั้งค่านี้ แอปจะ fail ตั้งแต่ startup ทันที (ไม่มี auto-generate/random fallback แล้ว เพื่อไม่ให้ต้องไปงมหา
  รหัสผ่านใน log ตอน deploy) ตั้งค่าผ่าน local `.env` (gitignored) ตอน dev หรือผ่าน secret ของ hosting
  (เช่น Render env var) ตอน deploy จริง — ห้าม hardcode รหัสผ่าน admin ไว้ในไฟล์ที่ commit ลง git เด็ดขาด

### ⚠️ ขอบเขตสิทธิ์ ADMIN — บังคับใช้ตั้งแต่ Phase 2 เป็นต้นไป

`01-requirements.md` FR-3.4 ระบุชัดว่า **ADMIN ห้ามเข้าถึง** product/service catalog, promotion, booking,
payment, refund, report, rating เด็ดขาด — ตอนสร้าง module พวกนี้ใน Phase 2+ **ห้ามใส่ `hasRole('ADMIN')`**
ใน `@PreAuthorize` ของ endpoint กลุ่มนี้เด็ดขาด (เหมือน pattern ที่ share_money กัน ADMIN ออกจาก `/api/debts/**`)

## Roadmap

ดู `09-implementation-roadmap.md` เต็ม — Phase 1 (Foundation/Auth/RBAC/Profile) กำลังอยู่ระหว่างทำใน repo นี้
ดูสถานะจริงจาก git log ไม่ใช่จากไฟล์นี้

1. **Foundation, Auth, RBAC, Profile** (กำลังทำ) — schema พื้นฐาน (users/refresh_tokens/login_logs/
   menu_items/menu_permissions), auth lifecycle, RBAC, CRUD user 3 role แรก (Admin สร้าง Supervisor,
   Supervisor สร้าง Employee, Customer self-register), profile, menu API, login log viewer, Swagger,
   health endpoint, deploy scaffolding — **ไม่รวม OTP/phone verification** (ดูหัวข้อด้านบน)
2. Product/Service Catalog, Promotion, Booking
3. Payment (slip/cash), Walk-in sale (POS-lite), Cancel/Refund, Rating
4. Report, Employee Schedule, Admin Ops, Release
5. Issue — รีวิวหาบั๊ก/ช่องโหว่/ช่องว่างทั้งระบบก่อน launch จริง (ไม่ใช่ feature ใหม่)
