package br.com.luismatos.session;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class AtulizacaoAutomaticaDeMensagens implements PhaseListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void beforePhase(PhaseEvent event) {
        FacesContext.getCurrentInstance().getPartialViewContext()
            .getRenderIds().add(":mensagens");
    }

    @Override
    public void afterPhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
