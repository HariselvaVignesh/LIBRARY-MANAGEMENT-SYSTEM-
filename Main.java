import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Main {
    private JFrame frame;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField searchField;
    private JTextField deleteField;
    private JTextField updateIdField;
    private JTextField updateTitleField;
    private JTextField updateAuthorField;
    private JTextField updateIsbnField;
    private JButton addButton;
    private JButton viewButton;
    private JButton searchButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    // Database URL and credentials
    private static final String DB_URL;
    private static final String USER;
    private static final String PASS;

    static {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database properties");
        }

        // Initialize database credentials
        DB_URL = properties.getProperty("db.url");
        USER = properties.getProperty("db.user");
        PASS = properties.getProperty("db.pass");
    }
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    public Main() {
        // Initialize the GUI components
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Adjusted size for better view
        frame.setLayout(new BorderLayout());

        // Create the background panel for login with the image
        BackgroundPanel loginBackgroundPanel = new BackgroundPanel();
        loginBackgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Login and Signup panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginBackgroundPanel.add(loginPanel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK); // Set label text color
        loginPanel.add(usernameLabel);
        usernameField = new JTextField(20);
        loginPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK); // Set label text color
        loginPanel.add(passwordLabel);
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginPanel.add(loginButton);
        signupButton = new JButton("Signup");
        loginPanel.add(signupButton);

        // Add the login background panel to the frame
        frame.add(loginBackgroundPanel);
        frame.setVisible(true);

        // Button actions
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both username and password.");
            return; // Prevent further execution
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Login successful, display main features
                displayMainFeatures();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error logging in.");
        }
    }

    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both username and password.");
            return; // Prevent further execution
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Signup successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error signing up. Username might already exist.");
        }
    }

    private void displayMainFeatures() {
        // Create main features panel with background
        BackgroundPanel mainBackgroundPanel = new BackgroundPanel();
        mainBackgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Title Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth=2;
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(Color.white); // Set label text color
        mainBackgroundPanel.add(titleLabel, gbc);
        titleField = new JTextField(20);
        gbc.gridx = 1;
        mainBackgroundPanel.add(titleField, gbc);

        // Author Input
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth=2;
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(authorLabel, gbc);
        authorField = new JTextField(20);
        gbc.gridx = 1;
        mainBackgroundPanel.add(authorField, gbc);

        // ISBN Input
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth=2;
        JLabel isbnLabel = new JLabel("ISBN:");
        isbnLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(isbnLabel, gbc);
        isbnField = new JTextField(20);
        gbc.gridx = 1;
        mainBackgroundPanel.add(isbnField, gbc);

        // Buttons Row for Add and View Book
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Book");
        viewButton = new JButton("View Books");
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the button panel
        mainBackgroundPanel.add(buttonPanel, gbc);

        // Search Input
        gbc.gridwidth = 2; // Reset to default
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel searchLabel = new JLabel("Search by Title:");
        searchLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(searchLabel, gbc);
        searchField = new JTextField(20);
        gbc.gridx = 1;
        mainBackgroundPanel.add(searchField, gbc);

        // Center Search Button
        searchButton = new JButton("Search");
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        mainBackgroundPanel.add(searchButton, gbc);

        // Delete Input
        gbc.gridwidth = 2; // Reset to default
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel deleteLabel = new JLabel("Delete Book ID:");
        deleteLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(deleteLabel, gbc);
        deleteField = new JTextField(5);
        gbc.gridx = 1;
        mainBackgroundPanel.add(deleteField, gbc);

        // Center Delete Button
        deleteButton = new JButton("Delete Book");
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        mainBackgroundPanel.add(deleteButton, gbc);

        // Update Input
        gbc.gridwidth = 2; // Reset to default
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel updateIdLabel = new JLabel("Update Book ID:");
        updateIdLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(updateIdLabel, gbc);
        updateIdField = new JTextField(5);
        gbc.gridx = 1;
        mainBackgroundPanel.add(updateIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        JLabel updateTitleLabel = new JLabel("New Title:");
        updateTitleLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(updateTitleLabel, gbc);
        updateTitleField = new JTextField(20);
        gbc.gridx = 1;
        mainBackgroundPanel.add(updateTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        JLabel updateAuthorLabel = new JLabel("New Author:");
        updateAuthorLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(updateAuthorLabel, gbc);
        updateAuthorField = new JTextField(20);
        gbc.gridx = 1;
        mainBackgroundPanel.add(updateAuthorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        JLabel updateIsbnLabel = new JLabel("New ISBN:");
        updateIsbnLabel.setForeground(Color.WHITE); // Set label text color
        mainBackgroundPanel.add(updateIsbnLabel, gbc);
        updateIsbnField = new JTextField(20);
        gbc.gridx = 1;
        mainBackgroundPanel.add(updateIsbnField, gbc);

        // Center Update Button
        updateButton = new JButton("Update Book");
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        mainBackgroundPanel.add(updateButton, gbc);

        // Table for displaying books
        String[] columnNames = {"ID", "Title", "Author", "ISBN"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        gbc.gridy = 13;
        gbc.gridwidth = 2; // Span across two columns
        mainBackgroundPanel.add(scrollPane, gbc);

        // Add main features background panel to the frame
        frame.setContentPane(mainBackgroundPanel);
        frame.revalidate();
        frame.repaint();

        // Add button actions for book management
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBooks();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBooks();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook();
            }
        });
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            return; // Prevent further execution
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)")) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding book.");
        }
    }

    private void viewBooks() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error retrieving books.");
        }
    }

    private void searchBooks() {
        String title = searchField.getText();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a title to search.");
            return; // Prevent further execution
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ?")) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error searching for books.");
        }
    }

    private void deleteBook() {
        String id = deleteField.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter an ID to delete.");
            return; // Prevent further execution
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
            pstmt.setInt(1, Integer.parseInt(id));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(frame, "Book deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "No book found with that ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting book.");
        }
    }

    private void updateBook() {
        String id = updateIdField.getText();
        String title = updateTitleField.getText();
        String author = updateAuthorField.getText();
        String isbn = updateIsbnField.getText();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            return; // Prevent further execution
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE books SET title = ?, author = ?, isbn = ? WHERE id = ?")) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            pstmt.setInt(4, Integer.parseInt(id));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(frame, "Book updated successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "No book found with that ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating book.");
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}

// BackgroundPanel class remains unchanged
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        try {
            backgroundImage = new ImageIcon("C:\\Users\\Selvam\\Downloads\\Gemini_Generated_Image_5n7fht5n7fht5n7f.jpeg").getImage(); // Set your image path
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
