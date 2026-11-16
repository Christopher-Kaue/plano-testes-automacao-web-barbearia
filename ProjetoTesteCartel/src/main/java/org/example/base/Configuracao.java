package org.example.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Le o config.properties e a conexao do banco de TESTE (do .env.local da copia do sistema). */
public final class Configuracao {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = Configuracao.class.getResourceAsStream("/config.properties")) {
            props.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Nao encontrei src/main/resources/config.properties", e);
        }
    }

    private Configuracao() {}

    public static String get(String chave) {
        String valor = System.getProperty(chave, props.getProperty(chave));
        if (valor == null) throw new IllegalStateException("Falta a chave '" + chave + "' no config.properties");
        return valor.trim();
    }

    public static String urlBase() { return get("url.base"); }
    public static int esperaSegundos() { return Integer.parseInt(get("espera.segundos")); }

    /** Conexao JDBC montada a partir do DATABASE_URL (postgresql://usuario:senha@host:porta/banco). */
    public static String[] bancoJdbc() {
        Path env = Path.of(System.getProperty("user.dir")).resolve(get("sistema.env")).normalize();
        try {
            for (String linha : Files.readAllLines(env, StandardCharsets.UTF_8)) {
                if (linha.trim().startsWith("DATABASE_URL=")) {
                    URI uri = URI.create(linha.trim().substring("DATABASE_URL=".length()));
                    String[] credenciais = uri.getRawUserInfo().split(":", 2);
                    String usuario = URLDecoder.decode(credenciais[0], StandardCharsets.UTF_8);
                    String senha = credenciais.length > 1 ? URLDecoder.decode(credenciais[1], StandardCharsets.UTF_8) : "";
                    int porta = uri.getPort() == -1 ? 5432 : uri.getPort();
                    String jdbc = "jdbc:postgresql://" + uri.getHost() + ":" + porta + uri.getPath();
                    return new String[] { jdbc, usuario, senha };
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Nao consegui ler " + env, e);
        }
        throw new IllegalStateException("DATABASE_URL nao encontrada em " + env);
    }
}
