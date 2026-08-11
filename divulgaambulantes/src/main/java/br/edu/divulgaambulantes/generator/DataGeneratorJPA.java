package br.edu.divulgaambulantes.generator;

import br.edu.divulgaambulantes.entity.AuditLog;
import br.edu.divulgaambulantes.entity.CommunityEvent;
import br.edu.divulgaambulantes.entity.ConsentLog;
import br.edu.divulgaambulantes.entity.DeviceToken;
import br.edu.divulgaambulantes.entity.Favorite;
import br.edu.divulgaambulantes.entity.Like;
import br.edu.divulgaambulantes.entity.Notification;
import br.edu.divulgaambulantes.entity.PageSession;
import br.edu.divulgaambulantes.entity.Product;
import br.edu.divulgaambulantes.entity.ProductClick;
import br.edu.divulgaambulantes.entity.Report;
import br.edu.divulgaambulantes.entity.Review;
import br.edu.divulgaambulantes.entity.ReviewView;
import br.edu.divulgaambulantes.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

/**
 * DataGeneratorJPA — EclipseLink.
 *
 * Gera os mesmos volumes usados no gerador JDBC para permitir
 * uma comparação justa entre as duas abordagens.
 *
 * SCALE = 1: carga de validação.
 * SCALE = 7: carga grande, com aproximadamente 700 mil usuários.
 *
 * ATENÇÃO:
 * - SCALE = 7 pode levar muitas horas ou até dias via JPA.
 * - Verifique o espaço em disco antes de executar.
 * - O programa limpa as tabelas antes de iniciar.
 */
public class DataGeneratorJPA {

    private static final String PERSISTENCE_UNIT = "default";

    //private static final int SCALE = 7;
    private static final int SCALE = 1;
    private static final int BATCH_SIZE = 1_000;
    private static final int PROGRESS_INTERVAL = 50_000;

    private static final int QTD_USERS = 100_000 * SCALE; // 700.000
    private static final int QTD_PRODUCTS = 300_000 * SCALE; // 2.100.000
    private static final int QTD_LIKES = 500_000 * SCALE; // 3.500.000
    private static final int QTD_FAVORITES = 300_000 * SCALE; // 2.100.000
    private static final int QTD_REVIEWS = 300_000 * SCALE; // 2.100.000
    private static final int QTD_REVIEW_VIEWS = 500_000 * SCALE; // 3.500.000
    private static final int QTD_PRODUCT_CLICKS = 800_000 * SCALE; // 5.600.000
    private static final int QTD_PAGE_SESSIONS = 150_000 * SCALE; // 1.050.000
    private static final int QTD_REPORTS = 100_000 * SCALE; // 700.000
    private static final int QTD_NOTIFICATIONS = 200_000 * SCALE; // 1.400.000
    private static final int QTD_DEVICE_TOKENS = 100_000 * SCALE; // 700.000
    private static final int QTD_AUDIT_LOGS = 150_000 * SCALE; // 1.050.000
    private static final int QTD_CONSENT_LOGS = 120_000 * SCALE; // 840.000
    private static final int QTD_COMMUNITY_EVENTS = 50_000 * SCALE; // 350.000

    private static final Random RAND = new Random(20260805L);

    private static final String TEXTO_1K = "X".repeat(1_000);
    private static final String TEXTO_500 = "Y".repeat(500);

    private static final String[] CATEGORIAS = {
            "comida", "roupa", "servico",
            "artesanato", "eletronico", "outro"
    };

    private static final String[] MOTIVOS = {
            "spam", "fake", "inappropriate", "scam", "other"
    };

    private static final String[] TIPOS_NOTIFICACAO = {
            "new_message", "new_like", "new_follower",
            "product_sold", "system"
    };

    private static final String[] PAGINAS = {
            "home", "product_detail", "profile", "map"
    };

    private static final String[] PLATAFORMAS = {
            "android", "ios", "web"
    };

    private static final String[] TITULOS_EVENTOS = {
            "São João", "Carnaval", "Feira de Artesanato",
            "Festival Gastronômico", "Natal"
    };

