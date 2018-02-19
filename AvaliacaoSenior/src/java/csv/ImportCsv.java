package csv;

import controller.MyConnection;
import dao.CityDAO;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.City;

/**
 * @author Jeyson Gomes
 */
public class ImportCsv {
    
    MyConnection conn;
    CityDAO    cidadeDao;
    City       cidade;
    BigDecimal   bigDecimal;
    
    //csv/cidades.csv
    public void lerArquivo(){        
        try {
            // ** Mudar o caminho do arquivo CSV **
            FileReader arq = new FileReader("C:\\Users\\Jeyson Gomes\\Desktop\\Avaliacao\\cidades.csv");
            
            BufferedReader lerArq = new BufferedReader(arq);
            //lendo a primeira linha
            String linha = lerArq.readLine();
            
            //a variavel linha recebe o valor 'null' quando chegar no final do arquivo
            while (linha != null){
                //criando um array que recebe os atributos divididos pelo split
                String[] atributos = linha.split(",");

                //System.out.println("teste: " +atributos[0] + " " + atributos[1]);
                
                //capturando a proxima linha
                linha = lerArq.readLine();
                
                //pulando a linha do cabeçalho do arquivo.
                if(atributos[0].equals("ibge_id"))
                {
                    continue;
                }
                //preenchendo o objeto cidade para inserir os valores no BD
                cidade = setCidades(atributos);
                
                //Adicionar os dados no BD.
                inserirDados(cidade);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImportCsv.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImportCsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public City setCidades(String[] atributos){
        cidade = new City();
        cidade.setIbge_id(Integer.parseInt(atributos[0]));
        cidade.setUf(atributos[1]);
        cidade.setName(atributos[2]);
        //se capital for true setar para 1 no Oracle
        if(atributos[3].equals("true")){
            cidade.setCapital("1");
        }
        
        bigDecimal = new BigDecimal(atributos[4]);
        cidade.setLat(bigDecimal);
        bigDecimal = new BigDecimal(atributos[5]);
        cidade.setLon(bigDecimal);
        cidade.setNo_accents(atributos[6]);
        cidade.setAlternative_names(atributos[7]);
        cidade.setMicro_region(atributos[8]);
        cidade.setMeso_region(atributos[9]);
        
        return cidade;
    }
    
    public void inserirDados(City cidade){
        cidadeDao = new CityDAO();
        cidadeDao.newCity(cidade);
    }
    
    /*
    IMPORTANTE: No caso do Oracle XE, foi necessário aumentar o limite
    de processos do Oracle na execução do método MAIN, que usei para teste, 
    pois os limites de processos estavam sendo atingidos.
    Sendo assim a execução era interrompida por uma exceção de limite de processo.
    Erro ORA-12516. Foi solucionado aumentando o limite de processos, porém não tenho
    certeza se isso ocorreu devido a ser uma versão mais limitada do Oracle.
    FONTES: 
    https://community.oracle.com/thread/362226
    https://dba.stackexchange.com/questions/110819/oracle-intermittently-throws-ora-12516-tnslistener-could-not-find-available-h
    
    public static void main(String[] args) {
        ImportCsv c = new ImportCsv();
        c.lerArquivo();
    }

    */
    
}
