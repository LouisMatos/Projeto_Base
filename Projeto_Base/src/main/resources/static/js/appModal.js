/**
 * AppModal — Módulo global de modais de feedback.
 * Disponível em todas as páginas do projeto via layout.xhtml.
 *
 * ── API completa ────────────────────────────────────────────────────────────
 *
 *   AppModal.show({
 *       titulo:   'Título do modal',
 *       mensagem: 'Texto exibido ao usuário.',
 *       tipo:     'success' | 'error' | 'warning' | 'info',   // padrão: 'info'
 *       onClose:  function() { ... }                           // opcional
 *   });
 *
 * ── Atalhos ─────────────────────────────────────────────────────────────────
 *
 *   AppModal.showSucesso('Registro salvo com sucesso!');
 *   AppModal.showErro('Não foi possível concluir a operação.');
 *   AppModal.showAviso('Atenção: verifique os dados antes de prosseguir.');
 *   AppModal.showInfo('O prazo de envio é até sexta-feira.');
 *
 * ── Com callback ao fechar ───────────────────────────────────────────────────
 *
 *   AppModal.showSucesso('Salvo!', function() {
 *       window.location.href = '/aplicacao/principal.xhtml';
 *   });
 *
 * ── Chamada a partir de um ManagedBean (server-side) ────────────────────────
 *
 *   FacesUtil.executeScript("AppModal.showSucesso('Operação realizada!');");
 *
 */
var AppModal = (function ($) {

    /* Configuração visual por tipo ---------------------------------------- */
    var _tipos = {
        success: {
            headerClass: 'modal-header-success',
            iconeGlyph:  'glyphicon-ok-circle',
            iconeClass:  'app-modal-icon-success',
            tituloDefault: 'Sucesso!'
        },
        error: {
            headerClass: 'modal-header-error',
            iconeGlyph:  'glyphicon-remove-circle',
            iconeClass:  'app-modal-icon-error',
            tituloDefault: 'Erro'
        },
        warning: {
            headerClass: 'modal-header-warning',
            iconeGlyph:  'glyphicon-exclamation-sign',
            iconeClass:  'app-modal-icon-warning',
            tituloDefault: 'Atenção'
        },
        info: {
            headerClass: 'modal-header-info',
            iconeGlyph:  'glyphicon-info-sign',
            iconeClass:  'app-modal-icon-info',
            tituloDefault: 'Informação'
        }
    };

    /* Classes usadas nas trocas de estado (facilita o removeClass) --------- */
    var _todasHeaderClasses = 'modal-header-success modal-header-error modal-header-warning modal-header-info';
    var _todasIconeClasses  = 'app-modal-icon-success app-modal-icon-error app-modal-icon-warning app-modal-icon-info';
    var _todosGlyphs        = 'glyphicon-ok-circle glyphicon-remove-circle glyphicon-exclamation-sign glyphicon-info-sign';

    /* Exibe o modal com as opções fornecidas -------------------------------- */
    function show(options) {
        var tipo = (options && options.tipo && _tipos[options.tipo]) ? options.tipo : 'info';
        var cfg  = _tipos[tipo];

        /* Header */
        $('#appModal .modal-header')
            .removeClass(_todasHeaderClasses)
            .addClass(cfg.headerClass);

        $('#appModalTitulo').text(options.titulo || cfg.tituloDefault);

        /* Ícone */
        $('#appModalIcone')
            .attr('class', 'glyphicon app-modal-icon ' + cfg.iconeGlyph + ' ' + cfg.iconeClass);

        /* Mensagem */
        $('#appModalMensagem').text(options.mensagem || '');

        /* Callback ao fechar — registrado apenas uma vez */
        if (typeof options.onClose === 'function') {
            $('#appModal').one('hidden.bs.modal', options.onClose);
        }

        /* Exibe */
        $('#appModal').modal({ backdrop: 'static', keyboard: true });
        $('#appModal').modal('show');
    }

    /* API pública ---------------------------------------------------------- */
    return {
        show: show,

        showSucesso: function (mensagem, onClose) {
            show({ tipo: 'success', mensagem: mensagem, onClose: onClose });
        },
        showErro: function (mensagem, onClose) {
            show({ tipo: 'error', mensagem: mensagem, onClose: onClose });
        },
        showAviso: function (mensagem, onClose) {
            show({ tipo: 'warning', mensagem: mensagem, onClose: onClose });
        },
        showInfo: function (mensagem, onClose) {
            show({ tipo: 'info', mensagem: mensagem, onClose: onClose });
        }
    };

}(jQuery));

/* ════════════════════════════════════════════════════════════════════════════
   AppConfirm — Modal de confirmação reutilizável.

   Uso:
     AppConfirm.show({
         titulo:   'Excluir usuário',                           // opcional
         mensagem: 'Deseja realmente excluir "fulano@..."?',
         onConfirm: function() { ... }                          // obrigatório
     });
   ════════════════════════════════════════════════════════════════════════════ */
var AppConfirm = (function ($) {

    function show(options) {
        var $modal = $('#appConfirmModal');

        $('#confirmModalTitulo').text(options.titulo || 'Confirmar');
        $('#confirmModalMensagem').text(options.mensagem || '');

        /* Remove listener anterior para evitar chamadas duplas */
        $('#btnConfirmModalOk')
            .off('click.appconfirm')
            .on('click.appconfirm', function () {
                $modal.modal('hide');
                if (typeof options.onConfirm === 'function') {
                    /* Aguarda o modal fechar antes de executar a ação */
                    setTimeout(options.onConfirm, 300);
                }
            });

        $modal.modal({ backdrop: 'static', keyboard: false });
        $modal.modal('show');
    }

    return { show: show };

}(jQuery));
