package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private PasswordField txtHaslo;
    @FXML
    private TextField txtEmail;
    @FXML
    private Label blad;

    public void zaloguj(ActionEvent event)  {

        String email = txtEmail.getText();
        String haslo=txtHaslo.getText();
        URL url = null;
        try {
            url = new URL("http://smieszne-koty.herokuapp.com/oauth/token?grant_type=password&email="+email+"&password="+haslo);


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(30000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = input.readLine()) != null)
                response.append(inputLine);
            input.close();

            JSONObject autoryzacja = new JSONObject(response.toString());
            String token = autoryzacja.getString("access_token");
            url = new URL("http://smieszne-koty.herokuapp.com/api/kittens?access_token=" + token);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(30000);

            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            response = new StringBuilder();


            while ((inputLine = input.readLine()) != null)
                response.append(inputLine);
            input.close();


            JSONObject json = new JSONObject();

            JSONArray kotki = new JSONArray(response.toString());
            JSONObject kotek = kotki.getJSONObject(0);

            json.put("imie", kotek.getString("name"));
            json.put("glosy",kotek.getInt("vote_count"));
            json.put("url",kotek.getString("url"));
            json.put("token",token);

            Stage stageTheEventSourceNodeBelongs = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kotek.fxml"));
            stageTheEventSourceNodeBelongs.setScene(new Scene((Parent) loader.load()));
            loader.<KotkiController>getController().pierwszykotecek(json);

        } catch (MalformedURLException e) {
            blad.setVisible(true);
        } catch (ProtocolException e) {
            blad.setVisible(true);
        } catch (IOException e) {
            blad.setVisible(true);
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
