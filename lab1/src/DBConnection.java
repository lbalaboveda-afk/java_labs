import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public final class DBConnection {

    private static final String PROPERTIES_FILE = "database.properties";

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Не удалось загрузить файл настроек БД: " + PROPERTIES_FILE +
                            ". Убедитесь, что он лежит в корне проекта.", e);
        }

        String drivers = props.getProperty("jdbc.drivers");
        if (drivers != null) {
            System.setProperty("jdbc.drivers", drivers);
        }

        URL      = props.getProperty("jdbc.url");
        USERNAME = props.getProperty("jdbc.username");
        PASSWORD = props.getProperty("jdbc.password");

    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}