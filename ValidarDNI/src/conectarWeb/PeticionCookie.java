/*
 * Autor : Cmop
 */
package conectarWeb;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URISyntaxException;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 *
 * @author CMOP
 */
public class PeticionCookie {

    public Image peticionConCookieImagen(String urlp, String metodo, String parametros, List<HttpCookie> cookies) throws Exception {
        System.setProperty("http.agent", BusquedaCookie.USER_AGENT_FIREFOX);
        try {
            CookieManager manager = new CookieManager();
            CookieHandler.setDefault(manager);
            CookieStore cookieJar = manager.getCookieStore();
            URL url = new URL(urlp);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(SSLByPass.getInstancia().getSslSocketFactory());
            cookieJar.add(url.toURI(), cookies.get(0));
            //cookieJar.add(url.toURI(), cookies.get(1));

            connection.setRequestMethod(metodo);
            //-----------------------------------------------------------------------------------------
            connection.connect(); //Conectar
            connection.getContent();
            BufferedInputStream reader = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            int c;
            while ((c = reader.read()) != -1) {
                byteArrayOut.write(c);
            }
            Image image = Toolkit.getDefaultToolkit().createImage(byteArrayOut.toByteArray());
            reader.close();
            return image;
        } catch (Exception e) {
            System.out.println("No se puede asignar la cookie " + e);
            throw e;
        }
    }

    public String peticionConCookieString(String urlp, String metodo, String parametros, List<HttpCookie> cookies) throws Exception {
        System.setProperty("http.agent", BusquedaCookie.USER_AGENT_FIREFOX);
        try {
            CookieManager manager = new CookieManager();
            CookieHandler.setDefault(manager);
            CookieStore cookieJar = manager.getCookieStore();
            URL url = new URL(urlp);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(SSLByPass.getInstancia().getSslSocketFactory());
            cookieJar.add(url.toURI(), cookies.get(0));
            //cookieJar.add(url.toURI(), cookies.get(1));

            connection.setRequestMethod(metodo);
            connection.setDoOutput(true); // Esto permite agregar los parametros
            // Escribiendo las variables
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(parametros);
            writer.flush();
            //-----------------------------------------------------------------------------------------
            connection.connect(); //Conectar
            connection.getContent();
            StringBuilder answer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            int linea = 1;
            while ((line = reader.readLine()) != null) {
                if (linea >= 154 && linea <= 156) {
                    answer.append(" ");
                    answer.append(line.trim());
                } else if (linea == 160 && answer.toString().trim().isEmpty()) {
                    answer.append(prepararRpta(line));
                } else if (linea == 166 && answer.toString().trim().isEmpty()) {
                    answer.append(prepararRpta(line));
                }
                if (linea++ > 166) {
                    break;
                }
            }
            writer.close();
            reader.close();
            return answer.toString().replace("<br>", "");
        } catch (IOException e) {
            System.out.println("No se puede asignar la cookie " + e);
            throw e;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No se puede asignar la cookie " + e);
            throw e;
        } catch (KeyManagementException e) {
            System.out.println("No se puede asignar la cookie " + e);
            throw e;
        } catch (URISyntaxException e) {
            System.out.println("No se puede asignar la cookie " + e);
            throw e;
        }
    }

    private String prepararRpta(String rpta) {
        rpta = rpta.trim();
        rpta = rpta.substring(rpta.indexOf(">") + 1);
        return rpta;
    }
}
