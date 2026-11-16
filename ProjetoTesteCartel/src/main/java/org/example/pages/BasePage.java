package org.example.pages;

import org.example.base.Configuracao;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base dos Page Objects (mesmo modelo da aula), com esperas:
 * o sistema e feito em React e as telas mudam depois de chamar o servidor,
 * entao o teste espera o elemento aparecer antes de usar.
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait espera;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.espera = new WebDriverWait(driver, Duration.ofSeconds(Configuracao.esperaSegundos()));
        PageFactory.initElements(driver, this);
    }

    /** Espera o elemento com esse id ficar visivel e devolve ele. */
    protected WebElement porId(String id) {
        return espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
    }

    /**
     * Rola ate o elemento e clica. O site usa rolagem suave (scroll-behavior: smooth), entao a rolagem aqui e
     * instantanea para o botao nao estar "andando" na hora do clique; se algo ainda cobrir o botao, tenta de novo.
     */
    protected void clicar(WebElement elemento) {
        espera.until(ExpectedConditions.elementToBeClickable(elemento));
        espera.ignoring(org.openqa.selenium.ElementClickInterceptedException.class).until(d -> {
            ((JavascriptExecutor) d).executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});", elemento);
            elemento.click();
            return true;
        });
    }

    protected void clicarId(String id) {
        clicar(porId(id));
    }

    /** Apaga o campo e digita o texto. */
    protected void digitar(WebElement campo, String texto) {
        espera.until(ExpectedConditions.visibilityOf(campo));
        campo.clear();
        campo.sendKeys(texto);
    }

    /** Texto do elemento, com o espaco especial do "R$ 30,00" trocado por espaco normal. */
    protected String textoDoId(String id) {
        return porId(id).getText().replace(' ', ' ').trim();
    }

    /** Espera um texto aparecer dentro do elemento (ex.: status mudar para "Concluído"). */
    protected void esperarTexto(String id, String texto) {
        espera.until(d -> {
            try {
                return d.findElement(By.id(id)).getText().replace(' ', ' ').contains(texto);
            } catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e) {
                return false;
            }
        });
    }

    protected boolean existe(String id) {
        return !driver.findElements(By.id(id)).isEmpty();
    }
}
