$(document).ready(function() {
    $("#addReservation").submit(function(e){
        return false;
    });

    var calendar = $('#calendar');

    calendar.fullCalendar({
        header: {
            left:   'title',
            center: '',
            right:  'today,month,agendaDay,agendaWeek prev,next'
        }
    });

    calendar.fullCalendar('changeView', 'agendaDay', $("#date").val());

    getEvents(calendar);
});

function getEvents(calendar) {
    $.ajax({
        type: "GET",
        url: "http://websockets.dnet.hu:9000/reservation/api",
        dataType: "json",
        success: function (data) {
            $.each( data, function(index, reservation) {
                var event = new Object();
                event.id = index;
                event.title = reservation.tableNumber + ". " + reservation.name;
                event.start = reservation.date + " " + reservation.startTime;
                event.end = reservation.date + " " + reservation.endTime;
                calendar.fullCalendar('renderEvent', event);
            });
        }
    });
}

function submitForm(e){
    var $form = $("#addReservation");
    var data = getFormData($form);
    $.ajax({
        type: "POST",
        url: "http://websockets.dnet.hu:9000/reservation/api",
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