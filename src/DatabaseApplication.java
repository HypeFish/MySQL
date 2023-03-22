import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseApplication {
    private final String user = "root";
    private final String password = "skyehigh";
    private final String server = "localhost";
    private final int port = 3306;
    private final String database = "librarytorals";

    /**
     * Constructor for class DatabaseApplication
     */
    public DatabaseApplication() {
    }

    /**
     * Creates a connection to the MySQL database with the given username and password
     *
     * @param root     The string for the username
     * @param password The string for the password
     * @return The connection to the MySQL database
     * @throws SQLException When the database username or password is incorrect
     */
    private Connection connection(String root, String password) throws SQLException {
        Connection conn;
        Properties connectionProps = new Properties();
        connectionProps.put("user", root);
        connectionProps.put("password", password);
        conn = DriverManager.getConnection("jdbc:mysql://"
                        + this.server + ":" + this.port + "/" +
                        this.database + "?characterEncoding=UTF-8&useSSL=false",
                connectionProps);
        return conn;
    }

    /**
     * Gets the genre of the book from the user
     *
     * @return String of the genre of the book
     */
    private String getGenre() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the genre of the book: \n");
        return scanner.next();
    }

    /**
     * Using the stored procedure, list the genres of the books in the database and validate the user's input
     */
    private ArrayList<String> validateGenre(Connection conn) {
        ResultSet rs;
        ArrayList<String> genres = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT name FROM GENRE");
            while (rs.next()) {
                genres.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        for (String genre : genres) {
            System.out.println(genre);
        }
        return genres;
    }

    /**
     * Using the stored procedure, list the books that match the given genre
     *
     * @param conn  The connection to the database
     * @param genre The genre of the book
     */
    public void matchBooks(Connection conn, String genre) {
        ResultSet rs;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("CALL book_has_genre('" + genre + "')");
            while (rs.next()) {
                System.out.println(rs.getString("isbn_var")
                        + ", " + rs.getString("author_var") +
                        ", " + rs.getString("page_var") +
                        ",  " + rs.getString("publisher_var"));
            }
            while (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                while (rs.next()) {
                    System.out.println(rs.getString("isbn_var")
                            + ", " + rs.getString("author_var") +
                            ", " + rs.getString("page_var") +
                            ", " + rs.getString("publisher_var"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void startApp() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Username: \n");
        String root = scanner.next();
        System.out.println("Enter your Password: \n");
        String pass = scanner.next();

        try (Connection ignored = this.connection(root, pass)) {
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println("Incorrect username or password. Try again.");
        }
    }

    /**
     * Runs the database application
     */
    private void run() {
        try {
            this.startApp();
            Connection conn = this.connection(this.user, this.password);
            ArrayList<String> genres = validateGenre(conn);
            String genre = getGenre();
            while (!genres.contains(genre)) {
                System.out.println("Invalid genre. Try again.");
                genre = getGenre();
            }
            this.matchBooks(conn, genre);
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        DatabaseApplication app = new DatabaseApplication();
        app.run();
    }
}
