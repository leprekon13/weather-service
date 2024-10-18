import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Инициализируем экземпляр WeatherService для взаимодействия с сервисом погоды
        WeatherService weatherService = new WeatherService();
        Scanner scanner = new Scanner(System.in);

        // Объявляем переменные lat и lon вне блока try, чтобы они были доступны в дальнейшем коде
        double lat = 0.0;
        double lon = 0.0;

        try {
            // Получение координат от пользователя
            System.out.println("Введите широту (можно использовать запятую или точку для дробной части): ");
            String latInput = scanner.next();
            lat = Double.parseDouble(latInput.replace(",", "."));

            System.out.println("Введите долготу (можно использовать запятую или точку для дробной части): ");
            String lonInput = scanner.next();
            lon = Double.parseDouble(lonInput.replace(",", "."));

        } catch (NumberFormatException e) {
            System.err.println("Ошибка: введите корректное числовое значение.");
            return;  // Завершаем выполнение программы при некорректном вводе
        }

        try {
            // Вывод всех данных о погоде
            System.out.println("\nВсе данные о погоде для указанных координат:");
            String allWeatherData = weatherService.getAllWeatherData(lat, lon);
            System.out.println(allWeatherData);

            // Получаем текущую температуру по заданным координатам
            double currentTemp = weatherService.getCurrentTemperature(lat, lon);
            System.out.println("Текущая температура в Москве: " + currentTemp + "°C");

            // Задаем количество дней для расчета средней температуры (например, последние 3 дня)
            int limit = 3;
            double averageTemp = weatherService.getAverageTemperature(lat, lon, limit);
            System.out.println("Средняя температура в Москве за последние " + limit + " дня(ей): " + averageTemp + "°C");
        } catch (RuntimeException e) {
            // Обработка ошибок, возникающих при взаимодействии с API или парсинге JSON
            System.err.println("Ошибка при получении данных о погоде: " + e.getMessage());
        }
    }
}
