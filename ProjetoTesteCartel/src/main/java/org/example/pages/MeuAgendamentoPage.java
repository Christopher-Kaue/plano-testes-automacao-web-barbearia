package org.example.pages;

import org.openqa.selenium.WebDriver;

/** Tela /agendamento/{codigo}: o cliente ve o horario, cancela ou troca a forma de pagamento. */
public class MeuAgendamentoPage extends BasePage {

    public MeuAgendamentoPage(WebDriver driver) {
        super(driver);
    }

    /** "Tá marcado", "Agendamento cancelado"... */
    public String buscarTitulo() {
        return textoDoId("meu-titulo");
    }

    public String buscarFormaPagamento() {
        return textoDoId("meu-forma-pagamento");
    }

    public MeuAgendamentoPage clicarCancelar() {
        clicarId("meu-cancelar");
        return this;
    }

    /** Na janela de confirmacao, clica em "Cancelar agendamento". */
    public MeuAgendamentoPage confirmarNaJanela() {
        clicarId("confirmacao-permitir");
        return this;
    }

    /** Na janela de confirmacao, clica em "Voltar" (desiste). */
    public MeuAgendamentoPage desistirNaJanela() {
        clicarId("confirmacao-cancelar");
        return this;
    }

    public MeuAgendamentoPage clicarTrocarPagamento() {
        clicarId("meu-trocar");
        return this;
    }

    /** forma: pix, dinheiro, credito ou debito */
    public MeuAgendamentoPage escolherNovaForma(String forma) {
        clicarId("trocar-" + forma);
        return this;
    }

    public MeuAgendamentoPage esperarTitulo(String titulo) {
        esperarTexto("meu-titulo", titulo);
        return this;
    }

    public MeuAgendamentoPage esperarFormaPagamento(String forma) {
        esperarTexto("meu-forma-pagamento", forma);
        return this;
    }

    public boolean botaoCancelarVisivel() {
        return existe("meu-cancelar");
    }

    /** Texto da pagina inteira (para conferir avisos). */
    public String buscarTextoDaPagina() {
        return driver.findElement(org.openqa.selenium.By.tagName("main")).getText();
    }
}
