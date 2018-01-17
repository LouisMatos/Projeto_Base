function mascara(o,f){
	v_obj=o
	v_fun=f
	setTimeout("execmascara()",1)
}
function execmascara(){
	v_obj.value=v_fun(v_obj.value)
}
function soLetras(v){
	return v.replace(/\d/g,"") //Remove tudo o que não é Letra
}
function soLetrasMA(v){
	v=v.toUpperCase() //Maiúsculas
	return v.replace(/\d/g,"") //Remove tudo o que não é Letra ->maiusculas
}
function soLetrasMI(v){
	v=v.toLowerCase() //Minusculas
	return v.replace(/\d/g,"") //Remove tudo o que não é Letra ->minusculas
}
function soNumeros(v){
	return v.replace(/\D/g,"") //Remove tudo o que não é dígito
}



$(document).ready(function(){
	$("#example").dataTable({                              
		 "oLanguage": {
		    "sProcessing": "Aguarde enquanto os dados são carregados ...",
		    "sLengthMenu": "Mostrar _MENU_ registros por pagina",
		    "sZeroRecords": "Nenhum registro correspondente ao criterio encontrado",
		    "sInfoEmtpy": "Exibindo 0 a 0 de 0 registros",
		    "sInfo": "Exibindo de _START_ a _END_ de _TOTAL_ registros",
		    "sInfoFiltered": "",
		    "sSearch": "Procurar",
		    "oPaginate": {
		       "sFirst":    "Primeiro",
		       "sPrevious": "Anterior",
		       "sNext":     "Próximo",
		       "sLast":     "Último"
		    }
		 }                              
		});   
	});




