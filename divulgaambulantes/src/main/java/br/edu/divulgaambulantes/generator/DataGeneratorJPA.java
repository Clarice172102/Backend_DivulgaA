package br.edu.divulgaambulantes.generator;

import br.edu.divulgaambulantes.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * DataGeneratorJPA — EclipseLink
 * Popula TODAS as tabelas do schema divulgaambulantes com dados de exemplo,
 * usando JPA para persistência.
 */
public class DataGeneratorJPA {

    private static final Random rand = new Random();
    private static final String[] categorias = {"comida", "roupa", "servico", "artesanato", "eletronico", "outro"};
    private static final String[] comentarios = {"Ótimo produto!", "Muito bom, recomendo.", "Razoável.", "Não gostei.", "Excelente vendedor!"};
    private static final String[] motivos = {"spam", "fake", "inappropriate", "scam", "other"};
    private static final String[] tiposNotif = {"new_message", "new_like", "new_follower", "product_sold", "system"};
    private static final String[] paginas = {"home", "product_detail", "profile", "map"};
    private static final String[] plataformas = {"android", "ios", "web"};
    private static final String[] titulosEventos = {"São João", "Carnaval", "Feira de Artesanato", "Festival Gastronômico", "Natal"};

    /** Gera um LocalDateTime aleatório nos últimos 30 dias */
    private static LocalDateTime dataAleatoria() {
        return LocalDateTime.now().minusDays(rand.nextInt(30)).minusHours(rand.nextInt(24));
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        System.out.println("=== INICIANDO GERAÇÃO COMPLETA DE DADOS (JPA) ===");
        long inicio = System.currentTimeMillis();

        try {
            em.getTransaction().begin();

            // ============================================================
            // 1. USUÁRIOS (50)
            // ============================================================
            System.out.print("Inserindo 50 usuários... ");
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                User u = new User();
                u.setId(UUID.randomUUID());
                u.setFullName("User " + i);
                u.setUsername("user_" + i);
                u.setEmail("user" + i + "@example.com");
                u.setAccountType(i % 2 == 0 ? "seller" : "buyer");
                u.setSellingStatus(rand.nextBoolean() ? "active" : "inactive");
                u.setPhone("+558199999" + (1000 + i));
                u.setCpf(String.format("%011d", 10000000000L + i));
                em.persist(u);
                users.add(u);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 2. PRODUTOS (200)
            // ============================================================
            System.out.print("Inserindo 200 produtos... ");
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 200; i++) {
                Product p = new Product();
                p.setId(UUID.randomUUID());
                p.setSeller(users.get(rand.nextInt(users.size())));
                p.setTitle("Produto " + i);
                p.setCategory(categorias[rand.nextInt(categorias.length)]);
                p.setPrice(new BigDecimal(rand.nextDouble() * 1000).setScale(2, RoundingMode.HALF_UP));
                p.setPriceType("fixed");
                p.setStatus("active");
                p.setDescription("Descrição do produto " + i);
                em.persist(p);
                products.add(p);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 3. LIKES (100)
            // ============================================================
            System.out.print("Inserindo 100 likes... ");
            for (int i = 0; i < 100; i++) {
                Like like = new Like();
                like.setId(UUID.randomUUID());
                like.setUser(users.get(rand.nextInt(users.size())));
                like.setProduct(products.get(rand.nextInt(products.size())));
                like.setCreatedAt(dataAleatoria());
                em.persist(like);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 4. FAVORITES (80)
            // ============================================================
            System.out.print("Inserindo 80 favoritos... ");
            for (int i = 0; i < 80; i++) {
                User buyer = users.get(rand.nextInt(users.size()));
                User seller = users.get(rand.nextInt(users.size()));
                while (seller.getId().equals(buyer.getId())) {
                    seller = users.get(rand.nextInt(users.size()));
                }
                Favorite fav = new Favorite();
                fav.setId(UUID.randomUUID());
                fav.setBuyer(buyer);
                fav.setSeller(seller);
                fav.setCreatedAt(dataAleatoria());
                em.persist(fav);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 5. REVIEWS (60)
            // ============================================================
            System.out.print("Inserindo 60 reviews... ");
            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                Review r = new Review();
                r.setId(UUID.randomUUID());
                r.setReviewer(users.get(rand.nextInt(users.size())));
                r.setSeller(users.get(rand.nextInt(users.size())));
                r.setProduct(products.get(rand.nextInt(products.size())));
                r.setRating((short) (rand.nextInt(5) + 1));
                r.setComment(comentarios[rand.nextInt(comentarios.length)]);
                r.setIsPublic(true);
                r.setCreatedAt(dataAleatoria());
                em.persist(r);
                reviews.add(r);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 6. REVIEW_VIEWS (40)
            // ============================================================
            System.out.print("Inserindo 40 visualizações de reviews... ");
            for (int i = 0; i < 40; i++) {
                ReviewView rv = new ReviewView();
                rv.setId(UUID.randomUUID());
                rv.setReview(reviews.get(rand.nextInt(reviews.size())));
                rv.setViewer(users.get(rand.nextInt(users.size())));
                rv.setViewedAt(dataAleatoria());
                em.persist(rv);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 7. PRODUCT_CLICKS (150)
            // ============================================================
            System.out.print("Inserindo 150 cliques em produtos... ");
            for (int i = 0; i < 150; i++) {
                ProductClick pc = new ProductClick();
                pc.setId(UUID.randomUUID());
                pc.setUser(users.get(rand.nextInt(users.size())));
                pc.setProduct(products.get(rand.nextInt(products.size())));
                pc.setClickedAt(dataAleatoria());
                em.persist(pc);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 8. PAGE_SESSIONS (30)
            // ============================================================
            System.out.print("Inserindo 30 sessões de página... ");
            for (int i = 0; i < 30; i++) {
                PageSession ps = new PageSession();
                ps.setId(UUID.randomUUID());
                ps.setUser(users.get(rand.nextInt(users.size())));
                ps.setPage(paginas[rand.nextInt(paginas.length)]);
                ps.setRefId(UUID.randomUUID());
                LocalDateTime entrada = dataAleatoria();
                ps.setEnteredAt(entrada);
                ps.setLeftAt(entrada.plusMinutes(rand.nextInt(30) + 1));
                em.persist(ps);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 9. REPORTS (10)
            // ============================================================
            System.out.print("Inserindo 10 denúncias... ");
            for (int i = 0; i < 10; i++) {
                Report rep = new Report();
                rep.setId(UUID.randomUUID());
                rep.setReporter(users.get(rand.nextInt(users.size())));
                rep.setTargetProduct(products.get(rand.nextInt(products.size())));
                rep.setReason(motivos[rand.nextInt(motivos.length)]);
                rep.setStatus("pending");
                rep.setCreatedAt(dataAleatoria());
                em.persist(rep);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 10. NOTIFICATIONS (50)
            // ============================================================
            System.out.print("Inserindo 50 notificações... ");
            for (int i = 0; i < 50; i++) {
                Notification n = new Notification();
                n.setId(UUID.randomUUID());
                n.setUser(users.get(rand.nextInt(users.size())));
                n.setType(tiposNotif[rand.nextInt(tiposNotif.length)]);
                n.setTitle("Título da notificação " + i);
                n.setBody("Corpo da notificação " + i);
                n.setIsRead(rand.nextBoolean());
                n.setCreatedAt(dataAleatoria());
                em.persist(n);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 11. DEVICE_TOKENS (30)
            // ============================================================
            System.out.print("Inserindo 30 tokens de dispositivos... ");
            for (int i = 0; i < 30; i++) {
                DeviceToken dt = new DeviceToken();
                dt.setId(UUID.randomUUID());
                dt.setUser(users.get(rand.nextInt(users.size())));
                dt.setToken("fcm_token_" + UUID.randomUUID().toString().substring(0, 8));
                dt.setPlatform(plataformas[rand.nextInt(plataformas.length)]);
                dt.setDeviceName("Dispositivo " + i);
                dt.setIsActive(rand.nextBoolean());
                em.persist(dt);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 12. AUDIT_LOGS (20)
            // ============================================================
            System.out.print("Inserindo 20 logs de auditoria... ");
            String[] acoes = {"view_cpf", "delete_user", "update_profile", "login"};
            for (int i = 0; i < 20; i++) {
                AuditLog al = new AuditLog();
                al.setAction(acoes[rand.nextInt(acoes.length)]);
                al.setActor(users.get(rand.nextInt(users.size())));
                al.setTargetUser(users.get(rand.nextInt(users.size())));
                al.setDetails("{}");
                al.setIpAddress("192.168.1." + (rand.nextInt(255) + 1));
                em.persist(al);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 13. CONSENT_LOGS (15)
            // ============================================================
            System.out.print("Inserindo 15 registros de consentimento... ");
            String[] tiposConsent = {"privacy_policy", "location", "data_processing"};
            for (int i = 0; i < 15; i++) {
                ConsentLog cl = new ConsentLog();
                cl.setId(UUID.randomUUID());
                cl.setUser(users.get(rand.nextInt(users.size())));
                cl.setConsentType(tiposConsent[rand.nextInt(tiposConsent.length)]);
                cl.setAccepted(rand.nextBoolean());
                cl.setPolicyVersion("v" + (rand.nextInt(3) + 1) + ".0");
                em.persist(cl);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            // ============================================================
            // 14. COMMUNITY_EVENTS (5)
            // ============================================================
            System.out.print("Inserindo 5 eventos comunitários... ");
            for (int i = 0; i < 5; i++) {
                CommunityEvent ce = new CommunityEvent();
                ce.setId(UUID.randomUUID());
                ce.setTitle(titulosEventos[i]);
                ce.setDescription("Evento " + titulosEventos[i]);
                ce.setEventDate(LocalDateTime.now().plusDays(rand.nextInt(60)));
                ce.setLocationName("Local do evento " + i);
                ce.setCity("Recife");
                ce.setState("PE");
                ce.setLatitude(-8.05 + rand.nextDouble() * 0.1);
                ce.setLongitude(-34.9 + rand.nextDouble() * 0.1);
                ce.setCreatedBy(users.get(rand.nextInt(users.size())));
                ce.setIsActive(true);
                em.persist(ce);
            }
            em.flush(); em.clear();
            System.out.println("OK!");

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
            emf.close();
        }

        long fim = System.currentTimeMillis();
        System.out.printf("Tempo total (JPA): %.3f segundos%n", (fim - inicio) / 1000.0);
    }
}