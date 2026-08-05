package br.edu.divulgaambulantes.generator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * DataGenerator — JDBC Puro
 * Popula TODAS as tabelas do schema divulgaambulantes com 300 registros cada,
 * permitindo consultas reais e testes de desempenho.
 */
public class DataGenerator {

    private static final String URL = "jdbc:mysql://localhost:3306/divulgaambulantes";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final Random rand = new Random();

public static void main(String[] args) {
    System.out.println("=== INICIANDO GERAÇÃO COMPLETA DE DADOS (JDBC) ===");
    long inicio = System.currentTimeMillis();

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        conn.setAutoCommit(false);

        // --------------------------------------------------
        // 1. USUÁRIOS (41000)
        // --------------------------------------------------
        System.out.print("Inserindo 150000 usuários... ");
        gerarUsuarios(conn, 150000);
        System.out.println("OK!");

        List<String> userIds = carregarIds(conn, "SELECT id FROM users");

        // --------------------------------------------------
        // 2. PRODUTOS (100000)
        // --------------------------------------------------
        System.out.print("Inserindo 100000 produtos... ");
        gerarProdutos(conn, 250000, userIds);
        System.out.println("OK!");

        List<String> productIds = carregarIds(conn, "SELECT id FROM products");

        // --------------------------------------------------
        // 3. LIKES (10000)
        // --------------------------------------------------
        System.out.print("Inserindo 100000 likes... ");
        gerarLikes(conn, 1000000, userIds, productIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 4. FAVORITES (80000)
        // --------------------------------------------------
        System.out.print("Inserindo 80000 favoritos... ");
        gerarFavorites(conn, 700000, userIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 5. REVIEWS (70000)
        // --------------------------------------------------
        System.out.print("Inserindo 70000 reviews... ");
        gerarReviews(conn, 400000, userIds, productIds);
        System.out.println("OK!");

        List<String> reviewIds = carregarIds(conn, "SELECT id FROM reviews");

        // --------------------------------------------------
        // 6. REVIEW_VIEWS (50000)
        // --------------------------------------------------
        System.out.print("Inserindo 50000 visualizações de reviews... ");
        gerarReviewViews(conn, 1200000, reviewIds, userIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 7. PRODUCT_CLICKS (70000)
        // --------------------------------------------------
        System.out.print("Inserindo 70000 cliques em produtos... ");
        gerarProductClicks(conn, 1000000, userIds, productIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 8. PAGE_SESSIONS (50000)
        // --------------------------------------------------
        System.out.print("Inserindo 50000 sessões de página... ");
        gerarPageSessions(conn, 50000, userIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 9. REPORTS (67000)
        // --------------------------------------------------
        System.out.print("Inserindo 67000 denúncias... ");
        gerarReports(conn, 67000, userIds, productIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 10. NOTIFICATIONS (1000)
        // --------------------------------------------------
        System.out.print("Inserindo 50000 notificações... ");
        gerarNotifications(conn, 50000, userIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 11. DEVICE_TOKENS (100000)
        // --------------------------------------------------
        System.out.print("Inserindo 100000 tokens de dispositivos... ");
        gerarDeviceTokens(conn, 100000, userIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 12. AUDIT_LOGS (50000)
        // --------------------------------------------------
        System.out.print("Inserindo 50000 logs de auditoria... ");
        gerarAuditLogs(conn, 50000, userIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 13. CONSENT_LOGS (65000)
        // --------------------------------------------------
        System.out.print("Inserindo 65000 registros de consentimento... ");
        gerarConsentLogs(conn, 65000, userIds);
        System.out.println("OK!");

        // --------------------------------------------------
        // 14. COMMUNITY_EVENTS (1000)
        // --------------------------------------------------
        System.out.print("Inserindo 50000 eventos comunitários... ");
        gerarCommunityEvents(conn, 50000, userIds);
        System.out.println("OK!");

        conn.commit();
    } catch (Exception e) {
        e.printStackTrace();
    }

    long fim = System.currentTimeMillis();
    System.out.printf("Tempo total (JDBC): %.3f segundos%n", (fim - inicio) / 1000.0);
}

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private static List<String> carregarIds(Connection conn, String sql) throws Exception {
        List<String> ids = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) ids.add(rs.getString("id"));
        }
        return ids;
    }

    private static LocalDateTime dataAleatoria() {
        return LocalDateTime.now().minusDays(rand.nextInt(30)).minusHours(rand.nextInt(24));
    }

    // --------------------------------------------------
    // Usuários
    // --------------------------------------------------
    private static void gerarUsuarios(Connection conn, int qtd) throws Exception {
        String sql = "INSERT INTO users (id, full_name, username, email, account_type, selling_status, phone, cpf) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String[] tipos = {"buyer", "seller", "both"};
        String[] status = {"active", "inactive"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, "User " + i);
                ps.setString(3, "user_" + i);
                ps.setString(4, "user" + i + "@example.com");
                ps.setString(5, tipos[rand.nextInt(tipos.length)]);
                ps.setString(6, status[rand.nextInt(status.length)]);
                ps.setString(7, "+558199999" + (1000 + i));
                ps.setString(8, String.format("%011d", 10000000000L + i));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Produtos
    // --------------------------------------------------
    private static void gerarProdutos(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO products (id, seller_id, title, category, price, price_type, status, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String[] categorias = {"comida", "roupa", "servico", "artesanato", "eletronico", "outro"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, "Produto " + i);
                ps.setString(4, categorias[rand.nextInt(categorias.length)]);
                ps.setDouble(5, Math.round(rand.nextDouble() * 1000.0 * 100.0) / 100.0);
                ps.setString(6, "fixed");
                ps.setString(7, "active");
                ps.setString(8, "Descrição do produto " + i);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Likes
    // --------------------------------------------------
    private static void gerarLikes(Connection conn, int qtd, List<String> userIds, List<String> productIds) throws Exception {
        String sql = "INSERT INTO likes (id, user_id, product_id, created_at) VALUES (?, ?, ?, ?)";
        Set<String> existentes = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int inseridos = 0;
            while (inseridos < qtd) {
                String user = userIds.get(rand.nextInt(userIds.size()));
                String prod = productIds.get(rand.nextInt(productIds.size()));
                String chave = user + "-" + prod;
                if (existentes.contains(chave)) continue;
                existentes.add(chave);
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, user);
                ps.setString(3, prod);
                ps.setObject(4, dataAleatoria());
                ps.addBatch();
                inseridos++;
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Favoritos
    // --------------------------------------------------
    private static void gerarFavorites(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO favorites (id, buyer_id, seller_id, created_at) VALUES (?, ?, ?, ?)";
        Set<String> existentes = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int inseridos = 0;
            while (inseridos < qtd) {
                String buyer = userIds.get(rand.nextInt(userIds.size()));
                String seller = userIds.get(rand.nextInt(userIds.size()));
                if (buyer.equals(seller)) continue;
                String chave = buyer + "-" + seller;
                if (existentes.contains(chave)) continue;
                existentes.add(chave);
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, buyer);
                ps.setString(3, seller);
                ps.setObject(4, dataAleatoria());
                ps.addBatch();
                inseridos++;
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Reviews
    // --------------------------------------------------
    private static void gerarReviews(Connection conn, int qtd, List<String> userIds, List<String> productIds) throws Exception {
        String sql = "INSERT INTO reviews (id, reviewer_id, seller_id, product_id, rating, comment, is_public, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String[] comentarios = {"Ótimo produto!", "Muito bom, recomendo.", "Razoável.", "Não gostei.", "Excelente vendedor!"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(4, productIds.get(rand.nextInt(productIds.size())));
                ps.setInt(5, rand.nextInt(5) + 1);
                ps.setString(6, comentarios[rand.nextInt(comentarios.length)]);
                ps.setBoolean(7, true);
                ps.setObject(8, dataAleatoria());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Review Views
    // --------------------------------------------------
    private static void gerarReviewViews(Connection conn, int qtd, List<String> reviewIds, List<String> userIds) throws Exception {
        String sql = "INSERT INTO review_views (id, review_id, viewer_id, viewed_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, reviewIds.get(rand.nextInt(reviewIds.size())));
                ps.setString(3, userIds.get(rand.nextInt(userIds.size())));
                ps.setObject(4, dataAleatoria());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Product Clicks
    // --------------------------------------------------
    private static void gerarProductClicks(Connection conn, int qtd, List<String> userIds, List<String> productIds) throws Exception {
        String sql = "INSERT INTO product_clicks (id, user_id, product_id, clicked_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, productIds.get(rand.nextInt(productIds.size())));
                ps.setObject(4, dataAleatoria());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Page Sessions
    // --------------------------------------------------
    private static void gerarPageSessions(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO page_sessions (id, user_id, page, ref_id, entered_at, left_at) VALUES (?, ?, ?, ?, ?, ?)";
        String[] paginas = {"home", "product_detail", "profile", "map"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                LocalDateTime entrada = dataAleatoria();
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, paginas[rand.nextInt(paginas.length)]);
                ps.setString(4, UUID.randomUUID().toString());
                ps.setObject(5, entrada);
                ps.setObject(6, entrada.plusMinutes(rand.nextInt(30) + 1));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Reports
    // --------------------------------------------------
    private static void gerarReports(Connection conn, int qtd, List<String> userIds, List<String> productIds) throws Exception {
        String sql = "INSERT INTO reports (id, reporter_id, target_product_id, reason, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        String[] motivos = {"spam", "fake", "inappropriate", "scam", "other"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, productIds.get(rand.nextInt(productIds.size())));
                ps.setString(4, motivos[rand.nextInt(motivos.length)]);
                ps.setString(5, "pending");
                ps.setObject(6, dataAleatoria());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Notifications
    // --------------------------------------------------
    private static void gerarNotifications(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO notifications (id, user_id, type, title, body, is_read, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String[] tipos = {"new_message", "new_like", "new_follower", "product_sold", "system"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, tipos[rand.nextInt(tipos.length)]);
                ps.setString(4, "Título da notificação " + i);
                ps.setString(5, "Corpo da notificação " + i);
                ps.setBoolean(6, rand.nextBoolean());
                ps.setObject(7, dataAleatoria());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Device Tokens
    // --------------------------------------------------
    private static void gerarDeviceTokens(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO device_tokens (id, user_id, token, platform, device_name, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        String[] plataformas = {"android", "ios", "web"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, "fcm_token_" + UUID.randomUUID().toString().substring(0, 8));
                ps.setString(4, plataformas[rand.nextInt(plataformas.length)]);
                ps.setString(5, "Dispositivo " + i);
                ps.setBoolean(6, rand.nextBoolean());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Audit Logs
    // --------------------------------------------------
    private static void gerarAuditLogs(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO audit_logs (action, actor_user_id, target_user_id, details, ip_address) VALUES (?, ?, ?, ?, ?)";
        String[] acoes = {"view_cpf", "delete_user", "update_profile", "login"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, acoes[rand.nextInt(acoes.length)]);
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(4, "{}");
                ps.setString(5, "192.168.1." + (rand.nextInt(255) + 1));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Consent Logs
    // --------------------------------------------------
    private static void gerarConsentLogs(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO consent_logs (id, user_id, consent_type, accepted, policy_version) VALUES (?, ?, ?, ?, ?)";
        String[] tipos = {"privacy_policy", "location", "data_processing"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, tipos[rand.nextInt(tipos.length)]);
                ps.setBoolean(4, rand.nextBoolean());
                ps.setString(5, "v" + (rand.nextInt(3) + 1) + ".0");
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --------------------------------------------------
    // Community Events
    // --------------------------------------------------
    private static void gerarCommunityEvents(Connection conn, int qtd, List<String> userIds) throws Exception {
        String sql = "INSERT INTO community_events (id, title, description, event_date, location_name, city, state, latitude, longitude, created_by, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] titulos = {"São João", "Carnaval", "Feira de Artesanato", "Festival Gastronômico", "Natal"};
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= qtd; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, titulos[rand.nextInt(titulos.length)] + " #" + i);
                ps.setString(3, "Descrição do evento " + i);
                ps.setObject(4, LocalDateTime.now().plusDays(rand.nextInt(60)));
                ps.setString(5, "Local do evento " + i);
                ps.setString(6, "Recife");
                ps.setString(7, "PE");
                ps.setDouble(8, -8.05 + rand.nextDouble() * 0.1);
                ps.setDouble(9, -34.9 + rand.nextDouble() * 0.1);
                ps.setString(10, userIds.get(rand.nextInt(userIds.size())));
                ps.setBoolean(11, true);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}