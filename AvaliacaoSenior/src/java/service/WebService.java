package service;

import com.google.gson.Gson;
import csv.ImportCsv;
import dao.CityDAO;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import model.CalcDistancia;
import model.City;
import model.Quantity;
import model.QuantityCity;
import util.DistanciaKm;

/**
 * REST Web Service
 * @author Jeyson Gomes
 */
@Path("api")
public class WebService {
    
    Gson gson = new Gson();

    @Context
    private UriInfo context;
    private CityDAO cidadeDao;

    /**
     * Creates a new instance of WebService
     */
    public WebService() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("inserir_csv")
    public String inserir(){
        
         Gson gson = new Gson();
         ImportCsv csv = new ImportCsv();
         
         csv.lerArquivo();
         
         return gson.toJson("Dados Inseridos");
         
    
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get_capitais")
    public String getCapitais(){
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        List<City> listCidade = cidadeDao.getCapitais();
        
        //ocorreu erro, então retorna o erro
        if(listCidade.get(0).getIbge_id() == -1){
            return gson.toJson(listCidade.get(0).getName());
        }else {
             return gson.toJson(listCidade);
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("new_city")
    public String newCity(String context){
        Gson gson = new Gson();
        String retorno = null;
        cidadeDao = new CityDAO();
        City cidade = (City) gson.fromJson(context, City.class);
        retorno = cidadeDao.newCity(cidade);
        return gson.toJson(retorno);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("min_max_city")
    public String getMinMaxCity(){
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        List<QuantityCity> listQtde = cidadeDao.getMinMaxCity();
        
        //ocorreu erro, então retorna o erro
        if(listQtde.get(0).getQuantity_city() == -1){
            return gson.toJson(listQtde.get(0).getUf());
        }else {
             return gson.toJson(listQtde);
        }
       
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("quantity_cities")
    public String getQuantityCities(){
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        List<Quantity> listQtde = cidadeDao.getQuantityCities();
        
        //ocorreu erro, então retorna o erro
        if(listQtde.get(0).getQuantity_city() == -1){
            return gson.toJson(listQtde.get(0).getUf());
        }else {
            return gson.toJson(listQtde);
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get_city/{ibge_id}")
    public String getCityFromIbgeId(@PathParam("ibge_id") int ibge_id){
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        List<City> listCidade = cidadeDao.getCityFromIbgeId(ibge_id);
        
        //ocorreu erro, então retorna o erro
        if(listCidade.get(0).getIbge_id() == -1){
            return gson.toJson(listCidade.get(0).getName());
        }else {
             return gson.toJson(listCidade);
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get_cities/{uf}")
    public String getCitiesFromState(@PathParam("uf") String uf){
        
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        List<String> listCities = cidadeDao.getCitiesFromState(uf);
        
        return gson.toJson(listCities);
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("del_city/{ibge_id}")
    public String deleteCity(@PathParam("ibge_id") int ibge_id){
        
        Gson gson = new Gson();
        String retorno = null;
        cidadeDao = new CityDAO();
        retorno = cidadeDao.deleteCity(ibge_id);
        
        return gson.toJson(retorno);
    
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get_col/{coluna}/{string}")
    public String getColFromString(@PathParam("coluna") String coluna,
                                   @PathParam("string") String string){
        
    Gson gson = new Gson();
    cidadeDao = new CityDAO();
    List<City> listCidade = cidadeDao.getColFromString(coluna, string);   
    
    //ocorreu erro, então retorna o erro
    if(listCidade.get(0).getIbge_id() == -1){
        return gson.toJson(listCidade.get(0).getName());
    }else {
         return gson.toJson(listCidade);
    }
    
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get_qtde_col/{coluna}")
    public String getQuantityCol(@PathParam("coluna") String coluna){
        
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        List<String> listQtde = cidadeDao.getQuantityCol(coluna);
        
        return gson.toJson(listQtde);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get_total_rec")
    public String getTotalRec(){
        
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        List<String> qtdeTotal = cidadeDao.getTotalRec();
        
        return gson.toJson(qtdeTotal);
    
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get_calc")
    public String teste(){
        Gson gson = new Gson();
        cidadeDao = new CityDAO();
        DistanciaKm km = new DistanciaKm();
        List<CalcDistancia> listCalc = cidadeDao.getCalcDist();
        List<CalcDistancia> listAux = new ArrayList<>(listCalc);
        BigDecimal lat_ini, lon_ini, lat_fim, lon_fim;
        double distancia, max = 0;
        String name_ini, name_fim, uf_ini, uf_fim;
        String retorno = null;
        DecimalFormat df2 = new DecimalFormat(".##");
        
        for (int i = 0; i < listCalc.size(); i++) {
            
            for (int j = 0; j < listAux.size(); j++) {
                
                //calcular a distancia entre as cidades
                distancia = km.calc_dist_km(listCalc.get(i).getLat().doubleValue(), 
                                listCalc.get(i).getLon().doubleValue(), 
                                listAux.get(j).getLat().doubleValue(), 
                                listAux.get(j).getLon().doubleValue());
                //verificar qual é a maior distância
                if(distancia > max){
                    max = distancia;
                    name_ini = listCalc.get(i).getName();
                    name_fim = listAux.get(j).getName();
                    uf_ini = listCalc.get(i).getUf();
                    uf_fim = listAux.get(j).getUf();
                    
                    retorno = "A cidade " + name_ini + " de " + uf_ini +  
                              " fica a " + df2.format(max) + " KM de distância da cidade " + name_fim + 
                              " de " + uf_fim;
                }
            }
        }
        
        return gson.toJson(retorno);
    }
    
}
