$(document).ready(function() {
    $("#addReservation").submit(function(e){
        return false;
    });
});

function submitForm(e){
    var $form = $("#addReservation");
    var data = getFormData($form);
    $.ajax({
        type: "POST",
        url: "http://websockets.dnet.hu:9000/reservation",
        data: JSON.stringify(data),
        contentType: "application/json",
        dataType: 'json',
        success: function() {
            location.reload();
        },
        error: function(error){
            console.log(error);
            alert(error.responseText);
        }
    });
}

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};
    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });
    return indexed_array;
}