import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

/**
 * Класс WeatherApiClient для взаимодействия с API Яндекс.Погода.
 * Выполняет запросы к API и возвращает данные о погоде в виде JSON-строки.
 */
public class WeatherApiClient {
    // Поля для хранения API-ключа и базового URL
    private final String apiKey;
    private final String baseUrl;

    /**
     * Конструктор WeatherApiClient.
     * Загружает настройки из файла application.properties, включая API-ключ и базовый URL.
     */
    public WeatherApiClient() {
        // Создаем объект Properties для хранения конфигурационных данных
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("resources/application.properties")) {
            // Загружаем свойства из файла
            properties.load(input);
            // Читаем ключ API из файла конфигурации
            this.apiKey = properties.getProperty("X-Yandex-Weather-Key");
            // Читаем базовый URL API из файла конфигурации
            this.baseUrl = properties.getProperty("weather-api-url");
        } catch (IOException e) {
            // Обрабатываем ошибки при чтении файла
            throw new RuntimeException("Ошибка при загрузке свойств из файла application.properties", e);
        }
    }

    /**
     * Метод getWeatherData выполняет запрос к API Яндекс.Погода и возвращает данные в виде строки JSON.
     *
     * @param lat Широта места, для которого запрашивается погода.
     * @param lon Долгота места, для которого запрашивается погода.
     * @return JSON-строка с данными о погоде.
     */
    public String getWeatherData(double lat, double lon) {
        // Формируем URL для запроса, включая координаты широты и долготы
        String url = String.format("%s?lat=%s&lon=%s", baseUrl, lat, lon);

        // Создаем экземпляр HttpClient для выполнения HTTP-запросов
        HttpClient client = HttpClient.newHttpClient();

        // Создаем HTTP-запрос с использованием HttpRequest.Builder
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // Устанавливаем URI запроса, создавая его на основе строки URL
                .header("X-Yandex-Weather-Key", apiKey) // Добавляем заголовок с ключом API для авторизации
                .GET() // Указываем, что запрос является GET-запросом
                .build(); // Строим объект запроса

        try {
            // Отправляем запрос и получаем ответ в виде строки
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Проверяем, был ли запрос успешным (статус-код 200)
            if (response.statusCode() == 200) {
                // Если запрос успешен, возвращаем тело ответа (JSON строку)
                return response.body();
            } else {
                // Если запрос не успешен, выбрасываем исключение с указанием статус-кода ошибки
                throw new RuntimeException("Ошибка при запросе к API: код ответа " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            // Обрабатываем исключения, связанные с ошибками ввода-вывода или прерыванием потока
            throw new RuntimeException("Ошибка при выполнении HTTP-запроса", e);
        }
    }
}
