package br.edu.divulgaambulantes.generator;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

/**
 * DataGenerator — JDBC puro.
 *
 * Gera grande volume de dados relacionados para testes comparativos
 * entre JDBC e JPA.
 *
 * SCALE = 1: carga de validação.
 * SCALE = 7: carga grande, com aproximadamente 700 mil usuários.
 *
 * ATENÇÃO:
 * - SCALE = 7 pode levar muitas horas.
 * - Verifique o espaço em disco antes de executar.
 * - O programa limpa as tabelas antes de começar.
 */
public class DataGenerator {

    private static final String URL =
            "jdbc:mysql://localhost:3306/divulgaambulantes"
                    + "?rewriteBatchedStatements=true"
                    + "&useServerPrepStmts=false"
                    + "&cachePrepStmts=true"
                    + "&useUnicode=true"
                    + "&characterEncoding=UTF-8";

    private static final String USER = "root";
    private static final String PASSWORD = "root";

    //private static final int SCALE = 7;
    private static final int SCALE = 1;
    private static final int BATCH_SIZE = 1_000;
    private static final int PROGRESS_INTERVAL = 50_000;

    private static final int QTD_USERS = 100_000 * SCALE;              // 700.000
    private static final int QTD_PRODUCTS = 300_000 * SCALE;           // 2.100.000
    private static final int QTD_LIKES = 500_000 * SCALE;              // 3.500.000
    private static final int QTD_FAVORITES = 300_000 * SCALE;          // 2.100.000
    private static final int QTD_REVIEWS = 300_000 * SCALE;            // 2.100.000
    private static final int QTD_REVIEW_VIEWS = 500_000 * SCALE;       // 3.500.000
    private static final int QTD_PRODUCT_CLICKS = 800_000 * SCALE;     // 5.600.000
    private static final int QTD_PAGE_SESSIONS = 150_000 * SCALE;      // 1.050.000
    private static final int QTD_REPORTS = 100_000 * SCALE;            // 700.000
    private static final int QTD_NOTIFICATIONS = 200_000 * SCALE;      // 1.400.000
    private static final int QTD_DEVICE_TOKENS = 100_000 * SCALE;      // 700.000
    private static final int QTD_AUDIT_LOGS = 150_000 * SCALE;         // 1.050.000
    private static final int QTD_CONSENT_LOGS = 120_000 * SCALE;       // 840.000
    private static final int QTD_COMMUNITY_EVENTS = 50_000 * SCALE;    // 350.000

    private static final Random RAND = new Random(20260805L);

    private static final String TEXTO_1K = "X".repeat(1_000);
    private static final String TEXTO_500 = "Y".repeat(500);
    private static final String TEXTO_300 = "Z".repeat(300);

    public static void main(String[] args) {
        imprimirCabecalho();
        long inicioTotal = System.currentTimeMillis();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            executarEtapa("LIMPEZA DAS TABELAS", 0, () -> limparTabelas(conn));
            executarEtapa("USUÁRIOS", QTD_USERS, () -> gerarUsuarios(conn));
            executarEtapa("PRODUTOS", QTD_PRODUCTS, () -> gerarProdutos(conn));
            executarEtapa("LIKES", QTD_LIKES, () -> gerarLikes(conn));
            executarEtapa("FAVORITOS", QTD_FAVORITES, () -> gerarFavorites(conn));
            executarEtapa("REVIEWS", QTD_REVIEWS, () -> gerarReviews(conn));
            executarEtapa("VISUALIZAÇÕES DE REVIEWS", QTD_REVIEW_VIEWS,
                    () -> gerarReviewViews(conn));
            executarEtapa("CLIQUES EM PRODUTOS", QTD_PRODUCT_CLICKS,
                    () -> gerarProductClicks(conn));
            executarEtapa("SESSÕES DE PÁGINA", QTD_PAGE_SESSIONS,
                    () -> gerarPageSessions(conn));
            executarEtapa("DENÚNCIAS", QTD_REPORTS, () -> gerarReports(conn));
            executarEtapa("NOTIFICAÇÕES", QTD_NOTIFICATIONS,
                    () -> gerarNotifications(conn));
            executarEtapa("TOKENS DE DISPOSITIVOS", QTD_DEVICE_TOKENS,
                    () -> gerarDeviceTokens(conn));
            executarEtapa("LOGS DE AUDITORIA", QTD_AUDIT_LOGS,
                    () -> gerarAuditLogs(conn));
            executarEtapa("LOGS DE CONSENTIMENTO", QTD_CONSENT_LOGS,
                    () -> gerarConsentLogs(conn));
            executarEtapa("EVENTOS COMUNITÁRIOS", QTD_COMMUNITY_EVENTS,
                    () -> gerarCommunityEvents(conn));

            conn.commit();
        } catch (Exception e) {
            System.err.println("\nERRO DURANTE A GERAÇÃO:");
            e.printStackTrace();
            System.exit(1);
        }

