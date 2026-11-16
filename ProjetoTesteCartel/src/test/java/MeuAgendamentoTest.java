import org.example.base.BancoDeTeste;
import org.example.base.BaseTest;
import org.example.pages.MeuAgendamentoPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Cenario CT03 - Link "Meu agendamento" do cliente (/agendamento/{codigo}).
 * Massa de dados: agendamentos 1 a 4 com codigos fixos (ver massa-de-dados.sql).
 */
public class MeuAgendamentoTest extends BaseTest {

    private static final String CODIGO_CANCELA = "00000000-0000-0000-0000-000000000001";
    private static final String CODIGO_TROCA = "00000000-0000-0000-0000-000000000002";
    private static final String CODIGO_DESISTE = "00000000-0000-0000-0000-000000000003";
    private static final String CODIGO_EM_CIMA = "00000000-0000-0000-0000-000000000004";

    @Test(priority = 1, description = "CT03.1 - Cliente cancela o agendamento pelo link")
    public void cancelarAgendamento() {
        abrir("/agendamento/" + CODIGO_CANCELA);
        MeuAgendamentoPage page = new MeuAgendamentoPage(getDriver());
        Assert.assertEquals(page.buscarTitulo(), "Tá marcado");

        page.clicarCancelar()
                .confirmarNaJanela()
                .esperarTitulo("Agendamento cancelado");

        Assert.assertEquals(page.buscarTitulo(), "Agendamento cancelado");
        Assert.assertFalse(page.botaoCancelarVisivel(), "Botao Cancelar deveria sumir");
        Assert.assertEquals(BancoDeTeste.statusDoAgendamento(1), "cancelado");
    }

    @Test(priority = 2, description = "CT03.2 - Cliente troca a forma de pagamento")
    public void trocarFormaDePagamento() {
        abrir("/agendamento/" + CODIGO_TROCA);
        MeuAgendamentoPage page = new MeuAgendamentoPage(getDriver());
        Assert.assertEquals(page.buscarFormaPagamento(), "Dinheiro");

        page.clicarTrocarPagamento()
                .escolherNovaForma("pix")
                .esperarFormaPagamento("Pix");

        Assert.assertEquals(page.buscarFormaPagamento(), "Pix");
        Assert.assertEquals(BancoDeTeste.formaDoAgendamento(2), "pix");
    }

    @Test(priority = 3, description = "CT03.3 - Cliente desiste na janela de confirmacao e o horario continua marcado")
    public void desistirDeCancelar() {
        abrir("/agendamento/" + CODIGO_DESISTE);
        MeuAgendamentoPage page = new MeuAgendamentoPage(getDriver());

        page.clicarCancelar()
                .desistirNaJanela();

        Assert.assertEquals(page.buscarTitulo(), "Tá marcado");
        Assert.assertTrue(page.botaoCancelarVisivel(), "Botao Cancelar deveria continuar");
        Assert.assertEquals(BancoDeTeste.statusDoAgendamento(3), "confirmado");
    }

    @Test(priority = 4, description = "CT03.4 - Faltando menos de 60 minutos o cliente nao consegue cancelar pelo site")
    public void naoCancelaEmCimaDaHora() {
        abrir("/agendamento/" + CODIGO_EM_CIMA);
        MeuAgendamentoPage page = new MeuAgendamentoPage(getDriver());

        Assert.assertEquals(page.buscarTitulo(), "Tá marcado");
        Assert.assertFalse(page.botaoCancelarVisivel(), "Nao deveria mostrar o botao Cancelar");
        Assert.assertTrue(page.buscarTextoDaPagina().contains("fale com a barbearia"), "Deveria orientar a falar com a barbearia");
    }
}
