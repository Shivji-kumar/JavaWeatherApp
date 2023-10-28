package MyPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("index.html");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String inputData = request.getParameter("userInput");

		// Api Setup
		String apiKey = "967bcef71649b1db57004b601a79d0b1";
		String city = request.getParameter("city");
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// Reading the data from network
			InputStream inputStream = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);

			StringBuilder responseContent = new StringBuilder();

			// for input from the reader will create scanner object
			Scanner scanner = new Scanner(reader);
			while (scanner.hasNext()) {
				responseContent.append(scanner.nextLine());
			}
			scanner.close();
			// Typecasting the data into JSON
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

			Long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
			String date = new Date(dateTimestamp).toString();

			double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int temperatureCelsius = (int) (temperatureKelvin - 273.15);

			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main")
					.getAsString();

			request.setAttribute("date", date);
			request.setAttribute("city", city);
			request.setAttribute("temperature", temperatureCelsius);
			request.setAttribute("weatherCondition", weatherCondition);
			request.setAttribute("humidity", humidity);
			request.setAttribute("windSpeed", windSpeed);
			request.setAttribute("weatherDate", responseContent.toString());

			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
