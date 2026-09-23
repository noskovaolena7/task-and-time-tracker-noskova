# Звіт про невідповідності проєкту «Task and time tracker»

Дата аналізу: 2026-09-22
Методика: (1) `mvnw compile` / `mvnw test-compile` — компілюється; (2) запуск додатку та реальні HTTP-запити; (3) прямі SQL-запити до Postgres, що імітують SQL Hibernate; (4) повний звір `api.yaml` ↔ контролери ↔ DTO ↔ сервіси ↔ мапери ↔ сутності ↔ БД; (5) аналіз існуючих тестів (тести **не чіпаємо** — вони задають жорсткі контракти).

Статус на момент аналізу: **компілюється**, але **додаток не стартує без `JWT_SECRET`**, **Swagger (`/v3/api-docs`) повертає 500**, **реєстрація користувача повертає 500**, і щонайменше 8 таблиць БД не збігаються з JPA-сутностями.

---

## БЛОК A. Помилки, які ламають запуск додатку / Swagger (P0)

### A1. `JWT_SECRET` без значення за замовчуванням — додаток НЕ стартує
- `src/main/resources/application.yaml:25` → `secret: ${JWT_SECRET}`.
- **Доведено запуском**: `PlaceholderResolutionException: Could not resolve placeholder 'JWT_SECRET'` → `Error creating bean 'tokenServiceImpl'` → падіння Tomcat.
- Фікс: `secret: ${JWT_SECRET:...default-dev-key...}` (мінімум 32 байти для HMAC-SHA256) або документувати обов'язковість змінної.

### A2. springdoc 2.5.0 несумісний з Spring Boot 4.1 / Framework 7 — Swagger мертвий
- `pom.xml` → `springdoc-openapi-starter-webmvc-ui` **2.5.0**, батьківський `spring-boot-starter-parent` **4.1.0** (Spring Framework 7.0.8).
- **Доведено запуском**: `GET /v3/api-docs` → **500**, лог: `NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'`.
- `GET /swagger-ui/index.html` → 200, але UI не має чим наповнити специфікацію (помилка при генерації).
- Фікс: оновити до `springdoc-openapi-starter-webmvc-ui` **3.1.1** (лінія 3.x створена саме під Spring Boot 4 / Framework 7; перевірити, що `io.swagger.v3.core.jackson.ModelResolver` у `OpenApiConfig` лишається сумісним).

### A3. У prod відсутній провайдер валідації — `@Valid` нічого не робить
- `pom.xml`: `hibernate-validator` має `<scope>test</scope>`; `spring-boot-starter-validation` відсутній.
- **Доведено логом старту**: `Failed to set up a Bean Validation provider: ... no Jakarta Validation provider could be found`.
- Наслідок: усі анотації `@NotNull/@NotBlank/@Email/...` на DTO ігноруються в робочому додатку (в тестах валідація працює, бо hibernate-validator є в test-classpath → поведінка тестів і продакшену розходиться).
- Фікс: додати залежність `spring-boot-starter-validation` (compile scope).

### A4. Кастомний `ObjectMapper` перехоплює біновий конвеєр Boot
- `config/OpenApiConfig.java:24-31` — власний бін `ObjectMapper` зі стратегією `SNAKE_CASE`.
- Це підміняє автоматично сконфігурований мапер Boot: (1) відсутні кастомізери/модулі Boot (ризик для `Instant` — JSR-310); (2) уся JSON-контракція стає snake_case (`first_name`, `is_read`), що збігається з `api.yaml`, але **розходиться з тестами**, які очікують camelCase (`$.firstName`, `$.isRead`) — у тестах свій мапер, тому тести не падають.
- Фікс: зібрати мапер через `Jackson2ObjectMapperBuilder` (щоб підключились модулі дати/часу) і лишити `SNAKE_CASE`; перевірити серіалізацію `Instant` після змін.

### A5. `GlobalExceptionHandler` маскує помилки: 404 → 500
- `exception/GlobalExceptionHandler.java` — `@ExceptionHandler(Exception.class)` повертає 500 для **всього**, включно з `NoResourceFoundException` (невідомий маршрут → має бути 404), `MethodArgumentNotValidException` (400), `MethodArgumentTypeMismatchException` (400), `AccessDeniedException` (403).
- **Доведено**: запит до неіснуючого маршруту (`/auth/sign-up/personal`, якого нема в контролері) дає 500, а не 404 — саме це зараз валить 2 тести `AuthControllerTest` (див. Блок F).

