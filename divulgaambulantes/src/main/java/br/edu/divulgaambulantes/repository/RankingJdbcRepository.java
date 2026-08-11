package br.edu.divulgaambulantes.repository;

import br.edu.divulgaambulantes.dto.RankingVendedorDTO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RankingJdbcRepository {

    private final DataSource dataSource;

    public RankingJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String SQL = """
        WITH
        produtos AS (
            SELECT
                seller_id,
                COUNT(*) AS total_produtos
            FROM products
            WHERE deleted_at IS NULL
            GROUP BY seller_id
        ),

        denuncias AS (
            SELECT
                p.seller_id,
                COUNT(DISTINCT r.target_product_id) AS produtos_denunciados
            FROM reports r
            INNER JOIN products p
                ON p.id = r.target_product_id
            GROUP BY p.seller_id
        ),

        cliques AS (
            SELECT
                p.seller_id,
                COUNT(*) AS total_clicks
            FROM product_clicks pc
            INNER JOIN products p
                ON p.id = pc.product_id
            GROUP BY p.seller_id
        ),

        likes_total AS (
            SELECT
                p.seller_id,
                COUNT(*) AS total_likes
            FROM likes l
            INNER JOIN products p
                ON p.id = l.product_id
            GROUP BY p.seller_id
        ),

        favoritos AS (
            SELECT
                seller_id,
                COUNT(*) AS total_favoritos
            FROM favorites
            GROUP BY seller_id
        ),

        reviews_total AS (
            SELECT
                seller_id,
                COUNT(*) AS total_reviews
            FROM reviews
            GROUP BY seller_id
        ),

        views_reviews AS (
            SELECT
                r.seller_id,
                COUNT(*) AS total_review_views
            FROM review_views rv
            INNER JOIN reviews r
                ON r.id = rv.review_id
            GROUP BY r.seller_id
        )

        SELECT
            u.id AS seller_id,
            u.full_name,
            u.email,

            COALESCE(p.total_produtos, 0) AS total_produtos,

            COALESCE(d.produtos_denunciados, 0)
                AS produtos_denunciados,

            ROUND(
                COALESCE(d.produtos_denunciados, 0)
                * 100.0
                / NULLIF(p.total_produtos, 0),
                2
            ) AS pct_denuncias,

            COALESCE(c.total_clicks, 0)
                AS total_clicks,

            COALESCE(l.total_likes, 0)
                AS total_likes,

            COALESCE(f.total_favoritos, 0)
                AS total_favoritos,

            COALESCE(r.total_reviews, 0)
                AS total_reviews,

            COALESCE(v.total_review_views, 0)
                AS total_review_views,

            (
                COALESCE(c.total_clicks, 0) * 0.30
                + COALESCE(l.total_likes, 0) * 0.20
                + COALESCE(f.total_favoritos, 0) * 0.20
                + COALESCE(r.total_reviews, 0) * 0.15
                + COALESCE(v.total_review_views, 0) * 0.15
            )
            -
            (
                COALESCE(
                    d.produtos_denunciados
                    * 100.0
                    / NULLIF(p.total_produtos, 0),
                    0
                ) * 0.5
            ) AS score_ponderado

        FROM users u

        INNER JOIN produtos p
            ON p.seller_id = u.id

        LEFT JOIN denuncias d
            ON d.seller_id = u.id

        LEFT JOIN cliques c
            ON c.seller_id = u.id

        LEFT JOIN likes_total l
            ON l.seller_id = u.id

        LEFT JOIN favoritos f
            ON f.seller_id = u.id

        LEFT JOIN reviews_total r
            ON r.seller_id = u.id

        LEFT JOIN views_reviews v
            ON v.seller_id = u.id

        ORDER BY
            pct_denuncias ASC,
            total_clicks DESC,
            total_likes DESC,
            total_favoritos DESC,
            total_reviews DESC,
            total_review_views DESC

        LIMIT 20
        """;

    public List<RankingVendedorDTO> buscarTop20() {

        List<RankingVendedorDTO> ranking = new ArrayList<>();

        long inicio = System.nanoTime();

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                RankingVendedorDTO dto =
                        new RankingVendedorDTO();

                dto.setSellerId(
                        rs.getString("seller_id")
                );

                dto.setFullName(
                        rs.getString("full_name")
                );

                dto.setEmail(
                        rs.getString("email")
                );

                dto.setTotalProdutos(
                        rs.getLong("total_produtos")
                );

                dto.setProdutosDenunciados(
                        rs.getLong("produtos_denunciados")
                );

                dto.setPctDenuncias(
                        rs.getDouble("pct_denuncias")
                );

                dto.setTotalClicks(
                        rs.getLong("total_clicks")
                );

                dto.setTotalLikes(
                        rs.getLong("total_likes")
                );

                dto.setTotalFavoritos(
                        rs.getLong("total_favoritos")
                );

                dto.setTotalReviews(
                        rs.getLong("total_reviews")
                );

                dto.setTotalReviewViews(
                        rs.getLong("total_review_views")
                );

                dto.setScorePonderado(
                        rs.getDouble("score_ponderado")
                );

                ranking.add(dto);
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao executar ranking JDBC",
                    e
            );
        }

        long fim = System.nanoTime();

        System.out.printf(
                "[JDBC] Repository SQL: %.3f ms%n",
                (fim - inicio) / 1_000_000.0
        );

        return ranking;
    }
}