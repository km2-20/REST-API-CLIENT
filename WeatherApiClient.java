import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.Scanner;

/**
 * WeatherApiClient.java
 * Fetches and displays weather data using a public REST API (OpenWeatherMap).
 * Handles user input, HTTP requests, and parses JSON responses with user-defined output.
 */
public class WeatherApiClient {

    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Replace with your OpenWeatherMap API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Accept city input from the user
        System.out.print("Enter city name: ");
        String city = scanner.nextLine().trim();

        // Validate city input
        if (city.isEmpty()) {
            System.out.println("City name cannot be empty. Exiting...");
            return;
        }

        String url = String.format(BASE_URL, city, API_KEY);

        try {
            // Fetch data from the API
            String response = sendGET(url);
            // Parse the weather data
            JSONObject weatherData = new JSONObject(response);

            // If the city is not found, print an error message
            if (weatherData.has("message") && weatherData.getString("message").equals("city not found")) {
                System.out.println("City not found. Please check the city name and try again.");
                return;
            }

            // Display the menu for user to choose output format
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            // Display weather based on user choice
            switch (choice) {
                case 1:
                    displayTemperature(weatherData);
                    break;
                case 2:
                    displayCompleteReport(weatherData);
                    break;
                case 3:
                    displayAdditionalInfo(weatherData);
                    break;
                default:
                    System.out.println("Invalid choice. Exiting...");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    /**
     * Sends an HTTP GET request to the given URL.
     * @param urlStr the URL to request
     * @return the response body as a String
     * @throws Exception if an error occurs during the HTTP request
     */
    public static String sendGET(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000); // Set connection timeout to 5 seconds
        conn.setReadTimeout(5000); // Set read timeout to 5 seconds

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP GET Request Failed with Error code: " + responseCode);
        }

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * Displays the menu for the user to choose the output format.
     */
    public static void displayMenu() {
        System.out.println("\nChoose the type of weather report to display:");
        System.out.println("1. Display Temperature");
        System.out.println("2. Display Complete Report");
        System.out.println("3. Display Additional Info (Wind, Pressure)");
    }

    /**
     * Displays only the temperature.
     * @param weatherData JSON weather data
     */
    public static void displayTemperature(JSONObject weatherData) {
        JSONObject main = weatherData.getJSONObject("main");
        double temp = main.getDouble("temp");
        System.out.println("\nTemperature: " + temp + "°C");
    }

    /**
     * Displays the complete weather report.
     * @param weatherData JSON weather data
     */
    public static void displayCompleteReport(JSONObject weatherData) {
        String city = weatherData.getString("name");
        JSONObject main = weatherData.getJSONObject("main");
        double temp = main.getDouble("temp");
        int humidity = main.getInt("humidity");
        JSONObject weather = weatherData.getJSONArray("weather").getJSONObject(0);
        String description = weather.getString("description");

        System.out.println("\n==== Weather Report ====");
        System.out.println("City: " + city);
        System.out.println("Temperature: " + temp + "°C");
        System.out.println("Humidity: " + humidity + "%");
        System.out.println("Weather: " + description);
        System.out.println("=========================");
    }

    /**
     * Displays additional information like wind and pressure.
     * @param weatherData JSON weather data
     */
    public static void displayAdditionalInfo(JSONObject weatherData) {
        JSONObject main = weatherData.getJSONObject("main");
        double pressure = main.getDouble("pressure");
        JSONObject wind = weatherData.getJSONObject("wind");
        double windSpeed = wind.getDouble("speed");

        System.out.println("\n==== Additional Info ====");
        System.out.println("Pressure: " + pressure + " hPa");
        System.out.println("Wind Speed: " + windSpeed + " m/s");
        System.out.println("=========================");
    }
}
