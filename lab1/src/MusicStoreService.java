import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MusicStoreService {

    private static final int MIN_DURATION_THRESHOLD = 5;

    public MusicStoreService() {
    }

    public int addArtist(String name) throws SQLException {
        final String sql = "INSERT INTO Artist (artist_name) VALUES (?) RETURNING artist_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("artist_id");
                }
            }
            return -1;
        }
    }


    public String updateArtist(int artistId, String newArtistName) throws SQLException {
        final String sql = "UPDATE Artist SET artist_name = ? WHERE artist_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newArtistName);
            ps.setInt(2, artistId);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                return "Артист с id = " + artistId + " переименован в \"" + newArtistName + "\".";
            }
            return "Артиста с id = " + artistId + " не существует.";
        }
    }

    public String removeArtist(int artistId) throws SQLException {
        final String sql = "DELETE FROM Artist WHERE artist_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            int rows = ps.executeUpdate();

            if (rows == 1) {
                return "Артист с id = " + artistId + " удалён.";
            }
            return "Артиста с id = " + artistId + " не существует.";
        }
    }

    public List<String> getAllArtists() throws SQLException {
        final String sql = "SELECT artist_id, artist_name FROM Artist ORDER BY artist_id";
        List<String> result = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                result.add(String.format("%d | %s",
                        rs.getInt("artist_id"),
                        rs.getString("artist_name")));
            }
        }
        return result;
    }


    public List<String> getAlbumNameAndShortestTrack() throws SQLException {
        final String sql = """
            SELECT a.album_name,
                   c.composition_name,
                   c.duration
            FROM (
                SELECT album_id, MIN(duration) AS min_duration
                FROM Composition
                GROUP BY album_id
                HAVING MIN(duration) > ?
            ) AS aMin
            JOIN Album a       ON a.album_id = aMin.album_id
            JOIN Composition c ON c.album_id = aMin.album_id
                              AND c.duration = aMin.min_duration
            ORDER BY a.album_name
            """;

        List<String> result = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, MIN_DURATION_THRESHOLD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(String.format("Альбом: %-20s | Композиция: %-25s | Длительность: %s сек.",
                            rs.getString("album_name"),
                            rs.getString("composition_name"),
                            rs.getString("duration")));
                }
            }
        }
        return result;
    }


    public List<String> getDbTablesNames() throws SQLException {
        final List<String> tables = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet rs = meta.getTables(null, "public", null,
                    new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        }
        return tables;
    }

    public List<String> getColumnsForTableName(String tableName) throws SQLException {
        final List<String> columns = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet rs = meta.getColumns(null, "public",
                    tableName.toLowerCase(), null)) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }
        return columns;
    }
}