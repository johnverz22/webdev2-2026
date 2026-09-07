// static/js/app.js
//
// STATIC FILES: Spring Boot automatically serves anything placed under
// src/main/resources/static/ at the web root. This file lives at
// static/js/app.js on disk, so the browser requests it at /js/app.js —
// no controller, no @GetMapping, nothing written on the Java side at all.
//
// This script adds one small piece of interactivity to the book list
// page: typing in the search box hides table rows that don't match,
// live, without a page reload and without contacting the server.
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('book-search');
    if (!searchInput) {
        // We're not on the list page — nothing to do.
        return;
    }

    const rows = document.querySelectorAll('.book-table tbody tr');

    searchInput.addEventListener('input', function () {
        const query = searchInput.value.trim().toLowerCase();

        rows.forEach(function (row) {
            const title = row.children[0].textContent.toLowerCase();
            const author = row.children[1].textContent.toLowerCase();
            const matches = title.includes(query) || author.includes(query);
            row.style.display = matches ? '' : 'none';
        });
    });
});
