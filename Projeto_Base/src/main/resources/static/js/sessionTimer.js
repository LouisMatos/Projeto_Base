/**
 * SessionTimer — Controle de timeout de sessão no lado do cliente.
 *
 * Fluxo:
 *   0 min ──────────────────────────── 8 min ── aviso (countdown 2:00) ── 10 min
 *                                                  ↓ "Continuar" → keep-alive AJAX + reset
 *                                                  ↓ "Sair"      → logout + login
 *                                                  ↓ sem ação    → modal "Sessão encerrada" + redirect
 *
 * Reset automático:
 *   Qualquer request AJAX ao servidor (botão, formulário) dispara jsf.ajax.addOnEvent
 *   e reseta o timer no cliente — mantendo-o sincronizado com o timeout do servidor.
 *
 * Inicialização (feita automaticamente por layout.xhtml para usuários autenticados):
 *   SessionTimer.init('/contextPath/index.xhtml');
 */
var SessionTimer = (function ($) {

    /* ── Configuração (em milissegundos) ──────────────────────────────────── */
    var SESSION_MS  = 10 * 60 * 1000;   // Timeout total da sessão: 10 minutos
    var WARNING_MS  =  2 * 60 * 1000;   // Exibir aviso faltando: 2 minutos
    /* O aviso dura exatamente WARNING_MS até o redirecionamento automático.    */

    /* ── Estado interno ───────────────────────────────────────────────────── */
    var _warningTimer;
    var _expireTimer;
    var _countdownInterval;
    var _loginUrl      = '/index.xhtml';
    var _lastAjaxReset = 0;             // throttle: evita resets em < 5s

    /* ════════════════════════════════════════════════════════════════════════
       Timer principal
       ════════════════════════════════════════════════════════════════════════ */

    function _startTimers() {
        clearTimeout(_warningTimer);
        clearTimeout(_expireTimer);
        clearInterval(_countdownInterval);

        _warningTimer = setTimeout(_showWarning,  SESSION_MS - WARNING_MS);
        _expireTimer  = setTimeout(_onExpire,     SESSION_MS);
    }

    function reset() {
        /* Fecha o modal de aviso se estiver aberto e reinicia os timers. */
        if ($('#sessionWarningModal').hasClass('in')) {
            $('#sessionWarningModal').modal('hide');
        }
        _startTimers();
    }

    /* ════════════════════════════════════════════════════════════════════════
       Modal de aviso (faltam 2 minutos)
       ════════════════════════════════════════════════════════════════════════ */

    function _showWarning() {
        var secsLeft = Math.floor(WARNING_MS / 1000);   /* 120 */
        _updateCountdown(secsLeft);

        $('#sessionWarningModal').modal({ backdrop: 'static', keyboard: false });
        $('#sessionWarningModal').modal('show');

        _countdownInterval = setInterval(function () {
            secsLeft--;
            _updateCountdown(secsLeft);
            if (secsLeft <= 0) {
                clearInterval(_countdownInterval);
            }
        }, 1000);
    }

    function _updateCountdown(secs) {
        var m = Math.floor(secs / 60);
        var s = secs % 60;
        var $el = $('#sessionCountdown');
        $el.text((m < 10 ? '0' : '') + m + ':' + (s < 10 ? '0' : '') + s);
        /* Últimos 30 segundos: destaque visual vermelho pulsante */
        if (secs <= 30) {
            $el.addClass('urgente');
        } else {
            $el.removeClass('urgente');
        }
    }

    /* ════════════════════════════════════════════════════════════════════════
       Sessão expirada
       ════════════════════════════════════════════════════════════════════════ */

    function _onExpire() {
        clearInterval(_countdownInterval);

        /* Fecha aviso antes de abrir o modal de expirado */
        $('#sessionWarningModal').modal('hide');
        setTimeout(function () {
            $('#sessionExpiredModal').modal({ backdrop: 'static', keyboard: false });
            $('#sessionExpiredModal').modal('show');
        }, 350);
    }

    /* ════════════════════════════════════════════════════════════════════════
       Keep-alive: toca o servidor para renovar o timeout da HttpSession
       ════════════════════════════════════════════════════════════════════════ */

    function keepAlive() {
        /* Aciona o botão JSF oculto — o AJAX já renova a sessão no servidor */
        var btn = document.getElementById('sessionControlForm:btnKeepAlive');
        if (btn) {
            btn.click();
        }
        $('#sessionWarningModal').modal('hide');
        _startTimers();
    }

    /* ════════════════════════════════════════════════════════════════════════
       Logout (chamado pelo botão "Sair" no modal de aviso)
       ════════════════════════════════════════════════════════════════════════ */

    function _doLogout() {
        $('#sessionWarningModal').modal('hide');
        /* Aciona o botão de logout oculto (non-AJAX → redirect completo) */
        setTimeout(function () {
            var btnLogout = document.getElementById('sessionControlForm:btnLogoutSession');
            if (btnLogout) {
                btnLogout.click();
            } else {
                window.location.href = _loginUrl;
            }
        }, 350);
    }

    /* ════════════════════════════════════════════════════════════════════════
       Integração com o runtime AJAX do JSF/PrimeFaces
       Qualquer requisição AJAX concluída reseta o timer no cliente,
       mantendo-o sincronizado com o timeout do servidor.
       ════════════════════════════════════════════════════════════════════════ */

    if (typeof jsf !== 'undefined' && jsf.ajax && typeof jsf.ajax.addOnEvent === 'function') {
        jsf.ajax.addOnEvent(function (data) {
            if (data.status === 'complete') {
                var now = Date.now();
                /* Throttle: no máximo 1 reset a cada 5 s para evitar trabalho desnecessário */
                if (now - _lastAjaxReset > 5000) {
                    _lastAjaxReset = now;
                    _startTimers();
                }
            }
        });
    }

    /* ════════════════════════════════════════════════════════════════════════
       Inicialização pública
       ════════════════════════════════════════════════════════════════════════ */

    function init(loginUrl) {
        _loginUrl = loginUrl || '/index.xhtml';

        /* Botão "Continuar" no modal de aviso */
        $(document).on('click', '#btnSessionKeepAlive', keepAlive);

        /* Botão "Sair" no modal de aviso */
        $(document).on('click', '#btnSessionLogout', _doLogout);

        /* Botão "Ir para o Login" no modal de expirado */
        $(document).on('click', '#btnSessionExpiredLogin', function () {
            window.location.href = _loginUrl;
        });

        _startTimers();
    }

    /* ── API pública ─────────────────────────────────────────────────────── */
    return {
        init:      init,
        reset:     reset,
        keepAlive: keepAlive
    };

}(jQuery));
