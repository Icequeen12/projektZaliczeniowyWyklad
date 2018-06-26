package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class KotkiController implements Initializable {


    private String accessToken;

    @FXML
    private Label lGlosy;
    @FXML
    private Label lImie;
    @FXML
    private Label txtImie;
    @FXML
    private Label txtGlosy;
    @FXML
    private Label blad;
    @FXML
    private ImageView obrazek;
    @FXML
    private Button next;

    private int licznikKotkow = 1;
    private JSONArray kotki;
    private String page = "";


    public void strona(String nrPage) throws IOException {
        URL url = new URL("http://smieszne-koty.herokuapp.com/api/kittens?access_token=" + accessToken + nrPage);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(30000);

        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();

        String inputLine;
        while ((inputLine = input.readLine()) != null)
            response.append(inputLine);
        input.close();

        kotki = new JSONArray(response.toString());

        kolejnyKotek();
    }

    public void kolejnyKotek() {


        try {
            if (licznikKotkow == 25 && page.equals("")) {
                licznikKotkow = 0;
                next.setDisable(true);
                page = "&page=2";
                strona(page);
                next.setDisable(false);
            } else if (licznikKotkow == 25 && page.equals("&page=2")) {
                licznikKotkow = 0;
                next.setDisable(true);
                page = "";
                strona(page);
                next.setDisable(false);
            } else {

                JSONObject kotek = kotki.getJSONObject(licznikKotkow);
                licznikKotkow = licznikKotkow + 1;

                txtImie.setText(kotek.getString("name"));
                txtGlosy.setText(String.valueOf(kotek.getInt("vote_count")));
                Image image = new Image(kotek.getString("url"));
                obrazek.setImage(image);
            }
        } catch (IOException e) {
            blad.setVisible(true);
        }
    }


    public void pierwszykotecek(JSONObject json) {

        lImie.setVisible(true);
        lGlosy.setVisible(true);
        txtGlosy.setVisible(true);
        txtImie.setVisible(true);
        next.setVisible(true);


        txtImie.setText(json.getString("imie"));
        txtGlosy.setText(String.valueOf(json.getInt("glosy")));
        Image image = new Image(json.getString("url"));
        obrazek.setImage(image);
        accessToken = json.getString("token");

        try {
            strona(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
}