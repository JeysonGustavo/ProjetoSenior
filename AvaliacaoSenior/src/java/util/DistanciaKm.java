package util;

/**
 * @author Jeyson Gomes
 */
public class DistanciaKm {
    
    public double calc_dist_km(double first_lat, double first_lon, 
                               double second_lat, double second_lon){
        
        //Declarar o raio da terra em 6371Km.
        int raio_terra = 6371;
        
        // Conversão de graus pra radianos das latitudes
        double firstLatToRad = Math.toRadians(first_lat);
        double secondLatToRad = Math.toRadians(second_lat);
        
        // Diferença das longitudes
        double deltaLongitudeInRad = Math.toRadians(second_lon - first_lon);
        
        // Cálcula da distância entre os pontos
        return Math.acos(Math.cos(firstLatToRad) * Math.cos(secondLatToRad)
        * Math.cos(deltaLongitudeInRad) + Math.sin(firstLatToRad)
        * Math.sin(secondLatToRad))
        * raio_terra;
    
    }
    
    /*public static void main(String[] args) {
        DistanciaKm d = new DistanciaKm();
        
        double retorno = d.calc_dist_km(-21.2544714994, -48.3203497513, -21.3580490113, -48.065583022);
        
        System.out.println("Retorno: " + retorno);
    }*/
    
}
