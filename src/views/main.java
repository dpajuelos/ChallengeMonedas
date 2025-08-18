package views;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Conversor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== Conversor de Monedas ===");
            System.out.println("1. Dólar a Peso Argentino");
            System.out.println("2. Peso Argentino a Dólar");
            System.out.println("3. Dólar a Real Brasileño");
            System.out.println("4. Real Brasileño a Dólar");
            System.out.println("5. Dólar a Peso Colombiano");
            System.out.println("6. Peso Colombiano a Dólar");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            if (opcion.equals("7")) {
                System.out.println("¡Gracias por usar el conversor!");
                continuar = false;
                continue;
            }

            System.out.print("Ingrese la cantidad a convertir: ");
            double cantidad = Double.parseDouble(scanner.nextLine());

            String resultado = realizarConversion(opcion, cantidad);
            System.out.println(resultado);
        }
        scanner.close();
    }

   private static String realizarConversion(String opcion, double cantidad)
           throws IOException, InterruptedException {
       String baseUrl = "https://api.currencyfreaks.com/v2.0/rates/latest?apikey=817b392e6b9147348ed9685de20e31b2";
       String monedaOrigen, monedaDestino, simbolos;
       boolean esConversionInversa = false;

       switch (opcion) {
           case "1" -> { // USD a ARS
               monedaOrigen = "USD"; monedaDestino = "ARS";
               simbolos = "&symbols=ARS";
           }
           case "2" -> { // ARS a USD (inversa)
               monedaOrigen = "ARS"; monedaDestino = "USD";
               simbolos = "&symbols=ARS";
               esConversionInversa = true;
           }
           case "3" -> { // USD a BRL
               monedaOrigen = "USD"; monedaDestino = "BRL";
               simbolos = "&symbols=BRL";
           }
           case "4" -> { // BRL a USD (inversa)
               monedaOrigen = "BRL"; monedaDestino = "USD";
               simbolos = "&symbols=BRL";
               esConversionInversa = true;
           }
           case "5" -> { // USD a COP
               monedaOrigen = "USD"; monedaDestino = "COP";
               simbolos = "&symbols=COP";
           }
           case "6" -> { // COP a USD (inversa)
               monedaOrigen = "COP"; monedaDestino = "USD";
               simbolos = "&symbols=COP";
               esConversionInversa = true;
           }
           default -> {
               return "Opción no válida";
           }
       }

       HttpClient client = HttpClient.newHttpClient();
       HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create(baseUrl + simbolos))
               .build();

       HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

       Gson gson = new GsonBuilder()
               .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
               .create();

       Conversor conversor = gson.fromJson(response.body(), Conversor.class);
       String tasaCambio = conversor.rates().values().iterator().next();
       double tasa = Double.parseDouble(tasaCambio);

       double resultado;
       if (esConversionInversa) {
           // Para convertir de otra moneda a USD, dividimos entre la tasa
           resultado = cantidad / tasa;
       } else {
           // Para convertir de USD a otra moneda, multiplicamos por la tasa
           resultado = cantidad * tasa;
       }

       return String.format("%.2f %s = %.2f %s", cantidad, monedaOrigen, resultado, monedaDestino);
   }
}