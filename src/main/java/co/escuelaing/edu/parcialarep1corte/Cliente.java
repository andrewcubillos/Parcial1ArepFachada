
package co.escuelaing.edu.parcialarep1corte;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Cliente {
    public static String getResponse(String uri) throws UnirestException{
        HttpResponse<String> response = Unirest.get(uri).asString();
        return response.getBody();
    }
}