---

## БЛОК B. БД (Liquibase, changeset `009-rebuild-schema`) ↔ JPA-сутності (P0 — все падає в рантаймі)

Фактична схема БД = changeset 009 (перевірено `information_schema`). Кожен пункт нижче **відтворено прямим SQL-запитом** — усі запити повертають `ERROR: column ... does not exist`.

| # | Таблиця | Колонка в БД | Поле в сутності | Наслідок | Напрямок фіксу |
|---|---------|--------------|-----------------|----------|----------------|
| B1 | `companies` | **немає** `description` | `CompanyEntity.description` | кожен SELECT/INSERT компанії → 500 | `ALTER TABLE companies ADD COLUMN IF NOT EXISTS description TEXT;` (new changeset 010) |
| B2 | `workspaces` | **немає** `type` | `WorkspaceEntity.type` | **доведено**: `POST /auth/signup/personal` → 500: `column "type" of relation "workspaces" does not exist` | `ADD COLUMN IF NOT EXISTS type TEXT;` |
| B3 | `comments` | `message` | `CommentEntity.text` → колонка `text` | SELECT/INSERT коментарів → 500 (доведено) | `@Column(name = "message")` в сутності (БД не чіпаємо) |
| B4 | `task_history` | `field_changed` | `TaskHistoryEntity.field` → колонка `field` | SELECT історії → 500 (доведено) | `@Column(name = "field_changed")` |
| B5 | `notifications` | `is_read` | `NotificationEntity.read` → колонка `read` | SELECT нотифікацій → 500 (доведено) | `@Column(name = "is_read")` |
| B6 | `notifications` | **немає** `status` | `NotificationEntity.status` | SELECT → 500 (доведено); `NotificationMapper.toDomain` ще й NPE при null status | `ADD COLUMN IF NOT EXISTS status TEXT;` + null-safe мапінг |
| B7 | `attachments` | **немає** `uploaded_by`, `uploaded_at` (натомість є `owner_id`, `created_at`) | `AttachmentEntity.uploadedBy/uploadedAt` | INSERT/SELECT вкладень → 500 (доведено); `SecurityService.canManageAttachmentById` використовує `getUploadedBy()` | `ADD COLUMN IF NOT EXISTS uploaded_by UUID; ADD COLUMN IF NOT EXISTS uploaded_at TIMESTAMPTZ;` (значення за замовчуванням для наявних рядків) |
| B8 | `task_reminders` | **немає** `remind_at`, `message`; натомість `reminder_periods TEXT[] NOT NULL`, який сутність не заповнює | `TaskReminderEntity.remindAt/message` | INSERT нагадувань → 500 одразу за двома причинами | `ADD COLUMN remind_at TIMESTAMPTZ; ADD COLUMN message TEXT; ALTER COLUMN reminder_periods DROP NOT NULL;` |
| B9 | `project_deadlines` | **немає** `title` | `ProjectDeadlineEntity.title` (встановлюється в `ProjectDeadlineServiceImpl.updateDeadline`) | `PUT /projects/{id}/deadlines/{id}` → 500 (доведено) | `ADD COLUMN IF NOT EXISTS title TEXT;` |
| B10 | `project_deadlines` | колонка `reminder_periods TEXT[]` | `@ElementCollection` → окрема таблиця `project_deadlines_reminder_periods`, якої **не існує** | SELECT дедлайнів → 500 (доведено: `relation ... does not exist`) | замінити `@ElementCollection` на `@Column(name="reminder_periods") @JdbcTypeCode(SqlTypes.ARRAY)` (прямий мапінг на `TEXT[]`, таблиця не потрібна) |
| B11 | `projects` | `created_by UUID NOT NULL` | у `ProjectEntity` **взагалі немає** поля `createdBy` | **доведено**: `INSERT` без `created_by` → `null value in column "created_by" ... violates not-null constraint` → **створення проєкту завжди 500** | додати `createdBy` у `ProjectEntity` + мапінг у `ProjectMapper` (DTO вже має `createdBy`, `api.yaml` має `created_by`) |
| B12 | `tasks` | `description TEXT NOT NULL` | опис не обов'язковий у `TaskCreateRequestDto` (без `@NotNull`) | створення таски без description → 500 | `ALTER TABLE tasks ALTER COLUMN description DROP NOT NULL;` (або default `''`) |
| B13 | `projects` | `description TEXT NOT NULL` | опис не обов'язковий у `ProjectCreateRequestDto` | створення проєкту без description → 500 | `ALTER TABLE projects ALTER COLUMN description DROP NOT NULL;` |
| B14 | `attachments` | `owner_id`, `created_at` | у сутності **немає** відповідних полів | колонки мертві (не фатально), `api.yaml` теж очікує `owner_id`/`created_at` у відповіді | або додати поля в сутність, або прибрати з `api.yaml` (див. C9) |
| B15 | `users` | OK (`first_name…status, role, workspace_id, company_id, created_at, updated_at`) | збігається | — | ✓ |
| B16 | `invites`, `user_company_roles`, `project_members`, `time_entries` | OK | збігаються | — | ✓ |

