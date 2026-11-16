package org.example.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Base de todas as classes de teste (mesmo modelo da aula):
 * antes da suite prepara o banco de teste; antes de cada classe abre o Edge; no fim fecha o navegador.
 */
public class BaseTest {
    public String url;
    public WebDriver driver;

    private static boolean bancoPronto = false;

    @BeforeSuite
    public void prepararBanco() {
        if (!bancoPronto) {
            BancoDeTeste.recriarMassaDeDados();
            bancoPronto = true;
        }
    }

    @BeforeClass
    public void preCond() {
        url = Configuracao.urlBase();
        driver = new EdgeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
    }

    @AfterClass(alwaysRun = true)
    public void posCond() {
        if (driver != null) driver.quit();
    }

    protected WebDriver getDriver() {
        return driver;
    }

    /** Abre uma pagina do sistema, ex.: abrir("/agendar"). */
    protected void abrir(String caminho) {
        driver.get(url + caminho);
    }

    /** Data de hoje + n dias, no fuso da barbearia, no formato do sistema (AAAA-MM-DD). */
    protected static String diaDaqui(int dias) {
        return LocalDate.now(ZoneId.of("America/Sao_Paulo")).plusDays(dias).toString();
    }
}
