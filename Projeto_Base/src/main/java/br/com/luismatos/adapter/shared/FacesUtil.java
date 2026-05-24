package br.com.luismatos.adapter.shared;

import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesUtil {

    private FacesUtil() {}

    public static boolean isPostback() {
        return FacesContext.getCurrentInstance().isPostback();
    }

    public static boolean isNotPostback() {
        return !isPostback();
    }

    public static void addErrorMessage(String message) {
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    }

    public static void addInfoMessage(String message) {
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    }

    /**
     * Executa um trecho de JavaScript no cliente após a resposta AJAX do PrimeFaces.
     *
     * Uso típico para exibir o modal de feedback:
     *   FacesUtil.executeScript("AppModal.showSucesso('Registro salvo!');");
     *   FacesUtil.executeScript("AppModal.showErro('Erro ao processar.');");
     */
    public static void executeScript(String script) {
        RequestContext.getCurrentInstance().execute(script);
    }

    /**
     * Escapa caracteres especiais de uma String para uso seguro em literais JavaScript.
     * Previne quebra de sintaxe quando o valor contém aspas, barras ou quebras de linha.
     *
     * Uso:
     *   FacesUtil.executeScript("AppModal.showSucesso('" + FacesUtil.escapeJs(email) + "');");
     */
    public static String escapeJs(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("'",  "\\'")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
