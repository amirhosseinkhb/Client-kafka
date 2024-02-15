import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;



class Message{
private byte[] value;
private String key;

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }
}


public class Main {

    Consumer<String> f = message -> System.out.println("Received message: " + message);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    String name;
    public void SetName(String name){
        this.name=name;
    }



    public static void main(String[] args) throws Exception {
        Scanner scanner=new Scanner(System.in);
        System.out.println("who are you?");
        String name=scanner.nextLine();
        System.out.println("welcome");
        String input;
        URL url = new URL("http://127.0.0.1:8000/api/push/");


        Main main=new Main();
        main.SetName(name);

        while (true) {
            input=scanner.nextLine();
            if (input.equals("exit")) break;
            else if (input.equals("pull")) {
                main.pull();
            }
            else if (input.equals("subscribe")) {
                main.subscribe(main.f);
            }
            else main.push(name,input.getBytes(),url);
        }
    }



    public void subscribe(Consumer<String> f) {
        executor.submit(() -> {
                         Message message = null;
                    try {
                        String urlName = "http://127.0.0.1:8000/api/subscribe/?format=json";
                        URL urlForGetReq = new URL(urlName);
                        String read = null;
                        HttpURLConnection connection = (HttpURLConnection) urlForGetReq.openConnection();
                        connection.setRequestMethod("GET");

                        int codeResponse = connection.getResponseCode();

                        if (codeResponse == HttpURLConnection.HTTP_OK)
                        {
                            InputStreamReader isrObj = new InputStreamReader(connection.getInputStream());
                            BufferedReader bf = new BufferedReader(isrObj);
                            StringBuilder responseStr = new StringBuilder();

                            while ((read = bf .readLine()) != null)
                            {
                                responseStr.append(read);
                            }
                            bf.close();
                            connection.disconnect();
                            System.out.println("ine:"+responseStr);
                            message=JsonToMessage(responseStr.toString());
                            System.out.println("message pull: "+new String(message.getValue()));
                    }} catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    if (message != null) {
                    f.accept(new String(message.getValue()));
                }
           }
        );
    }




    public Message pull() throws IOException, JSONException {
        String urlName = "http://127.0.0.1:8000/api/pull/?format=json";
        URL urlForGetReq = new URL(urlName);
        String read = null;
        HttpURLConnection connection = (HttpURLConnection) urlForGetReq.openConnection();
        connection.setRequestMethod("GET");


        int codeResponse = connection.getResponseCode();
        if (codeResponse == HttpURLConnection.HTTP_OK)
        {
            InputStreamReader isrObj = new InputStreamReader(connection.getInputStream());
            BufferedReader bf = new BufferedReader(isrObj);
            StringBuilder responseStr = new StringBuilder();
            while ((read = bf .readLine()) != null)
            {
                responseStr.append(read);
            }
            bf.close();
            connection.disconnect();
            Message message=JsonToMessage(responseStr.toString());
            System.out.println("message pull: "+new String(message.getValue()));
            return message;
            //System.out.println("JSON String Result is: \n" + responseStr.toString());
//           return new Message(responseStr.getChars;);
        }
        else
        {
            System.out.println("GET Request did not work");
            return null;
        }
    }


    private void push(String key,byte[] value,URL url) throws Exception {
        String jsonReq="{\n"+
                "\n\"key\":\"" +
                key +
                "\"" +
                ",\n\"value\":\"" +
                new String(value) +
                "\"" +
                "\n}";

        HttpURLConnection postCon = (HttpURLConnection) url.openConnection();
        postCon.setRequestMethod("POST");
        postCon.setRequestProperty("Content-Type", "application/json");
        postCon.setDoOutput(true);


        OutputStream osObj = postCon.getOutputStream();
        osObj.write(jsonReq.getBytes());

        osObj.flush();
        osObj.close();
        int respCode = postCon.getResponseCode();


        System.out.println("Response from the server is:");
        System.out.println("The POST Request Response Code :  " + respCode);
        System.out.println("The POST Request Response Message : " + postCon.getResponseMessage());

    }

    public Message JsonToMessage(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        String key = jsonObject.getString("key");
        String value = jsonObject.getString("value");
        Message message=new Message();
        message.setKey(key);
        message.setValue(value.getBytes());
        return message;
    }
    }

