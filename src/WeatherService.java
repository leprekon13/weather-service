import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

/**
 * Класс WeatherService обрабатывает данные, полученные от WeatherApiClient.
 * Извлекает фактическую температуру и вычисляет среднюю температуру за период.
 */
public class WeatherService {
    private final WeatherApiClient weatherApiClient;
    private final ObjectMapper objectMapper;

    /**
     * Конструктор WeatherService.
     * Инициализирует WeatherApiClient и ObjectMapper для обработки JSON данных.
     */
    public WeatherService() {
        this.weatherApiClient = new WeatherApiClient();
        this.objectMapper = new ObjectMapper();
    }

    // Метод для получения всей информации о погоде в виде отформатированной строки JSON
    public String getAllWeatherData(double lat, double lon) {
        try {
            String jsonResponse = weatherApiClient.getWeatherData(lat, lon);
            // Преобразуем JSON-строку в форматированный вид
            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return writer.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке JSON-ответа", e);
        }
    }

    /**
     * Метод для получения фактической температуры в заданной точке по координатам.
     *
     * @param lat Широта места, для которого запрашивается погода.
     * @param lon Долгота места, для которого запрашивается погода.
     * @return Фактическая температура в градусах Цельсия.
     * @throws RuntimeException если возникает ошибка при обработке JSON данных.
     */
    public double getCurrentTemperature(double lat, double lon) {
        try {
            // Получаем данные о погоде в формате JSON от WeatherApiClient
            String jsonResponse = weatherApiClient.getWeatherData(lat, lon);

            // Читаем JSON и находим фактическую температуру в узле "fact"
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode factNode = rootNode.path("fact");
            double temperature = factNode.path("temp").asDouble();

            return temperature;
        } catch (IOException e) {
            // Обрабатываем ошибки при чтении JSON
            throw new RuntimeException("Ошибка при обработке JSON-ответа", e);
        }
    }

    /**
     * Метод для вычисления средней температуры за указанный период.
     *
     * @param lat   Широта места, для которого запрашивается погода.
     * @param lon   Долгота места, для которого запрашивается погода.
     * @param limit Количество дней для расчета средней температуры.
     * @return Средняя температура за указанный период.
     * @throws RuntimeException если возникает ошибка при обработке JSON данных.
     */
    public double getAverageTemperature(double lat, double lon, int limit) {
        try {
            // Получаем данные о погоде в формате JSON от WeatherApiClient
            String jsonResponse = weatherApiClient.getWeatherData(lat, lon);

            // Читаем JSON и находим узел "forecasts"
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode forecastsNode = rootNode.path("forecasts");

            // Проверяем, есть ли данные для запрошенного количества дней
            if (forecastsNode.isArray() && forecastsNode.size() >= limit) {
                double totalTemperature = 0.0;

                // Итерируем по первым 'limit' записям и суммируем температуру
                for (int i = 0; i < limit; i++) {
                    JsonNode dayNode = forecastsNode.get(i).path("parts").path("day");
                    double dayTemp = dayNode.path("temp_avg").asDouble();
                    totalTemperature += dayTemp;
                }

                // Возвращаем среднее арифметическое температуры
                return totalTemperature / limit;
            } else {
                throw new RuntimeException("Недостаточно данных для расчета средней температуры.");
            }
        } catch (IOException e) {
            // Обрабатываем ошибки при чтении JSON
            throw new RuntimeException("Ошибка при обработке JSON-ответа", e);
        }
    }
}
