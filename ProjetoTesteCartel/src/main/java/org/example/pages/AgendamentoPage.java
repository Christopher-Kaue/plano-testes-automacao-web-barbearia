package org.example.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Tela /agendar: o cliente escolhe salao, servico, barbeiro, dia, horario, informa os dados e a forma de pagamento.
 * Botoes que se repetem (servicos, dias, horarios) tem id com o valor: servico-1, data-2026-11-16, hora-10:00, forma-pix.
 */
public class AgendamentoPage extends BasePage {

    @FindBy(id = "agendamento-nome")
    private WebElement txtNome;

    @FindBy(id = "agendamento-telefone")
    private WebElement txtTelefone;

    @FindBy(id = "agendamento-observacao")
    private WebElement txtObservacao;

    @FindBy(id = "agendamento-confirmar")
    private WebElement btnConfirmar;

    public AgendamentoPage(WebDriver driver) {
        super(driver);
    }

    public AgendamentoPage selecionarServico(int idServico) {
        clicarId("servico-" + idServico);
        return this;
    }

    public AgendamentoPage selecionarSemPreferencia() {
        clicarId("barbeiro-sem-preferencia");
        return this;
    }

    public AgendamentoPage selecionarBarbeiro(int idBarbeiro) {
        clicarId("barbeiro-" + idBarbeiro);
        return this;
    }

    /** data no formato AAAA-MM-DD */
    public AgendamentoPage selecionarDia(String data) {
        clicarId("data-" + data);
        return this;
    }

    /** hora no formato HH:MM (espera os horarios livres carregarem do servidor) */
    public AgendamentoPage selecionarHorario(String hora) {
        clicarId("hora-" + hora);
        return this;
    }

    public AgendamentoPage informarNome(String nome) {
        digitar(txtNome, nome);
        return this;
    }

    public AgendamentoPage informarTelefone(String telefone) {
        digitar(txtTelefone, telefone);
        return this;
    }

    public AgendamentoPage informarObservacao(String observacao) {
        digitar(txtObservacao, observacao);
        return this;
    }

    /** forma: pix, dinheiro, credito ou debito */
    public AgendamentoPage selecionarFormaPagamento(String forma) {
        clicarId("forma-" + forma);
        return this;
    }

    public AgendamentoPage clicarConfirmar() {
        clicar(btnConfirmar);
        return this;
    }

    public boolean botaoConfirmarHabilitado() {
        return btnConfirmar.isEnabled();
    }

    /** O navegador aceita o telefone digitado? (regra do campo: "(51) 99999-9999") */
    public boolean telefoneValido() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].validity.valid;", txtTelefone);
    }

    public String buscarTelefoneDigitado() {
        return txtTelefone.getAttribute("value");
    }

    /** Titulo da tela de sucesso ("Tá marcado!"). */
    public String buscarTituloConfirmacao() {
        return textoDoId("confirmado-titulo");
    }

    /** Quadro com servico, barbeiro, dia, hora, preco e forma de pagamento do agendamento feito. */
    public String buscarDetalhesConfirmacao() {
        return textoDoId("confirmado-detalhes");
    }

    public boolean confirmacaoApareceu() {
        return existe("confirmado-titulo");
    }

    /** Aviso do passo "Que horas?" quando o barbeiro esta de folga. */
    public String buscarAvisoFolga() {
        return textoDoId("aviso-folga");
    }

    /** Endereco do link "Meu agendamento" da tela de sucesso. */
    public String buscarLinkMeuAgendamento() {
        return porId("link-meu-agendamento").getAttribute("href");
    }
}