**Що працює зараз**: `users` (реєстрація падає лише на B2), `invites`, `user_company_roles`, `project_members`, `time_entries`, `tasks` (читання).

### B17. Liquibase
- `db.changelog-master.yaml` **не містить** changeset `002` (файл `002-add-workspace-id.sql` сирота — не запускається; не фатально, бо 003/005 дублюють `workspace_id`).
- `004` має жорстко прописаний `validCheckSum: 9:6caf...` — при зміні файлів 004 впаде перевірка; не чіпати без потреби.
- Свіжий прогін: 001→009 проходить (009 перебудовує схему), але **кінцева схема 009 не збігається з кодом** — див. таблицю вище. Пропоновані правки (010) додаються **після** 009 і сумісні з уже мігрованою БД.
- `docker-compose` — postgres:18, CI — postgres:15 (дрібна розбіжність).

---

## БЛОК C. `api.yaml` ↔ контролери ↔ DTO (розбіжності контракту)

Нота: продакшен-мапер має стратегію **SNAKE_CASE**, тому JSON-назви у `api.yaml` (snake) зіставляються з Java-полями camelCase.

### C1. Маршрути, які є в `api.yaml`, але відсутні в коді (404/405)
| api.yaml | Код | Фікс |
|----------|-----|------|
| `POST /invites`, `POST /invites/{code}/accept` | **немає `InviteController`** (сервіс `InviteService` існує; тест `InviteControllerTest` — `@Disabled("No InviteController present")`) | додати `InviteController` (сервіс уже готовий: `generateInvite`, `resolveCompany`) |
| `GET/POST /tasks/{taskId}/reminders` | **немає `TaskReminderController`** (сервіс `TaskReminderService` існує) | додати `TaskReminderController` |

### C2. Маршрути в коді, яких нема в `api.yaml` (Swagger «не бачить» реальний API)
- `POST /auth/signup/personal`, `POST /auth/signup/company` (AuthController).
- `PUT/DELETE /tasks/{taskId}/attachments/{attachmentId}` (AttachmentController).
- `DELETE /projects/{projectId}/members/{userId}` (ProjectMemberController).
- `PUT /projects/{projectId}/deadlines/{deadlineId}` (ProjectDeadlineController).

### C3. Розбіжність самих маршрутів
| api.yaml | Контролер | Тести (не чіпаємо) | Фікс |
|----------|-----------|--------------------|------|
| `PUT /notifications/{notificationId}/read` | `PUT /users/{userId}/notifications/{notificationId}/read` | очікують шлях контролера | виправити `api.yaml` на шлях контролера |
| `POST /auth/signup` (+ немає company-варіантів) | `/auth/signup/personal`, `/auth/signup/company`, `/auth/signup` | `AuthControllerTest` постить на **`/auth/sign-up/personal`**, **`/auth/sign-up/company`** → зараз 404→500 → **2 падаючі тести** | додати альїаси `@PostMapping({"/auth/signup/personal", "/auth/sign-up/personal"})` тощо + дозволити їх у `SecurityConfig` (`/auth/sign-up/**`) + задокументувати в `api.yaml` |

