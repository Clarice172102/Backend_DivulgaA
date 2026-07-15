package br.edu.divulgaambulantes;


import org.junit.jupiter.api.Test;


import java.sql.*;


public class JdbcRankingTest {


    private static final String URL =
            "jdbc:mysql://localhost:3306/divulgaambulantes";


    private static final String USER = "root";
    private static final String PASSWORD = "root";




    @Test
    void testarRankingJDBC() throws Exception {


        String sql = """
        WITH seller_metrics AS (
            SELECT
                u.id AS seller_id,
                u.full_name,
                u.email,


                COUNT(DISTINCT p.id) AS total_produtos,


                COUNT(DISTINCT CASE
                    WHEN r.id IS NOT NULL
                    THEN p.id END
                ) AS produtos_denunciados,


                COUNT(DISTINCT pc.id) AS total_clicks,
                COUNT(DISTINCT l.id) AS total_likes,
                COUNT(DISTINCT f.id) AS total_favoritos,
                COUNT(DISTINCT rv.id) AS total_reviews,
                COUNT(DISTINCT rvw.id) AS total_review_views


            FROM users u


            INNER JOIN products p
                ON p.seller_id = u.id


            LEFT JOIN reports r
                ON r.target_product_id = p.id


            LEFT JOIN product_clicks pc
                ON pc.product_id = p.id


            LEFT JOIN likes l
                ON l.product_id = p.id


            LEFT JOIN favorites f
                ON f.seller_id = u.id


            LEFT JOIN reviews rv
                ON rv.seller_id = u.id


            LEFT JOIN review_views rvw
                ON rvw.review_id = rv.id


            GROUP BY
                u.id,
                u.full_name,
                u.email
        )


        SELECT *
        FROM seller_metrics


        ORDER BY
            total_clicks DESC,
            total_likes DESC,
            total_favoritos DESC


        LIMIT 20;
        """;




        long inicio = System.currentTimeMillis();




        Class.forName("com.mysql.cj.jdbc.Driver");




        try(Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {




            System.out.println("==============================");
            System.out.println("RESULTADO DO RANKING JDBC");
            System.out.println("==============================");




            int contador = 1;




            while(rs.next()) {


                System.out.println(
                        contador + "º - "
                        + rs.getString("full_name")
                        + " | Email: "
                        + rs.getString("email")
                        + " | Produtos: "
                        + rs.getInt("total_produtos")
                        + " | Denúncias: "
                        + rs.getInt("produtos_denunciados")
                        + " | Cliques: "
                        + rs.getInt("total_clicks")
                        + " | Likes: "
                        + rs.getInt("total_likes")
                );


                contador++;
            }




            System.out.println("==============================");
            System.out.println("Consulta JDBC finalizada");
            System.out.println("==============================");


        }




        long fim = System.currentTimeMillis();




        System.out.println(
                "Tempo JDBC: "
                + (fim - inicio)
                + " ms"
        );


    }
}