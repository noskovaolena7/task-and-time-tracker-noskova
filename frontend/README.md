# Frontend — Task & Time Tracker

Легкий SPA без збірки (чистий HTML/CSS/JS), побудований за `../api.yaml`.
Бекенд говорить snake_case, помилки мають вигляд `{message, code}`.

## Запуск

Бекенд має бути запущений (`./mvnw spring-boot:run`, порт 8080).
Потім будь-який статичний сервер з папки `frontend/`, наприклад:

```bash
cd frontend
python3 -m http.server 63342
# або: npx serve .
```

Відкрити: http://localhost:63342 (URL бекенда можна змінити на формі входу).

## Флоу, які враховано

- **2 реєстрації**: Personal (акаунт + personal workspace) і Company
  (акаунт + нова компанія, автор стає OWNER). Третього шляху нема.
- **Новачок з інвайтом**: вкладка «Маю інвайт» — реєстрація personal
  + одразу accept кода → стає USER у компанії. Personal workspace лишається.
- **Кабінет з 3 вкладками**: Personal / Companies / All; всередині Companies
  групування за компаніями. Сортування (назва/дата, asc/desc) — на клієнті.
- **Ролі**: оунер дає USER/MANAGER/ADMIN/OWNER, адмін — USER/MANAGER.
  Ко-оунери рівні; викинути оунера не можна, піти може лише сам.
  Зміна ролі: `PUT /companies/{id}/roles/{roleId}`, вихід: `DELETE …`.
- **Воркспейси**: живуть до явного видалення (лише оунер).
- **Вкладення** — лише URL (бінарного аплоада в API нема).
- **Сповіщення** — опитування списку, realtime нема; непрочитані з бейджем.
- **Logout** — токен просто видаляється з localStorage (бекенд stateless).

## Структура

- `index.html` — каркас (auth + layout застосунку).
- `styles.css` — темна сучасна тема, адаптив.
- `js/api.js` — клієнт усіх 29 шляхів API.
- `js/app.js` — роутер (`#/dashboard`, `#/projects`, `#/tasks`, `#/companies`,
  `#/team`, `#/notifications`, `#/workspaces`) та в'юхи.