### C4. Тіла запитів: `api.yaml` vs DTO (зараз клієнт з Swagger отримує 400 або пише нічого)
| Endpoint | api.yaml | DTO (контролер) | Фікс |
|----------|----------|-----------------|------|
| `POST /companies` | `CompanyCreateRequest`: лише `name` | `CompanyCreateRequestDto`: **`owner_id` @NotNull**, `name`, `description` | додати `owner_id` (required) і `description` в `api.yaml` |
| `PUT /companies/{id}` | `CompanyCreateRequest` | `CompanyUpdateRequestDto` (`name`+`description`) | окремий `CompanyUpdateRequest` у схемах |
| `POST /projects` | `company_id, name, description` (description required) | `ProjectCreateRequestDto`: **`created_by` @NotNull** додатково; description — ні | додати `created_by` (required) у `api.yaml`; description у DTO зробити відповідним (див. B13) |
| `POST /projects/{id}/members` | поле **`member_role`** | поле JSON = `member_role_dto` (від поля `memberRoleDto`) | `@JsonProperty("member_role")` на `ProjectMemberCreateRequestDto.memberRoleDto` і `ProjectMemberResponseDto.memberRoleDto` (Java-поле лишається — його використовують тести) |
| `POST /tasks/{id}/comments` | поле **`message`** | `CommentCreateRequestDto.text` (тести очікують `$.text`) | змінити `api.yaml`: `message` → `text` (і відповідь теж: `text`) |
| `POST /tasks/{id}/time-entries` | `user_id`, `start_time`, `end_time` (опц.) | **`task_id` @NotNull** + `end_time` @NotNull | прибрати `@NotNull` з `task_id` у DTO (шлях уже містить taskId, сервіс його перезаписує) і прибрати з `api.yaml` (тести його встановлюють — безпечне); `end_time` лишити required і виправити `api.yaml` |
| `POST /tasks/{id}/attachments` | `id, task_id, file_url, uploaded_by, uploaded_at` | `file_name` @NotBlank, `file_url`, `uploaded_by` @NotNull | переписати `AttachmentCreateRequest` під DTO (`file_name`, `file_url`, `uploaded_by`) |
| `POST /projects/{id}/deadlines` | `deadline`, `reminder_periods` | + **`created_by` @NotNull** | додати `created_by` у `api.yaml` |
| `POST /tasks/{id}/reminders` | `reminder_periods: [string]` + `created_by` | `TaskReminderCreateRequestDto`: **`remind_at`**, `message`, `created_by` | переписати схеми `TaskReminder*` у `api.yaml` під DTO |
| `POST /auth/signup` | `first_name…phone_number` (snake) | `RegisterUserRequestDto` (camel → snake мапиться коректно) | ✓ збігається завдяки SNAKE_CASE |

### C5. Відповіді: `api.yaml` vs Response DTO
| Endpoint/schema | api.yaml | Response DTO | Фікс |
|-----------------|----------|--------------|------|
| `User` | `status, role, workspace_id, company_id, created_at, updated_at, password…` | `UserResponseDto`: `id, first_name, last_name, email, phone_number, member_role` | привести `api.yaml` до фактичного DTO (прибрати зайве, `member_role` замість `role`) |
| `Company` | `owner_id, workspace_id` | `CompanyResponseDto`: `id, name, description, created_at, updated_at` | привести `api.yaml` (додати `description`, прибрати `owner_id/workspace_id`) |
| `Attachment` | `project_id, file_name, owner_id, created_at, uploaded_by…` | `AttachmentResponseDto`: `id, project_id, task_id, file_name, file_url, uploaded_by, uploaded_at, updated_at` | прибрати `owner_id/created_at` зі схеми (див. B14) |
| `Comment` | `message` | `text` | `api.yaml` → `text` |
| `TaskReminder` | `reminder_periods` | `remind_at` | `api.yaml` → `remind_at` |
| `ProjectMember` | `member_role` | JSON-назва `member_role_dto` | див. C4 (`@JsonProperty`) |
| `TokenResponse` | `token, tokenType, expiresIn` | `TokenResponseDto`: лише `token` | прибрати `tokenType/expiresIn` з `api.yaml` або додати в DTO |
| `Notification` | має `status` | DTO `status` відсутній (є в домені/сутності) | додати `status` у `NotificationResponseDto`+мапер **або** прибрати з `api.yaml` (разом з B6 — статус треба зберігати) |
| `Project` | `created_by` (readOnly) | DTO має `createdBy`, але мапер його **не заповнює** (див. B11) → у відповіді завжди null | мапити `createdBy` |
| `ProjectDeadline` | без `title` | `ProjectDeadlineUpdateRequestDto` має `title` (PUT) | додати `title` в схему відповіді або лишити write-only (див. B9) |
| `Workspace` | схема є, **шляхів нема** | WorkspaceController відсутній (тест `@Disabled`) | лишити схему як довідкову або видалити — не критично |
| `Error` | `message, code` | `ErrorDto` `message, code` | ✓ |

