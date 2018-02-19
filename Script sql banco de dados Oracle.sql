/*CRIAÇÃO DO USUÁRIO SENIOR E TODOS OS PREVILÉGIOS NO BANCO DE DADOS*/
CREATE USER senior IDENTIFIED BY "senior@2018";

GRANT CONNECT TO senior;

GRANT ALL PRIVILEGES TO senior;

/*CRIAÇÃO DA TABELA QUE SERÁ UTILIZADA NO BANCO DE DADOS*/
CREATE TABLE tb_cidades(
    ibge_id                 INTEGER NOT NULL,
    uf                      CHAR(2) NOT NULL,
    name                    NVARCHAR2(100) NOT NULL,
    capital                 CHAR(1),
    lon                     NUMERIC(20,12),
    lat                     NUMERIC(20,12),
    no_accents              NVARCHAR2(160) NOT NULL,
    alternative_names       NVARCHAR2(160),
    microregion             NVARCHAR2(150),
    mesoregion              NVARCHAR2(150)
);

ALTER TABLE tb_cidades
    ADD CONSTRAINT pk_ibge_id_tb_cidades PRIMARY KEY(ibge_id);

/*CRIAÇÃO DAS PACKAGES QUE SERÃO UTILIZADAS NO WEB SERVICE*/
create or replace PACKAGE pkg_cidades
IS

    --Procedure que retorna somente as cidades que são capitais
    PROCEDURE sp_ret_capital(
                              p_cursor  OUT SYS_REFCURSOR,
                              p_retorno OUT VARCHAR
                             );
                             
    --Procedure que retorna a maior e menor quantidade de cidades por estado                         
    PROCEDURE sp_ret_min_max_city(
                                   p_cursor  OUT SYS_REFCURSOR,
                                   p_retorno OUT VARCHAR
                                  );
                                  
    --Procedure que retorna a quantidade de cidades por estado
    PROCEDURE sp_ret_quantity_cities(
                              p_cursor  OUT SYS_REFCURSOR,
                              p_retorno OUT VARCHAR
                             );
                             
    --Procedure que retorna os dados da cidade a partir do ibge_id
    PROCEDURE sp_ret_city(
                              p_ibge_id IN tb_cidades.ibge_id%TYPE,
                              p_cursor  OUT SYS_REFCURSOR,
                              p_retorno OUT VARCHAR
                             );
                             
    --Procedure que retorna todas as cidades a partir do estado
    PROCEDURE sp_ret_city_from_state(
                                       p_uf IN tb_cidades.uf%TYPE,
                                       p_cursor OUT SYS_REFCURSOR,
                                       p_retorno OUT VARCHAR
                                     );
                                     
    --Procedure que realiza a inserção de uma nova cidade.
    PROCEDURE sp_new_city(
                          p_ibge_id               IN tb_cidades.ibge_id%TYPE,
                          p_uf                    IN tb_cidades.uf%TYPE,
                          p_name                  IN tb_cidades.name%TYPE,
                          p_capital               IN tb_cidades.capital%TYPE,
                          p_lon                   IN tb_cidades.lon%TYPE,
                          p_lat                   IN tb_cidades.lat%TYPE,
                          p_no_accents            IN tb_cidades.no_accents%TYPE,
                          p_alternative_names     IN tb_cidades.alternative_names%TYPE,
                          p_microregion           IN tb_cidades.microregion%TYPE,
                          p_mesoregion            IN tb_cidades.mesoregion%TYPE,
                          p_retorno               OUT VARCHAR
                          );
                          
    --Procedure que realiza a deleção de uma cidade.                     
    PROCEDURE sp_del_city(
                           p_ibge_id               IN tb_cidades.ibge_id%TYPE,
                           p_retorno               OUT VARCHAR
                         );
                         
    --Procedure que retorna os dados a partir de uma coluna e uma string desejada
    PROCEDURE sp_ret_col_from_string(
                                      p_col_name    IN VARCHAR,
                                      p_string      IN VARCHAR,
                                      p_cursor      OUT SYS_REFCURSOR
                                    );
                                    
    --Procedure que retorna a quantidade de registros por coluna informada
    PROCEDURE sp_ret_qtde_col(
                               p_col_name    IN VARCHAR,
                               p_cursor      OUT SYS_REFCURSOR
                             );
                             
    --Procedure que retorna a quantidade total de registros
    PROCEDURE sp_ret_total_rec(
                                p_cursor      OUT SYS_REFCURSOR
                               );
    
    --Procedure que retornar os dados para calculo da distancia entre as cidades                           
    PROCEDURE sp_ret_calc_dist(
                                p_cursor      OUT SYS_REFCURSOR
                              );


