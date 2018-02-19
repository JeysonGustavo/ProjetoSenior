package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.City;
import controller.MyConnection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.CalcDistancia;
import model.Quantity;
import model.QuantityCity;
import oracle.jdbc.OracleTypes;

/**
 * @author Jeyson Gomes
 */
public class CityDAO {
    
    //Definição dos objetos do BD.
    private PreparedStatement ps;
    private ResultSet         rs;
    private Connection        conn;
    private String            cmd;
    private CallableStatement cs;
    City                      cidade;
    QuantityCity              qc;
    Quantity                  qtde;
    CalcDistancia             calcDist; 
    
    //Retornar somente as cidades que são capitais
    public List<City> getCapitais(){
        List<City> listCidade = new ArrayList<>();
        cidade = null; 
        String retorno = null;
    
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_capital(?, ?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            rs = (ResultSet) cs.getObject(1);
            retorno = (String) cs.getObject(2);
            
            //Verifica se ocorreu algum erro
            if(!retorno.equals("OK")){
                
                cidade = new City();
                //-1 siginifica erro
                cidade.setIbge_id(-1);
                cidade.setName(retorno);
                
                listCidade.add(cidade);
                
            }else{
            
                //trazendo os dados
                while(rs.next()){
                   cidade = new City();
                   cidade.setIbge_id(rs.getInt("ibge_id"));
                   cidade.setUf(rs.getString("uf"));
                   cidade.setName(rs.getString("name"));
                   cidade.setCapital(rs.getString("capital"));
                   cidade.setLat(rs.getBigDecimal("lat"));
                   cidade.setLon(rs.getBigDecimal("lon"));
                   cidade.setNo_accents(rs.getString("no_accents"));
                   cidade.setAlternative_names(rs.getString("alternative_names"));
                   cidade.setMicro_region(rs.getString("microregion"));
                   cidade.setMeso_region(rs.getString("mesoregion"));

                   listCidade.add(cidade);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return listCidade;
    }
    
    //Inserir dados na tabela tb_cidades, para importação do arquivo e Web Service
    public String newCity(City cidade){
        
        String retorno = null;
    
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_new_city(?,?,?,?,?,?,?,?,?,?,?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            
            //setando os parâmetros de inserção
            cs.setInt(1, cidade.getIbge_id());
            cs.setString(2, cidade.getUf());
            cs.setString(3, cidade.getName());
            cs.setString(4, cidade.getCapital());
            cs.setBigDecimal(5, cidade.getLat());
            cs.setBigDecimal(6, cidade.getLon());
            cs.setString(7, cidade.getNo_accents());
            cs.setString(8, cidade.getAlternative_names());
            cs.setString(9, cidade.getMicro_region());
            cs.setString(10, cidade.getMeso_region());
            cs.registerOutParameter(11, Types.VARCHAR);
            
            cs.execute();
            
            retorno = (String) cs.getObject(11);
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
            retorno = ex.toString();
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return retorno;
    }
    
    //Retornar a maior e menor qtde de cidade por estado.
    public List<QuantityCity> getMinMaxCity(){
    
        List<QuantityCity> listQtde = new ArrayList<>();
        qc = null;
        String retorno = null;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_min_max_city(?, ?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            rs = (ResultSet) cs.getObject(1);
            retorno = (String) cs.getObject(2);
            
            //Verifica se ocorreu algum erro
            if(!retorno.equals("OK")){
                
                qc = new QuantityCity();
                //-1 siginifica erro
                qc.setQuantity_city(-1);
                qc.setUf(rs.getString(retorno));
                qc.setEspecificacao("");
                listQtde.add(qc);
            
            }else{
            
                while(rs.next()){
                    qc = new QuantityCity();
                    qc.setQuantity_city(rs.getInt("QTDE"));
                    qc.setUf(rs.getString("UF"));
                    qc.setEspecificacao(rs.getString("ESPECIFICACAO"));
                    listQtde.add(qc);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }    
        
        return listQtde;
    }
    
    //Retornar a quantidade de cidade por estado.
    public List<Quantity> getQuantityCities(){
        
        List<Quantity> listQtde = new ArrayList<>();
        qtde = null;
        String retorno;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_quantity_cities(?, ?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            rs = (ResultSet) cs.getObject(1);
            retorno = (String) cs.getObject(2);
            
            //Verifica se ocorreu algum erro
            if(!retorno.equals("OK")){
                
                qtde = new Quantity();
                //-1 siginifica erro
                qtde.setQuantity_city(-1);
                qtde.setUf(rs.getString(retorno));
                listQtde.add(qtde);
            
            }else{
                while(rs.next()){
                    qtde = new Quantity();
                    qtde.setQuantity_city(rs.getInt("QTDE DE CIDADES"));
                    qtde.setUf(rs.getString("UF"));
                    listQtde.add(qtde);
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }    
        
        return listQtde;
        
    }
    
    //Retornar a cidade de acordo com o código do IBGE
    public List<City> getCityFromIbgeId(int ibge_id){
        
        List<City> listCidade = new ArrayList<>();
        cidade = null; 
        String retorno = null;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_city(?, ?, ?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.setInt(1, ibge_id);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            retorno = (String) cs.getObject(3);
            
            //Verifica se ocorreu algum erro
            if(!retorno.equals("OK")){
                
                cidade = new City();
                //-1 siginifica erro
                cidade.setIbge_id(-1);
                cidade.setName(retorno);
                
                listCidade.add(cidade);
                
            }else{
            
                //trazendo os dados
                while(rs.next()){
                   cidade = new City();
                   cidade.setIbge_id(rs.getInt("ibge_id"));
                   cidade.setUf(rs.getString("uf"));
                   cidade.setName(rs.getString("name"));
                   cidade.setCapital(rs.getString("capital"));
                   cidade.setLat(rs.getBigDecimal("lat"));
                   cidade.setLon(rs.getBigDecimal("lon"));
                   cidade.setNo_accents(rs.getString("no_accents"));
                   cidade.setAlternative_names(rs.getString("alternative_names"));
                   cidade.setMicro_region(rs.getString("microregion"));
                   cidade.setMeso_region(rs.getString("mesoregion"));

                   listCidade.add(cidade);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return listCidade;
    }
    
    //Retornar as cidades de acordo com o estado informado
    public List<String> getCitiesFromState(String estado){
        
        List<String> listCity = new ArrayList<>();
        cidade = null; 
        String retorno, city;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_city_from_state(?, ?, ?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.setString(1, estado);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            retorno = (String) cs.getObject(3);
            
            //Verifica se ocorreu algum erro
            if(!retorno.equals("OK")){
                
                //-1 siginifica erro                
                listCity.add(retorno);
                
            }else{
            
                //trazendo os dados
                while(rs.next()){
                    city = "";
                    city = rs.getString("NAME");
                   
                    listCity.add(city);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return listCity;
    
    }
    
    //Deletar uma cidade
    public String deleteCity(int ibge_id){
        
        String retorno = null;
    
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_del_city(?,?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            
            //setando os parâmetros de inserção
            cs.setInt(1, ibge_id);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            
            retorno = (String) cs.getObject(2);
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
            retorno = ex.toString();
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return retorno;
        
    }
    
    //Retornar os objetos de determinada coluna
    public List<City> getColFromString(String coluna, String string){
        
        List<City> listCidade = new ArrayList<>();
        cidade = null; 
        String retorno = null;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_col_from_string(?, ?, ?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.setString(1, coluna);
            cs.setString(2, string);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(3);
            
            if(!rs.isBeforeFirst()){
                cidade = new City();
                //-1 siginifica erro
                cidade.setIbge_id(-1);
                cidade.setName("Nehum dado encontrado");
                listCidade.add(cidade);                
            }else{
                //trazendo os dados
                while(rs.next()){
                   cidade = new City();
                   cidade.setIbge_id(rs.getInt("ibge_id"));
                   cidade.setUf(rs.getString("uf"));
                   cidade.setName(rs.getString("name"));
                   cidade.setCapital(rs.getString("capital"));
                   cidade.setLat(rs.getBigDecimal("lat"));
                   cidade.setLon(rs.getBigDecimal("lon"));
                   cidade.setNo_accents(rs.getString("no_accents"));
                   cidade.setAlternative_names(rs.getString("alternative_names"));
                   cidade.setMicro_region(rs.getString("microregion"));
                   cidade.setMeso_region(rs.getString("mesoregion"));

                   listCidade.add(cidade);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return listCidade;
    
    }
    
    //Retornar a quantidade de dados de acordo com uma coluna
    public List<String> getQuantityCol(String coluna){
        
        List<String> listQtde = new ArrayList<>();
        cidade = null; 
        String retorno = null;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_qtde_col(?, ?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.setString(1, coluna);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            
            if(!rs.isBeforeFirst()){
                cidade = new City();
                // Nenhum dado foi encontrado
                retorno = "Nehum dado encontrado";
                listQtde.add(retorno);                
            }else{
                //trazendo os dados
                while(rs.next()){
                    
                    retorno = rs.getString("QTDE");
                    
                    listQtde.add(retorno);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return listQtde;
        
    }
    
    //Retornar a quantidade de registros total
    public List<String> getTotalRec(){
        
        List<String> listQtde = new ArrayList<>();
        cidade = null; 
        String retorno = null;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_total_rec(?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(1);
            
            if(!rs.isBeforeFirst()){
                cidade = new City();
                // Nenhum dado foi encontrado
                retorno = "Nehum dado encontrado";
                listQtde.add(retorno);                
            }else{
                //trazendo os dados
                while(rs.next()){
                    
                    retorno = rs.getString("QTDE TOTAL DE REGISTROS");
                    
                    listQtde.add(retorno);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return listQtde;
        
    }
    
    //Retornar os dados das cidades para calculo da distância
    public List<CalcDistancia> getCalcDist(){
        
        List<CalcDistancia> listCalc = new ArrayList<>();
        calcDist = null; 
        String retorno = null;
        
        try {
            //Chamada da procedure que retorna os valores
            cmd = "{call pkg_cidades.sp_ret_calc_dist(?)}";
            conn = MyConnection.OpenDB();
            cs = conn.prepareCall(cmd);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(1);
            
            if(!rs.isBeforeFirst()){
                calcDist = new CalcDistancia();
                //-1 siginifica erro
                calcDist.setIbge_id(-1);
                calcDist.setName("Nehum dado encontrado");
                listCalc.add(calcDist);                
            }else{
                //trazendo os dados
                while(rs.next()){
                   calcDist = new CalcDistancia();
                   calcDist.setIbge_id(rs.getInt("ibge_id"));
                   calcDist.setName(rs.getString("name"));
                   calcDist.setUf(rs.getString("uf"));
                   calcDist.setLat(rs.getBigDecimal("lat"));
                   calcDist.setLon(rs.getBigDecimal("lon"));                   
                   calcDist.setMicro_region(rs.getString("microregion"));
                   calcDist.setMeso_region(rs.getString("mesoregion"));

                   listCalc.add(calcDist);
                }
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            MyConnection.CloseDB();
        }   
        
        return listCalc;
    
    }
}