### C6. Query-параметри, задокументовані, але не підтримувані
| Endpoint | api.yaml | Контролер | Обмеження тестів | Фікс |
|----------|----------|-----------|------------------|------|
| `GET /companies` | `page`, `size` | параметрів нема | тест фіксує `companyService.getCompanies()` (без аргументів) | прибрати `page/size` з `api.yaml` |
| `GET /users` | `page`, `size` | параметрів нема | тест фіксує `getUsers(companyId)` | прибрати з `api.yaml` |
| `GET /projects` | `page`, `size`, `company_id` | параметрів нема; хардкод `getProjects(0, 20, null)` | тест фіксує виклик **рівно** `getProjects(0, 20, null)` | додати необов'язкові `page/size/company_id` у контролер з дефолтами 0/20/null → без параметрів виклик лишається ідентичним ✓ |
| `GET /tasks` | `page,size,status,project_id,assigned_to` | параметри є, але **camelCase** (`projectId`, `assignedTo`) → клієнт з `project_id` фільтр не отримує | тест викликає `getTasks(null,null,null,null,null)` | перейменувати `@RequestParam("project_id")`, `@RequestParam("assigned_to")` (на тести не впливає) |
| `GET /users/{id}/notifications` | `page`, `size` | параметрів нема (сервіс підтримує) | тест фіксує `getAllNotifications(userId)` | додати опційні `page/size` у контролер (без параметрів — той самий виклик) **або** прибрати з `api.yaml` |