END pkg_cidades;


create or replace PACKAGE BODY pkg_cidades
AS

/*
Variáveis iniciando com ** p_nome **, quer dizer que vem por parâmetro.
*/

    --Procedure que retorna somente as cidades que são capitais
    PROCEDURE sp_ret_capital(
                              p_cursor  OUT SYS_REFCURSOR,
                              p_retorno OUT VARCHAR
                             )AS
    BEGIN
    
        p_retorno := 'OK';
    
        OPEN p_cursor FOR
            SELECT
                city.IBGE_ID,
                city.UF,
                city.NAME,
                city.CAPITAL,
                city.LON,
                city.LAT,
                city.NO_ACCENTS,
                city.ALTERNATIVE_NAMES,
                city.MICROREGION,
                city.MESOREGION
            FROM 
                tb_cidades city
            WHERE 
                city.CAPITAL = '1'
            ORDER BY 
                city.uf ASC;
				
	EXCEPTION
		WHEN OTHERS THEN
        
            p_retorno := 'NÃO FOI POSSÍVEL RECUPERAR AS INFORMAÇÕES';
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL LOCALIZAR OS REGISTROS.');
    
    END sp_ret_capital; -- Fim do bloco da Procedure sp_ret_capital
    
     --Procedure que retorna a maior e menor quantidade de cidades por estado                         
    PROCEDURE sp_ret_min_max_city(
                                   p_cursor  OUT SYS_REFCURSOR,
                                   p_retorno OUT VARCHAR
                                  )AS
    BEGIN
    
        p_retorno := 'OK';
    
        OPEN p_cursor FOR
            SELECT BASE."UF", QTDE."QTDE", QTDE."ESPECIFICACAO"
            FROM
            (
                SELECT 
                    MAX(max.QTDE) AS "QTDE", 
                    'MÁXIMO' AS "ESPECIFICACAO"
                FROM 
                    (
                        SELECT 
                            COUNT(0) AS "QTDE",
                            city.UF
                        FROM 
                            tb_cidades city
                        GROUP BY 
                            city.UF  
                    ) max
                
                UNION ALL
                
                SELECT 
                    MIN(min.QTDE) AS "QTDE", 
                    'MINIMO' AS "ESPECIFICACAO"
                FROM 
                    (
                        SELECT 
                            COUNT(0) AS "QTDE",
                            city.UF
                        FROM 
                            tb_cidades city
                        GROUP BY 
                            city.UF  
                    ) min
            ) QTDE
            INNER JOIN
            (
                SELECT 
                    COUNT(0) AS "QTDE",
                    city.UF
                FROM 
                    tb_cidades city
                GROUP BY 
                    city.UF  
            ) BASE
            ON(BASE."QTDE" = QTDE."QTDE");
				
	EXCEPTION
		WHEN OTHERS THEN
        
            p_retorno := 'NÃO FOI POSSÍVEL RECUPERAR AS INFORMAÇÕES';
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL LOCALIZAR OS REGISTROS.');
    
    END sp_ret_min_max_city; -- Fim do bloco da Procedure sp_ret_min_max_city
    
    --Procedure que retorna a quantidade de cidades por estado
    PROCEDURE sp_ret_quantity_cities(
                              p_cursor  OUT SYS_REFCURSOR,
                              p_retorno OUT VARCHAR
                             )AS
    BEGIN
    
        p_retorno := 'OK';
    
        OPEN p_cursor FOR
            SELECT 
                COUNT(0) AS "QTDE DE CIDADES", 
                city.uf
            FROM 
                tb_cidades city
            GROUP BY 
                city.uf
            ORDER BY 
                city.uf ASC;
				
	EXCEPTION
		WHEN OTHERS THEN
        
            p_retorno := 'NÃO FOI POSSÍVEL RECUPERAR AS INFORMAÇÕES';
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL RECUPERAR AS INFORMAÇÕES');
    
    END sp_ret_quantity_cities; -- Fim do bloco da Procedure sp_ret_qtde_cidades

    --Procedure que retorna os dados da cidade a partir do ibge_id
    PROCEDURE sp_ret_city(
                              p_ibge_id IN tb_cidades.ibge_id%TYPE,
                              p_cursor  OUT SYS_REFCURSOR,
                              p_retorno OUT VARCHAR
                             )AS
    BEGIN
    
        p_retorno := 'OK';
    
        OPEN p_cursor FOR
            SELECT
                    city.IBGE_ID,
                    city.UF,
                    city.NAME,
                    city.CAPITAL,
                    city.LON,
                    city.LAT,
                    city.NO_ACCENTS     ,
                    city.ALTERNATIVE_NAMES,
                    city.MICROREGION,
                    city.MESOREGION
                FROM 
                    tb_cidades city
                WHERE 
                    city.ibge_id = p_ibge_id;
					
	EXCEPTION
		WHEN OTHERS THEN
        
            p_retorno := 'NÃO FOI POSSÍVEL RECUPERAR OS DADOS';
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL LOCALIZAR OS REGISTROS.');
    
    END; -- Fim do bloco da Procedure sp_ret_city

    --Procedure que retorna todas as cidades a partir do estado
    PROCEDURE sp_ret_city_from_state(
                                       p_uf IN tb_cidades.uf%TYPE,
                                       p_cursor OUT SYS_REFCURSOR,
                                       p_retorno OUT VARCHAR
                                     )AS
    BEGIN
    
        p_retorno := 'OK';
    
        OPEN p_cursor FOR
            SELECT
                    city.NAME                    
                FROM 
                    tb_cidades city
                WHERE 
                    city.uf = UPPER(p_uf);
					
	EXCEPTION
		WHEN OTHERS THEN
        
            p_retorno := 'NÃO FOI POSSÍVEL RECUPERAR OS DADOS';
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL RECUPERAR OS DADOS');
    
    END sp_ret_city_from_state; -- Fim do bloco da Procedure sp_ret_city_from_state

    --Procedure que realiza a inserção de uma nova cidade.
    PROCEDURE sp_new_city(
                          p_ibge_id               IN tb_cidades.ibge_id%TYPE,
                          p_uf                    IN tb_cidades.uf%TYPE,
                          p_name                  IN tb_cidades.name%TYPE,
                          p_capital               IN tb_cidades.capital%TYPE,
                          p_lon                   IN tb_cidades.lon%TYPE,
                          p_lat                   IN tb_cidades.lat%TYPE,
                          p_no_accents            IN tb_cidades.no_accents%TYPE,
                          p_alternative_names     IN tb_cidades.alternative_names%TYPE,
                          p_microregion           IN tb_cidades.microregion%TYPE,
                          p_mesoregion            IN tb_cidades.mesoregion%TYPE,
                          p_retorno               OUT VARCHAR
                          ) AS
    BEGIN
    
        p_retorno := 'DADOS INSERIDOS';

        INSERT INTO tb_cidades(
                                ibge_id,
                                uf, 
                                name, 
                                capital, 
                                lon, 
                                lat, 
                                no_accents, 
                                alternative_names,
                                microregion,
                                mesoregion
                              )
        VALUES(
                p_ibge_id, 
                UPPER(p_uf), 
                UPPER(p_name), 
                p_capital,
                p_lon, 
                p_lat, 
                UPPER(p_no_accents), 
                UPPER(p_alternative_names),
                UPPER(p_microregion), 
                UPPER(p_mesoregion)
               );
         
         /* Varificar se aconteceu o insert de forma correta */      
         IF SQL%ROWCOUNT > 0 THEN

            COMMIT;
            
         ELSE
         
            p_retorno := 'NÃO FOI POSSÍVEL REALIZAR A INSERÇÃO DOS DADOS';

            ROLLBACK;
         
         END IF;
         
         /* Exceções caso algum dos parâmetros seja NULL*/      
         EXCEPTION
            WHEN OTHERS THEN
            
                p_retorno := 'NÃO FOI POSSÍVEL REALIZAR A INSERÇÃO DOS DADOS';
				
				RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL REALIZAR A INSERÇÃO DOS DADOS');
				
                ROLLBACK;

    END sp_new_city; -- Fim do bloco da Procedure sp_new_city
    
    --Procedure que realiza a deleção de uma cidade.                     
    PROCEDURE sp_del_city(
                           p_ibge_id               IN tb_cidades.ibge_id%TYPE,
                           p_retorno               OUT VARCHAR
                         )AS
    BEGIN
    
        p_retorno := 'CIDADE DELETADA COM SUCESSO';
        
        DELETE
        FROM
            tb_cidades city
        WHERE 
            city.ibge_id = p_ibge_id;
            
        /* Varificar se aconteceu o delete de forma correta */      
         IF SQL%ROWCOUNT > 0 THEN

            COMMIT;
            
         ELSE
         
            p_retorno := 'NÃO FOI POSSÍVEL DELETAR OS REGISTROS.';

            ROLLBACK;
         
         END IF;
         
         /* Exceções caso algum dos parâmetros seja NULL*/      
         EXCEPTION
            WHEN OTHERS THEN
            
                p_retorno := 'NÃO FOI POSSÍVEL DELETAR OS REGISTROS.';
			
				RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL DELETAR OS REGISTROS.');

                ROLLBACK;
    
    END sp_del_city; -- Fim do bloco da Procedure sp_del_city

    --Procedure que retorna os dados a partir de uma coluna e uma string desejada
    PROCEDURE sp_ret_col_from_string(
                                      p_col_name    IN VARCHAR,
                                      p_string      IN VARCHAR,
                                      p_cursor      OUT SYS_REFCURSOR
                                    )AS
    
    v_sql VARCHAR(5000);
        
    BEGIN
    
        v_sql := ' SELECT '                    ||
                 '   city.IBGE_ID, '           ||
                 '   city.UF, '                ||
                 '   city.NAME, '              ||
                 '   city.CAPITAL, '           ||
                 '   city.LON, '               ||
                 '   city.LAT, '               ||
                 '   city.NO_ACCENTS, '        ||
                 '   city.ALTERNATIVE_NAMES, ' ||
                 '   city.MICROREGION, '       ||
                 '   city.MESOREGION '         ||
                 ' FROM '                      ||
                 '   tb_cidades city '         ||
                 ' WHERE city.' ||  UPPER(p_col_name) || ' LIKE ''%' || UPPER(p_string) || '%''';
        
        --Executar a consulta com coluna dinâmica.         
        OPEN p_cursor
            FOR v_sql;
			
	EXCEPTION
		WHEN OTHERS THEN
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL LOCALIZAR OS REGISTROS.');
    
    END sp_ret_col_from_string; -- Fim do bloco da Procedure sp_ret_filters
    
    --Procedure que retorna a quantidade de registros por coluna informada
    PROCEDURE sp_ret_qtde_col(
                               p_col_name    IN VARCHAR,
                               p_cursor      OUT SYS_REFCURSOR
                             )AS
                             
    v_sql VARCHAR(5000);
    
    BEGIN
    
        v_sql := ' SELECT ' ||
                 ' COUNT(DISTINCT('|| UPPER(p_col_name) ||')) || '' REGISTRO(S) ECONTRADO(S) NA COLUNA '|| UPPER(p_col_name) || ''' AS "QTDE"' ||
                 ' FROM ' || 
                 ' tb_cidades';
                 
        --Executar a consulta com coluna dinâmica.         
        OPEN p_cursor
            FOR v_sql;
			
	EXCEPTION
		WHEN OTHERS THEN
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL LOCALIZAR OS REGISTROS.');
    
    END sp_ret_qtde_col; -- Fim do bloco da Procedure sp_ret_qtde_col
	
	--Procedure que retorna a quantidade total de registros
    PROCEDURE sp_ret_total_rec(
                               p_cursor      OUT SYS_REFCURSOR
                             )AS              
							 
	BEGIN
    
       OPEN p_cursor FOR
        
            SELECT COUNT(0) || ' REGISTROS ENCONTRADOS NA TABELA tb_cidades' AS "QTDE TOTAL DE REGISTROS"
            FROM tb_cidades;
		
	EXCEPTION
		WHEN OTHERS THEN
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL LOCALIZAR OS REGISTROS.');
	
	END sp_ret_total_rec;
    
    --Procedure que retornar os dados para calculo da distancia entre as cidades                           
    PROCEDURE sp_ret_calc_dist(
                                p_cursor      OUT SYS_REFCURSOR
                              )AS
    
    BEGIN
    
        OPEN p_cursor FOR
            SELECT 
                city.ibge_id,
                city.name,
                city.lon,
                city.lat,
                city.microregion,
                city.mesoregion
            FROM 
                tb_cidades city;
                
    EXCEPTION
		WHEN OTHERS THEN
			
			RAISE_APPLICATION_ERROR(-20002,'NÃO FOI POSSÍVEL LOCALIZAR OS REGISTROS.');
    
    END sp_ret_calc_dist;

END pkg_cidades; -- Fim da Package Body