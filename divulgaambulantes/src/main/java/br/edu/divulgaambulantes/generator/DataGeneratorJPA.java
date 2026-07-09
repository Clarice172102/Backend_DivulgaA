package br.edu.divulgaambulantes.generator;

import br.edu.divulgaambulantes.entity.Product;
import br.edu.divulgaambulantes.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DataGeneratorJPA {

    private static final Random rand = new Random();
    private static final String[] categorias = {"comida", "roupa", "servico", "artesanato", "eletronico", "outro"};

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        System.out.println("=== INICIANDO GERAÇÃO DE DADOS COM JPA ===");
        long inicio = System.currentTimeMillis();

        try {
            em.getTransaction().begin();

            // Inserir 100.000 usuários
            System.out.print("Inserindo 100.000 usuários... ");
            for (int i = 0; i < 100_000; i++) {
                User user = new User();
                user.setId(UUID.randomUUID()); // como UUID no banco
                user.setFullName("User " + i);
                user.setUsername("user_" + i);
                user.setEmail("user" + i + "@example.com");
                em.persist(user);

                if (i % 1000 == 0) {
                    em.flush();
                    em.clear(); // libera memória
                    System.out.print(".");
                }
            }
            em.flush();
            em.clear();
            System.out.println(" OK!");

            // Carregar IDs dos usuários inseridos
            List<UUID> userIds = em.createQuery("SELECT u.id FROM User u", UUID.class).getResultList();

            // Inserir 500.000 produtos
            System.out.print("Inserindo 500.000 produtos... ");
            for (int i = 0; i < 500_000; i++) {
                Product product = new Product();
                product.setId(UUID.randomUUID());
                User seller = new User();
                seller.setId(userIds.get(rand.nextInt(userIds.size())));
                product.setSeller(seller);
                product.setTitle("Produto " + i);
                product.setCategory(categorias[rand.nextInt(categorias.length)]);
                product.setPrice(rand.nextDouble() * 1000);
                product.setPriceType("fixed");
                product.setStatus("active");
                em.persist(product);

                if (i % 1000 == 0) {
                    em.flush();
                    em.clear();
                    System.out.print(".");
                }
            }
            em.flush();
            em.clear();
            System.out.println(" OK!");

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
            emf.close();
        }

        long fim = System.currentTimeMillis();
        double tempo = (fim - inicio) / 1000.0;
        System.out.printf("Tempo total (JPA): %.2f segundos%n", tempo);
    }
}