### C7. Логіка контролерів, що суперечить сервісам/API
1. **`GET /tasks` у продакшені ніколи не працює**: `@PreAuthorize("@securityService.canAccessTask(#projectId)")` — при `projectId=null` (типовий випадок «список усіх») викликає `taskRepository.findById(null)` → 500/помилка доступу; а якщо `projectId` передано — він трактується як **taskId** (несумісна логіка). Потрібна перевірка на кшталт «якщо projectId != null → canAccessProject, інакше авторизований/список компанії».
2. **`CompanyController.getCompanyById` / `CompanyRoleController.getRoles`** використовують `@securityService.canAccessUser(#id)` з **companyId** — перевірка «чи користувачеві дозволено бачити *іншого користувача*» застосована до id компанії (semantically wrong; у більшості випадків випадково поверне false → 403).
3. **`CompanyController.getAllCompanies`** використовує `canListUsers()` для списку компаній (зайва залежність від логіки користувачів).
4. **`UserServiceImpl.getUsers(companyId)` ігнорує `companyId`** → повертає **усіх** користувачів БД, хоча контролер свідомо бере `getCurrentUserCompanyId()` (порушення мульти-тенантності).
5. **`NotificationController.markNotificationAsRead` ігнорує `userId`** зі шляхи — будь-який авторизований користувач може позначити читання чужої нотифікації (api.yaml передбачає 403).
6. **`AttachmentController.createAttachment`** ігнорує `uploadedBy` із тіла запиту і завжди ставить `securityService.getCurrentUserId()` → DTO-поле `uploadedBy @NotNull` оманливе (має бути опційним або документованим як ignored).
7. **`TaskServiceImpl.getProjectIdByTaskId`** кидає `RuntimeException("Task not found")` замість `TaskNotFoundException` → глобальний обробник віддасть 500 замість 404 (так само `UserServiceImpl.updateRole` кидає голий `RuntimeException`, `InviteServiceImpl` кидає голий `RuntimeException` замість `InvalidCredentialsException`/400-404).
8. **`TaskServiceImpl.updateTask`**: валідація `TaskStatus.valueOf(task.getStatus().name())` — беззмістовна (enum вже є enum); нічого не ламає, але зайва.
9. **`ProjectDeadlineServiceImpl.updateDeadline`** не перевіряє, що `deadlineId` належить `projectId` (шлях містить обидва id, перевіряється лише один).
10. **`AttachmentServiceImpl.deleteAttachment(taskId, attachmentId)`** ігнорує `taskId` — можна видалити вкладення будь-якої задачі, знаючи лише його id (розбіжність із сигнатурою/шляхом).
11. **`UserCompanyRoleMapper/UserCompanyRoleCreateRequestDto`** приймає `RoleDto` зі **6** значеннями (`USER…PERSONAL_USER, COMPANY_USER`), а мапить через `MemberRole.valueOf(...)` (лише **4**) → запит з `PERSONAL_USER` (який дозволяє `api.yaml`) → `IllegalArgumentException` → 500. Те саме для `Invite.role` в `api.yaml`.
12. **`ProjectController.createProject`**: `createdBy` береться з тіла запиту, а не з токена — будь-який користувач може вказати чужого автора (розбіжність із SecurityService-підходом в AttachmentController). Зафіксувати рішення: або з токена, або лишити в тілах (api.yaml вимагає `created_by`).

### C8. `OpenApiConfig` vs `api.yaml` (метадані Swagger)
- `OpenApiConfig.customOpenAPI()` → title **«Loyalty Card»**, description «…Loyalty Card application»; `api.yaml` → «Project & Task Management API». Копіпаста з іншого проєкту — виправити назву/опис.
- `api.yaml` описує `security: bearerAuth` глобально ✓ збігається з `SecurityConfig`.

---

## БЛОК D. Конфігурація безпеки
- `SecurityConfig` дозволяє `/auth/signup/**`, `/auth/signup`, **але не** `/auth/sign-up/**` → після додавання альїасів (C3) треба розширити matcher.
- `TokenAuthFilter` будує authorities «від початку списку Role до ролі з токена» — тобто `PERSONAL_USER` отримує права `USER, MANAGER, ADMIN, OWNER, PERSONAL_USER` (усі попередні значення enum). Це **розширює** повноваження понад роль і суперечить `@PreAuthorize`-логіці, яка спирається на `SecurityService` (БД), але варто задокументувати/обмежити (наприклад, одна роль у authorities).
- `emptyDetailsService` кидає виняток — ок, запобігає default-user.
- CORS: `allowedOrigins("*")` без `allowCredentials` — прийнятно для dev.

---

## БЛОК E. Інші дрібні невідповідності
- `PROJECT_FIXES_SUMMARY.txt` стверджує «Maven test: successful» — зараз **2 тести падають** (див. F) → файл застарів.
- `pom.xml`: `spring-modulith-bom` та репозиторії `spring-releases/milestones` — використовуються?? (modulith не застосовується) → зайве.
- `TaskReminder` домен має незадіяний імпорт `LocalDateTime`.
- `NotificationEntity.getIsRead/setIsRead` дублюють Lombok-гетери `read` (може давати два JSON-властивості в деяких конфігураціях).
- `TimeEntryCreateRequestDto.taskId` дублює path variable (див. C4).
- `api.yaml` не документує `PUT/DELETE` для вкладень і `DELETE` для учасників проєкту (див. C2).
- `scripts/reset-db.sh` — коректний (повний скид БД разом із `DATABASECHANGELOG`; після скиду 009 перестворить схему, а нові 010-правки теж застосуються).

---

