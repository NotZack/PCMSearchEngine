package Indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {

    public static String readConfigBasePath() {

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/config.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
