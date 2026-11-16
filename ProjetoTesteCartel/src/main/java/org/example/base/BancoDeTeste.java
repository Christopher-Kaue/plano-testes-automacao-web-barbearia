package org.example.base;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Acesso ao banco de TESTE: recria a massa de dados e consulta resultados para conferir nos testes. */
public final class BancoDeTeste {

    private BancoDeTeste() {}

    private static Connection conectar() throws SQLException {
        String[] c = Configuracao.bancoJdbc();
        // Trava de seguranca: so mexe em banco cujo nome termina com "_teste".
        if (!c[0].endsWith("_teste")) {
            throw new IllegalStateException("Recusado: o banco " + c[0] + " nao e um banco de teste (precisa terminar com _teste).");
        }
        if (!c[0].contains("127.0.0.1") && !c[0].contains("localhost")) {
            throw new IllegalStateException("Recusado: o banco de teste precisa ser local, nao " + c[0]);
        }
        return DriverManager.getConnection(c[0], c[1], c[2]);
    }

    /** Apaga tudo e recria os dados conhecidos (src/main/resources/massa-de-dados.sql). */
    public static void recriarMassaDeDados() {
        try (InputStream in = BancoDeTeste.class.getResourceAsStream("/massa-de-dados.sql");
             Connection con = conectar();
             Statement st = con.createStatement()) {
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            st.execute(sql);
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Falha ao preparar o banco de teste: " + e.getMessage(), e);
        }
    }

    /** Status atual de um agendamento (confirmado, concluido, cancelado, faltou). */
    public static String statusDoAgendamento(int id) {
        return texto("SELECT status FROM agendamento WHERE id = ?", id);
    }

    /** Forma de pagamento gravada no agendamento. */
    public static String formaDoAgendamento(int id) {
        return texto("SELECT forma_pagamento FROM agendamento WHERE id = ?", id);
    }

    /** Soma do que entrou no caixa para um agendamento, por forma: "dinheiro=20.00;pix=10.00". */
    public static String entradasDoAgendamento(int id) {
        return texto("SELECT string_agg(forma_pagamento || '=' || valor, ';' ORDER BY forma_pagamento) FROM lancamento "
                + "WHERE agendamento_id = ? AND categoria = 'servico'", id);
    }

    /** Quantos agendamentos um telefone tem (para conferir o que o site gravou). */
    public static int agendamentosDoTelefone(String telefone) {
        return Integer.parseInt(texto("SELECT count(*)::text FROM agendamento WHERE cliente_telefone = ?", telefone));
    }

    private static String texto(String sql, Object parametro) {
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao consultar o banco de teste: " + e.getMessage(), e);
        }
    }
}
