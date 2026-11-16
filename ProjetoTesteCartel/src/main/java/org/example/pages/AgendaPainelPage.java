package org.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Aba Agenda do painel (/admin): lista os atendimentos do dia e o formulario "Receber".
 * Cada atendimento tem id com o numero dele: agendamento-5, agendamento-5-status, agendamento-5-receber.
 */
public class AgendaPainelPage extends BasePage {

    public AgendaPainelPage(WebDriver driver) {
        super(driver);
    }

    public AgendaPainelPage clicarReceber(int idAgendamento) {
        clicarId("agendamento-" + idAgendamento + "-receber");
        return this;
    }

    /** forma: pix, dinheiro, credito, debito ou permuta */
    public AgendaPainelPage selecionarForma(String forma) {
        clicarId("receber-forma-" + forma);
        return this;
    }

    public AgendaPainelPage clicarDividirPagamento() {
        clicarId("receber-dividir");
        return this;
    }

    /** Linha do pagamento dividido (1, 2, 3...): escolhe a forma pelo valor (pix, dinheiro...). */
    public AgendaPainelPage escolherFormaDaParte(int parte, String forma) {
        WebElement select = porId("receber-parte-" + parte + "-forma");
        new Select(select).selectByValue(forma);
        return this;
    }

    public AgendaPainelPage informarValorDaParte(int parte, String valor) {
        digitar(porId("receber-parte-" + parte + "-valor"), valor);
        return this;
    }

    public String buscarValorDaParte(int parte) {
        return porId("receber-parte-" + parte + "-valor").getAttribute("value");
    }

    /** "Fecha R$ 30,00 ✓", "Faltam R$ ...", "Passou R$ ..." */
    public String buscarSituacaoDivisao() {
        return textoDoId("receber-situacao");
    }

    public boolean botaoReceberHabilitado() {
        return porId("receber-confirmar").isEnabled();
    }

    public String buscarTextoBotaoReceber() {
        return textoDoId("receber-confirmar");
    }

    public AgendaPainelPage clicarConfirmarRecebimento() {
        clicarId("receber-confirmar");
        return this;
    }

    /** Espera a etiqueta de status do atendimento mostrar o texto (ex.: "Concluído"). */
    public AgendaPainelPage esperarStatus(int idAgendamento, String status) {
        esperarTexto("agendamento-" + idAgendamento + "-status", status);
        return this;
    }

    public String buscarStatus(int idAgendamento) {
        return textoDoId("agendamento-" + idAgendamento + "-status");
    }

    /** Etiqueta de pagamento, ex.: "Pago: Pix" ou "Pago: Dinheiro + Pix". */
    public String buscarPagamento(int idAgendamento) {
        return textoDoId("agendamento-" + idAgendamento + "-pagamento");
    }
}
