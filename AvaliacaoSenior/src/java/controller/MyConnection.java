package controller;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jeyson Gomes
 */
public class MyConnection {
    
    //URL do meu banco de dados ** NECESSÁRIO MUDAR PARA TESTES**
    private static final String url = "jdbc:oracle:thin:senior@//localhost:1521/xe";
    //driver de conexão Oracle
    private static final String driver = "oracle.jdbc.driver.OracleDriver";
    //usuário do banco ** NECESSÁRIO MUDAR PARA TESTES**
    private static final String user = "senior";
    //senha do banco ** NECESSÁRIO MUDAR PARA TESTES**
    private static final String password = "senior@2018";
    //Objeto para conexão
    private static java.sql.Connection conn;
    
    //Conexão com o banco de dados
    public static java.sql.Connection OpenDB() throws ClassNotFoundException, SQLException{
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
    
    //Fechar conexão
    public static void CloseDB(){
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        //tentativa de abrir conexão com o BD.
        try {
            OpenDB();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MyConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
