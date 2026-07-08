package br.edu.divulgaambulantes.generator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DataGenerator {

    // Ajuste a URL, usuário e senha conforme o laboratório
    private static final String URL = "jdbc:mysql://localhost:3306/divulgaambulantes";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        System.out.println("=== INICIANDO GERAÇÃO DE DADOS ===");
        long inicio = System.currentTimeMillis();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            // 1. Inserir 100 mil usuários
            System.out.print("Inserindo 100.000 usuários... ");
            gerarUsuarios(conn, 100_000);
            System.out.println("OK!");

            // 2. Inserir 500 mil produtos (usa usuários existentes)
            System.out.print("Inserindo 500.000 produtos... ");
            gerarProdutos(conn, 500_000);
            System.out.println("OK!");

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long fim = System.currentTimeMillis();
        double tempo = (fim - inicio) / 1000.0;
        System.out.printf("Tempo total: %.2f segundos%n", tempo);
    }

    private static void gerarUsuarios(Connection conn, int quantidade) throws SQLException {
        String sql = "INSERT INTO users (id, full_name, username, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < quantidade; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, "User " + i);
                ps.setString(3, "user_" + i);
                ps.setString(4, "user" + i + "@example.com");
                ps.addBatch();

                // Executa o lote a cada 1000 registros
                if (i % 1000 == 0) {
                    ps.executeBatch();
                    System.out.print("."); // feedback visual
                }
            }
            ps.executeBatch();
        }
    }

    private static void gerarProdutos(Connection conn, int quantidade) throws SQLException {
        // Primeiro carrega todos os IDs de usuários existentes
        List<String> userIds = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM users")) {
            while (rs.next()) {
                userIds.add(rs.getString("id"));
            }
        }

        if (userIds.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado! Execute novamente.");
            return;
        }

        Random rand = new Random();
        String[] categorias = {"comida", "roupa", "servico", "artesanato", "eletronico", "outro"};

        String sql = "INSERT INTO products (id, seller_id, title, category, price, price_type, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < quantidade; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userIds.get(rand.nextInt(userIds.size())));
                ps.setString(3, "Produto " + i);
                ps.setString(4, categorias[rand.nextInt(categorias.length)]);
                ps.setDouble(5, rand.nextDouble() * 1000);
                ps.setString(6, "fixed");
                ps.setString(7, "active");

                ps.addBatch();

                if (i % 1000 == 0) {
                    ps.executeBatch();
                    System.out.print("."); // feedback visual
                }
            }
            ps.executeBatch();
        }
    }
}