    public static void main(String[] args) {
        imprimirCabecalho();
        long inicioTotal = System.currentTimeMillis();

        EntityManagerFactory emf = null;
        EntityManager entityManager = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            entityManager = emf.createEntityManager();

            final EntityManager em = entityManager;

            limparTabelas(em);

            executarEtapa(
                    "USUÁRIOS",
                    QTD_USERS,
                    () -> gerarUsuarios(em));

            executarEtapa(
                    "PRODUTOS",
                    QTD_PRODUCTS,
                    () -> gerarProdutos(em));

            executarEtapa(
                    "LIKES",
                    QTD_LIKES,
                    () -> gerarLikes(em));

            executarEtapa(
                    "FAVORITOS",
                    QTD_FAVORITES,
                    () -> gerarFavorites(em));

            executarEtapa(
                    "REVIEWS",
                    QTD_REVIEWS,
                    () -> gerarReviews(em));

            executarEtapa(
                    "VISUALIZAÇÕES DE REVIEWS",
                    QTD_REVIEW_VIEWS,
                    () -> gerarReviewViews(em));

            executarEtapa(
                    "CLIQUES EM PRODUTOS",
                    QTD_PRODUCT_CLICKS,
                    () -> gerarProductClicks(em));

            executarEtapa(
                    "SESSÕES DE PÁGINA",
                    QTD_PAGE_SESSIONS,
                    () -> gerarPageSessions(em));

            executarEtapa(
                    "DENÚNCIAS",
                    QTD_REPORTS,
                    () -> gerarReports(em));

            executarEtapa(
                    "NOTIFICAÇÕES",
                    QTD_NOTIFICATIONS,
                    () -> gerarNotifications(em));

            executarEtapa(
                    "TOKENS DE DISPOSITIVOS",
                    QTD_DEVICE_TOKENS,
                    () -> gerarDeviceTokens(em));

            executarEtapa(
                    "LOGS DE AUDITORIA",
                    QTD_AUDIT_LOGS,
                    () -> gerarAuditLogs(em));

            executarEtapa(
                    "LOGS DE CONSENTIMENTO",
                    QTD_CONSENT_LOGS,
                    () -> gerarConsentLogs(em));

            executarEtapa(
                    "EVENTOS COMUNITÁRIOS",
                    QTD_COMMUNITY_EVENTS,
                    () -> gerarCommunityEvents(em));

        } catch (Exception e) {
            System.err.println("Erro durante a geração JPA:");
            e.printStackTrace();

            if (entityManager != null
                    && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }

            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }

        long fimTotal = System.currentTimeMillis();

