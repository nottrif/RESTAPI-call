import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;


public class RESTAPI {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        Transcipt transcript = new Transcipt();
        transcript.setAudio_url("https://bit.ly/3yxKEIY");

        Gson gson = new Gson();
        String jsonBody = gson.toJson(transcript);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", "fbc0930b9ce2475f8c43fb5a69456261")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();   //the post/create request is built

        HttpClient httpClient = HttpClient.newHttpClient(); //HttpClient is used to send requests and retrieve responses

        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());

        transcript = gson.fromJson(postResponse.body(), Transcipt.class);
        System.out.println(transcript.getID());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getID()))
                .header("Authorization", "fbc0930b9ce2475f8c43fb5a69456261")
                .GET().build();

        do {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcipt.class);
            System.out.println(getResponse.body());

            if(transcript.equals("error")) {
               break;
            }
            Thread.sleep(3000);
        } while (transcript.getStatus().equals("processing"));

        System.out.println("Transcription completed");
        System.out.println(transcript.getText());
    }
}