        long fimTotal = System.currentTimeMillis();

        System.out.println("\n============================================================");
        System.out.println("GERAÇÃO JDBC FINALIZADA COM SUCESSO");
        System.out.printf("Tempo total: %.3f segundos%n",
                (fimTotal - inicioTotal) / 1000.0);
        imprimirMemoria();
        System.out.println("============================================================");
    }

    @FunctionalInterface
    private interface Etapa {
        void executar() throws Exception;
    }

    private static void executarEtapa(String nome, int quantidade, Etapa etapa)
            throws Exception {
        System.out.println("\n------------------------------------------------------------");
        System.out.println(nome);
        if (quantidade > 0) {
            System.out.printf("Quantidade planejada: %,d%n", quantidade);
        }
        System.out.println("------------------------------------------------------------");

        long inicio = System.currentTimeMillis();
        etapa.executar();
        long fim = System.currentTimeMillis();

        double segundos = (fim - inicio) / 1000.0;

        System.out.printf("%n✔ %s FINALIZADA%n", nome);
        System.out.printf("Tempo: %.3f segundos%n", segundos);

        if (quantidade > 0 && segundos > 0) {
            System.out.printf("Velocidade média: %,.0f registros/segundo%n",
                    quantidade / segundos);
        }

        imprimirMemoria();
    }

    private static void imprimirCabecalho() {
        System.out.println("============================================================");
        System.out.println("DIVULGA AMBULANTES — GERADOR JDBC");
        System.out.println("============================================================");
        System.out.printf("SCALE: %d%n", SCALE);
        System.out.printf("Usuários: %,d%n", QTD_USERS);
        System.out.printf("Produtos: %,d%n", QTD_PRODUCTS);
        System.out.printf("Likes: %,d%n", QTD_LIKES);
        System.out.printf("Favoritos: %,d%n", QTD_FAVORITES);
        System.out.printf("Reviews: %,d%n", QTD_REVIEWS);
        System.out.printf("Review views: %,d%n", QTD_REVIEW_VIEWS);
        System.out.printf("Product clicks: %,d%n", QTD_PRODUCT_CLICKS);
        System.out.println("============================================================");
    }

    private static void imprimirProgresso(
            String tabela,
            int processados,
            int total,
            long inicio
    ) {
        if (processados % PROGRESS_INTERVAL != 0 && processados != total) {
            return;
        }

        double percentual = processados * 100.0 / total;
        double segundos = (System.currentTimeMillis() - inicio) / 1000.0;
        double velocidade = segundos > 0 ? processados / segundos : 0;

        System.out.printf(
                "%s: %,d/%,d (%.2f%%) | %.1f s | %,.0f reg/s%n",
                tabela,
                processados,
                total,
                percentual,
                segundos,
                velocidade
        );
    }

    private static void imprimirMemoria() {
        Runtime runtime = Runtime.getRuntime();
        long usadaMb =
                (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long totalMb = runtime.totalMemory() / 1024 / 1024;
        long maxMb = runtime.maxMemory() / 1024 / 1024;

        System.out.printf(
                "Memória JVM: usada=%d MB | alocada=%d MB | máxima=%d MB%n",
                usadaMb,
                totalMb,
                maxMb
        );
    }

    private static UUID uuid(String tipo, long indice) {
        return UUID.nameUUIDFromBytes(
                (tipo + ":" + indice).getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String id(String tipo, long indice) {
        return uuid(tipo, indice).toString();
    }

    private static long indice(long i, int limite, long multiplicador) {
        return Math.floorMod(i * multiplicador, limite) + 1L;
    }

    private static LocalDateTime dataAleatoria() {
        return LocalDateTime.now()
                .minusDays(RAND.nextInt(365))
                .minusHours(RAND.nextInt(24))
                .minusMinutes(RAND.nextInt(60));
    }

    private static void executarLote(
            Connection conn,
            PreparedStatement ps,
            int processados
    ) throws Exception {
        if (processados % BATCH_SIZE == 0) {
            ps.executeBatch();
            ps.clearBatch();
            conn.commit();
        }
    }

    private static void finalizarLote(
            Connection conn,
            PreparedStatement ps
    ) throws Exception {
        ps.executeBatch();
        ps.clearBatch();
        conn.commit();
    }

    private static void limparTabelas(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            String[] tabelas = {
                    "review_views",
                    "product_clicks",
                    "page_sessions",
                    "community_events",
                    "consent_logs",
                    "audit_logs",
                    "events",
                    "device_tokens",
                    "notifications",
                    "reports",
                    "likes",
                    "favorites",
                    "reviews",
                    "locations",
                    "products",
                    "users"
            };

            for (String tabela : tabelas) {
                System.out.println("Limpando " + tabela + "...");
                stmt.execute("TRUNCATE TABLE " + tabela);
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            conn.commit();
        }
    }

    private static void gerarUsuarios(Connection conn) throws Exception {
        String sql = """
                INSERT INTO users (
                    id, full_name, username, email, phone, bio,
                    account_type, selling_status, city, state, country,
                    cpf, privacy_accepted
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String[] tipos = {"buyer", "seller", "both"};
        String[] status = {"active", "inactive"};
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_USERS; i++) {
                ps.setString(1, id("user", i));
                ps.setString(2, "Usuário de desempenho " + i);
                ps.setString(3, "user_" + i);
                ps.setString(4, "user" + i + "@example.com");
                ps.setString(5, "+5581" + String.format("%09d",
                        i % 1_000_000_000));
                ps.setString(6, "Biografia do usuário " + i + " " + TEXTO_300);
                ps.setString(7, tipos[i % tipos.length]);
                ps.setString(8, status[i % status.length]);
                ps.setString(9, "Recife");
                ps.setString(10, "PE");
                ps.setString(11, "BR");
                ps.setString(12, String.format("%011d",
                        10_000_000_000L + i));
                ps.setBoolean(13, true);

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso("users", i, QTD_USERS, inicio);
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarProdutos(Connection conn) throws Exception {
        String sql = """
                INSERT INTO products (
                    id, seller_id, title, description, category, subcategory,
                    price, price_type, currency, status, city, state, country
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String[] categorias = {
                "comida", "roupa", "servico",
                "artesanato", "eletronico", "outro"
        };
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_PRODUCTS; i++) {
                long userIndex = indice(i, QTD_USERS, 97);

                ps.setString(1, id("product", i));
                ps.setString(2, id("user", userIndex));
                ps.setString(3, "Produto para teste de desempenho " + i);
                ps.setString(4,
                        "Descrição extensa do produto " + i + " " + TEXTO_1K);
                ps.setString(5, categorias[i % categorias.length]);
                ps.setString(6, "subcategoria_" + (i % 25));
                ps.setDouble(7, 1.0 + (i % 100_000) / 100.0);
                ps.setString(8, "fixed");
                ps.setString(9, "BRL");
                ps.setString(10, "active");
                ps.setString(11, "Recife");
                ps.setString(12, "PE");
                ps.setString(13, "BR");

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso("products", i, QTD_PRODUCTS, inicio);
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarLikes(Connection conn) throws Exception {
        String sql = """
                INSERT INTO likes (id, user_id, product_id, created_at)
                VALUES (?, ?, ?, ?)
                """;
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < QTD_LIKES; i++) {
                long userIndex = (i % (long) QTD_USERS) + 1;
                long productIndex =
                        ((i / (long) QTD_USERS) % QTD_PRODUCTS) + 1;

                ps.setString(1, id("like", i + 1L));
                ps.setString(2, id("user", userIndex));
                ps.setString(3, id("product", productIndex));
                ps.setObject(4, dataAleatoria());

                int processados = i + 1;
                ps.addBatch();
                executarLote(conn, ps, processados);
                imprimirProgresso("likes", processados, QTD_LIKES, inicio);
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarFavorites(Connection conn) throws Exception {
        String sql = """
                INSERT INTO favorites (
                    id, buyer_id, seller_id, created_at
                ) VALUES (?, ?, ?, ?)
                """;
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < QTD_FAVORITES; i++) {
                long buyer = (i % (long) QTD_USERS) + 1;
                long bloco = i / (long) QTD_USERS;
                long seller = ((buyer + bloco) % QTD_USERS) + 1;

                ps.setString(1, id("favorite", i + 1L));
                ps.setString(2, id("user", buyer));
                ps.setString(3, id("user", seller));
                ps.setObject(4, dataAleatoria());

                int processados = i + 1;
                ps.addBatch();
                executarLote(conn, ps, processados);
                imprimirProgresso(
                        "favorites",
                        processados,
                        QTD_FAVORITES,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarReviews(Connection conn) throws Exception {
        String sql = """
                INSERT INTO reviews (
                    id, reviewer_id, seller_id, product_id,
                    rating, comment, is_public, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_REVIEWS; i++) {
                ps.setString(1, id("review", i));
                ps.setString(2, id("user", indice(i, QTD_USERS, 101)));
                ps.setString(3, id("user", indice(i, QTD_USERS, 103)));
                ps.setString(4, id("product", indice(i, QTD_PRODUCTS, 107)));
                ps.setInt(5, (i % 5) + 1);
                ps.setString(6,
                        "Comentário detalhado da avaliação "
                                + i + " " + TEXTO_500);
                ps.setBoolean(7, true);
                ps.setObject(8, dataAleatoria());

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso("reviews", i, QTD_REVIEWS, inicio);
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarReviewViews(Connection conn) throws Exception {
        String sql = """
                INSERT INTO review_views (
                    id, review_id, viewer_id, viewed_at
                ) VALUES (?, ?, ?, ?)
                """;
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_REVIEW_VIEWS; i++) {
                ps.setString(1, id("review_view", i));
                ps.setString(2,
                        id("review", indice(i, QTD_REVIEWS, 109)));
                ps.setString(3,
                        id("user", indice(i, QTD_USERS, 113)));
                ps.setObject(4, dataAleatoria());

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "review_views",
                        i,
                        QTD_REVIEW_VIEWS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarProductClicks(Connection conn) throws Exception {
        String sql = """
                INSERT INTO product_clicks (
                    id, user_id, product_id, clicked_at
                ) VALUES (?, ?, ?, ?)
                """;
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_PRODUCT_CLICKS; i++) {
                ps.setString(1, id("product_click", i));
                ps.setString(2,
                        id("user", indice(i, QTD_USERS, 127)));
                ps.setString(3,
                        id("product", indice(i, QTD_PRODUCTS, 131)));
                ps.setObject(4, dataAleatoria());

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "product_clicks",
                        i,
                        QTD_PRODUCT_CLICKS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarPageSessions(Connection conn) throws Exception {
        String sql = """
                INSERT INTO page_sessions (
                    id, user_id, page, ref_id, entered_at, left_at
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        String[] paginas = {"home", "product_detail", "profile", "map"};
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_PAGE_SESSIONS; i++) {
                LocalDateTime entrada = dataAleatoria();

                ps.setString(1, id("page_session", i));
                ps.setString(2,
                        id("user", indice(i, QTD_USERS, 137)));
                ps.setString(3, paginas[i % paginas.length]);
                ps.setString(4, id("ref", i));
                ps.setObject(5, entrada);
                ps.setObject(6, entrada.plusMinutes((i % 60) + 1));

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "page_sessions",
                        i,
                        QTD_PAGE_SESSIONS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarReports(Connection conn) throws Exception {
        String sql = """
                INSERT INTO reports (
                    id, reporter_id, target_product_id, reason,
                    description, status, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        String[] motivos = {"spam", "fake", "inappropriate", "scam", "other"};
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_REPORTS; i++) {
                ps.setString(1, id("report", i));
                ps.setString(2,
                        id("user", indice(i, QTD_USERS, 139)));
                ps.setString(3,
                        id("product", indice(i, QTD_PRODUCTS, 149)));
                ps.setString(4, motivos[i % motivos.length]);
                ps.setString(5,
                        "Descrição detalhada da denúncia "
                                + i + " " + TEXTO_300);
                ps.setString(6, "pending");
                ps.setObject(7, dataAleatoria());

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso("reports", i, QTD_REPORTS, inicio);
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarNotifications(Connection conn) throws Exception {
        String sql = """
                INSERT INTO notifications (
                    id, user_id, type, title, body, is_read, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        String[] tipos = {
                "new_message", "new_like", "new_follower",
                "product_sold", "system"
        };
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_NOTIFICATIONS; i++) {
                ps.setString(1, id("notification", i));
                ps.setString(2,
                        id("user", indice(i, QTD_USERS, 151)));
                ps.setString(3, tipos[i % tipos.length]);
                ps.setString(4, "Notificação de teste " + i);
                ps.setString(5,
                        "Corpo extenso da notificação "
                                + i + " " + TEXTO_500);
                ps.setBoolean(6, i % 2 == 0);
                ps.setObject(7, dataAleatoria());

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "notifications",
                        i,
                        QTD_NOTIFICATIONS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarDeviceTokens(Connection conn) throws Exception {
        String sql = """
                INSERT INTO device_tokens (
                    id, user_id, token, platform, device_name, is_active
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        String[] plataformas = {"android", "ios", "web"};
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_DEVICE_TOKENS; i++) {
                ps.setString(1, id("device_token", i));
                ps.setString(2,
                        id("user", indice(i, QTD_USERS, 157)));
                ps.setString(3, "fcm_" + id("token", i) + "_" + i);
                ps.setString(4, plataformas[i % plataformas.length]);
                ps.setString(5, "Dispositivo de teste " + i);
                ps.setBoolean(6, i % 3 != 0);

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "device_tokens",
                        i,
                        QTD_DEVICE_TOKENS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarAuditLogs(Connection conn) throws Exception {
        String sql = """
                INSERT INTO audit_logs (
                    action, actor_user_id, target_user_id,
                    details, ip_address
                ) VALUES (?, ?, ?, ?, ?)
                """;

        String[] acoes = {
                "view_cpf", "delete_user", "update_profile", "login"
        };
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_AUDIT_LOGS; i++) {
                ps.setString(1, acoes[i % acoes.length]);
                ps.setString(2,
                        id("user", indice(i, QTD_USERS, 163)));
                ps.setString(3,
                        id("user", indice(i, QTD_USERS, 167)));
                ps.setString(4,
                        "{\"indice\":" + i
                                + ",\"detalhes\":\"" + TEXTO_500 + "\"}");
                ps.setString(5,
                        "192.168." + (i % 255)
                                + "." + ((i * 7) % 255));

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "audit_logs",
                        i,
                        QTD_AUDIT_LOGS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarConsentLogs(Connection conn) throws Exception {
        String sql = """
                INSERT INTO consent_logs (
                    id, user_id, consent_type,
                    accepted, policy_version
                ) VALUES (?, ?, ?, ?, ?)
                """;

        String[] tipos = {
                "privacy_policy", "location", "data_processing"
        };
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_CONSENT_LOGS; i++) {
                ps.setString(1, id("consent_log", i));
                ps.setString(2,
                        id("user", indice(i, QTD_USERS, 173)));
                ps.setString(3, tipos[i % tipos.length]);
                ps.setBoolean(4, i % 4 != 0);
                ps.setString(5, "v" + ((i % 3) + 1) + ".0");

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "consent_logs",
                        i,
                        QTD_CONSENT_LOGS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }

    private static void gerarCommunityEvents(Connection conn) throws Exception {
        String sql = """
                INSERT INTO community_events (
                    id, title, description, event_date,
                    location_name, city, state,
                    latitude, longitude, created_by, is_active
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String[] titulos = {
                "São João", "Carnaval", "Feira de Artesanato",
                "Festival Gastronômico", "Natal"
        };
        long inicio = System.currentTimeMillis();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= QTD_COMMUNITY_EVENTS; i++) {
                ps.setString(1, id("community_event", i));
                ps.setString(2,
                        titulos[i % titulos.length] + " #" + i);
                ps.setString(3,
                        "Descrição extensa do evento "
                                + i + " " + TEXTO_1K);
                ps.setObject(4,
                        LocalDateTime.now().plusDays(i % 365));
                ps.setString(5, "Local do evento " + i);
                ps.setString(6, "Recife");
                ps.setString(7, "PE");
                ps.setDouble(8,
                        -8.05 + (i % 1_000) / 10_000.0);
                ps.setDouble(9,
                        -34.90 + (i % 1_000) / 10_000.0);
                ps.setString(10,
                        id("user", indice(i, QTD_USERS, 179)));
                ps.setBoolean(11, i % 5 != 0);

                ps.addBatch();
                executarLote(conn, ps, i);
                imprimirProgresso(
                        "community_events",
                        i,
                        QTD_COMMUNITY_EVENTS,
                        inicio
                );
            }

            finalizarLote(conn, ps);
        }
    }
}
