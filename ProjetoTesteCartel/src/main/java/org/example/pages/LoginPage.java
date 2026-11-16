package org.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Tela /admin/login (Area do barbeiro). */
public class LoginPage extends BasePage {

    @FindBy(id = "login-usuario")
    private WebElement txtUsuario;

    @FindBy(id = "login-senha")
    private WebElement txtSenha;

    @FindBy(id = "login-entrar")
    private WebElement btnEntrar;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage informarUsuario(String usuario) {
        digitar(txtUsuario, usuario);
        return this;
    }

    public LoginPage informarSenha(String senha) {
        digitar(txtSenha, senha);
        return this;
    }

    public LoginPage clicarEntrar() {
        clicar(btnEntrar);
        return this;
    }

    /** Mensagem de erro mostrada abaixo dos campos. */
    public String buscarMensagemErro() {
        return textoDoId("login-erro");
    }

    /** Espera o painel abrir depois do login e devolve o endereco da pagina. */
    public String esperarPainel() {
        espera.until(ExpectedConditions.urlMatches(".*/admin/?$"));
        porId("painel");
        return driver.getCurrentUrl();
    }
}
