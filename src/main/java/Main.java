import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Base64;
import java.util.Scanner;

public class Main {

    public void Main(){
    }
    public static void main(String[] args) throws Exception {
        Scanner scanner=new Scanner(System.in);
        System.out.println("who are you?");
        String name=scanner.nextLine();
        System.out.println("welcome");
        String input;
        URL url = new URL("http://127.0.0.1:8080/api/push/");


        Main main=new Main();
        while (true) {
            input=scanner.nextLine();
            if (input.equals("exit")) break;
            else if (input.equals("pull")) main.GETRequest();
            else main.sendPost(name,input.getBytes(),url);
        }
    }

    public void subscibe(){

    }

    public void GETRequest() throws IOException
    {
        String urlName = "http://127.0.0.1:8080/api/pull/?format=json";
        URL urlForGetReq = new URL(urlName);
        String read = null;
        HttpURLConnection connection = (HttpURLConnection) urlForGetReq.openConnection();
        connection.setRequestMethod("GET");


        int codeResponse = connection.getResponseCode();
        if (codeResponse == HttpURLConnection.HTTP_OK)
        {
            InputStreamReader isrObj = new InputStreamReader(connection.getInputStream());
            BufferedReader bf = new BufferedReader(isrObj);
            StringBuffer responseStr = new StringBuffer();
            while ((read = bf .readLine()) != null)
            {
                responseStr.append(read);
            }
            bf.close();
            connection.disconnect();
            System.out.println("JSON String Result is: \n" + responseStr.toString());
        }
        else
        {
            System.out.println("GET Request did not work");
        }
    }


    private void sendPost(String key,byte[] value,URL url) throws Exception {
        String jsonReq="{\n"+
                "\n\"key\":\"" +
                key +
                "\"" +
                ",\n\"value\":\"" +
                new String(value) +
                "\"" +
                "\n}";

        System.out.println(jsonReq);
        HttpURLConnection postCon = (HttpURLConnection) url.openConnection();
        postCon.setRequestMethod("POST");
        postCon.setRequestProperty("Content-Type", "application/json");
        postCon.setDoOutput(true);


        OutputStream osObj = postCon.getOutputStream();
        osObj.write(jsonReq.getBytes());

        osObj.flush();
        osObj.close();
        int respCode = postCon.getResponseCode();
       ;

        System.out.println("Response from the server is: \n");
        System.out.println("The POST Request Response Code :  " + respCode);
        System.out.println("The POST Request Response Message : " + postCon.getResponseMessage());
     }
    }