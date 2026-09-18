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
  common/     response wrapper, error code, exception handler, base entity, security util, audit
  auth/       login/refresh/logout/me/change-password, JWT issue/verify, LINE OAuth client
  user/       User entity (Admin/Supervisor/Employee/Customer), registration, profile, LINE link
  menu/       menu_items/menu_permissions — sidebar เท่านั้น ไม่ใช่ catalog สินค้า/บริการสปา
  admin/      login_logs viewer
  catalog/    service_categories/services (FR-4)
  promotion/  promotions + promotion_services join (FR-5)
  booking/    bookings — สร้าง/มอบหมาย/mark completed (FR-6)
  shop/       shop_info — ข้อมูลติดต่อร้าน แถวเดียว (FR-11)
```

Payment/refund/rating/report/employee-schedule ยังไม่มี module — เป็นงาน Phase 3-4 ตาม
`09-implementation-roadmap.md` **Cloudinary/`FileStorageService` ยังไม่ implement** — `services.image_url`/
`image_public_id` เป็นแค่ column เปล่าตอนนี้ Supervisor ยังอัปโหลดรูปผ่าน API ไม่ได้จนกว่าจะต่อ Cloudinary จริง

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
- Public endpoint (`/api/auth/{login,refresh,line-login}`, `/api/customers/register`,
  `GET /api/{services,service-categories,promotions,shop-info}/**`, swagger, `/actuator/health`) ต้อง
  permitAll ที่ `SecurityConfig` ตั้งแต่ filter-chain level — ไม่ใช่แค่ optional-auth (`/api/customers/
  register-line` ถูกตัดออกแล้ว — รวมเข้ากับ `/api/auth/line-login` ตั้งแต่ 2026-09-18)
- Password เก็บด้วย BCrypt เท่านั้น, JWT access token อายุสั้น (~30 นาที) + refresh token เก็บใน DB (hash)
  เพื่อ revoke ได้, rotate ทุกครั้งที่ `/refresh`
- Login ผิดครบ 5 ครั้งต่อ username → lock ชั่วคราว 15 นาที (`LoginAttemptService`, in-memory ต่อ instance —
  ถ้า scale เป็นหลาย instance ต้องย้ายไป shared store เช่น Redis)
- `JwtSecretGuard` เช็คตอน startup ว่าถ้า profile ≠ `dev` ห้ามใช้ `JWT_SECRET` ค่า default เด็ดขาด (fail fast)
- `server.port: ${PORT:${SERVER_PORT:8080}}` ต้องคงไว้แบบนี้เสมอ — Render inject `PORT` ตอน runtime ถ้าใช้
  แค่ `SERVER_PORT` health check จะ fail (บทเรียนจาก share_money)
- ห้าม hardcode secret ที่ดูเหมือนใช้งานได้จริงใน `application.yml` (บทเรียนจาก share_money ที่เคย leak
  Cloudinary key + DB password ผ่าน default fallback) — ใช้ placeholder ที่ดูปลอมชัดเจนเท่านั้น

## Customer Verification — LINE OAuth เท่านั้น (ไม่ใช่ SMS OTP, ไม่ใช่ Google)

`users.verified_at` (เดิมชื่อ `phone_verified_at` ก่อน `V4__customer_line_verification.sql` — เปลี่ยนชื่อ
เพราะไม่เกี่ยวกับเบอร์โทรอีกต่อไป) เป็น timestamp ที่ set ทันทีเมื่อบัญชีผูกกับ LINE แล้วเท่านั้น — **Google
login เคยพิจารณาแต่ตัดออกแล้ว** เพราะ Google Cloud บังคับ billing verification ที่มีค่าใช้จ่ายล่วงหน้า
(ดู `01-requirements.md` FR-1.7–FR-1.9)

**สมัครสมาชิกได้ 2 ทาง**:
- `POST /api/customers/register` — กรอกเอง (username/password/ชื่อ/เบอร์โทร **บังคับกรอกหมด**) →
  `verified_at = null`
- `POST /api/auth/line-login` — endpoint เดียวทำทั้ง login และ register (**เปลี่ยนดีไซน์แล้ว 2026-09-18** —
  เดิมแยก `/register-line` ออกมาต่างหาก ตอนนี้รวมเป็นตัวเดียว): แลก `code` เป็น LINE profile ก่อน แล้วเช็คว่า
  เคยมี user ผูก `line_user_id` นี้อยู่แล้วหรือยัง — **ถ้ามี** = login ปกติ, **ถ้าไม่มี** = สร้าง Customer ใหม่ให้
  อัตโนมัติในคำขอเดียวกันเลย (`username` auto-gen เป็น `"line_" + lineUserId`, `password_hash = null`,
  `phone = null`, `fullName` = LINE display name, `verified_at = now()` ทันที) แล้ว login ต่อเลยในคำขอเดียว
  — **ไม่มีฟอร์มคั่นระหว่างทาง ไม่ถาม username/password/เบอร์โทรเลย** (ต่างจาก FR-1.7 เดิมที่เขียนไว้ว่าทุกทาง
  ต้องกรอกครบ — บัญชี LINE-only เป็นข้อยกเว้นตอนนี้ตามที่ user ยืนยันแล้วว่าเบอร์โทร optional สำหรับทางนี้)
- ลูกค้าที่สมัครผ่าน LINE **เพิ่มเบอร์โทรทีหลังได้** ผ่าน `PUT /api/profile` ปกติ เมื่อไหร่ก็ได้ ไม่บังคับ

**ลิงก์บัญชีทีหลัง** (สำหรับคนที่สมัครกรอกเองแล้วอยากผูก LINE เพิ่ม): `POST /api/profile/link-line`
(ต้อง login ด้วย username/password ก่อน) — ผูก `line_user_id` เข้ากับ user เดิม + set `verified_at`

**`users.password_hash` เป็น nullable แล้ว** (`V9__line_only_customers.sql`) — บัญชีที่เกิดจาก LINE ล้วน ๆ
ไม่มีรหัสผ่าน login ด้วย username/password ไม่ได้ (ตั้งใจ) `AuthService.login()` เช็ค null ก่อนเทียบรหัสผ่าน
เสมอ (กัน NPE) — ถ้าจะเพิ่ม logic ที่เกี่ยวกับ password ที่ไหนอีก **ห้ามลืมเช็ค null ก่อน**

**`auth/service/LineOAuthClient.java`** เป็นตัวกลางเดียวที่คุยกับ LINE API (แลก `code` → access token →
เรียก `/v2/profile`) — ทุก flow ข้างบนเรียกผ่านตัวนี้ทั้งหมด ห้ามเขียนซ้ำ `RestClient` call เอง
ต้องตั้ง env var `LINE_CHANNEL_ID`/`LINE_CHANNEL_SECRET` (จาก LINE Developers Console, channel ประเภท
"LINE Login") — `redirectUri` ส่งมาจาก frontend ทุกครั้ง (ต้องตรงกับ Callback URL ที่ตั้งไว้ใน LINE Console
เป๊ะ ๆ) ไม่ได้อ่านจาก env ฝั่ง backend

แก้เบอร์โทรผ่าน `PUT /api/profile` **ไม่กระทบ `verified_at` เลย** — verify มาจากการลิงก์ LINE เท่านั้น
ไม่เกี่ยวกับเบอร์โทรอีกต่อไป (ต่างจากดีไซน์เดิมที่เคยผูกกับ SMS OTP)

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

ดู `09-implementation-roadmap.md` เต็ม — ดูสถานะจริงจาก git log ไม่ใช่จากไฟล์นี้

1. **Foundation, Auth, RBAC, Profile** ✅ เสร็จ — schema พื้นฐาน (users/refresh_tokens/login_logs/
   menu_items/menu_permissions), auth lifecycle, RBAC, CRUD user 3 role แรก (Admin สร้าง Supervisor,
   Supervisor สร้าง Employee), profile, menu API, login log viewer, Swagger, health endpoint, deploy
   scaffolding
2. **Product/Service Catalog, Promotion, Booking, Customer register (LINE)** — โค้ดเขียนเสร็จแล้ว
   (`catalog/`, `promotion/`, `booking/`, `shop/`, LINE auth flow), **ยังไม่ได้รัน migration จริงกับ DB /
   ยังไม่ได้ทดสอบ end-to-end** เพราะยังไม่มี Postgres รันอยู่ตอน implement — ต้องรัน `docker-compose up -d
   postgres` แล้วทดสอบ flow จริงก่อนถือว่า Phase 2 เสร็จสมบูรณ์ **ยังไม่มี Cloudinary/image upload** สำหรับรูป
   สินค้า/บริการ (FR-4.2) — ทำทีหลัง
3. Payment (slip/cash), Walk-in sale (POS-lite), Cancel/Refund, Rating
4. Report, Employee Schedule, Admin Ops, Release
5. Issue — รีวิวหาบั๊ก/ช่องโหว่/ช่องว่างทั้งระบบก่อน launch จริง (ไม่ใช่ feature ใหม่)
