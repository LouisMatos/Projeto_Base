$(document).ready(function() {

    // Ativa os dropdowns do Bootstrap
    $(".dropdown-toggle").dropdown();

    // Permite abrir submenus clicando no item pai (dropdown-submenu)
    $('.dropdown-submenu a.test').on("click", function(e) {
        $(this).next('ul').toggle();
        e.stopPropagation();
        e.preventDefault();
    });

});
