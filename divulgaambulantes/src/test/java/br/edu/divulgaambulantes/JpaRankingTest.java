package br.edu.divulgaambulantes;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;


@SpringBootTest
public class JpaRankingTest {




    @PersistenceContext
    private EntityManager entityManager;




    @Test
    void testarRankingJPA() {




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




        SELECT
            seller_id,
            full_name,
            email,
            total_produtos,
            produtos_denunciados,


            ROUND(
                produtos_denunciados * 100.0
                / NULLIF(total_produtos,0),
                2
            ) AS pct_denuncias,




            total_clicks,
            total_likes,
            total_favoritos,
            total_reviews,
            total_review_views,




            (
                total_clicks * 0.3
                + total_likes * 0.2
                + total_favoritos * 0.2
                + total_reviews * 0.15
                + total_review_views * 0.15
            )
            -
            (
                COALESCE(
                    produtos_denunciados * 100.0
                    / NULLIF(total_produtos,0),
                    0
                ) * 0.5
            ) AS score_ponderado




        FROM seller_metrics




        ORDER BY
            pct_denuncias ASC,
            total_clicks DESC,
            total_likes DESC,
            total_favoritos DESC,
            total_reviews DESC,
            total_review_views DESC




        LIMIT 20
        """;




        long inicio = System.currentTimeMillis();




        Query query = entityManager.createNativeQuery(sql);




        List<Object[]> resultado = query.getResultList();






        System.out.println("==============================");
        System.out.println("RESULTADO DO RANKING JPA");
        System.out.println("==============================");




        int posicao = 1;




        for(Object[] linha : resultado){




            System.out.println(
                    posicao + "º - "
                    + linha[1]
                    + " | Email: "
                    + linha[2]
                    + " | Produtos: "
                    + linha[3]
                    + " | Denúncias: "
                    + linha[4]
                    + " | Cliques: "
                    + linha[6]
                    + " | Likes: "
                    + linha[7]
                    + " | Score: "
                    + linha[11]
            );




            posicao++;


        }




        long fim = System.currentTimeMillis();




        System.out.println("==============================");
        System.out.println(
                "Tempo JPA: "
                + (fim - inicio)
                + " ms"
        );
        System.out.println("==============================");




    }
}