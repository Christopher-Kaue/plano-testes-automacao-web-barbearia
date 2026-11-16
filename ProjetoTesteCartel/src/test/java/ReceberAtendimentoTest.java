import org.example.base.BancoDeTeste;
import org.example.base.BaseTest;
import org.example.base.Configuracao;
import org.example.pages.AgendaPainelPage;
import org.example.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Cenario CT04 - Receber atendimento no painel (aba Agenda).
 * Massa de dados: atendimentos de hoje 5, 6 e 7, Corte degradê de R$ 30,00, sem forma de pagamento definida.
 */
public class ReceberAtendimentoTest extends BaseTest {

    @BeforeClass
    public void entrarNoPainel() {
        abrir("/admin/login");
        new LoginPage(getDriver())
                .informarUsuario(Configuracao.get("admin.usuario"))
                .informarSenha(Configuracao.get("admin.senha"))
                .clicarEntrar()
                .esperarPainel();
    }

    @Test(priority = 1, description = "CT04.1 - Receber o atendimento todo no Pix")
    public void receberNoPix() {
        AgendaPainelPage page = new AgendaPainelPage(getDriver());

        page.clicarReceber(5)
                .selecionarForma("pix");
        Assert.assertEquals(page.buscarTextoBotaoReceber(), "Receber R$ 30,00");

        page.clicarConfirmarRecebimento()
                .esperarStatus(5, "Concluído");

        Assert.assertEquals(page.buscarPagamento(5), "Pago: Pix");
        Assert.assertEquals(BancoDeTeste.statusDoAgendamento(5), "concluido");
        Assert.assertEquals(BancoDeTeste.entradasDoAgendamento(5), "pix=30.00");
    }

    @Test(priority = 2, description = "CT04.2 - Pagamento dividido: R$ 20 em dinheiro + R$ 10 no Pix")
    public void receberDividido() {
        AgendaPainelPage page = new AgendaPainelPage(getDriver());

        page.clicarReceber(6)
                .clicarDividirPagamento()
                .escolherFormaDaParte(1, "dinheiro")
                .escolherFormaDaParte(2, "pix")
                .informarValorDaParte(1, "20");

        // A segunda forma completa sozinha o que falta.
        Assert.assertEquals(page.buscarValorDaParte(2), "10");
        Assert.assertEquals(page.buscarSituacaoDivisao(), "Fecha R$ 30,00 ✓");

        page.clicarConfirmarRecebimento()
                .esperarStatus(6, "Concluído");

        String pagamento = page.buscarPagamento(6);
        Assert.assertTrue(pagamento.contains("Dinheiro") && pagamento.contains("Pix"), pagamento);
        Assert.assertEquals(BancoDeTeste.entradasDoAgendamento(6), "dinheiro=20.00;pix=10.00");
    }

    @Test(priority = 3, description = "CT04.3 - Valores divididos acima do total bloqueiam o recebimento")
    public void divisaoAcimaDoTotalBloqueia() {
        AgendaPainelPage page = new AgendaPainelPage(getDriver());

        page.clicarReceber(7)
                .clicarDividirPagamento()
                .informarValorDaParte(1, "40");

        Assert.assertEquals(page.buscarSituacaoDivisao(), "Passou R$ 10,00 de R$ 30,00");
        Assert.assertFalse(page.botaoReceberHabilitado(), "Receber deveria estar desabilitado");
        Assert.assertEquals(BancoDeTeste.statusDoAgendamento(7), "confirmado");
    }
}
