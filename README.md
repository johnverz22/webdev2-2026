# Book Manager — Week 5 Companion Code

A small, complete Spring Boot + Thymeleaf app built to accompany the
WEBDEV2 Week 5 slides. Every topic this week maps to specific files.

## Running it

```
mvn spring-boot:run
```

Then open http://localhost:8080/books

Requires Java 17+ and an internet connection the first time (Maven needs
to download dependencies from Maven Central).

> This project was written and reviewed by hand but could not be
> compiled inside the authoring sandbox (no access to Maven Central
> there). Run `mvn spring-boot:run` locally as your first step and let
> me know if anything needs adjusting.

## Deliberate scope limit

There is **no real database and no Spring Data JPA** here — that's Week 7
material. `BookRepository` is a plain in-memory class (a `Map` under the
hood) that behaves like a repository without a database. Data resets every
time the app restarts. This keeps the code honest to what's been taught
through Week 5.

## Topic → File map

| Slide Topic | What to look at |
|---|---|
| **Topic 1** — Controller/Service/Repository | `controller/BookController.java`, `service/BookService.java`, `repository/BookRepository.java` |
| **Topic 2** — DTOs | `dto/BookSummaryDto.java`, `BookService.getSummary()`, `templates/books/summary.html` |
| **Topic 3** — Bean Validation | `model/Book.java` (`@NotBlank`), `BookController.create()` (`@Valid`), `templates/books/form.html` (`th:errors`) |
| **Topic 4** — Error Handling | `exception/BookNotFoundException.java`, `BookController.handleNotFound()` (local), `exception/GlobalExceptionHandler.java` (`@ControllerAdvice`, global) |
| **Topic 5** — Refactor | The whole project **is** the "after" state — compare any method here to the tangled "Before" code on the slides |
| **Topic 6** — Capstone Planning | No code — this topic is planning artifacts (requirements, domain model, UI flow), not part of this repo |
| **Week 4 callback** — Templates & fragments | `templates/fragments/layout.html`, reused via `th:replace` in every page under `templates/books/` |

## Project structure

```
src/main/java/com/webdev2/bookmanager/
├── BookManagerApplication.java   (seeds demo data on startup)
├── model/Book.java               (domain object + validation rules)
├── dto/BookSummaryDto.java        (view-shaped object, Topic 2)
├── repository/BookRepository.java (in-memory storage, Topic 1)
├── service/BookService.java       (business logic + DTO conversion)
├── controller/BookController.java (routing + delegation)
└── exception/
    ├── BookNotFoundException.java
    └── GlobalExceptionHandler.java

src/main/resources/
├── templates/
│   ├── fragments/layout.html      (Week 4: shared header/footer)
│   └── books/
│       ├── list.html
│       ├── form.html
│       ├── detail.html
│       └── summary.html
├── templates/error.html
└── static/css/style.css
```

## For the video walkthrough

See `narration_code_walkthrough.md` — a single continuous narration script
written for TTS synthesis, with `[SCREEN: ...]` cues marking what to show
on screen at each point. Delivery tone cues in brackets match the style of
this week's `speaker_notes.md` files.