## БЛОК F. Тести (не чіпаємо — лише фіксуємо стан)
- `mvnw compile` ✓, `mvnw test-compile` ✓.
- Базовий прогін: **1 клаc/2 тести падають** — `AuthControllerTest.signUpPersonal_returns201` та `signUpCompany_returns201`: очікують маршрути `/auth/sign-up/personal`, `/auth/sign-up/company`, а контролер має `/auth/signup/personal` (404 → маскується в 500 обробником A5). **Фікс у продакшен-коді (альїаси маршрутів, C3) зробить ці тести зеленими без зміни тестів.**
- Тести фіксують такі контракти (їх не можна ламати): назви Java-полів `memberRoleDto`, `text` (Comment), `title` (ProjectDeadlineUpdateRequestDto), `remindAt/message` (TaskReminderDto), `ownerId` (CompanyCreateRequestDto), `createdBy` (Project/Task/Deadline), виклик `projectService.getProjects(0, 20, null)` для `GET /projects`, `companyService.getCompanies()` для `GET /companies`, jsonPath `$.text`, `$.isRead`, `$.firstName`, шляхи контролерів (зокрема `/users/{userId}/notifications/{notificationId}/read`).
- `InviteControllerTest` / `WorkspaceControllerTest` — `@Disabled` з коментарем «controller absent» (підтверджує B/C1).

---

## ПЛАН ВИПРАВЛЕНЬ (порядок виконання)

**Крок 1 — старт/конфіг (A):**
1. `application.yaml`: дефолт для `jwt.secret` (A1).
2. `pom.xml`: springdoc → `3.1.1` (A2); додати `spring-boot-starter-validation` (A3); прибрати test-scope у hibernate-validator, якщо лишиться дублем.
3. `OpenApiConfig`: зібрати `ObjectMapper` через `Jackson2ObjectMapperBuilder` + `SNAKE_CASE` (A4); виправити title/description на «Project & Task Management API» (C8).

**Крок 2 — БД (B):** новий changeset `010-align-entities.sql` (після 009, `IF NOT EXISTS`, сумісний з наявною БД):
`companies.description`, `workspaces.type`, `notifications.status`, `attachments.uploaded_by/uploaded_at`, `task_reminders.remind_at/message` (+ `reminder_periods DROP NOT NULL`), `project_deadlines.title`, `tasks/projects.description DROP NOT NULL`.
Кодові правки: `@Column(name="message")` (comments), `@Column(name="field_changed")` (history), `@Column(name="is_read")` (notifications), `@JdbcTypeCode(SqlTypes.ARRAY)` замість `@ElementCollection` (deadlines), `createdBy` у `ProjectEntity`+`ProjectMapper`, null-safe статус у `NotificationMapper`.

**Крок 3 — контракт API (C):**
4. `api.yaml`: виправити шлях notification-read; схеми Company/Project/Comment/Attachment/TimeEntry/TaskReminder/User/Token; прибрати непідтримувані page/size; enum ролей → 4 значення; задокументувати відсутні в коді PUT/DELETE; `created_by`/`owner_id` required.
5. DTO: `@JsonProperty("member_role")` (members), зняти `@NotNull` з `TimeEntryCreateRequestDto.taskId`.
6. Контролери: альїаси `/auth/sign-up/**` + SecurityConfig; параметри `GET /projects` (дефолти 0/20/null) і `GET /notifications` (page/size); snake_case query-параметри в `GET /tasks`; валідна перевірка доступу для `GET /tasks`; `@Valid` там, де його бракує (members POST, deadline PUT).
7. Додати `InviteController` і `TaskReminderController` (сервіси вже є).

**Крок 4 — логіка/безпека (C7, D):** фільтрація користувачів за компанією; `companyId`-замість`userId` у перевірках компаній; ownership перевірки (notification read, attachment delete); типізовані винятки замість голих `RuntimeException`; `@PreAuthorize` з урахуванням null.

**Крок 5 — обробник помилок (A5):** 404/400/403 для відповідних Spring-винятків, generic — останнім.

**Контроль якості:** `mvnw compile` → `mvnw test` (тести не редагувати, мають бути зелені або кращі за базові 2 фейли) → старт додатку → `GET /v3/api-docs` 200 → `GET /swagger-ui/index.html` 200 → smoke: signup personal/company, login, create company/project/task, comments, attachments, deadlines, notifications, invites, reminders — усі без 500.
