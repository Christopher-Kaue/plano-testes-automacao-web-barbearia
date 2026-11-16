import org.example.base.BancoDeTeste;
import org.example.base.BaseTest;
import org.example.pages.AgendamentoPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Cenario CT02 - Agendamento online pelo cliente (tela /agendar).
 * Massa de dados: servico 1 = Corte degradê (R$ 30, 40 min); Barbeiro Dois (id 2) de folga amanha.
 */
public class AgendamentoOnlineTest extends BaseTest {

    private final String amanha = diaDaqui(1);

    @Test(priority = 1, description = "CT02.1 - Agendar com todos os dados validos")
    public void agendarComDadosValidos() {
        abrir("/agendar");
        AgendamentoPage page = new AgendamentoPage(getDriver());

        String titulo = page.selecionarServico(1)
                .selecionarSemPreferencia()
                .selecionarDia(amanha)
                .selecionarHorario("10:00")
                .informarNome("Joao Teste")
                .informarTelefone("51987654321")
                .informarObservacao("Teste automatizado")
                .selecionarFormaPagamento("dinheiro")
                .clicarConfirmar()
                .buscarTituloConfirmacao();

        Assert.assertEquals(titulo, "Tá marcado!");
        String detalhes = page.buscarDetalhesConfirmacao();
        Assert.assertTrue(detalhes.contains("Corte degradê com Barbeiro Um"), detalhes);
        Assert.assertTrue(detalhes.contains("às 10:00"), detalhes);
        Assert.assertTrue(detalhes.contains("R$ 30,00"), detalhes);
        Assert.assertTrue(detalhes.contains("Pagamento na barbearia: Dinheiro"), detalhes);
        Assert.assertTrue(page.buscarLinkMeuAgendamento().contains("/agendamento/"), "Link do cliente nao gerado");
        // Conferencia no banco: o agendamento foi gravado uma unica vez.
        Assert.assertEquals(BancoDeTeste.agendamentosDoTelefone("51987654321"), 1);
    }

    @Test(priority = 2, description = "CT02.2 - Sem escolher a forma de pagamento o botao Confirmar fica bloqueado")
    public void confirmarBloqueadoSemFormaDePagamento() {
        abrir("/agendar");
        AgendamentoPage page = new AgendamentoPage(getDriver());

        page.selecionarServico(1)
                .selecionarDia(amanha)
                .selecionarHorario("11:00")
                .informarNome("Maria Teste")
                .informarTelefone("51988887777");

        Assert.assertFalse(page.botaoConfirmarHabilitado(), "Confirmar deveria estar desabilitado");
    }

    @Test(priority = 3, description = "CT02.3 - Telefone incompleto nao deixa agendar")
    public void telefoneIncompletoNaoAgenda() {
        abrir("/agendar");
        AgendamentoPage page = new AgendamentoPage(getDriver());

        page.selecionarServico(1)
                .selecionarDia(amanha)
                .selecionarHorario("11:00")
                .informarNome("Pedro Teste")
                .informarTelefone("5199")
                .selecionarFormaPagamento("pix")
                .clicarConfirmar();

        Assert.assertEquals(page.buscarTelefoneDigitado(), "(51) 99", "Mascara do telefone");
        Assert.assertFalse(page.telefoneValido(), "O campo deveria estar invalido");
        Assert.assertFalse(page.confirmacaoApareceu(), "Nao deveria confirmar o agendamento");
        Assert.assertEquals(BancoDeTeste.agendamentosDoTelefone("5199"), 0);
    }

    @Test(priority = 4, description = "CT02.4 - Barbeiro de folga no dia nao mostra horarios")
    public void barbeiroDeFolgaNaoMostraHorarios() {
        abrir("/agendar");
        AgendamentoPage page = new AgendamentoPage(getDriver());

        String aviso = page.selecionarServico(1)
                .selecionarBarbeiro(2)
                .selecionarDia(amanha)
                .buscarAvisoFolga();

        Assert.assertTrue(aviso.contains("está de folga nesse dia"), "Aviso recebido: " + aviso);
    }
}
