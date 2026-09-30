import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        MusicStoreService service = new MusicStoreService();

        try {
            System.out.println("Таблицы в БД:");
            System.out.println(service.getDbTablesNames());
            System.out.println();

            System.out.println("Столбцы таблицы Album:");
            System.out.println(service.getColumnsForTableName("Album"));
            System.out.println();

            System.out.println("Запрос: альбом + самая короткая композиция (> 5 сек.):");
            for (String line : service.getAlbumNameAndShortestTrack()) {
                System.out.println(line);
            }
            System.out.println();

            System.out.println("Список исполнителей (до изменений):");
            for (String line : service.getAllArtists()) {
                System.out.println(line);
            }
            System.out.println();

            System.out.println("Добавление исполнителя:");
            int newArtistId = service.addArtist("Клод Дебюсси");
            if (newArtistId > 0) {
                System.out.println("Артист \"Клод Дебюсси\" добавлен (id = " + newArtistId + ").");
            } else {
                System.out.println("Не удалось добавить артиста.");
            }
            for (String line : service.getAllArtists()) {
                System.out.println(line);
            }
            System.out.println();

            if (newArtistId > 0) {
                System.out.println("Обновление исполнителя:");
                System.out.println(service.updateArtist(newArtistId, "Клод Ашиль Дебюсси"));
                for (String line : service.getAllArtists()) {
                    System.out.println(line);
                }
                System.out.println();

                System.out.println("Удаление исполнителя:");
                System.out.println(service.removeArtist(newArtistId));
                for (String line : service.getAllArtists()) {
                    System.out.println(line);
                }
                System.out.println();
            } else {
                System.out.println("Пропускаем обновление и удаление: артист не был добавлен.");
            }

        } catch (SQLException e) {
            System.err.println("Критическая ошибка при работе с БД: " + e.getMessage());
            e.printStackTrace();
        }
    }
}