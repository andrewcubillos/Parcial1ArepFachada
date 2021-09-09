
package co.escuelaing.edu.parcialarep1corte;

import static spark.Spark.*;


public class App {
    public static void main(String[] args){
        port(getPort());
        get ("/", (req,res) -> { 
        String numero= req.queryParams("numero");
        String trigfuncion = req.queryParams("trigfuncion");
        String url = "https://parcial1arepcalculadora.herokuapp.com/calculadora?numero="+numero+"&trigfuncion"+trigfuncion;
        return Cliente.getResponse(url);});
        }
    
    private static int getPort(){
        if(System.getenv("PORT")!= null){
            return Integer.parseInt(System.getenv("PORT"));
        }
        else return 35000;
            
    }
}