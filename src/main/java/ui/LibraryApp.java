package ui;

import interfaces.IBookService;
import model.Book;
import service.BookService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LibraryApp extends JFrame {
    private final IBookService bookService = new BookService();
    private final JTextField searchField;
    private final JTextArea textArea;

    public LibraryApp() {
        // Инициализация интерфейса
        setTitle("Библиотека");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel searchLabel = new JLabel("Поиск по автору или названию: ");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Искать");
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JButton addButton = new JButton("Добавить книгу");
        JButton deleteButton = new JButton("Удалить книгу");
        JButton sortByAuthorButton = new JButton("Сортировать по автору");
        JButton sortByYearButton = new JButton("Сортировать по году");
        bottomPanel.add(addButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(sortByAuthorButton);
        bottomPanel.add(sortByYearButton);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel);

        // Загрузка книг при запуске
        loadBooks();

        // Обработчики событий
        addButton.addActionListener(e -> addBook());

        deleteButton.addActionListener(e -> deleteBook());

        sortByAuthorButton.addActionListener(e -> {
            List<Book> books = bookService.getAllBooksOrderedByAuthor();
            displayBooks(books);
        });

        sortByYearButton.addActionListener(e -> {
            List<Book> books = bookService.getAllBooksOrderedByYear();
            displayBooks(books);
        });

        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            if (!query.isBlank()) {
                List<Book> filteredBooks = bookService.findBooks(query);
                displayBooks(filteredBooks);
            } else {
                loadBooks(); // Показать все книги, если строка поиска пустая
            }
        });
    }

    private void addBook() {
        String author = JOptionPane.showInputDialog("Введите автора:");
        if (author == null || author.isEmpty()) {
            showErrorMessage("Автор не может быть пустым.");
            return;
        }

        String title = JOptionPane.showInputDialog("Введите название:");
        if (title == null || title.isEmpty()) {
            showErrorMessage("Название не может быть пустым.");
            return;
        }

        String yearInput = JOptionPane.showInputDialog("Введите год издания:");
        if (yearInput == null || yearInput.isEmpty()) {
            showErrorMessage("Год издания не может быть пустым.");
            return;
        }

        String copiesInput = JOptionPane.showInputDialog("Введите количество экземпляров:");
        if (copiesInput == null || copiesInput.isEmpty()) {
            showErrorMessage("Количество экземпляров не может быть пустым.");
            return;
        }

        try {
            int year = Integer.parseInt(yearInput);
            int copies = Integer.parseInt(copiesInput);

            Book book = new Book(author, title, year, copies);
            if (bookService.addBook(book)) {
                showSuccessMessage("Книга добавлена!");
                loadBooks(); // Обновляем список книг
            } else {
                showErrorMessage("Ошибка при добавлении книги!");
            }
        } catch (NumberFormatException ex) {
            showErrorMessage("Некорректный ввод! Введите числа для года и количества экземпляров.");
        }
    }

    private void deleteBook() {
        String author = JOptionPane.showInputDialog("Введите автора книги для удаления:");
        if (author == null || author.isEmpty()) {
            showErrorMessage("Автор не может быть пустым.");
            return;
        }

        String title = JOptionPane.showInputDialog("Введите название книги для удаления:");
        if (title == null || title.isEmpty()) {
            showErrorMessage("Название не может быть пустым.");
            return;
        }

        String copiesInput = JOptionPane.showInputDialog("Введите количество экземпляров для удаления:");
        if (copiesInput == null || copiesInput.isEmpty()) {
            showErrorMessage("Количество экземпляров не может быть пустым.");
            return;
        }

        try {
            int count = Integer.parseInt(copiesInput);

            if (bookService.updateCopies(author, title, count)) {
                showSuccessMessage("Экземпляры удалены!");
                loadBooks(); // Обновляем список книг
            } else {
                showErrorMessage("Ошибка при удалении экземпляров!");
            }
        } catch (NumberFormatException ex) {
            showErrorMessage("Некорректный ввод! Введите число для количества экземпляров.");
        }
    }

    private void loadBooks() {
        new Thread(() -> {
            try {
                List<Book> books = bookService.getAllBooksOrderedByAuthor();
                SwingUtilities.invokeLater(() -> displayBooks(books));
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showErrorMessage("Ошибка при загрузке книг: " + e.getMessage()));
            }
        }).start();
    }

    private void displayBooks(List<Book> books) {
        StringBuilder sb = new StringBuilder();
        for (Book book : books) {
            sb.append(book.toString()).append("\n");
        }
        textArea.setText(sb.toString());
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b); // Вызов родительского метода
    }
}