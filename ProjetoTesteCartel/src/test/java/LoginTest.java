import org.example.base.BaseTest;
import org.example.base.Configuracao;
import org.example.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Cenario CT01 - Login no painel (Area do barbeiro). */
public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "CT01.2 - Senha errada nao entra e mostra aviso")
    public void loginComSenhaErrada() {
        abrir("/admin/login");
        LoginPage page = new LoginPage(getDriver());

        String mensagem = page.informarUsuario("admin")
                .informarSenha("senhaErrada")
                .clicarEntrar()
                .buscarMensagemErro();

        Assert.assertTrue(mensagem.contains("Usuário ou senha incorretos"), "Mensagem recebida: " + mensagem);
        Assert.assertTrue(getDriver().getCurrentUrl().endsWith("/admin/login"), "Nao deveria sair da tela de login");
    }

    @Test(priority = 2, description = "CT01.3 - Usuario que nao existe recebe a mesma mensagem (nao revela quem existe)")
    public void loginComUsuarioInexistente() {
        abrir("/admin/login");
        LoginPage page = new LoginPage(getDriver());

        String mensagem = page.informarUsuario("ninguem")
                .informarSenha("qualquer123")
                .clicarEntrar()
                .buscarMensagemErro();

        Assert.assertTrue(mensagem.contains("Usuário ou senha incorretos"), "Mensagem recebida: " + mensagem);
    }

    @Test(priority = 3, description = "CT01.1 - Login do dono com usuario e senha corretos abre o painel")
    public void loginValido() {
        abrir("/admin/login");
        LoginPage page = new LoginPage(getDriver());

        String endereco = page.informarUsuario(Configuracao.get("admin.usuario"))
                .informarSenha(Configuracao.get("admin.senha"))
                .clicarEntrar()
                .esperarPainel();

        Assert.assertTrue(endereco.endsWith("/admin"), "Endereco depois do login: " + endereco);
    }
}