        System.out.println("\n============================================================");
        System.out.println("GERAÇÃO JPA FINALIZADA COM SUCESSO");
        System.out.printf(
                "Tempo total: %.3f segundos%n",
                (fimTotal - inicioTotal) / 1000.0);
        imprimirMemoria();
        System.out.println("============================================================");
    }

    @FunctionalInterface
    private interface Etapa {
        void executar() throws Exception;
    }

    private static void executarEtapa(
            String nome,
            int quantidade,
            Etapa etapa) throws Exception {
        System.out.println("\n------------------------------------------------------------");
        System.out.println(nome);
        System.out.printf("Quantidade planejada: %,d%n", quantidade);
        System.out.println("------------------------------------------------------------");

        long inicio = System.currentTimeMillis();
        etapa.executar();
        long fim = System.currentTimeMillis();

        double segundos = (fim - inicio) / 1000.0;

        System.out.printf("%n✔ %s FINALIZADA%n", nome);
        System.out.printf("Tempo: %.3f segundos%n", segundos);

        if (segundos > 0) {
            System.out.printf(
                    "Velocidade média: %,.0f registros/segundo%n",
                    quantidade / segundos);
        }

        imprimirMemoria();
    }

    private static void imprimirCabecalho() {
        System.out.println("============================================================");
        System.out.println("DIVULGA AMBULANTES — GERADOR JPA / ECLIPSELINK");
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
            long inicio) {
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
                velocidade);
    }

    private static void imprimirMemoria() {
        Runtime runtime = Runtime.getRuntime();

        long usadaMb = (runtime.totalMemory() - runtime.freeMemory())
                / 1024 / 1024;
        long totalMb = runtime.totalMemory() / 1024 / 1024;
        long maximaMb = runtime.maxMemory() / 1024 / 1024;

        System.out.printf(
                "Memória JVM: usada=%d MB | alocada=%d MB | máxima=%d MB%n",
                usadaMb,
                totalMb,
                maximaMb);
    }

    private static UUID uuid(String tipo, long indice) {
        return UUID.nameUUIDFromBytes(
                (tipo + ":" + indice).getBytes(StandardCharsets.UTF_8));
    }

    private static long indice(
            long i,
            int limite,
            long multiplicador) {
        return Math.floorMod(i * multiplicador, limite) + 1L;
    }

    private static LocalDateTime dataAleatoria() {
        return LocalDateTime.now()
                .minusDays(RAND.nextInt(365))
                .minusHours(RAND.nextInt(24))
                .minusMinutes(RAND.nextInt(60));
    }

    private static User userRef(EntityManager em, long indice) {
        return em.getReference(User.class, uuid("user", indice));
    }

    private static Product productRef(EntityManager em, long indice) {
        return em.getReference(Product.class, uuid("product", indice));
    }

    private static Review reviewRef(EntityManager em, long indice) {
        return em.getReference(Review.class, uuid("review", indice));
    }

    private static void iniciarTransacao(EntityManager em) {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private static void processarLote(
            EntityManager em,
            int processados) {
        if (processados % BATCH_SIZE == 0) {
            em.flush();
            em.clear();
            em.getTransaction().commit();
            em.getTransaction().begin();
        }
    }

    private static void finalizarEtapa(EntityManager em) {
        em.flush();
        em.clear();

        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private static void limparTabelas(EntityManager em) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("LIMPEZA DAS TABELAS");
        System.out.println("------------------------------------------------------------");

        em.getTransaction().begin();

        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0")
                .executeUpdate();

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
            em.createNativeQuery("TRUNCATE TABLE " + tabela)
                    .executeUpdate();
        }

        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1")
                .executeUpdate();

        em.getTransaction().commit();

        System.out.println("✔ LIMPEZA FINALIZADA");
    }

    private static void gerarUsuarios(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_USERS; i++) {
            User u = new User();

            u.setId(uuid("user", i));
            u.setFullName("Usuário de desempenho " + i);
            u.setUsername("user_" + i);
            u.setEmail("user" + i + "@example.com");
            u.setPhone(
                    "+5581" + String.format(
                            "%09d",
                            i % 1_000_000_000));
            u.setAccountType(
                    i % 3 == 0
                            ? "both"
                            : i % 2 == 0
                                    ? "seller"
                                    : "buyer");
            u.setSellingStatus(
                    i % 2 == 0 ? "active" : "inactive");
            u.setCpf(
                    String.format(
                            "%011d",
                            10_000_000_000L + i));

            em.persist(u);
            processarLote(em, i);
            imprimirProgresso("users", i, QTD_USERS, inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarProdutos(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_PRODUCTS; i++) {
            Product p = new Product();

            p.setId(uuid("product", i));
            p.setSeller(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 97)));
            p.setTitle(
                    "Produto para teste de desempenho " + i);
            p.setCategory(
                    CATEGORIAS[i % CATEGORIAS.length]);
            p.setPrice(
                    BigDecimal.valueOf(
                            1.0
                                    + (i % 100_000)
                                            / 100.0)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP));
            p.setPriceType("fixed");
            p.setStatus("active");
            p.setDescription(
                    "Descrição extensa do produto "
                            + i + " " + TEXTO_1K);

            em.persist(p);
            processarLote(em, i);
            imprimirProgresso(
                    "products",
                    i,
                    QTD_PRODUCTS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarLikes(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 0; i < QTD_LIKES; i++) {
            long userIndex = (i % (long) QTD_USERS) + 1;
            long productIndex = ((i / (long) QTD_USERS)
                    % QTD_PRODUCTS) + 1;

            Like like = new Like();

            like.setId(uuid("like", i + 1L));
            like.setUser(userRef(em, userIndex));
            like.setProduct(
                    productRef(em, productIndex));
            like.setCreatedAt(dataAleatoria());

            int processados = i + 1;

            em.persist(like);
            processarLote(em, processados);
            imprimirProgresso(
                    "likes",
                    processados,
                    QTD_LIKES,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarFavorites(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 0; i < QTD_FAVORITES; i++) {
            long buyer = (i % (long) QTD_USERS) + 1;
            long bloco = i / (long) QTD_USERS;
            long seller = ((buyer + bloco) % QTD_USERS) + 1;

            Favorite favorite = new Favorite();

            favorite.setId(
                    uuid("favorite", i + 1L));
            favorite.setBuyer(
                    userRef(em, buyer));
            favorite.setSeller(
                    userRef(em, seller));
            favorite.setCreatedAt(dataAleatoria());

            int processados = i + 1;

            em.persist(favorite);
            processarLote(em, processados);
            imprimirProgresso(
                    "favorites",
                    processados,
                    QTD_FAVORITES,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarReviews(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_REVIEWS; i++) {
            Review review = new Review();

            review.setId(uuid("review", i));
            review.setReviewer(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 101)));
            review.setSeller(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 103)));
            review.setProduct(
                    productRef(
                            em,
                            indice(i, QTD_PRODUCTS, 107)));
            review.setRating(
                    (short) ((i % 5) + 1));
            review.setComment(
                    "Comentário detalhado da avaliação "
                            + i + " " + TEXTO_500);
            review.setIsPublic(true);
            review.setCreatedAt(dataAleatoria());

            em.persist(review);
            processarLote(em, i);
            imprimirProgresso(
                    "reviews",
                    i,
                    QTD_REVIEWS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarReviewViews(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_REVIEW_VIEWS; i++) {
            ReviewView reviewView = new ReviewView();

            reviewView.setId(
                    uuid("review_view", i));
            reviewView.setReview(
                    reviewRef(
                            em,
                            indice(i, QTD_REVIEWS, 109)));
            reviewView.setViewer(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 113)));
            reviewView.setViewedAt(dataAleatoria());

            em.persist(reviewView);
            processarLote(em, i);
            imprimirProgresso(
                    "review_views",
                    i,
                    QTD_REVIEW_VIEWS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarProductClicks(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_PRODUCT_CLICKS; i++) {
            ProductClick click = new ProductClick();

            click.setId(
                    uuid("product_click", i));
            click.setUser(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 127)));
            click.setProduct(
                    productRef(
                            em,
                            indice(i, QTD_PRODUCTS, 131)));
            click.setClickedAt(dataAleatoria());

            em.persist(click);
            processarLote(em, i);
            imprimirProgresso(
                    "product_clicks",
                    i,
                    QTD_PRODUCT_CLICKS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarPageSessions(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_PAGE_SESSIONS; i++) {
            LocalDateTime entrada = dataAleatoria();

            PageSession session = new PageSession();

            session.setId(
                    uuid("page_session", i));
            session.setUser(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 137)));
            session.setPage(
                    PAGINAS[i % PAGINAS.length]);
            session.setRefId(uuid("ref", i));
            session.setEnteredAt(entrada);
            session.setLeftAt(
                    entrada.plusMinutes((i % 60) + 1));

            em.persist(session);
            processarLote(em, i);
            imprimirProgresso(
                    "page_sessions",
                    i,
                    QTD_PAGE_SESSIONS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarReports(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_REPORTS; i++) {
            Report report = new Report();

            report.setId(uuid("report", i));
            report.setReporter(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 139)));
            report.setTargetProduct(
                    productRef(
                            em,
                            indice(i, QTD_PRODUCTS, 149)));
            report.setReason(
                    MOTIVOS[i % MOTIVOS.length]);
            report.setStatus("pending");
            report.setCreatedAt(dataAleatoria());

            em.persist(report);
            processarLote(em, i);
            imprimirProgresso(
                    "reports",
                    i,
                    QTD_REPORTS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarNotifications(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_NOTIFICATIONS; i++) {
            Notification notification = new Notification();

            notification.setId(
                    uuid("notification", i));
            notification.setUser(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 151)));
            notification.setType(
                    TIPOS_NOTIFICACAO[i % TIPOS_NOTIFICACAO.length]);
            notification.setTitle(
                    "Notificação de teste " + i);
            notification.setBody(
                    "Corpo extenso da notificação "
                            + i + " " + TEXTO_500);
            notification.setIsRead(i % 2 == 0);
            notification.setCreatedAt(dataAleatoria());

            em.persist(notification);
            processarLote(em, i);
            imprimirProgresso(
                    "notifications",
                    i,
                    QTD_NOTIFICATIONS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarDeviceTokens(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_DEVICE_TOKENS; i++) {
            DeviceToken token = new DeviceToken();

            token.setId(uuid("device_token", i));
            token.setUser(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 157)));
            token.setToken(
                    "fcm_" + uuid("token", i) + "_" + i);
            token.setPlatform(
                    PLATAFORMAS[i % PLATAFORMAS.length]);
            token.setDeviceName(
                    "Dispositivo de teste " + i);
            token.setIsActive(i % 3 != 0);

            em.persist(token);
            processarLote(em, i);
            imprimirProgresso(
                    "device_tokens",
                    i,
                    QTD_DEVICE_TOKENS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarAuditLogs(EntityManager em) {
        String[] acoes = {
                "view_cpf",
                "delete_user",
                "update_profile",
                "login"
        };

        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_AUDIT_LOGS; i++) {
            AuditLog auditLog = new AuditLog();

            auditLog.setAction(
                    acoes[i % acoes.length]);
            auditLog.setActor(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 163)));
            auditLog.setTargetUser(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 167)));
            auditLog.setDetails(
                    "{\"indice\":" + i
                            + ",\"detalhes\":\""
                            + TEXTO_500 + "\"}");
            auditLog.setIpAddress(
                    "192.168." + (i % 255)
                            + "." + ((i * 7) % 255));

            em.persist(auditLog);
            processarLote(em, i);
            imprimirProgresso(
                    "audit_logs",
                    i,
                    QTD_AUDIT_LOGS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarConsentLogs(EntityManager em) {
        String[] tiposConsentimento = {
                "privacy_policy",
                "location",
                "data_processing"
        };

        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_CONSENT_LOGS; i++) {
            ConsentLog consentLog = new ConsentLog();

            consentLog.setId(
                    uuid("consent_log", i));
            consentLog.setUser(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 173)));
            consentLog.setConsentType(
                    tiposConsentimento[i % tiposConsentimento.length]);
            consentLog.setAccepted(i % 4 != 0);
            consentLog.setPolicyVersion(
                    "v" + ((i % 3) + 1) + ".0");

            em.persist(consentLog);
            processarLote(em, i);
            imprimirProgresso(
                    "consent_logs",
                    i,
                    QTD_CONSENT_LOGS,
                    inicio);
        }

        finalizarEtapa(em);
    }

    private static void gerarCommunityEvents(EntityManager em) {
        long inicio = System.currentTimeMillis();
        iniciarTransacao(em);

        for (int i = 1; i <= QTD_COMMUNITY_EVENTS; i++) {
            CommunityEvent event = new CommunityEvent();

            event.setId(
                    uuid("community_event", i));
            event.setTitle(
                    TITULOS_EVENTOS[i % TITULOS_EVENTOS.length] + " #" + i);
            event.setDescription(
                    "Descrição extensa do evento "
                            + i + " " + TEXTO_1K);
            event.setEventDate(
                    LocalDateTime.now()
                            .plusDays(i % 365));
            event.setLocationName(
                    "Local do evento " + i);
            event.setCity("Recife");
            event.setState("PE");
            event.setLatitude(
                    -8.05
                            + (i % 1_000) / 10_000.0);
            event.setLongitude(
                    -34.90
                            + (i % 1_000) / 10_000.0);
            event.setCreatedBy(
                    userRef(
                            em,
                            indice(i, QTD_USERS, 179)));
            event.setIsActive(i % 5 != 0);

            em.persist(event);
            processarLote(em, i);
            imprimirProgresso(
                    "community_events",
                    i,
                    QTD_COMMUNITY_EVENTS,
                    inicio);
        }

        finalizarEtapa(em);
    }
}