public class Main {
    public static void main(String[] args) {
        // Инициализируем экземпляр WeatherService для взаимодействия с сервисом погоды
        WeatherService weatherService = new WeatherService();

        // Задаем координаты для запроса (координаты Москвы)
        double lat = 55.7558; // Широта Москвы
        double lon = 37.6176; // Долгота Москвы

        try {
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
